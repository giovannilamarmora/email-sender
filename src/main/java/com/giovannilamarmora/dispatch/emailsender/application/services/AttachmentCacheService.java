package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.application.mapper.EmailMapper;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Logged
public class AttachmentCacheService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  public static Map<String, AttachmentDTO> attachmentMap = new HashMap<>();
  @Autowired private EmailMapper mapper;

  @Cacheable(cacheNames = "attachment")
  @LogInterceptor(type = LogTimeTracker.ActionType.APP_SERVICE)
  public ResponseEntity<AttachmentDTO> saveAttachmentDto(MultipartFile file) throws UtilsException {
    AttachmentDTO attachment;
    if (file == null || file.isEmpty()) {
      LOG.error("The file you have been passed is invalid");
      throw new UtilsException(
          EmailException.ERR_MAIL_SEND_003, "The file you have been passed is invalid");
    }
    try {
      attachment = mapper.fromPartToDto(file);
    } catch (UtilsException e) {
      LOG.error("Error on mapping attachment");
      throw new UtilsException(EmailException.ERR_MAIL_SEND_003, e.getMessage());
    }
    attachmentMap.put(attachment.getFileName(), attachment);
    return ResponseEntity.ok(attachment);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_SERVICE)
  public void removeAttachment(String filename) {
    attachmentMap.remove(filename);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_SERVICE)
  public AttachmentDTO getAttachment(String filename) {
    return attachmentMap.get(filename);
  }
}
