package com.dushmantha.weather_analyzer.service;

import com.dushmantha.weather_analyzer.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author dushmantha.sse@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);

        weatherService = new WeatherServiceImpl(webClientBuilder);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testWeatherSummary_Success() throws ExecutionException, InterruptedException {
        // Given
        String city = "London";
        WeatherApiResponse mockResponse = createMockWeatherResponse(city);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class))
                .thenReturn(Mono.just(mockResponse));

        // When
        CompletableFuture<WeatherSummary> result = weatherService.weatherSummary(city);
        WeatherSummary summary = result.get();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getCity()).isEqualTo("London");
        assertThat(summary.getAverageTemperature()).isEqualTo(14.75);
        assertThat(summary.getHottestDay()).isEqualTo("2024-11-21");
        assertThat(summary.getColdestDay()).isEqualTo("2024-11-19");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(contains("London"));
    }

    @Test
    void testWeatherSummary_CityNotFound() {
        // Given
        String city = "InvalidCity";

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        // When & Then
        CompletableFuture<WeatherSummary> result = weatherService.weatherSummary(city);

        assertThatThrownBy(result::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(WebClientResponseException.class);
    }

    @Test
    void testWeatherSummary_ApiServerError() {
        // Given
        String city = "London";

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When & Then
        CompletableFuture<WeatherSummary> result = weatherService.weatherSummary(city);

        assertThatThrownBy(result::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(WebClientResponseException.class);
    }

    @Test
    void testWeatherSummary_EmptyResponse() throws ExecutionException, InterruptedException {
        // Given
        String city = "London";
        WeatherApiResponse emptyResponse = createEmptyWeatherResponse(city);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class))
                .thenReturn(Mono.just(emptyResponse));

        // When
        CompletableFuture<WeatherSummary> result = weatherService.weatherSummary(city);
        WeatherSummary summary = result.get();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getCity()).isEqualTo("London");
        assertThat(summary.getAverageTemperature()).isEqualTo(0.0);
        assertThat(summary.getHottestDay()).isNull();
        assertThat(summary.getColdestDay()).isNull();
    }

    @Test
    void testWeatherSummary_SingleDataPoint() throws ExecutionException, InterruptedException {
        // Given
        String city = "Paris";
        WeatherApiResponse singleDataResponse = createSingleDataWeatherResponse(city);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class))
                .thenReturn(Mono.just(singleDataResponse));

        // When
        CompletableFuture<WeatherSummary> result = weatherService.weatherSummary(city);
        WeatherSummary summary = result.get();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getCity()).isEqualTo("Paris");
        assertThat(summary.getAverageTemperature()).isEqualTo(20.0);
        assertThat(summary.getHottestDay()).isEqualTo("2024-11-20");
        assertThat(summary.getColdestDay()).isEqualTo("2024-11-20");
    }

    private WeatherApiResponse createMockWeatherResponse(String cityName) {
        WeatherApiResponse response = new WeatherApiResponse();

        City city = new City();
        city.setName(cityName);
        response.setCity(city);

        List<WeatherData> weatherDataList = Arrays.asList(
                createWeatherData(10.0, "2024-11-19 12:00:00"),
                createWeatherData(12.0, "2024-11-19 15:00:00"),
                createWeatherData(15.0, "2024-11-20 12:00:00"),
                createWeatherData(18.0, "2024-11-20 15:00:00"),
                createWeatherData(20.0, "2024-11-21 12:00:00"),
                createWeatherData(16.0, "2024-11-21 15:00:00"),
                createWeatherData(14.0, "2024-11-22 12:00:00"),
                createWeatherData(13.0, "2024-11-22 15:00:00")
        );

        response.setList(weatherDataList);
        response.setCod("200");
        response.setCnt(weatherDataList.size());

        return response;
    }

    private WeatherApiResponse createEmptyWeatherResponse(String cityName) {
        WeatherApiResponse response = new WeatherApiResponse();

        City city = new City();
        city.setName(cityName);
        response.setCity(city);

        response.setList(List.of());
        response.setCod("200");
        response.setCnt(0);

        return response;
    }

    private WeatherApiResponse createSingleDataWeatherResponse(String cityName) {
        WeatherApiResponse response = new WeatherApiResponse();

        City city = new City();
        city.setName(cityName);
        response.setCity(city);

        List<WeatherData> weatherDataList = List.of(
                createWeatherData(20.0, "2024-11-20 12:00:00")
        );

        response.setList(weatherDataList);
        response.setCod("200");
        response.setCnt(1);

        return response;
    }

    private WeatherData createWeatherData(double temperature, String dateTime) {
        WeatherData weatherData = new WeatherData();

        Main main = new Main();
        main.setTemp(temperature);
        main.setTemp_min(temperature - 2);
        main.setTemp_max(temperature + 2);

        weatherData.setMain(main);
        weatherData.setDt_txt(dateTime);

        weatherData.setDt(System.currentTimeMillis() / 1000);

        return weatherData;
    }
}