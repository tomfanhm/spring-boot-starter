package com.tomfanhm.security.jwt.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tomfanhm.security.jwt.dto.request.LoginRequest;
import com.tomfanhm.security.jwt.dto.request.RefreshTokenRequest;
import com.tomfanhm.security.jwt.dto.request.RegisterRequest;
import com.tomfanhm.security.jwt.dto.request.ResetPasswordRequest;
import com.tomfanhm.security.jwt.dto.response.JwtResponse;
import com.tomfanhm.security.jwt.dto.response.MessageResponse;
import com.tomfanhm.security.jwt.dto.response.RefreshTokenResponse;
import com.tomfanhm.security.jwt.exception.InvalidTokenException;
import com.tomfanhm.security.jwt.model.RefreshToken;
import com.tomfanhm.security.jwt.security.JwtTokenProvider;
import com.tomfanhm.security.jwt.service.RefreshTokenService;
import com.tomfanhm.security.jwt.service.RegistrationService;
import com.tomfanhm.security.jwt.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private UserService userService;

	@PostMapping("/forgot-password")
	public ResponseEntity<MessageResponse> forgotPassword(@RequestBody Map<String, String> payload) {
		userService.forgotPassword(payload.get("email"));
		return ResponseEntity
				.ok(new MessageResponse("If your email exists in our system, you will receive a password reset link."));
	}

	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest body) {
		JwtResponse jwt = userService.authenticate(body);
		return ResponseEntity.ok(jwt);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<RefreshTokenResponse> refreshToken(
			@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
		String refreshToken = refreshTokenRequest.getRefreshToken();

		return refreshTokenService.findByToken(refreshToken).map(refreshTokenService::verify).map(RefreshToken::getUser)
				.map(user -> {
					String jwtToken = jwtTokenProvider.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new RefreshTokenResponse(refreshToken, jwtToken));
				}).orElseThrow(() -> new InvalidTokenException("Refresh token is invalid."));
	}

	@PostMapping("/register")
	public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest body) {
		registrationService.register(body);
		return ResponseEntity.ok(new MessageResponse("Please check your email to verify your account."));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<MessageResponse> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
		return ResponseEntity.ok(
				new MessageResponse("Password has been reset successfully. You can now login with your new password."));
	}

	@GetMapping("/verify-email")
	public ResponseEntity<MessageResponse> verify(@RequestParam String token) {
		userService.verifyEmail(token);
		return ResponseEntity.ok(new MessageResponse("Email verified successfully."));
	}

}
