package com.example.weather.exception;

public class RateLimitException extends WeatherSdkException {
    public RateLimitException(String message) { super(message); }
}
