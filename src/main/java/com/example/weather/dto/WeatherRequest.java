package com.example.weather.dto;

import java.util.Objects;

public class WeatherRequest {
    private final String location; // e.g., city name or lat,lon

    public WeatherRequest(String location) {
        this.location = Objects.requireNonNull(location, "location");
    }

    public String getLocation() {
        return location;
    }
}
