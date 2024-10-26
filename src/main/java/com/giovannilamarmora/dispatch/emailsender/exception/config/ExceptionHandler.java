package com.giovannilamarmora.dispatch.emailsender.exception.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.giovannilamarmora.dispatch.emailsender.exception.ValidationException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionHandler extends UtilsException {

  /**
   * Handle Jakarta Validation Input
   *
   * @param ex ServerWebInputException to be handled
   * @param request ServerHttpRequest
   * @return ValidationException
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(ServerWebInputException.class)
  public Mono<ResponseEntity<?>> handleServerWebInputException(
      ServerWebInputException ex, ServerHttpRequest request) {
    String message =
        ((WebExchangeBindException) ex)
            .getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList()
                .getFirst();
    ExceptionResponse response =
        ValidationException.getExceptionResponse(
            new ValidationException(message), request, ExceptionMap.ERR_VALID_MAIL_001);
    response.getError().setMessage(message);
    response.getError().setExceptionMessage(null);
    return Mono.just(new ResponseEntity<>(response, ex.getStatusCode()));
  }

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
