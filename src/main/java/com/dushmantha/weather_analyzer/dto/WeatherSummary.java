package com.dushmantha.weather_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dushmantha.sse@gmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSummary {
    private String city;
    private double averageTemperature;
    private String hottestDay;
    private String coldestDay;
}
