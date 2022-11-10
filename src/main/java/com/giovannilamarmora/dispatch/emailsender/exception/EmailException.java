package com.giovannilamarmora.dispatch.emailsender.exception;

import com.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum EmailException implements ExceptionCode {
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

  EmailException(String exceptionName, HttpStatus status, String message) {
    this.exceptionName = exceptionName;
    this.status = status;
    this.message = message;
  }

  @Override
  public String exceptionName() {
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
