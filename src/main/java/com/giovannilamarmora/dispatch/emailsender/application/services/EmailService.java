package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.application.Dispatcher;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.mapper.EmailMapper;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service("EmailService")
@Logged
public class EmailService implements IEmailService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private Dispatcher dispatcher;
  @Autowired private EmailMapper emailMapper;
  @Autowired private AttachmentCacheService attachmentCacheService;

  @Override
  @LogInterceptor(type = LogTimeTracker.ActionType.APP_SERVICE)
  public ResponseEntity<EmailResponseDTO> sendEmail(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, String filename)
      throws JsonProcessingException, UtilsException {
    MultipartFile file = null;
    if (filename != null && !filename.isBlank()) {
      LOG.info("Building attachment with filename {}", filename);
      file = getFile(filename);
      attachmentCacheService.removeAttachment(filename);
    }

    LOG.info("Building Message with Data {}", objectMapper.writeValueAsString(emailSenderDTO));
    if ((htmlText == null || !htmlText) && (file == null || file.isEmpty())) {
      sendSimpleMessage(emailSenderDTO);
    }
    if (file != null || htmlText != null) {
      sendMessageWithAttachmentOrHtml(emailSenderDTO, file, htmlText);
    }
    EmailResponseDTO responseDTO = new EmailResponseDTO();
    responseDTO.setMessage(
        String.format("Email to %s was successfully sent!", emailSenderDTO.getTo()));
    return ResponseEntity.ok(responseDTO);
  }

  private void sendSimpleMessage(EmailSenderDTO emailSenderDTO) throws UtilsException {
    SimpleMailMessage message = emailMapper.getSimpleMailMessage(emailSenderDTO);

    dispatcher.sendMessage(message, null);
  }

  private void sendMessageWithAttachmentOrHtml(
      EmailSenderDTO emailSenderDTO, MultipartFile multipartFile, Boolean htmlText)
      throws UtilsException {
    try {
      MimeMessage message =
          emailMapper.getMimeMessageHelper(emailSenderDTO, htmlText, multipartFile);

      dispatcher.sendMessage(null, message);
    } catch (MessagingException e) {
      LOG.error(
          "An error occurred during set data into MimeMessageHelper, error message {}",
          e.getMessage());
      throw new UtilsException(
          EmailException.ERR_MAIL_SEND_002,
          EmailException.ERR_MAIL_SEND_002.getMessage(),
          e.getMessage());
    }
  }

  private MultipartFile getFile(String filename) {
    AttachmentDTO attachmentDTO = attachmentCacheService.getAttachment(filename);
    return emailMapper.fromDtoToPart(attachmentDTO);
  }
}
