package com.starter.app.modules.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.starter.app.security.JwtTokenProvider;
import com.starter.app.security.UserPrincipal;
import com.starter.app.testconfig.PostgresTestContainerConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest extends PostgresTestContainerConfig {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  private User testUser;
  private String validToken;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    testUser =
        userRepository.save(
            User.builder()
                .email("integration@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Integration")
                .lastName("Test")
                .role(User.Role.USER)
                .build());

    UserPrincipal principal =
        new UserPrincipal(
            testUser.getId(),
            testUser.getEmail(),
            testUser.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    validToken = jwtTokenProvider.generateToken(principal);
  }

  @Test
  void getMe_withoutToken_shouldReturn401() throws Exception {
    mockMvc.perform(get("/v1/users/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void getMe_withValidToken_shouldReturnUserProfile() throws Exception {
    mockMvc
        .perform(get("/v1/users/me").header("Authorization", "Bearer " + validToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("integration@example.com"))
        .andExpect(jsonPath("$.firstName").value("Integration"))
        .andExpect(jsonPath("$.lastName").value("Test"));
  }
}
