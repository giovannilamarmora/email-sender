package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import org.springframework.http.ResponseEntity;

public interface IEmailService {

  ResponseEntity<EmailResponseDTO> sendEmail(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, String filename)
      throws JsonProcessingException, UtilsException;
}
