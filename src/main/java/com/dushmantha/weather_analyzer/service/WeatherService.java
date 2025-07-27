package com.dushmantha.weather_analyzer.service;

import com.dushmantha.weather_analyzer.dto.WeatherSummary;

import java.util.concurrent.CompletableFuture;

/**
 * @author dushmantha.sse@gmail.com
 */
public interface WeatherService {
    CompletableFuture<WeatherSummary> weatherSummary(String city);
}
