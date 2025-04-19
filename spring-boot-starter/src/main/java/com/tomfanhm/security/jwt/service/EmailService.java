package com.tomfanhm.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Value("${app.verification.base-url}")
	private String baseUrl;

	@Autowired
	private JavaMailSender mailSender;

	public void sendPasswordResetEmail(String to, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Password Reset");
		message.setText("Please click the link below to reset your password:\n" + baseUrl
				+ "/api/auth/reset-password?token=" + token + "\n\n"
				+ "This link is valid for 24 hours. If you did not request a password reset, please ignore this email.");

		mailSender.send(message);
	}

	public void sendVerificationEmail(String to, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Email Verification");
		message.setText("Please click the link below to verify your email:\n" + baseUrl
				+ "/api/auth/verify-email?token=" + token);

		mailSender.send(message);
	}
}
