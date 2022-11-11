package com.giovannilamarmora.dispatch.emailsender.application;

import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Logged
public class Dispatcher {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired public JavaMailSender emailSender;

  @LogInterceptor(type = LogTimeTracker.ActionType.APP_SERVICE)
  public void sendMessage(SimpleMailMessage simpleMailMessage, MimeMessage mimeMessage)
      throws UtilsException {
    if (simpleMailMessage != null) {
      LOG.info("Sending email with Subject {}", simpleMailMessage.getSubject());
      try {
        emailSender.send(simpleMailMessage);
      } catch (MailException exception) {
        LOG.error("An error occurred during send email, message {}", exception.getMessage());
        throw new UtilsException(EmailException.ERR_MAIL_SEND_002, exception.getMessage());
      }
    } else {
      try {
        LOG.info("Sending email with Subject {}", mimeMessage.getSubject());
      } catch (MessagingException e) {
        throw new UtilsException(EmailException.ERR_MAIL_SEND_002, e.getMessage());
      }
      try {
        emailSender.send(mimeMessage);
      } catch (MailException exception) {
        LOG.error("An error occurred during send email, message {}", exception.getMessage());
        throw new UtilsException(EmailException.ERR_MAIL_SEND_002, exception.getMessage());
      }
    }
  }
}
