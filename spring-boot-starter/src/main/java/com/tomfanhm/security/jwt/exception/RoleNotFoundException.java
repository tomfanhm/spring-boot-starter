package com.tomfanhm.security.jwt.exception;

public class RoleNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RoleNotFoundException(String roleName) {
		super("Role not found: " + roleName);
	}

}
