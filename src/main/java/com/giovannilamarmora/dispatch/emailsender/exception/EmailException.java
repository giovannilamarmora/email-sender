package com.giovannilamarmora.dispatch.emailsender.exception;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class EmailException extends UtilsException {

  public EmailException(ExceptionCode exceptionCode, String message) {
    super(exceptionCode, message);
  }

  public EmailException(ExceptionCode exceptionCode, String message, String exceptionMessage) {
    super(exceptionCode, message, exceptionMessage);
  }
}
