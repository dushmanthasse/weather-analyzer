package com.dushmantha.weather_analyzer.controller;

import com.dushmantha.weather_analyzer.dto.WeatherSummary;
import com.dushmantha.weather_analyzer.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dushmantha.sse@gmail.com
 */
@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void testGetWeatherSummary() throws Exception {
        WeatherSummary mockSummary = new WeatherSummary("London", 16.8, "2025-07-27", "2024-07-25");
        when(weatherService.weatherSummary("London"))
                .thenReturn(CompletableFuture.completedFuture(mockSummary));

        mockMvc.perform(get("/weather?city=London"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("London"))
                .andExpect(jsonPath("$.averageTemperature").value(16.8));
    }
}