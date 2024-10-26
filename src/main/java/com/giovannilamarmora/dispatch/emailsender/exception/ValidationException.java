package com.giovannilamarmora.dispatch.emailsender.exception;

import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class ValidationException extends UtilsException {
  private static final ExceptionCode DEFAULT_CODE = ExceptionMap.ERR_VALID_MAIL_001;

  public ValidationException(String message) {
    super(DEFAULT_CODE, message);
  }

  public ValidationException(ExceptionCode exceptionCode, String message) {
    super(exceptionCode, message);
  }
}
