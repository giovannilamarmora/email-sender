package com.giovannilamarmora.dispatch.emailsender.api.strapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giovannilamarmora.dispatch.emailsender.api.strapi.dto.StrapiEmailTemplate;
import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.MapperUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

@Service
public class StrapiService {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());
  @Autowired private StrapiClient strapiClient;

  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Mono<StrapiEmailTemplate> getTemplateById(String templateId, String locale) {
    return strapiClient
        .getTemplateById(templateId, locale)
        .flatMap(
            responseEntity -> {
              if (ObjectUtils.isEmpty(responseEntity.getBody())
                  || ObjectUtils.isEmpty(responseEntity.getBody().getData())) {
                LOG.error("Strapi returned an empty object");
                throw new StrapiException(
                    ExceptionMap.ERR_MAIL_SEND_002,
                    "Template with templateId (" + templateId + ") not found");
              }
              ObjectMapper mapper = MapperUtils.mapper().failOnEmptyBean().build();
              return Mono.just(
                  mapper.convertValue(
                      responseEntity.getBody().getData().getFirst().getAttributes(),
                      StrapiEmailTemplate.class));
            });
  }
}
