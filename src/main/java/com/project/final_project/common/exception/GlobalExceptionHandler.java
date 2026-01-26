package com.project.final_project.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    // 400 Bad Request
    return new ResponseEntity<>("잘못된 요청: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
    // 409 Conflict
    return new ResponseEntity<>("상태 에러: " + ex.getMessage(), HttpStatus.CONFLICT);
  }

  // 기타 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
    // 500 Internal Server Error
    return new ResponseEntity<>("서버 에러: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}