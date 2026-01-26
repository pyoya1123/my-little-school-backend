package com.project.final_project.common.exception;

import com.project.final_project.common.global.HttpResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 전역 예외 처리 핸들러
 * 모든 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleNotFoundException(
      NotFoundException ex, WebRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(HttpResponseEntity.error(ex.getMessage(), HttpStatus.NOT_FOUND));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleBadRequestException(
      BadRequestException ex, WebRequest request) {
    log.warn("Bad request: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpResponseEntity.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleConflictException(
      ConflictException ex, WebRequest request) {
    log.warn("Conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(HttpResponseEntity.error(ex.getMessage(), HttpStatus.CONFLICT));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Illegal argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpResponseEntity.error("잘못된 요청: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleIllegalStateException(
      IllegalStateException ex, WebRequest request) {
    log.warn("Illegal state: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(HttpResponseEntity.error("상태 에러: " + ex.getMessage(), HttpStatus.CONFLICT));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.warn("Validation error: {}", ex.getMessage());
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
        .orElse("입력값 검증 실패");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(HttpResponseEntity.error(errorMessage, HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<HttpResponseEntity.ResponseResult<?>> handleGlobalException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(HttpResponseEntity.error("서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
  }
}