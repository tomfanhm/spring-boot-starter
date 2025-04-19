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
import org.springframework.web.context.request.WebRequest;

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

	@ExceptionHandler({ AccountLockedException.class, InvalidCredentialsException.class, MailNotVerifiedException.class,
			InvalidTokenException.class, TokenExpiredException.class, NotFoundException.class })
	public ResponseEntity<MessageResponse> handle(RuntimeException exception) {
		return ResponseEntity.badRequest().body(new MessageResponse(exception.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<MessageResponse> handleAllExceptions(Exception exception, WebRequest request) {
		logger.error("Unhandled exception occurred: {}", exception);
		MessageResponse messageResponse = new MessageResponse("An unexpected error occurred. Please try again later.");
		return new ResponseEntity<>(messageResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<MessageResponse> handleDuplicate(DuplicateResourceException exception) {
		MessageResponse messageResponse = new MessageResponse(exception.getMessage());
		return new ResponseEntity<>(messageResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<MessageResponse> handleMissingServletRequestBodyException(
			HttpMessageNotReadableException exception, WebRequest request) {
		logger.warn("Missing or unreadable request body: {}", exception);
		MessageResponse messageResponse = new MessageResponse("Request body is missing or unreadable.");
		return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException exception,
			WebRequest request) {
		String errorMessage = exception.getBindingResult().getAllErrors().stream()
				.map(error -> error.getDefaultMessage()).collect(Collectors.joining("; "));
		logger.warn("Validation errors: {}", errorMessage);
		MessageResponse messageResponse = new MessageResponse(errorMessage);
		return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
	}

}