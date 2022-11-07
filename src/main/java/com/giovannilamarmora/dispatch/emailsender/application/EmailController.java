package com.giovannilamarmora.dispatch.emailsender.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.IEmailService;
import com.github.giovannilamarmora.utils.exception.UtilsException;
import com.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import com.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import com.github.giovannilamarmora.utils.interceptors.Logged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Logged
@RestController
@RequestMapping("v1")
public class EmailController {

  @Autowired private IEmailService emailService;

  @PostMapping(
      value = "send-email",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
      produces = MediaType.APPLICATION_JSON_VALUE)
  @LogInterceptor(type = LogTimeTracker.ActionType.APP_CONTROLLER)
  public void sendEmail(
      @RequestBody @Valid EmailSenderDTO emailSenderDTO,
      @RequestPart(name = "file", required = false) MultipartFile file,
      @RequestParam(required = false) Boolean htmlText)
      throws UtilsException, JsonProcessingException {
    emailService.sendEmail(emailSenderDTO, htmlText, file);
  }
}
