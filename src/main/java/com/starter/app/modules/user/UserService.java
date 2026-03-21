package com.starter.app.modules.user;

import com.starter.app.modules.user.dto.UpdateUserRequest;
import com.starter.app.modules.user.dto.UserResponse;
import com.starter.app.modules.user.mapper.UserMapper;
import com.starter.app.shared.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    return userMapper.toResponse(user);
  }

  @Transactional
  public UserResponse updateUser(UUID id, UpdateUserRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    if (request.firstName() != null) {
      user.setFirstName(request.firstName());
    }
    if (request.lastName() != null) {
      user.setLastName(request.lastName());
    }

    User saved = userRepository.save(user);
    return userMapper.toResponse(saved);
  }
}
