package com.giovannilamarmora.dispatch.emailsender.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @Schema(description = "Email blind carbon copy", example = "email@email.com")
  private String bbc;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @Schema(description = "Email Copy Receiver", example = "email@email.com")
  private String cc;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @Schema(
      description = "Email Coming From, Should match one of the emails set into the configuration",
      example = "email@email.com")
  private String from;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @Schema(description = "Email Reply", example = "email@email.com")
  private String replyTo;

  @Pattern(
      regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
      message = "Email format is invalid. Please enter a valid email address.")
  @NotBlank(message = "Please enter a valid email address.")
  @Schema(description = "Email Receiver", example = "email@email.com")
  private String to;

  @Schema(description = "Param to be changed on the email", example = "\"USER.NAME\": \"Username\"")
  private Map<String, String> params;
}
