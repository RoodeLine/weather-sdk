package com.example.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Clouds {
    @JsonProperty("all")
    private Integer all;

    public Clouds() {
    }

    public Clouds(Integer all) {
        this.all = all;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }
}
