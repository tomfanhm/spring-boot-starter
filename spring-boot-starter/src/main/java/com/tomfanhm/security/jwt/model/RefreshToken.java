package com.tomfanhm.security.jwt.model;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "expiry_date", nullable = false)
	private Instant expiryDate;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "token", nullable = false, unique = true)
	private String token;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public RefreshToken() {
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
