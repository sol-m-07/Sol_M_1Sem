package com.example.todolist.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.external")
public record ExternalApiProperties(
        String baseUrl,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
