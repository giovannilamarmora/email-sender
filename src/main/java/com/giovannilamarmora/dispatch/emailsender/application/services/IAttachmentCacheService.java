package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IAttachmentCacheService {

  ResponseEntity<AttachmentDTO> saveAttachmentDto(MultipartFile file) throws UtilsException;

  void removeAttachment(String filename);

  AttachmentDTO getAttachment(String filename);
}
