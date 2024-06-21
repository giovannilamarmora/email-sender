package com.giovannilamarmora.dispatch.emailsender.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.AttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.EmailService;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    Mono<ResponseEntity<EmailResponseDTO>> actual =
        emailService.sendEmail(emailSenderDTO, false, null);

    StepVerifier.create(actual)
        .assertNext(
            res -> {
              assertEquals(HttpStatus.OK, res.getStatusCode());
              Objects.requireNonNull(res.getBody())
                  .setTimestamp(res.getBody().getTimestamp().truncatedTo(ChronoUnit.MINUTES));
              assertEquals(expected, res.getBody());
            })
        .verifyComplete();
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
            attachmentDTO.getContentType().toString(),
            attachmentDTO.getBody());

    FilePart filePart = Mockito.mock(FilePart.class);
    when(filePart.filename()).thenReturn(attachmentDTO.getFileName());

    // Crea un DataBuffer con il contenuto del file
    DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
    DataBuffer dataBuffer = factory.wrap(attachmentDTO.getBody());

    // Definisci il comportamento del metodo content()
    when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(attachmentDTO.getContentType()));
    when(filePart.headers()).thenReturn(headers);

    Flux<AttachmentDTO> actualAttachment =
        attachmentCacheService.saveAttachmentDto(Flux.just(filePart));

    StepVerifier.create(actualAttachment)
        .assertNext(
            res -> {
              assertEquals(attachmentDTO, res);
            })
        .verifyComplete();

    Mono<ResponseEntity<EmailResponseDTO>> actual =
        emailService.sendEmail(emailSenderDTO, true, file.getOriginalFilename());

    StepVerifier.create(actual)
        .assertNext(
            res -> {
              assertEquals(HttpStatus.OK, res.getStatusCode());
              Objects.requireNonNull(res.getBody())
                  .setTimestamp(res.getBody().getTimestamp().truncatedTo(ChronoUnit.MINUTES));
              assertEquals(expected, res.getBody());
            })
        .verifyComplete();
  }
}
