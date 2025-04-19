package com.tomfanhm.security.jwt.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
public class User {
	@Column(name = "account_locked")
	private boolean accountLocked = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@NotBlank
	@Email
	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "email_verified")
	private boolean emailVerified = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lock_time")
	private Date lockTime;

	@Column(name = "login_attempts")
	private int loginAttempts = 0;

	@JsonIgnore
	@NotBlank
	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@JsonIgnore
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@Column(name = "reset_password_token_expiry")
	private Date resetPasswordTokenExpiry;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@NotBlank
	@Column(name = "username", nullable = false, length = 255)
	private String username;

	@JsonIgnore
	@Column(name = "verification_token")
	private String verificationToken;

	public User() {
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public int getLoginAttempts() {
		return loginAttempts;
	}

	public String getPassword() {
		return password;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public Date getResetPasswordTokenExpiry() {
		return resetPasswordTokenExpiry;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public String getUsername() {
		return username;
	}

	public String getVerificationToken() {
		return verificationToken;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public void setResetPasswordTokenExpiry(Date resetPasswordTokenExpiry) {
		this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}

}
