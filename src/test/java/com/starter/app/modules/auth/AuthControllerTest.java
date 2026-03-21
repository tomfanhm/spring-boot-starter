package com.starter.app.modules.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.app.modules.auth.dto.AuthResponse;
import com.starter.app.modules.auth.dto.LoginRequest;
import com.starter.app.modules.auth.dto.RegisterRequest;
import com.starter.app.shared.exception.GlobalExceptionHandler;
import com.starter.app.shared.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;

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
  void register_withDuplicateEmail_shouldReturn409() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new ResourceAlreadyExistsException("User", "email", "taken@example.com"));

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
