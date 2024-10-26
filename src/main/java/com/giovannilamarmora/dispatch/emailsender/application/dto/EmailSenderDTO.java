package com.giovannilamarmora.dispatch.emailsender.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSenderDTO {

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  private String bbc;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  private String cc;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  private String from;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  private String replyTo;

  private Date sentDate;

  @NotBlank(message = "Subject cannot be null or blank")
  private String subject;

  @NotBlank(message = "Text cannot be null or blank")
  private String text;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @NotBlank(message = "Email Destination cannot be null or blank")
  private String to;
}
