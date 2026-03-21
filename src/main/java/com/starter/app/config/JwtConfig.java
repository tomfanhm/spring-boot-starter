package com.starter.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtConfig(String secret, long expirationMs, String issuer) {}
