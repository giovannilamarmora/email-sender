package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.mapper.EmailMapper;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.giovannilamarmora.dispatch.emailsender.exception.ExceptionMap;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@Service
@Logged
public class AttachmentCacheService implements IAttachmentCacheService {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());
  public static Map<String, AttachmentDTO> attachmentMap = new HashMap<>();
  @Autowired private EmailMapper mapper;

  @Deprecated
  @Cacheable(cacheNames = "attachment")
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public ResponseEntity<AttachmentDTO> saveAttachmentDtoOld(MultipartFile file)
      throws EmailException {
    AttachmentDTO attachment;
    if (file == null || file.isEmpty()) {
      LOG.error("The file you have been passed is invalid");
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_003,
          "The file you have been passed is invalid",
          ExceptionMap.ERR_MAIL_SEND_003.getMessage());
    }
    try {
      attachment = mapper.fromPartToDto(file);
    } catch (UtilsException e) {
      LOG.error("Error on mapping attachment");
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_003, "Error on mapping attachment", e.getMessage());
    }
    attachmentMap.put(attachment.getFileName(), attachment);
    return ResponseEntity.ok(attachment);
  }

  @Override
  @Cacheable(cacheNames = "attachment")
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public Flux<AttachmentDTO> saveAttachmentDto(Flux<FilePart> file) {
    Flux<AttachmentDTO> attachment;
    if (file == null) {
      LOG.error("The file you have been passed is invalid");
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_003,
          "The file you have been passed is invalid",
          ExceptionMap.ERR_MAIL_SEND_003.getMessage());
    }

    try {
      attachment = EmailMapper.fromPartToDto(file);
    } catch (UtilsException e) {
      LOG.error("Error on mapping attachment");
      throw new EmailException(
          ExceptionMap.ERR_MAIL_SEND_003, "Error on mapping attachment", e.getMessage());
    }
    return attachment.map(
        attachmentDTO -> {
          attachmentMap.put(attachmentDTO.getFileName(), attachmentDTO);
          return attachmentDTO;
        });
  }

  @Override
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public void removeAttachment(String filename) {
    attachmentMap.remove(filename);
  }

  @Override
  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public AttachmentDTO getAttachment(String filename) {
    return attachmentMap.get(filename);
  }
}
