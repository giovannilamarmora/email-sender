package com.giovannilamarmora.dispatch.emailsender.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Paths;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = "io.github.giovannilamarmora.utils")
@Configuration
@EnableScheduling
@EnableCaching
@OpenAPIDefinition(
    info = @Info(title = "Email Sender Swagger", version = "1.0.0"),
    servers = {
      @Server(
          url = "https://email-sender.giovannilamarmora.com",
          description = "Default Server URL"),
      @Server(url = "http://localhost:8080", description = "Local Server URL")
    })
public class OpenAPIConfig {

  @Bean
  public OpenApiCustomizer applyStandardOpenAPIModifications() {
    return openApi -> {
      Paths paths = new Paths();
      openApi.getPaths().entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .forEach(
              entry ->
                  paths.addPathItem(
                      entry.getKey(),
                      io.github.giovannilamarmora.utils.config.OpenAPIConfig
                          .addJSONExamplesOnResource(entry.getValue(), OpenAPIConfig.class)));
      openApi.setPaths(paths);
    };
  }
}
