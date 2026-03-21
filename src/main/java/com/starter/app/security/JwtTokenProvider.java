package com.starter.app.security;

import com.starter.app.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

  private final JwtConfig jwtConfig;
  private final SecretKey signingKey;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
  }

  public String generateToken(UserPrincipal userPrincipal) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtConfig.expirationMs());

    return Jwts.builder()
        .subject(userPrincipal.getId().toString())
        .issuer(jwtConfig.issuer())
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(signingKey)
        .compact();
  }

  public String getUserIdFromToken(String token) {
    Claims claims =
        Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }
}
