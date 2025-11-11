package com.example.weather.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LruTtlCache<K, V> {
    private final int maxEntries;
    private final Duration ttl;
    private final Clock clock;
    private final Map<K, Entry<V>> map;

    public LruTtlCache(int maxEntries, Duration ttl) {
        this(maxEntries, ttl, Clock.systemUTC());
    }

    public LruTtlCache(int maxEntries, Duration ttl, Clock clock) {
        this.maxEntries = Math.max(1, maxEntries);
        this.ttl = Objects.requireNonNull(ttl, "ttl");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Entry<V>> eldest) {
                return size() > LruTtlCache.this.maxEntries;
            }
        };
    }

    public synchronized void put(K key, V value) {
        Instant expiresAt = clock.instant().plus(ttl);
        map.put(key, new Entry<>(value, expiresAt));
    }

    public synchronized Optional<V> get(K key) {
        Entry<V> e = map.get(key);
        if (e == null)
            return Optional.empty();
        if (clock.instant().isAfter(e.expiresAt)) {
            map.remove(key);
            return Optional.empty();
        }
        return Optional.of(e.value);
    }

    public synchronized int size() {
        return map.size();
    }

    private static class Entry<V> {
        final V value;
        final Instant expiresAt;

        Entry(V value, Instant expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }
}
