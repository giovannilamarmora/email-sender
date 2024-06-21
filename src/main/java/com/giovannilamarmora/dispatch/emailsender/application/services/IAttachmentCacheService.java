package com.giovannilamarmora.dispatch.emailsender.application.services;

import com.giovannilamarmora.dispatch.emailsender.application.dto.AttachmentDTO;
import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

public interface IAttachmentCacheService {

  ResponseEntity<AttachmentDTO> saveAttachmentDtoOld(MultipartFile file) throws EmailException;

  Flux<AttachmentDTO> saveAttachmentDto(Flux<FilePart> file) throws EmailException;

  void removeAttachment(String filename);

  AttachmentDTO getAttachment(String filename);
}
