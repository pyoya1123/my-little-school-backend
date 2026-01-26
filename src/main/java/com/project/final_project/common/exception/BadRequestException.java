package com.project.final_project.common.exception;

/**
 * 잘못된 요청 예외 (400 Bad Request)
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}

