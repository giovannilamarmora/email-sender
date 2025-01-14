package com.giovannilamarmora.dispatch.emailsender.application;

import com.giovannilamarmora.dispatch.emailsender.exception.EmailException;
import com.giovannilamarmora.dispatch.emailsender.exception.config.ExceptionMap;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.Utilities;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Logged
public class Dispatcher {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());

  @Autowired public JavaMailSender emailSender;

  @Value(value = "${spring.mail.sender-emails}")
  private List<String> senderEmails;

  @LogInterceptor(type = LogTimeTracker.ActionType.SERVICE)
  public void sendMessage(SimpleMailMessage simpleMailMessage, MimeMessage mimeMessage) {
    if (simpleMailMessage != null) {
      validateEmails(
          Utilities.isNullOrEmpty(simpleMailMessage.getFrom())
              ? senderEmails.getFirst()
              : simpleMailMessage.getFrom());
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
        validateEmails(
            Utilities.isNullOrEmpty(mimeMessage.getFrom())
                    || Utilities.isNullOrEmpty(mimeMessage.getFrom()[0])
                ? senderEmails.getFirst()
                : mimeMessage.getFrom()[0].toString());
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

  private void validateEmails(String email_from) {
    if (!senderEmails.contains(email_from)) {
      LOG.error(
          "The email {} not match the sender email configuration {}", email_from, senderEmails);
      throw new EmailException(ExceptionMap.ERR_VALID_MAIL_001, "Email Sender not match");
    }
  }
}
