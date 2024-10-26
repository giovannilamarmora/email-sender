package com.giovannilamarmora.dispatch.emailsender.application;

import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Logged
public class Dispatcher {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());

  @Autowired public JavaMailSender emailSender;

  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public void sendMessage(SimpleMailMessage simpleMailMessage, MimeMessage mimeMessage)
      throws EmailException {
    if (simpleMailMessage != null) {
      LOG.info("Sending email with Subject {}", simpleMailMessage.getSubject());
      try {
        emailSender.send(simpleMailMessage);
      } catch (MailException exception) {
        LOG.error("An error occurred during send email, message {}", exception.getMessage());
        throw new EmailException(
            ExceptionMap.ERR_MAIL_SEND_002,
            "An error occurred during send email",
            exception.getMessage());
      }
    } else {
      try {
        LOG.info("Sending email with Subject {}", mimeMessage.getSubject());
      } catch (MessagingException e) {
        throw new EmailException(
            ExceptionMap.ERR_MAIL_SEND_002, "An error occurred during send email", e.getMessage());
      }
      try {
        emailSender.send(mimeMessage);
      } catch (MailException exception) {
        LOG.error("An error occurred during send email, message {}", exception.getMessage());
        throw new EmailException(
            ExceptionMap.ERR_MAIL_SEND_002,
            "An error occurred during send email",
            exception.getMessage());
      }
    }
  }
}
