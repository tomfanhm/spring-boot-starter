package com.starter.app.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.starter.app.config.JwtConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    JwtConfig config =
        new JwtConfig(
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2LWFsZ29yaXRobQ==",
            86400000L,
            "test-issuer");
    jwtTokenProvider = new JwtTokenProvider(config);
  }

  @Test
  void generateToken_shouldReturnParseableTokenWithCorrectSubject() {
    UUID userId = UUID.randomUUID();
    UserPrincipal principal =
        new UserPrincipal(
            userId,
            "test@example.com",
            "password",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

    String token = jwtTokenProvider.generateToken(principal);

    assertThat(token).isNotBlank();
    assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId.toString());
  }

  @Test
  void validateToken_shouldReturnFalseForExpiredToken() {
    JwtConfig shortLivedConfig =
        new JwtConfig(
            "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2LWFsZ29yaXRobQ==",
            -1000L,
            "test-issuer");
    JwtTokenProvider shortLivedProvider = new JwtTokenProvider(shortLivedConfig);

    UUID userId = UUID.randomUUID();
    UserPrincipal principal =
        new UserPrincipal(
            userId,
            "test@example.com",
            "password",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

    String token = shortLivedProvider.generateToken(principal);

    assertThat(jwtTokenProvider.validateToken(token)).isFalse();
  }

  @Test
  void validateToken_shouldReturnFalseForTamperedToken() {
    UUID userId = UUID.randomUUID();
    UserPrincipal principal =
        new UserPrincipal(
            userId,
            "test@example.com",
            "password",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

    String token = jwtTokenProvider.generateToken(principal);
    String tampered = token.substring(0, token.length() - 5) + "XXXXX";

    assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
  }

  @Test
  void validateToken_shouldReturnFalseForTokenSignedWithDifferentSecret() {
    JwtConfig differentConfig =
        new JwtConfig(
            "YW5vdGhlci1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2LWFsZ29yaXRobQ==",
            86400000L,
            "test-issuer");
    JwtTokenProvider differentProvider = new JwtTokenProvider(differentConfig);

    UUID userId = UUID.randomUUID();
    UserPrincipal principal =
        new UserPrincipal(
            userId,
            "test@example.com",
            "password",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

    String token = differentProvider.generateToken(principal);

    assertThat(jwtTokenProvider.validateToken(token)).isFalse();
  }
}
