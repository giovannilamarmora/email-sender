package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.api.strapi.StrapiService;
import com.giovannilamarmora.dispatch.emailsender.application.Dispatcher;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailRequestDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.mapper.EmailMapper;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.giovannilamarmora.dispatch.emailsender.exception.ExceptionMap;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.Utilities;
import jakarta.mail.internet.MimeMessage;
import java.util.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service("EmailService")
@Logged
public class EmailService implements IEmailService {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());
  @Autowired private Dispatcher dispatcher;
  @Autowired private AttachmentCacheService attachmentCacheService;
  @Autowired private StrapiService strapiService;

  @Override
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Mono<ResponseEntity<EmailResponseDTO>> sendEmail(
      EmailSenderDTO emailSenderDTO, Boolean htmlText, String filename) {
    List<MultipartFile> file = new ArrayList<>();
    if (filename != null && !filename.isBlank()) {
      LOG.info("Building attachment with filename {}", filename);
      if (filename.contains(",")) {
        file =
            Arrays.stream(filename.split(","))
                .map(
                    filename1 -> {
                      MultipartFile file1 = getFile(filename1);
                      attachmentCacheService.removeAttachment(filename1);
                      return file1;
                    })
                .toList();
      } else {
        file.add(getFile(filename));
        attachmentCacheService.removeAttachment(filename);
      }
    }

    LOG.info("Building Message with Data {}", Utilities.convertObjectToJson(emailSenderDTO));
    if ((htmlText == null || !htmlText) && (file == null || file.isEmpty())) {
      sendSimpleMessage(emailSenderDTO);
    }
    if (file != null || htmlText != null) {
      sendMessageWithAttachmentOrHtml(emailSenderDTO, file, htmlText);
    }
    EmailResponseDTO responseDTO = new EmailResponseDTO();
    responseDTO.setMessage(
        String.format("Email to %s was successfully sent!", emailSenderDTO.getTo()));
    return Mono.just(ResponseEntity.ok(responseDTO));
  }

  @Override
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Mono<ResponseEntity<EmailResponseDTO>> sendEmailWithTemplate(
      String templateId,
      String locale,
      String filename,
      Boolean htmlText,
      EmailRequestDTO emailRequestDTO) {
    return strapiService
        .getTemplateById(templateId, locale)
        .flatMap(
            strapiEmailTemplate -> {
              EmailSenderDTO emailContent =
                  EmailSenderDTO.builder()
                      .subject(strapiEmailTemplate.getSubject())
                      .to(emailRequestDTO.getTo())
                      .sentDate(new Date())
                      .build();

              String template = strapiEmailTemplate.getTemplate();
              Map<String, String> finalParam =
                  Utilities.getFinalMapFromValue(
                      emailRequestDTO.getParams(), strapiEmailTemplate.getParams());
              for (String key : finalParam.keySet()) {
                template = template.replace("{{" + key + "}}", finalParam.get(key));
              }

              emailContent.setText(template);
              return sendEmail(emailContent, htmlText, filename)
                  .flatMap(
                      emailResponseResponseEntity -> {
                        if (ObjectUtils.isEmpty(emailResponseResponseEntity.getBody())) {
                          LOG.error("Send email returned a empty object");
                          throw new EmailException(
                              ExceptionMap.ERR_MAIL_SEND_002,
                              ExceptionMap.ERR_MAIL_SEND_002.getMessage(),
                              "Invalid Data Provided");
                        }
                        return Mono.just(emailResponseResponseEntity);
                      });
            });
  }

  private void sendSimpleMessage(EmailSenderDTO emailSenderDTO) throws EmailException {
    SimpleMailMessage message = EmailMapper.getSimpleMailMessage(emailSenderDTO);

    dispatcher.sendMessage(message, null);
  }

  private void sendMessageWithAttachmentOrHtml(
      EmailSenderDTO emailSenderDTO, List<MultipartFile> multipartFile, Boolean htmlText) {
    MimeMessage message = EmailMapper.getMimeMessageHelper(emailSenderDTO, htmlText, multipartFile);

    dispatcher.sendMessage(null, message);
  }

  private MultipartFile getFile(String filename) {
    AttachmentDTO attachmentDTO = attachmentCacheService.getAttachment(filename);
    return EmailMapper.fromDtoToPart(attachmentDTO);
  }
}
