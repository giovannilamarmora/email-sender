package com.giovannilamarmora.dispatch.emailsender.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
  @NotEmpty(message = "fileName must not be empty")
  private String fileName;

  @NotEmpty(message = "contentType must not be empty")
  private String contentType;

  private long size;

  @NotEmpty(message = "body must not be empty")
  private byte[] body;
}
