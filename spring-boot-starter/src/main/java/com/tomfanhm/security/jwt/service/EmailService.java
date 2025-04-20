package com.tomfanhm.security.jwt.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Value("${app.verification.base-url}")
	private String baseUrl;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	private void sendEmail(String to, String subject, String templateName, Map<String, Object> variables)
			throws MessagingException {
		Context context = new Context();
		variables.forEach(context::setVariable);

		String htmlContent = templateEngine.process(templateName, context);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		mailSender.send(message);
	}

	public void sendPasswordResetEmail(String to, String token) throws MessagingException {
		String resetUrl = baseUrl + "/reset-password?token=" + token;
		sendEmail(to, "Password Reset", "forgot-password", Map.of("resetUrl", resetUrl));
	}

	public void sendVerificationEmail(String to, String token) throws MessagingException {
		String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
		sendEmail(to, "Email Confirmation", "email-confirmation", Map.of("verificationUrl", verificationUrl));
	}

}
