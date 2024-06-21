package com.giovannilamarmora.dispatch.emailsender.application;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailRequestDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.IAttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.IEmailService;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Logged
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class EmailController {

  @Autowired private IEmailService emailService;
  @Autowired private IAttachmentCacheService attachmentCacheService;

  @PostMapping(
      value = "/send-email",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Tag(
      name = "Email Sender",
      description = "API to send email from giovannilamarmora.working@gmail.com")
  @Operation(
      description = "API to send email from giovannilamarmora.working@gmail.com",
      tags = "Email Sender")
  @LogInterceptor(type = LogTimeTracker.ActionType.CONTROLLER)
  public Mono<ResponseEntity<EmailResponseDTO>> sendEmail(
      @RequestBody @Valid EmailSenderDTO emailSenderDTO,
      @RequestParam(required = false) Boolean htmlText,
      @RequestParam(required = false) String filename) {
    return emailService.sendEmail(emailSenderDTO, htmlText, filename);
  }

  @PostMapping(
      value = "/send-email/{templateId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Tag(
      name = "Email Sender",
      description = "API to send email from giovannilamarmora.working@gmail.com")
  @Operation(
      description = "API to send email from giovannilamarmora.working@gmail.com",
      tags = "Email Sender")
  @LogInterceptor(type = LogTimeTracker.ActionType.CONTROLLER)
  public Mono<ResponseEntity<EmailResponseDTO>> sendEmailWithTemplate(
      @PathVariable(value = "templateId") String templateId,
      @RequestParam(required = false, defaultValue = "en-GB") String locale,
      @RequestBody @Valid EmailRequestDTO emailRequestDTO,
      @RequestParam(required = false) Boolean htmlText,
      @RequestParam(required = false) String filename) {
    return emailService.sendEmailWithTemplate(
        templateId, locale, filename, htmlText, emailRequestDTO);
  }

  @PostMapping(
      value = "/upload/attachment",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Tag(
      name = "Upload Attachment",
      description = "API to upload the attachment before to send email")
  @Operation(
      description = "API to upload the attachment before to send email",
      tags = "Upload Attachment")
  @LogInterceptor(type = LogTimeTracker.ActionType.CONTROLLER)
  public Flux<AttachmentDTO> uploadAttachment(
      @RequestPart(name = "file") Flux<FilePart> file) throws EmailException {
    return attachmentCacheService.saveAttachmentDto(file);
  }
}
