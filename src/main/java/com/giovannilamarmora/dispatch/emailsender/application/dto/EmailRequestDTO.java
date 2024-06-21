package com.giovannilamarmora.dispatch.emailsender.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

  @NotNull(message = "Email Destination cannot be null")
  @NotBlank(message = "Email Destination cannot be blank")
  private String to;

  private Map<String, String> params;
}
