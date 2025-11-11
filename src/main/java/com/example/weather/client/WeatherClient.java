package com.example.weather.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

import com.example.weather.cache.LruTtlCache;
import com.example.weather.config.SdkConfig;
import com.example.weather.dto.WeatherRequest;
import com.example.weather.dto.WeatherResponse;
import com.example.weather.exception.ApiKeyException;
import com.example.weather.exception.CityNotFoundException;
import com.example.weather.exception.RateLimitException;
import com.example.weather.exception.WeatherSdkException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherClient {
    private final HttpClient httpClient;
    private final SdkConfig config;
    private final LruTtlCache<String, WeatherResponse> cache;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherClient(SdkConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.getRequestTimeout())
                .build();
        this.cache = new LruTtlCache<>(config.getCacheMaxEntries(), config.getCacheTtl());
    }

    public WeatherResponse getWeather(WeatherRequest request) {
        String key = request.getLocation();
        Optional<WeatherResponse> cached = cache.get(key);
        if (cached.isPresent()) {
            return cached.get();
        }

        // Build OpenWeather endpoint: /data/2.5/weather?q={city}&appid={apiKey}
        String encodedCity = key.replace(" ", "%20"); // Simple URL encoding for spaces
        String path = "/data/2.5/weather?q=" + encodedCity + "&appid=" + config.getApiKey();
        URI uri = config.getBaseUri().resolve(path);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(config.getRequestTimeout())
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status == 401) {
                throw new ApiKeyException("Invalid API key");
            } else if (status == 404) {
                throw new CityNotFoundException("City not found: " + key);
            } else if (status == 429) {
                throw new RateLimitException("Rate limited by server");
            } else if (status < 200 || status >= 300) {
                throw new WeatherSdkException("Unexpected status: " + status);
            }
            WeatherResponse wr = objectMapper.readValue(response.body(), WeatherResponse.class);
            cache.put(key, wr);
            return wr;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherSdkException("Request interrupted", e);
        } catch (IOException e) {
            throw new WeatherSdkException("Failed to fetch or parse weather", e);
        }
    }
}
