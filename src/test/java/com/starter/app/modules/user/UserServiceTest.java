package com.starter.app.modules.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.starter.app.modules.user.dto.UserResponse;
import com.starter.app.modules.user.mapper.UserMapper;
import com.starter.app.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;

  @InjectMocks private UserService userService;

  @Test
  void getUserById_withExistingUser_shouldReturnUserResponse() {
    UUID userId = UUID.randomUUID();
    User user =
        User.builder()
            .id(userId)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .role(User.Role.USER)
            .build();
    UserResponse expected =
        new UserResponse(userId, "test@example.com", "John", "Doe", "USER", Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toResponse(user)).thenReturn(expected);

    UserResponse result = userService.getUserById(userId);

    assertThat(result.email()).isEqualTo("test@example.com");
    assertThat(result.firstName()).isEqualTo("John");
  }

  @Test
  void getUserById_withNonExistentUser_shouldThrowResourceNotFoundException() {
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getUserById(userId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("User not found");
  }
}
