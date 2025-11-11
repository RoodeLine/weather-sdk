package com.example.weather.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.example.weather.dto.WeatherResponse;

public class WeatherCache {
    private static final int MAX_ENTRIES = 10;
    private static final Duration TTL = Duration.ofMinutes(10);

    private final Map<String, Entry> map = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public synchronized void put(String city, WeatherResponse data) {
        Instant expiresAt = Instant.now().plus(TTL);
        map.put(city, new Entry(data, expiresAt));
    }

    public synchronized WeatherResponse get(String city) {
        Entry e = map.get(city);
        if (e == null)
            return null;
        if (Instant.now().isAfter(e.expiresAt)) {
            map.remove(city);
            return null;
        }
        return e.value;
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized Set<String> keys() {
        return new LinkedHashSet<>(map.keySet());
    }

    private static class Entry {
        final WeatherResponse value;
        final Instant expiresAt;

        Entry(WeatherResponse value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }
}
