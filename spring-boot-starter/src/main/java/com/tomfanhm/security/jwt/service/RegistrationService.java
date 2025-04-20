package com.tomfanhm.security.jwt.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomfanhm.security.jwt.dto.request.RegisterRequest;
import com.tomfanhm.security.jwt.enums.ERole;
import com.tomfanhm.security.jwt.exception.DuplicateResourceException;
import com.tomfanhm.security.jwt.exception.RoleNotFoundException;
import com.tomfanhm.security.jwt.model.Role;
import com.tomfanhm.security.jwt.model.User;
import com.tomfanhm.security.jwt.repository.RoleRepository;
import com.tomfanhm.security.jwt.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class RegistrationService {
	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public void register(RegisterRequest registerRequest) {

		if (userRepository.existsByUsername(registerRequest.getUsername())) {
			throw new DuplicateResourceException("Username is already taken.");
		}
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new DuplicateResourceException("Email is already in use.");
		}

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RoleNotFoundException(ERole.ROLE_USER.name()));
		user.setRoles(Set.of(userRole));

		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		user.setEmailVerified(false);

		userRepository.save(user);
		try {
			emailService.sendVerificationEmail(user.getEmail(), token);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send email.");
		}
	}
}
