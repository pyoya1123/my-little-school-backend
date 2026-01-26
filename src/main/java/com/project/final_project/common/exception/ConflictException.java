package com.project.final_project.common.exception;

/**
 * 충돌 예외 (409 Conflict)
 */
public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}

