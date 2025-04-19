package com.tomfanhm.security.jwt.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.expiration}")
	private int jwtExpiration;

	@Value("${jwt.secret}")
	private String jwtSecret;

	public String generateToken(Authentication authentication) {
		UserDetailsImplement userPrincipal = (UserDetailsImplement) authentication.getPrincipal();

		return generateTokenFromUsername(userPrincipal.getUsername());
	}

	public String generateTokenFromUsername(String username) {
		return Jwts.builder().subject(username).issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtExpiration)).signWith(key()).compact();
	}

	public String getUsername(String token) {
		return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	private SecretKey key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
			return true;
		} catch (SignatureException exception) {
			logger.error("Invalid JWT signature: {}", exception.getMessage());
		} catch (MalformedJwtException exception) {
			logger.error("Invalid JWT token: {}", exception.getMessage());
		} catch (ExpiredJwtException exception) {
			logger.error("JWT token is expired: {}", exception.getMessage());
		} catch (UnsupportedJwtException exception) {
			logger.error("JWT token is unsupported: {}", exception.getMessage());
		} catch (IllegalArgumentException exception) {
			logger.error("JWT claims string is empty: {}", exception.getMessage());
		}
		return false;
	}

}