package com.tomfanhm.security.jwt.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
	@NotBlank(message = "Refresh token is required")
	@JsonProperty("refresh_token")
	private String refreshToken;

	public RefreshTokenRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
