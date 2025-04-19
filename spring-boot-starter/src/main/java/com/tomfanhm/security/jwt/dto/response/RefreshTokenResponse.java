package com.tomfanhm.security.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenResponse {
	@JsonProperty("refresh_token")
	private final String refreshToken;

	@JsonProperty("token")
	private final String token;

	@JsonProperty("type")
	private final String type = "Bearer";

	public RefreshTokenResponse(String refreshToken, String token) {
		this.refreshToken = refreshToken;
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getToken() {
		return token;
	}

	public String getType() {
		return type;
	}

}
