package com.example.weather.config;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class SdkConfig {
    private final URI baseUri;
    private final Duration requestTimeout;
    private final Duration cacheTtl;
    private final int cacheMaxEntries;
    private final String apiKey;

    private SdkConfig(Builder builder) {
        this.baseUri = builder.baseUri != null ? builder.baseUri : URI.create("https://api.openweathermap.org");
        this.requestTimeout = builder.requestTimeout != null ? builder.requestTimeout : Duration.ofSeconds(10);
        this.cacheTtl = builder.cacheTtl != null ? builder.cacheTtl : Duration.ofMinutes(5);
        this.cacheMaxEntries = builder.cacheMaxEntries > 0 ? builder.cacheMaxEntries : 256;
        this.apiKey = Objects.requireNonNull(builder.apiKey, "apiKey");
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public Duration getCacheTtl() {
        return cacheTtl;
    }

    public int getCacheMaxEntries() {
        return cacheMaxEntries;
    }

    public String getApiKey() {
        return apiKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI baseUri;
        private Duration requestTimeout;
        private Duration cacheTtl;
        private int cacheMaxEntries;
        private String apiKey;

        public Builder baseUri(URI baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder requestTimeout(Duration timeout) {
            this.requestTimeout = timeout;
            return this;
        }

        public Builder cacheTtl(Duration ttl) {
            this.cacheTtl = ttl;
            return this;
        }

        public Builder cacheMaxEntries(int max) {
            this.cacheMaxEntries = max;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public SdkConfig build() {
            return new SdkConfig(this);
        }
    }
}
