package com.tomfanhm.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountLockedException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public AccountLockedException(String message) {
		super(message);
	}
}
