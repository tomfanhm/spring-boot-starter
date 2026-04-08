package com.starter.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(JwtConfig jwt, CorsConfig cors) {

  public record CorsConfig(String allowedOrigins) {}
}
