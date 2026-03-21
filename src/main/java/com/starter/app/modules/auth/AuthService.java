package com.starter.app.modules.auth;

import com.starter.app.modules.auth.dto.AuthResponse;
import com.starter.app.modules.auth.dto.LoginRequest;
import com.starter.app.modules.auth.dto.RegisterRequest;
import com.starter.app.modules.auth.mapper.AuthMapper;
import com.starter.app.modules.user.User;
import com.starter.app.modules.user.UserRepository;
import com.starter.app.security.JwtTokenProvider;
import com.starter.app.security.UserPrincipal;
import com.starter.app.shared.exception.AuthenticationException;
import com.starter.app.shared.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthMapper authMapper;

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new AuthenticationException("Invalid email or password");
    }

    UserPrincipal principal = UserPrincipal.fromUser(user);
    String token = jwtTokenProvider.generateToken(principal);
    return new AuthResponse(token);
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ResourceAlreadyExistsException("User", "email", request.email());
    }

    User user = authMapper.toUser(request);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(User.Role.USER);

    User saved = userRepository.save(user);
    UserPrincipal principal = UserPrincipal.fromUser(saved);
    String token = jwtTokenProvider.generateToken(principal);
    return new AuthResponse(token);
  }
}
