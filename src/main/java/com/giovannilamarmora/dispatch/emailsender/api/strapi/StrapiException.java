package com.giovannilamarmora.dispatch.emailsender.api.strapi;

import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class StrapiException extends UtilsException {

  private static final ExceptionCode DEFAULT_CODE = ExceptionMap.ERR_MAIL_SEND_002;

  public StrapiException(String message) {
    super(DEFAULT_CODE, message);
  }

  public StrapiException(ExceptionCode exceptionCode, String message) {
    super(exceptionCode, message);
  }
}
