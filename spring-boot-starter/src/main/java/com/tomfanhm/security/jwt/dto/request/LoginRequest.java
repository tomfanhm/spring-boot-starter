package com.tomfanhm.security.jwt.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	@NotBlank(message = "Email is required.")
	@Email(message = "Email should be valid.")
	@JsonProperty("email")
	private final String email;

	@NotBlank(message = "Password is required.")
	@JsonProperty("password")
	private final String password;

	public LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
