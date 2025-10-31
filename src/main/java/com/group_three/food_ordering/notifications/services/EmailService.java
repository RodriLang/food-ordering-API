package com.group_three.food_ordering.notifications.services;

import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.notifications.AsyncEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Year;

/**
 * Servicio principal para la lógica de negocio de correos.
 * Prepara el contenido del correo y delega el envío real
 * al componente AsyncEmailSender para no bloquear el hilo principal.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final AsyncEmailSender asyncSender;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Inyectamos la URL del frontend (Angular) desde application.properties
    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(AsyncEmailSender asyncSender, JavaMailSender mailSender) {
        this.asyncSender = asyncSender;
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo de prueba simple de forma SÍNCRONA.
     * Útil para verificar la configuración del SMTP.
     */
    public void sendTestEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail);
            message.setSubject("Test Email (Sync)");
            message.setText("This is a synchronous test email from " + fromEmail);

            mailSender.send(message);
            logger.info("Email de prueba SÍNCRONO enviado exitosamente a {}", to);
        } catch (Exception e) {
            logger.error("Fallo al enviar email de prueba síncrono a {}: {}", to, e.getMessage());
        }
    }

    /**
     * Prepara y solicita el envío asíncrono de un correo de texto simple.
     */
    public void sendSimpleMail(String to, String subject, String body) {
        asyncSender.sendSimpleMail(to, subject, body);
    }

    /**
     * Prepara y solicita el envío asíncrono de un correo de pre-registro.
     */
    public void sendPreRegisterEmail(String toEmail, String preRegisterToken) {
        String subject = "Pre-registration Token";
        String emailContent = "<p>Enter your email and the token and complete your registration details.</p>"
                + "<p>The token is valid for 48 hours.</p>"
                + "<p>Token: <strong>" + preRegisterToken + "</strong></p>";

        asyncSender.sendHtmlEmail(toEmail, subject, emailContent);
    }

    /**
     * Prepara y solicita el envío asíncrono de un correo de recuperación.
     */
    public void sendRecoveryEmail(String toEmail, String recoveryToken) {
        String subject = "Recovery Token";
        // Asegúrate de que este link apunte a tu frontend
        String recoveryLink = frontendUrl + "/reset-password?token=" + recoveryToken;
        String emailContent = "<p>Go to the following link and enter the token, then change your password:</p>"
                + "<p><a href=\"" + recoveryLink + "\">" + recoveryLink + "</a></p>"
                + "<p>Token: " + recoveryToken + "</p>";

        asyncSender.sendHtmlEmail(toEmail, subject, emailContent);
    }

    /**
     * Prepara y solicita el envío asíncrono de un código de verificación.
     */
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "Código de verificación";
        String emailContent = "Tu código de verificación es: <strong>" + verificationCode + "</strong>";

        asyncSender.sendHtmlEmail(toEmail, subject, emailContent);
    }

    /**
     * Prepara y solicita el envío asíncrono de un correo con adjunto.
     */
    public void sendEmailWithAttachment(String to, String subject, String body, File attachment) {
        asyncSender.sendEmailWithAttachment(to, subject, body, attachment);
    }

    /**
     * MÉTODO CORREGIDO:
     * Prepara y envía la invitación de empleo usando la plantilla HTML.
     */
    public void sendEmploymentInvitation(Employment employment) {

        User invitedUser = employment.getUser();

        logger.debug("[EmailService] Sending employment invitation to user {} with role {}", invitedUser.getEmail(), employment.getRole());

        String subject = "¡Has recibido una invitación de trabajo!";

        String htmlContent = buildHtmlForInvitation(
                employment.getUser(),
                employment.getFoodVenue(),
                employment.getRole().name(),
                employment.getInvitationToken()
        );

        asyncSender.sendHtmlEmail(invitedUser.getEmail(), subject, htmlContent);
    }


    // ===================================================================================
    // MÉTODOS PRIVADOS PARA CONSTRUIR LA PLANTILLA DE EMAIL
    // (Movidos aquí desde la otra clase)
    // ===================================================================================

    /**
     * Prepara los datos para la plantilla.
     */
    private String buildHtmlForInvitation(User invitedUser, FoodVenue foodVenue, String roleName, String invitationToken) {

        String venueLogoUrl = (foodVenue.getVenueStyle()!=null)? foodVenue.getVenueStyle().getLogoUrl() : null;
        String invitationUrl = frontendUrl + "/invitations/respond?token=" + invitationToken;

        return buildInvitationEmailTemplate(invitedUser.getName(), foodVenue.getName(), roleName, invitationUrl, venueLogoUrl);
    }

    /**
     * Genera el template HTML con los datos ya procesados.
     */
    private String buildInvitationEmailTemplate(String userName, String venueName, String roleName,
                                                String invitationUrl, String venueLogoUrl) {

        String friendlyRoleName = roleName.replace("ROLE_", "");
        int currentYear = Year.now().getValue();

        String logoHtml = "";
        if (venueLogoUrl != null && !venueLogoUrl.isBlank()) {
            logoHtml = """
                       <img src='%s' alt='Logo de %s'
                            style='max-width: 150px; height: auto; display: block; margin: 0 auto 20px auto; border-radius: 5px;'>
                     """.formatted(venueLogoUrl, venueName);
        }

        return """
           <!DOCTYPE html>
           <html lang='es'>
           <head>
           <meta charset='UTF-8'>
           <style>
             body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; line-height: 1.6; }
             .container { width: 90%%; max-width: 600px; margin: 20px auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }
             .header { background-color: #4A90E2; color: #ffffff; padding: 30px 20px; text-align: center; }
             .header h1 { margin: 0; font-size: 24px; }
             .content { padding: 40px; }
             .content p { font-size: 16px; color: #333; }
             .highlight { background-color: #f4f4f4; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; }
             .highlight strong { font-size: 18px; color: #000; }
             .buttons { text-align: center; margin-top: 30px; }
             .btn { display: inline-block; padding: 12px 25px; margin: 10px; border-radius: 5px; text-decoration: none; font-weight: bold; }
             .btn-primary { background-color: #4A90E2; color: #ffffff; }
             .footer { background-color: #f9f9f9; color: #888; padding: 20px; text-align: center; font-size: 12px; }
           </style>
           </head>
           <body>
             <div class='container'>
               <div class='header'>
                 %s <!-- Logo -->
                 <h1>Invitación de Empleo</h1>
               </div>
               <div class='content'>
                 <p>Hola, <strong>%s</strong>,</p>
                 <p>Has sido invitado a unirte al equipo de <strong>%s</strong>.</p>
                 <p>Se te ofrece el siguiente rol:</p>
                 <div class='highlight'>
                   <strong>%s</strong>
                 </div>
                 <p>Por favor, responde a esta oferta haciendo clic en el botón a continuación. Esta invitación expirará en 72 horas.</p>
                 <div class='buttons'>
                   <a href='%s' class='btn btn-primary'>Responder a la Invitación</a>
                 </div>
               </div>
               <div class='footer'>
                 <p>&copy; %d %s. Todos los derechos reservados.</p>
               </div>
             </div>
           </body>
           </html>
        """.formatted(
                logoHtml,
                userName,
                venueName,
                friendlyRoleName,
                invitationUrl,
                currentYear,
                venueName
        );
    }
}
