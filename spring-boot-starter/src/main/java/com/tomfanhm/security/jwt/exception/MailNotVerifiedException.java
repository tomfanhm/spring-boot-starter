package com.tomfanhm.security.jwt.exception;

public class MailNotVerifiedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MailNotVerifiedException(String message) {
		super(message);
	}
}