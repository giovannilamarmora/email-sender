package com.giovannilamarmora.dispatch.emailsender.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
  @NotNull(message = "Name must not be null")
  @NotEmpty(message = "Name must not be empty")
  private String name;

  @NotNull(message = "fileName must not be null")
  @NotEmpty(message = "fileName must not be empty")
  private String fileName;

  @NotNull(message = "contentType must not be null")
  @NotEmpty(message = "contentType must not be empty")
  private String contentType;

  private long size;

  @NotNull(message = "body must not be null")
  @NotEmpty(message = "body must not be empty")
  private byte[] body;
}
