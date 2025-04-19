package com.tomfanhm.security.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomfanhm.security.jwt.model.RefreshToken;
import com.tomfanhm.security.jwt.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUser(User user);

	Optional<RefreshToken> findByUserId(Long userId);

}