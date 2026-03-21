package com.starter.app.modules.user;

import com.starter.app.modules.user.dto.UpdateUserRequest;
import com.starter.app.modules.user.dto.UserResponse;
import com.starter.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser(
      @AuthenticationPrincipal UserPrincipal principal) {
    return ResponseEntity.ok(userService.getUserById(principal.getId()));
  }

  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateCurrentUser(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(userService.updateUser(principal.getId(), request));
  }
}
