package com.dushmantha.weather_analyzer.dto;

import lombok.Data;

import java.util.List;

/**
 * @author dushmantha.sse@gmail.com
 */
@Data
public class WeatherApiResponse {
    private String cod;
    private int message;
    private int cnt;
    private List<WeatherData> list;
    private City city;
}
