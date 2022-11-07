package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.github.giovannilamarmora.utils.exception.UtilsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IEmailService {

  ResponseEntity<EmailResponseDTO> sendEmail(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, MultipartFile multipartFile)
      throws JsonProcessingException, UtilsException;
}
