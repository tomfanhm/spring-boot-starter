package com.tomfanhm.security.jwt.model;

import com.tomfanhm.security.jwt.enums.ERole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "roles", uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false, length = 255)
	private ERole name;

	public Role() {
	}

	public Role(Long id, ERole name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public ERole getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(ERole name) {
		this.name = name;
	}

}
