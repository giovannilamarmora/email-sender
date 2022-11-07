package com.giovannilamarmora.dispatch.emailsender.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSenderDTO {

  private String bbc;
  private String cc;
  private String from;
  private String replyTo;
  private Date sentDate;

  @NotNull(message = "Subject cannot be null")
  @NotBlank(message = "Subject cannot be blank")
  private String subject;

  @NotNull(message = "Text cannot be null")
  @NotBlank(message = "Text cannot be blank")
  private String text;

  @NotNull(message = "Email Destination cannot be null")
  @NotBlank(message = "Email Destination cannot be blank")
  private String to;
}
