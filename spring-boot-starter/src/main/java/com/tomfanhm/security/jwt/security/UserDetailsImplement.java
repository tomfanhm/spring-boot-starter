package com.tomfanhm.security.jwt.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomfanhm.security.jwt.model.User;

public class UserDetailsImplement implements UserDetails {
	private static final long serialVersionUID = 1L;

	public static UserDetailsImplement build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new UserDetailsImplement(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
				!user.isAccountLocked(), user.isEmailVerified(), authorities);
	}

	private final boolean accountNonLocked;

	private final Collection<? extends GrantedAuthority> authorities;

	private final String email;

	private final boolean enabled;

	private final Long id;

	@JsonIgnore
	private final String password;

	private final String username;

	public UserDetailsImplement(Long id, String username, String email, String password, boolean accountNonLocked,
			boolean enabled, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.accountNonLocked = accountNonLocked;
		this.enabled = enabled;
		this.authorities = authorities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserDetailsImplement user = (UserDetailsImplement) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}