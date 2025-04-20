package com.tomfanhm.security.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
	@GetMapping("/reset-password")
	public String resetPasswordPage() {
		return "reset-password";
	}
}
