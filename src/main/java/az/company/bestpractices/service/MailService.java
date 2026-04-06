package az.company.bestpractices.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

     public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment(fileName, new ByteArrayResource(attachment));

            mailSender.send(message);
            log.info("Qoşmalı email uğurla göndərildi: {}", to);
        } catch (MessagingException e) {
            log.error("Qoşmalı email göndərərkən xəta: {}", e.getMessage());
        }
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Sadə email uğurla göndərildi: {}", to);
        } catch (Exception e) {
            log.error("Sadə email göndərərkən xəta: {}", e.getMessage());
        }
    }
}