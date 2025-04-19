package com.tomfanhm.security.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomfanhm.security.jwt.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsByEmail(String email);

	Boolean existsByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByResetPasswordToken(String token);

	Optional<User> findByUsername(String username);

	Optional<User> findByVerificationToken(String token);

}