package com.giovannilamarmora.dispatch.emailsender.exception.config;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum ExceptionMap implements ExceptionCode {
  /**
   * @Validation Exception Map for Validation Input
   */
  ERR_VALID_MAIL_001(
      "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "Required input are null or Empty"),
  ERR_MAIL_SEND_001(
      "ERROR-DESERIALIZE-EXCEPTION", HttpStatus.BAD_REQUEST, "Error on deserializing object"),
  ERR_MAIL_SEND_002(
      "ERROR-SMTP-SERVER",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "An error occurred during sending process"),
  ERR_MAIL_SEND_003(
      "ERROR-CACHING", HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during caching data");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  ExceptionMap(String exceptionName, HttpStatus status, String message) {
    this.exceptionName = exceptionName;
    this.status = status;
    this.message = message;
  }

  @Override
  public String exception() {
    return exceptionName;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public HttpStatus getStatus() {
    return status;
  }
}
