package com.dushmantha.weather_analyzer.exception;

/**
 * @author dushmantha.sse@gmail.com
 */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
