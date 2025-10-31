package com.group_three.food_ordering.notifications;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * Componente dedicado exclusivamente al envío asíncrono de correos.
 * Al estar en una clase separada, Spring puede crear correctamente el proxy
 * para manejar @Async.
 */
@Component
public class AsyncEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(AsyncEmailSender.class);
    private static final String ENCODING = "utf-8";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public AsyncEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo de texto plano de forma asíncrona.
     */
    @Async
    public void sendSimpleMail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email simple enviado exitosamente a {}", to);
        } catch (Exception e) {
            logger.error("Fallo al enviar email simple a {}: {}", to, e.getMessage());
        }
    }

    /**
     * Envía un correo en formato HTML de forma asíncrona.
     * Centraliza el manejo de excepciones de MimeMessage.
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, ENCODING); // false = no multipart

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = indica que el contenido es HTML

            mailSender.send(message);
            logger.info("Email HTML enviado exitosamente a {}", to);

        } catch (MessagingException e) {
            logger.error("Fallo al enviar email HTML a {} con asunto '{}': {}", to, subject, e.getMessage());
        }
    }

    /**
     * Envía un correo con adjuntos de forma asíncrona.
     */
    @Async
    public void sendEmailWithAttachment(String to, String subject, String body, File attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = habilita soporte multipart (necesario para adjuntos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(body, false); // false = texto plano, no HTML

            if (attachment != null && attachment.exists() && attachment.isFile()) {
                FileSystemResource file = new FileSystemResource(attachment);
                helper.addAttachment(StringUtils.cleanPath(attachment.getName()), file);
            }

            mailSender.send(message);
            logger.info("Email con adjunto enviado exitosamente a {}", to);
        } catch (MessagingException e) {
            logger.error("Fallo al enviar email con adjunto a {}: {}", to, e.getMessage());
        }
    }
}