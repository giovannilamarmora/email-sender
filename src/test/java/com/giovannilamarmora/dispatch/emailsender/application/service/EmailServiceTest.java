package com.giovannilamarmora.dispatch.emailsender.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.AttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.EmailService;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmailServiceTest {

  @Autowired private EmailService emailService;
  @Autowired private AttachmentCacheService attachmentCacheService;

  @MockBean JavaMailSender javaMailSender;

  private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @BeforeEach
  public void setUp() {
    Mockito.doNothing().when(javaMailSender).send((MimeMessage) Mockito.any());
  }

  @Test
  public void sendMailTest_successfully() throws EmailException, JsonProcessingException {
    EmailResponseDTO expected =
        new EmailResponseDTO(
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
            "Email to emsail@email.com was successfully sent!");
    EmailSenderDTO emailSenderDTO =
        new EmailSenderDTO(
            null, "email@email.com", null, null, null, "Subject", "Text", "emsail@email.com");

    ResponseEntity<EmailResponseDTO> actual = emailService.sendEmail(emailSenderDTO, false, null);
    Objects.requireNonNull(actual.getBody()).setTimestamp(actual.getBody().getTimestamp().truncatedTo(ChronoUnit.MINUTES));
    assertEquals(expected, actual.getBody());
  }

  @Test
  public void sendMailTestWithAttachmentAndHtml_successfully() throws EmailException, IOException {
    EmailResponseDTO expected =
        new EmailResponseDTO(
            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
            "Email to emsail@email.com was successfully sent!");
    EmailSenderDTO emailSenderDTO =
        new EmailSenderDTO(
            null, "email@email.com", null, null, null, "Subject", "Text", "emsail@email.com");

    AttachmentDTO attachmentDTO =
        objectMapper.readValue(
            new ClassPathResource("attachment.json").getInputStream(), AttachmentDTO.class);
    MockMultipartFile file =
        new MockMultipartFile(
            attachmentDTO.getName(),
            attachmentDTO.getFileName(),
            attachmentDTO.getContentType(),
            attachmentDTO.getBody());

    ResponseEntity<AttachmentDTO> actualAttachment = attachmentCacheService.saveAttachmentDto(file);
    assertEquals(attachmentDTO, actualAttachment.getBody());

    ResponseEntity<EmailResponseDTO> actual =
        emailService.sendEmail(emailSenderDTO, true, file.getOriginalFilename());
    Objects.requireNonNull(actual.getBody()).setTimestamp(actual.getBody().getTimestamp().truncatedTo(ChronoUnit.MINUTES));
    assertEquals(expected, actual.getBody());
  }
}
