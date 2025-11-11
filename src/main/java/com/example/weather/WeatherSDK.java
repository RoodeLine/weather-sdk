package com.example.weather;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.example.weather.cache.WeatherCache;
import com.example.weather.client.WeatherClient;
import com.example.weather.config.SdkConfig;
import com.example.weather.dto.WeatherRequest;
import com.example.weather.dto.WeatherResponse;
import com.example.weather.scheduler.PollScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherSDK {

    public enum Mode {
        ON_DEMAND, POLLING
    }

    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    public static WeatherSDK getInstance(String instanceKey, String apiKey, Mode mode) {
        Objects.requireNonNull(instanceKey, "instanceKey");
        Objects.requireNonNull(apiKey, "apiKey");
        Objects.requireNonNull(mode, "mode");
        if (instances.containsKey(instanceKey)) {
            throw new IllegalStateException("Instance for instanceKey already exists");
        }
        WeatherSDK sdk = new WeatherSDK(apiKey, mode);
        instances.put(instanceKey, sdk);
        return sdk;
    }

    public static void deleteInstance(String instanceKey) {
        WeatherSDK inst = instances.remove(instanceKey);
        if (inst != null) {
            inst.stopPolling();
        }
    }

    private final String apiKey;
    private final Mode mode;
    private final WeatherCache cache;
    private final WeatherClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private PollScheduler scheduler;
    private ScheduledFuture<?> pollingTask;

    private WeatherSDK(String apiKey, Mode mode) {
        this.apiKey = apiKey;
        this.mode = mode;
        this.cache = new WeatherCache();
        this.client = new WeatherClient(
                SdkConfig.builder()
                        .apiKey(apiKey)
                        .build());
        if (this.mode == Mode.POLLING) {
            startPolling();
        }
    }

    public String getWeather(String cityName) {
        // 1. Check cache first
        WeatherResponse cached = cache.get(cityName);
        WeatherResponse result;
        if (cached != null) {
            result = cached;
        } else {
            // 2. Fetch from WeatherClient and cache
            result = client.getWeather(new WeatherRequest(cityName));
            cache.put(cityName, result);
        }
        // 3. Return as JSON string
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            // Simple fallback string on serialization error
            return "{\"name\":\"" + cityName + "\",\"error\":\"serialization_failed\"}";
        }
    }

    private void startPolling() {
        this.scheduler = new PollScheduler();
        Duration period = Duration.ofMinutes(5);
        this.pollingTask = scheduler.scheduleAtFixedRate(() -> {
            for (String city : cache.keys()) {
                try {
                    WeatherResponse wr = client.getWeather(new WeatherRequest(city));
                    cache.put(city, wr);
                } catch (RuntimeException ex) {
                    // ignore errors during polling to keep task running
                }
            }
        }, period, period);
    }

    private void stopPolling() {
        if (pollingTask != null) {
            pollingTask.cancel(true);
            pollingTask = null;
        }
        if (scheduler != null) {
            scheduler.close();
            scheduler = null;
        }
    }
}
