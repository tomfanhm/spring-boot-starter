package com.tomfanhm.security.jwt.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomfanhm.security.jwt.dto.request.LoginRequest;
import com.tomfanhm.security.jwt.dto.response.JwtResponse;
import com.tomfanhm.security.jwt.exception.AccountLockedException;
import com.tomfanhm.security.jwt.exception.InvalidCredentialsException;
import com.tomfanhm.security.jwt.exception.InvalidTokenException;
import com.tomfanhm.security.jwt.exception.MailNotVerifiedException;
import com.tomfanhm.security.jwt.exception.NotFoundException;
import com.tomfanhm.security.jwt.exception.TokenExpiredException;
import com.tomfanhm.security.jwt.model.RefreshToken;
import com.tomfanhm.security.jwt.model.User;
import com.tomfanhm.security.jwt.repository.UserRepository;
import com.tomfanhm.security.jwt.security.JwtTokenProvider;
import com.tomfanhm.security.jwt.security.UserDetailsImplement;

@Service
public class UserService {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private UserRepository userRepository;

	public JwtResponse authenticate(LoginRequest loginRequest) {

		User user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found."));

		if (user.isAccountLocked() && user.getLockTime() != null) {
			long elapsed = new Date().getTime() - user.getLockTime().getTime();
			if (elapsed > 1800000) {
				user.setAccountLocked(false);
				user.setLoginAttempts(0);
				user.setLockTime(null);
				userRepository.save(user);
			}
		}

		if (user.isAccountLocked())
			throw new AccountLockedException("Account is locked. Please try again later.");

		if (!user.isEmailVerified())
			throw new MailNotVerifiedException("Please verify your email before login.");

		try {
			var auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			user.setLoginAttempts(0);
			userRepository.save(user);

			SecurityContextHolder.getContext().setAuthentication(auth);

			String accessToken = jwtTokenProvider.generateToken(auth);
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

			UserDetailsImplement details = (UserDetailsImplement) auth.getPrincipal();
			var roles = details.getAuthorities().stream().map(granted -> granted.getAuthority())
					.collect(Collectors.toList());

			return new JwtResponse(accessToken, refreshToken.getToken(), details.getId(), details.getUsername(),
					details.getEmail(), roles);

		} catch (BadCredentialsException ex) {
			user.setLoginAttempts(user.getLoginAttempts() + 1);
			if (user.getLoginAttempts() >= 3) {
				user.setAccountLocked(true);
				user.setLockTime(new Date());
			}
			userRepository.save(user);
			throw new InvalidCredentialsException("Invalid username/password.");
		}
	}

	@Transactional
	public void forgotPassword(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			String token = UUID.randomUUID().toString();

			user.setResetPasswordToken(token);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, 24);
			user.setResetPasswordTokenExpiry(calendar.getTime());
			userRepository.save(user);

			emailService.sendPasswordResetEmail(user.getEmail(), token);
		});
	}

	@Transactional
	public void resetPassword(String token, String newPassword) {
		User user = userRepository.findByResetPasswordToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token."));

		if (user.getResetPasswordTokenExpiry().before(new Date()))
			throw new TokenExpiredException("Password reset token has expired.");

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetPasswordToken(null);
		user.setResetPasswordTokenExpiry(null);
		userRepository.save(user);
	}

	@Transactional
	public void verifyEmail(String token) {
		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new InvalidTokenException("Invalid verification token."));

		user.setEmailVerified(true);
		user.setVerificationToken(null);
		userRepository.save(user);
	}
}
