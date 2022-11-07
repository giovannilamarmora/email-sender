package com.giovannilamarmora.dispatch.emailsender.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.giovannilamarmora.utils.exception.UtilsException;
import com.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandler extends UtilsException {

    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = JsonProcessingException.class)
    public ResponseEntity<ExceptionResponse> handleException(
            JsonProcessingException e, HttpServletRequest request) {
        LOG.error(
                "An error happened while calling {} Downstream API: {}",
                request.getRequestURI(),
                e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(getExceptionResponse(e, request, EmailException.ERR_MAIL_SEND_001, status), status);
    }
}