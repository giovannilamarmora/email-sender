package com.giovannilamarmora.dispatch.emailsender.application.mapper;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.github.giovannilamarmora.utils.exception.UtilsException;
import com.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import com.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Component
public class EmailMapper {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_MAPPER)
  public SimpleMailMessage getSimpleMailMessage(EmailSenderDTO emailSenderDTO) {
    LOG.info("SimpleMailMessage Mapper");
    SimpleMailMessage message = new SimpleMailMessage();
    if (emailSenderDTO.getBbc() != null && !emailSenderDTO.getBbc().isBlank()) {
      message.setBcc(emailSenderDTO.getBbc());
    }
    if (emailSenderDTO.getCc() != null && !emailSenderDTO.getCc().isBlank()) {
      message.setCc(emailSenderDTO.getCc());
    }
    if (emailSenderDTO.getFrom() != null && !emailSenderDTO.getFrom().isBlank()) {
      message.setFrom(emailSenderDTO.getFrom());
    }
    if (emailSenderDTO.getReplyTo() != null && !emailSenderDTO.getReplyTo().isBlank()) {
      message.setReplyTo(emailSenderDTO.getReplyTo());
    }
    if (emailSenderDTO.getSentDate() != null) {
      message.setSentDate(emailSenderDTO.getSentDate());
    }
    message.setSubject(emailSenderDTO.getSubject());
    message.setText(emailSenderDTO.getText());
    message.setTo(emailSenderDTO.getTo());
    return message;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_MAPPER)
  public MimeMessage getMimeMessageHelper(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, MultipartFile multipartFile)
      throws MessagingException, UtilsException {
    LOG.info("MimeMessageHelper Mapper");
    JavaMailSender sender = new JavaMailSenderImpl();
    MimeMessage message = sender.createMimeMessage();
    MimeMessageHelper helper =
        htmlText != null && !htmlText
            ? new MimeMessageHelper(message, true)
            : new MimeMessageHelper(message, true, "UTF-8");

    if (emailSenderDTO.getBbc() != null && !emailSenderDTO.getBbc().isBlank()) {
      helper.setBcc(emailSenderDTO.getBbc());
    }
    if (emailSenderDTO.getCc() != null && !emailSenderDTO.getCc().isBlank()) {
      helper.setCc(emailSenderDTO.getCc());
    }
    if (emailSenderDTO.getFrom() != null && !emailSenderDTO.getFrom().isBlank()) {
      helper.setFrom(emailSenderDTO.getFrom());
    }
    if (emailSenderDTO.getReplyTo() != null && !emailSenderDTO.getReplyTo().isBlank()) {
      helper.setReplyTo(emailSenderDTO.getReplyTo());
    }
    if (emailSenderDTO.getSentDate() != null) {
      helper.setSentDate(emailSenderDTO.getSentDate());
    }

    helper.setSubject(emailSenderDTO.getSubject());
    helper.setText(emailSenderDTO.getText());
    helper.setTo(emailSenderDTO.getTo());

    // TODO: Need to be tested
    if (multipartFile != null && !multipartFile.isEmpty()) {
      AttachmentDTO attachmentDTO = fromPartToDto(multipartFile);
      helper.addAttachment(attachmentDTO.getFileName(), multipartFile);
    }
    if (htmlText != null && htmlText) {
      helper.setText(emailSenderDTO.getText(), true);
    }
    return message;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_MAPPER)
  public AttachmentDTO fromPartToDto(MultipartFile file) throws UtilsException {
    try {
      return new AttachmentDTO(
          file.getName(),
          file.getOriginalFilename(),
          file.getContentType(),
          file.getSize(),
          file.getBytes());
    } catch (IOException e) {
      LOG.error("Error on converting Attachment: {}", e.getMessage());
      throw new UtilsException(EmailException.ERR_MAIL_SEND_001, e.getMessage());
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_MAPPER)
  public MultipartFile fromDtoToPart(AttachmentDTO attachmentDTO) {
    MultipartFile file =
        new MockMultipartFile(
            attachmentDTO.getName(),
            attachmentDTO.getFileName(),
            attachmentDTO.getContentType(),
            attachmentDTO.getBody());
    return file;
  }
}
