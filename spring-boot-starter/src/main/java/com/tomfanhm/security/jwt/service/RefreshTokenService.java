package com.tomfanhm.security.jwt.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomfanhm.security.jwt.exception.NotFoundException;
import com.tomfanhm.security.jwt.exception.TokenExpiredException;
import com.tomfanhm.security.jwt.model.RefreshToken;
import com.tomfanhm.security.jwt.repository.RefreshTokenRepository;
import com.tomfanhm.security.jwt.repository.UserRepository;

@Service
public class RefreshTokenService {
	@Value("${jwt.refreshExpiration}")
	private Long jwtRefreshExpiration;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public RefreshToken createRefreshToken(Long userId) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);

		RefreshToken refreshToken;
		if (existingToken.isPresent()) {
			refreshToken = existingToken.get();
			refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpiration));
			refreshToken.setToken(UUID.randomUUID().toString());
		} else {
			refreshToken = new RefreshToken();
			refreshToken.setUser(
					userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found.")));
			refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpiration));
			refreshToken.setToken(UUID.randomUUID().toString());
		}

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	@Transactional
	public void deleteByToken(String token) {
		refreshTokenRepository.findByToken(token)
				.ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken verify(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenExpiredException("Refresh token has expired.");
		}

		return token;
	}
}