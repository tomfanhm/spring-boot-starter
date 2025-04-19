package com.tomfanhm.security.jwt.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tomfanhm.security.jwt.security.UserDetailsImplement;

public class AuthUtil {
	private static Optional<UserDetailsImplement> getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserDetailsImplement userDetails) {
			return Optional.of(userDetails);
		}
		return Optional.empty();
	}

	public static String getUserEmail() {
		return getCurrentUser().map(UserDetailsImplement::getEmail).orElse(null);
	}

	public static Long getUserId() {
		return getCurrentUser().map(UserDetailsImplement::getId).orElse(null);
	}
}
