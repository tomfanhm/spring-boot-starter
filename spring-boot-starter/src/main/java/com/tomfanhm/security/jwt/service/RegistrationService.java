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
	public void register(RegisterRequest req) {

		if (userRepository.existsByUsername(req.getUsername())) {
			throw new DuplicateResourceException("Username is already taken.");
		}
		if (userRepository.existsByEmail(req.getEmail())) {
			throw new DuplicateResourceException("Email is already in use.");
		}

		User user = new User();
		user.setUsername(req.getUsername());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));

		Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RoleNotFoundException(ERole.ROLE_USER.name()));
		user.setRoles(Set.of(userRole));

		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		user.setEmailVerified(false);

		userRepository.save(user);
		emailService.sendVerificationEmail(user.getEmail(), token);
	}
}
