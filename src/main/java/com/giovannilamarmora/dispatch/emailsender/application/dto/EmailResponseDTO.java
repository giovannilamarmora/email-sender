package com.giovannilamarmora.dispatch.emailsender.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
public class EmailResponseDTO {

  private LocalDateTime timestamp;
  private String message;

  public EmailResponseDTO() {
    this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
