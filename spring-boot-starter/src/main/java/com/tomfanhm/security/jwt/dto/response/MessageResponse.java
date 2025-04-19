package com.tomfanhm.security.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponse {
	@JsonProperty("message")
	private final String message;

	public MessageResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
