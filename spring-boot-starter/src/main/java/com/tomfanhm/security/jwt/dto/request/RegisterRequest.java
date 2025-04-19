package com.tomfanhm.security.jwt.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message = "Email is required.")
	@Email(message = "Email should be valid.")
	@Size(max = 255, message = "Email cannot exceed 255 characters.")
	@JsonProperty("email")
	private final String email;

	@NotBlank(message = "Password is required.")
	@Size(min = 8, message = "Password should have at least 8 characters.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, one special character, and have no whitespace.")
	@JsonProperty("password")
	private final String password;

	@NotBlank(message = "Username is required.")
	@Size(max = 255, message = "Username cannot exceed 255 characters.")
	@JsonProperty("username")
	private final String username;

	public RegisterRequest(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

}
