package com.giovannilamarmora.dispatch.emailsender.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler extends UtilsException {

  @org.springframework.web.bind.annotation.ExceptionHandler(value = JsonProcessingException.class)
  public ResponseEntity<ExceptionResponse> handleException(
      JsonProcessingException e, ServerHttpRequest request) {
    LOG.error(
        "An error happened while calling {} Downstream API: {}",
        request.getPath().value(),
        e.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return new ResponseEntity<>(
        getExceptionResponse(e, request, ExceptionMap.ERR_MAIL_SEND_001), status);
  }
}
