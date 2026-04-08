package com.starter.app.modules.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.app.config.AppProperties;
import com.starter.app.config.SecurityConfig;
import com.starter.app.modules.auth.dto.AuthResponse;
import com.starter.app.modules.auth.dto.LoginRequest;
import com.starter.app.modules.auth.dto.RegisterRequest;
import com.starter.app.modules.user.UserRepository;
import com.starter.app.security.JwtTokenProvider;
import com.starter.app.shared.exception.AuthenticationException;
import com.starter.app.shared.exception.GlobalExceptionHandler;
import com.starter.app.shared.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, AuthControllerTest.TestConfig.class})
class AuthControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    AppProperties appProperties() {
      return new AppProperties(null, new AppProperties.CorsConfig("http://localhost:3000"));
    }
  }

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;
  @MockitoBean private JwtTokenProvider jwtTokenProvider;
  @MockitoBean private UserRepository userRepository;

  @Test
  void login_withValidCredentials_shouldReturn200WithToken() throws Exception {
    when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("test-jwt-token"));

    mockMvc
        .perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("test@example.com", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("test-jwt-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void login_withInvalidBody_shouldReturn400WithValidationErrors() throws Exception {
    mockMvc
        .perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("", ""))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors").isArray());
  }

  @Test
  void login_withUnknownEmail_shouldReturn401() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new AuthenticationException("Invalid email or password"));

    mockMvc
        .perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("unknown@example.com", "password123"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  void register_withValidRequest_shouldReturn201WithToken() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenReturn(new AuthResponse("new-jwt-token"));

    mockMvc
        .perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new RegisterRequest("new@example.com", "password123", "John", "Doe"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").value("new-jwt-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void register_withDuplicateEmail_shouldReturn409() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(
            new ResourceAlreadyExistsException(
                "User", "email", "An account with this email already exists"));

    mockMvc
        .perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new RegisterRequest("taken@example.com", "password123", "John", "Doe"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }
}
