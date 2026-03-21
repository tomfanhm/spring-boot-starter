package com.starter.app.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(int status, String message, Instant timestamp, List<FieldError> errors) {

  public ApiError(int status, String message) {
    this(status, message, Instant.now(), null);
  }

  public ApiError(int status, String message, List<FieldError> errors) {
    this(status, message, Instant.now(), errors);
  }

  public record FieldError(String field, String message) {}
}
