package com.giovannilamarmora.dispatch.emailsender.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailResponseDTO;
import com.giovannilamarmora.dispatch.emailsender.application.dto.EmailSenderDTO;
import com.giovannilamarmora.dispatch.emailsender.application.services.AttachmentCacheService;
import com.giovannilamarmora.dispatch.emailsender.application.services.IEmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ActiveProfiles("test")
@WebMvcTest(controllers = EmailController.class)
public class EmailControllerTest {

  @MockBean private AttachmentCacheService attachmentCacheService;
  @MockBean private IEmailService emailService;

  @Autowired private MockMvc mockMvc;

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
            expected.getContentType(),
            expected.getBody());
    AttachmentDTO attachmentDTO =
        new AttachmentDTO(
            file.getName(),
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes());
    Mockito.when(attachmentCacheService.getAttachment(Mockito.anyString()))
        .thenReturn(attachmentDTO);

    mockMvc
        .perform(multipart("/v1/upload/attachment").file(file))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testSendEmail_successfully() throws Exception {
    EmailResponseDTO expected =
        new EmailResponseDTO(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "Email Sent!");
    EmailSenderDTO emailSenderDTO =
        new EmailSenderDTO(
            null, "email@email.com", null, null, null, "Subject", "Text", "emsail@email.com");

    Mockito.when(emailService.sendEmail(emailSenderDTO, false, null))
        .thenReturn(ResponseEntity.ok(expected));

    String responseAsString = objectMapper.writeValueAsString(expected);
    String sender = objectMapper.writeValueAsString(emailSenderDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/v1/send-email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(sender))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
