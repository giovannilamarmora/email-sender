/*package com.giovannilamarmora.dispatch.emailsender.application;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.AttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.IEmailService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest
public class EmailControllerTest {

  @MockBean private AttachmentCacheService attachmentCacheService;
  @MockBean private IEmailService emailService;

  @Autowired private WebTestClient webTestClient;

  private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Test
  public void testUploadAttachment_successfully() throws Exception {
    AttachmentDTO expected =
        objectMapper.readValue(
            new ClassPathResource("attachment.json").getInputStream(), AttachmentDTO.class);
    MockMultipartFile file =
        new MockMultipartFile(
            expected.getName(),
            expected.getFileName(),
            expected.getContentType().toString(),
            expected.getBody());
    MediaType mediaType = MediaType.MULTIPART_FORM_DATA;
    AttachmentDTO attachmentDTO =
        new AttachmentDTO(
            file.getName(), file.getOriginalFilename(), mediaType, file.getSize(), file.getBytes());
    when(attachmentCacheService.getAttachment(Mockito.anyString())).thenReturn(attachmentDTO);

    String response =
        webTestClient
            .post()
            .uri("/api/v1/my")
            .body(BodyInserters.fromMultipartData((MultiValueMap<String, ?>) file))
            .accept(MediaType.MULTIPART_FORM_DATA)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class)
            .getResponseBody()
            .blockFirst();
  }

  @Test
  public void testSendEmail_successfully() throws Exception {
    EmailResponseDTO expected =
        new EmailResponseDTO(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "Email Sent!");
    EmailSenderDTO emailSenderDTO =
        new EmailSenderDTO(
            null, "email@email.com", null, null, null, "Subject", "Text", "emsail@email.com");

    when(emailService.sendEmail(emailSenderDTO, false, null))
        .thenReturn(Mono.just(ResponseEntity.ok(expected)));

    String responseAsString = objectMapper.writeValueAsString(expected);
    String sender = objectMapper.writeValueAsString(emailSenderDTO);

    // mockMvc
    //    .perform(
    //        MockMvcRequestBuilders.post("/v1/send-email")
    //            .contentType(MediaType.APPLICATION_JSON_VALUE)
    //            .content(sender))
    //    .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
*/
