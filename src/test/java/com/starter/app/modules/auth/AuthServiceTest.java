package com.starter.app.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.starter.app.modules.auth.dto.AuthResponse;
import com.starter.app.modules.auth.dto.LoginRequest;
import com.starter.app.modules.auth.dto.RegisterRequest;
import com.starter.app.modules.auth.mapper.AuthMapper;
import com.starter.app.modules.user.User;
import com.starter.app.modules.user.UserRepository;
import com.starter.app.security.JwtTokenProvider;
import com.starter.app.shared.exception.AuthenticationException;
import com.starter.app.shared.exception.ResourceAlreadyExistsException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtTokenProvider jwtTokenProvider;
  @Mock private AuthMapper authMapper;

  @InjectMocks private AuthService authService;

  @Test
  void login_withValidCredentials_shouldReturnToken() {
    User user =
        User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .passwordHash("encoded-password")
            .role(User.Role.USER)
            .build();

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
    when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

    AuthResponse response = authService.login(new LoginRequest("test@example.com", "password123"));

    assertThat(response.accessToken()).isEqualTo("jwt-token");
    assertThat(response.tokenType()).isEqualTo("Bearer");
  }

  @Test
  void login_withWrongPassword_shouldThrowAuthenticationException() {
    User user =
        User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .passwordHash("encoded-password")
            .role(User.Role.USER)
            .build();

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

    assertThatThrownBy(
            () -> authService.login(new LoginRequest("test@example.com", "wrong-password")))
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("Invalid email or password");
  }

  @Test
  void register_withExistingEmail_shouldThrowResourceAlreadyExistsException() {
    when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

    RegisterRequest request =
        new RegisterRequest("taken@example.com", "password123", "John", "Doe");

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("already exists");
  }
}
