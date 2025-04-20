package com.tomfanhm.security.jwt.advice;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tomfanhm.security.jwt.dto.response.MessageResponse;
import com.tomfanhm.security.jwt.exception.AccountLockedException;
import com.tomfanhm.security.jwt.exception.DuplicateResourceException;
import com.tomfanhm.security.jwt.exception.InvalidCredentialsException;
import com.tomfanhm.security.jwt.exception.InvalidTokenException;
import com.tomfanhm.security.jwt.exception.MailNotVerifiedException;
import com.tomfanhm.security.jwt.exception.NotFoundException;
import com.tomfanhm.security.jwt.exception.TokenExpiredException;

@ControllerAdvice
public class GlobalExceptionAdvice {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<MessageResponse> handleAll(Exception ex) {
		logger.error("Unhandled exception:", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new MessageResponse("An unexpected error occurred. Please try again later."));
	}

	@ExceptionHandler({ InvalidCredentialsException.class, MailNotVerifiedException.class })
	public ResponseEntity<MessageResponse> handleBadRequest(RuntimeException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<MessageResponse> handleConflict(DuplicateResourceException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ex.getMessage()));
	}

	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<MessageResponse> handleLocked(AccountLockedException ex) {
		return ResponseEntity.status(HttpStatus.LOCKED).body(new MessageResponse(ex.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<MessageResponse> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
	}

	@ExceptionHandler({ InvalidTokenException.class, TokenExpiredException.class })
	public ResponseEntity<MessageResponse> handleUnauthorized(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<MessageResponse> handleUnreadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse("Request body is missing or unreadable."));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getAllErrors().stream().map(err -> err.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return ResponseEntity.badRequest().body(new MessageResponse(errors));
	}
}