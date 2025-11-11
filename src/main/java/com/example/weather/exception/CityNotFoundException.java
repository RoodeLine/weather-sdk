package com.example.weather.exception;

public class CityNotFoundException extends WeatherSdkException {
    public CityNotFoundException(String message) { super(message); }
}
