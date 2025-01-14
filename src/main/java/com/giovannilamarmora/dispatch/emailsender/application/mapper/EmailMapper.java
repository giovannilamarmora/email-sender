package com.giovannilamarmora.dispatch.emailsender.application.mapper;

import com.giovannilamarmora.dispatch.emailsender.api.strapi.dto.StrapiEmailTemplate;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailRequestDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.Utilities;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EmailMapper {

  private static final Logger LOG = LoggerFilter.getLogger(EmailMapper.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static EmailSenderDTO mapEmailSenderDTO(
      EmailRequestDTO emailRequestDTO, StrapiEmailTemplate strapiEmailTemplate) {
    EmailSenderDTO emailSenderDTO = new EmailSenderDTO();
    if (!Utilities.isNullOrEmpty(emailRequestDTO.getBbc())) {
      emailSenderDTO.setBbc(emailRequestDTO.getBbc());
    }
    if (!Utilities.isNullOrEmpty(emailRequestDTO.getCc())) {
      emailSenderDTO.setCc(emailRequestDTO.getCc());
    }
    if (!Utilities.isNullOrEmpty(emailRequestDTO.getFrom())) {
      emailSenderDTO.setFrom(emailRequestDTO.getFrom());
    }
    if (!Utilities.isNullOrEmpty(emailRequestDTO.getReplyTo())) {
      emailSenderDTO.setReplyTo(emailRequestDTO.getReplyTo());
    }
    emailSenderDTO.setSentDate(new Date());
    emailSenderDTO.setSubject(strapiEmailTemplate.getSubject());
    emailSenderDTO.setTo(emailRequestDTO.getTo());

    String template = strapiEmailTemplate.getTemplate();
    Map<String, String> finalParam =
        Utilities.getFinalMapFromValue(
            emailRequestDTO.getParams(), strapiEmailTemplate.getParams());
    for (String key : finalParam.keySet()) {
      template = template.replace("{{" + key + "}}", finalParam.get(key));
    }
    emailSenderDTO.setText(template);
    return emailSenderDTO;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static SimpleMailMessage getSimpleMailMessage(EmailSenderDTO emailSenderDTO) {
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

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static MimeMessage getMimeMessageHelper(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, List<MultipartFile> multipartFiles) {
    LOG.info("MimeMessageHelper Mapper");
    JavaMailSender sender = new JavaMailSenderImpl();
    MimeMessage message = sender.createMimeMessage();
    MimeMessageHelper helper = null;
    try {
      helper =
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

      if (multipartFiles != null && !multipartFiles.isEmpty()) {
        MimeMessageHelper finalHelper = helper;
        multipartFiles.forEach(
            multipartFile -> {
              AttachmentDTO attachmentDTO = fromPartToDto(multipartFile);
              try {
                finalHelper.addAttachment(attachmentDTO.getFileName(), multipartFile);
              } catch (MessagingException e) {
                LOG.error(
                    "An error occurred during set data into MimeMessageHelper, error message {}",
                    e.getMessage());
                throw new EmailException(
                    ExceptionMap.ERR_MAIL_SEND_002,
                    ExceptionMap.ERR_MAIL_SEND_002.getMessage(),
                    e.getMessage());
              }
            });
      }
      if (htmlText != null && htmlText) {
        helper.setText(emailSenderDTO.getText(), true);
      }
    } catch (MessagingException e) {
      LOG.error(
          "An error occurred during set data into MimeMessageHelper, error message {}",
          e.getMessage());
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_002,
          ExceptionMap.ERR_MAIL_SEND_002.getMessage(),
          e.getMessage());
    }
    return message;
  }

  @Deprecated
  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static AttachmentDTO fromPartToDto(MultipartFile file) throws EmailException {
    try {
      return new AttachmentDTO(
          file.getName(),
          file.getOriginalFilename(),
          file.getContentType(),
          file.getSize(),
          file.getBytes());
    } catch (IOException e) {
      LOG.error("Error on converting Attachment: {}", e.getMessage());
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_001,
          ExceptionMap.ERR_MAIL_SEND_001.getMessage()
              + " with filename: "
              + file.getOriginalFilename(),
          e.getMessage());
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static Flux<AttachmentDTO> fromPartToDto(Flux<FilePart> files) throws EmailException {
    AtomicReference<Integer> size = new AtomicReference<>();
    return files.flatMap(
        file -> {
          Mono<byte[]> getByteArray =
              DataBufferUtils.join(file.content())
                  .map(
                      dataBuffer -> {
                        try {
                          size.set(dataBuffer.capacity());
                          return dataBuffer.asInputStream().readAllBytes();
                        } catch (IOException e) {
                          LOG.error("Error on converting Attachment: {}", e.getMessage());
                          throw new EmailException(
                              ExceptionMap.ERR_MAIL_SEND_001,
                              ExceptionMap.ERR_MAIL_SEND_001.getMessage()
                                  + " with filename: "
                                  + file.filename(),
                              e.getMessage());
                        }
                      });

          return getByteArray.map(
              bytes ->
                  new AttachmentDTO(
                      file.filename(),
                      file.filename(),
                      file.headers().getContentType().toString(),
                      size.get(),
                      bytes));
        });
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.MAPPER)
  public static MultipartFile fromDtoToPart(AttachmentDTO attachmentDTO) {
    MultipartFile file =
        new MockMultipartFile(
            attachmentDTO.getName(),
            attachmentDTO.getFileName(),
            attachmentDTO.getContentType().toString(),
            attachmentDTO.getBody());
    return file;
  }
}
