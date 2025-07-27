package com.dushmantha.weather_analyzer.controller;

import com.dushmantha.weather_analyzer.dto.WeatherSummary;
import com.dushmantha.weather_analyzer.exception.ExternalApiException;
import com.dushmantha.weather_analyzer.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author dushmantha.sse@gmail.com
 */
@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherSummary> getWeatherSummary(@RequestParam String city) {
        log.info("Weather summery for city: {}", city);
        try {
            WeatherSummary summary = weatherService.weatherSummary(city).get();
            return ResponseEntity.ok(summary);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExternalApiException) {
                throw (ExternalApiException) cause;
            }
            throw new RuntimeException("Unexpected error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        }
    }
}
