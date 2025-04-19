package com.tomfanhm.security.jwt.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
	@NotBlank(message = "Current Password is required.")
	@JsonProperty("current_password")
	private final String currentPassword;

	@NotBlank(message = "New password is required.")
	@Size(min = 8, message = "New password should have at least 8 characters.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_])(?=\\S+$).{8,}$", message = "New password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, one special character, and have no whitespace.")
	@JsonProperty("new_password")
	private final String newPassword;

	public ChangePasswordRequest(String currentPassword, String newPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

}
