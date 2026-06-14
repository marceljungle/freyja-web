package com.freyja.infrastructure.rest;

import java.util.stream.Collectors;

import com.freyja.domain.exception.AuthenticationException;
import com.freyja.domain.exception.ConflictException;
import com.freyja.domain.exception.DomainException;
import com.freyja.domain.exception.EntityNotFoundException;
import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
    return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<ApiError> handleMessaging(MessagingException ex, HttpServletRequest request) {
    log.error("Messaging failure: {}", ex.getMessage());
    return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
  }

  /**
   * Any other domain exception is treated as a bad request.
   */
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiError> handleDomain(DomainException ex, HttpServletRequest request) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleBeanValidation(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .collect(Collectors.joining("; "));
    return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation failed" : message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error handling {} {}", request.getMethod(), request.getRequestURI(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
    ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }
}
