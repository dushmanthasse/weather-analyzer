package com.dushmantha.weather_analyzer.dto;

import lombok.Data;

/**
 * @author dushmantha.sse@gmail.com
 */
@Data
public class WeatherData {
    private long dt;
    private Main main;
    private String dt_txt;
}
