package com.tomfanhm.security.jwt.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {
	private final static String TYPE = "Bearer";

	@JsonProperty("email")
	private final String email;

	@JsonProperty("id")
	private final Long id;

	@JsonProperty("refresh_token")
	private final String refreshToken;

	@JsonProperty("roles")
	private final List<String> roles;

	@JsonProperty("token")
	private final String token;

	@JsonProperty("username")
	private final String username;

	public JwtResponse(String token, String refreshToken, Long id, String username, String email, List<String> roles) {
		this.token = token;
		this.refreshToken = refreshToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getToken() {
		return token;
	}

	public String getType() {
		return TYPE;
	}

	public String getUsername() {
		return username;
	}

}
