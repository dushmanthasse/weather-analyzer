package com.dushmantha.weather_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class WeatherAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherAnalyzerApplication.class, args);
	}

}
