package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailRequestDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IEmailService {

  Mono<ResponseEntity<EmailResponseDTO>> sendEmail(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, String filename);

  Mono<ResponseEntity<EmailResponseDTO>> sendEmailWithTemplate(
      String templateId,
      String locale,
      String filename,
      Boolean htmlText,
      EmailRequestDTO emailRequestDTO);
}
