package com.giovannilamarmora.dispatch.emailsender.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.AttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.IEmailService;
import com.github.giovannilamarmora.utils.exception.UtilsException;
import com.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import com.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import com.github.giovannilamarmora.utils.interceptors.Logged;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Logged
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class EmailController {

  @Autowired private IEmailService emailService;
  @Autowired private AttachmentCacheService attachmentCacheService;

  @PostMapping(
      value = "/send-email",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(description = "Send Email")
  @LogInterceptor(type = LogTimeTracker.ActionType.APP_CONTROLLER)
  public ResponseEntity<EmailResponseDTO> sendEmail(
      @RequestBody @Valid EmailSenderDTO emailSenderDTO,
      @RequestParam(required = false) Boolean htmlText,
      @RequestParam(required = false) String filename)
      throws UtilsException, JsonProcessingException {
    return emailService.sendEmail(emailSenderDTO, htmlText, filename);
  }

  @PostMapping(
      value = "/upload/attachment",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(description = "Send Email")
  @LogInterceptor(type = LogTimeTracker.ActionType.APP_CONTROLLER)
  public ResponseEntity<AttachmentDTO> uploadAttachment(
      @RequestPart(name = "file") MultipartFile file) throws UtilsException {
    return attachmentCacheService.saveAttachmentDto(file);
  }
}
