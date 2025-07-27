package com.dushmantha.weather_analyzer.service;

import com.dushmantha.weather_analyzer.dto.WeatherApiResponse;
import com.dushmantha.weather_analyzer.dto.WeatherData;
import com.dushmantha.weather_analyzer.dto.WeatherSummary;
import com.dushmantha.weather_analyzer.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author dushmantha.sse@gmail.com
 */
@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final WebClient webClient;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public WeatherServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "weatherCache", key = "#city.toLowerCase()")
    public CompletableFuture<WeatherSummary> weatherSummary(String city) {
        try {
            WeatherApiResponse response = fetchWeatherData(city);
            WeatherSummary summary = processWeatherData(response);
            return CompletableFuture.completedFuture(summary);
        } catch (Exception e) {
            log.error("Error processing weather data for city: {}", city);
            return CompletableFuture.failedFuture(e);
        }
    }

    private WeatherApiResponse fetchWeatherData(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        log.info("Fetch weather data using url: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(errorBody -> {
                            int status = response.statusCode().value();

                            if (status == 404) {
                                log.error("City not found (404): {} | Response: {}", city, errorBody);
                                return Mono.error(new ExternalApiException("City not found for: " + city));
                            } else if (status == 401) {
                                log.error("Unauthorized (401): {} | Response: {}", city, errorBody);
                                return Mono.error(new ExternalApiException("Unauthorized access to weather API"));
                            } else {
                                log.error("Client error {} for city: {} | Response: {}", status, city, errorBody);
                                return Mono.error(new ExternalApiException("Client error: " + status));
                            }
                        })
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Server error for city: {} | Response: {}", city, errorBody);
                            return Mono.error(new ExternalApiException("Weather service API unavailable"));
                        })
                )
                .bodyToMono(WeatherApiResponse.class)
                .block();
    }

    private WeatherSummary processWeatherData(WeatherApiResponse response) {
        List<WeatherData> last7Days = response.getList().stream()
                .limit(56) // 7 days * 8 forecasts per day (3-hour intervals)
                .toList();

        double averageTemp = last7Days.stream()
                .mapToDouble(data -> data.getMain().getTemp())
                .average()
                .orElse(0.0);

        WeatherData hottestData = last7Days.stream()
                .max(Comparator.comparing(data -> data.getMain().getTemp()))
                .orElse(null);

        WeatherData coldestData = last7Days.stream()
                .min(Comparator.comparing(data -> data.getMain().getTemp()))
                .orElse(null);

        return new WeatherSummary(
                response.getCity().getName(),
                Math.round(averageTemp * 100.0) / 100.0,
                hottestData != null ? hottestData.getDt_txt().split(" ")[0] : null,
                coldestData != null ? coldestData.getDt_txt().split(" ")[0] : null
        );
    }
}
