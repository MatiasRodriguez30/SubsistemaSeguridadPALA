package com.example.subsistemaSeguridad.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final boolean mailEnabled;
    private final String mailProvider;
    private final String username;
    private final String password;
    private final String from;
    private final String fromName;
    private final String resendApiKey;
    private final String resendApiUrl;
    private final String resendFrom;
    private final long expirationMinutes;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            ObjectMapper objectMapper,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.provider:smtp}") String mailProvider,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${app.mail.from:}") String from,
            @Value("${app.mail.from-name:Subsistema Seguridad PALA}") String fromName,
            @Value("${app.mail.resend.api-key:}") String resendApiKey,
            @Value("${app.mail.resend.api-url:https://api.resend.com/emails}") String resendApiUrl,
            @Value("${app.mail.resend.from:}") String resendFrom,
            @Value("${app.security.verification.expiration-minutes:15}") long expirationMinutes
    ) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mailEnabled = mailEnabled;
        this.mailProvider = mailProvider;
        this.username = username;
        this.password = password;
        this.from = from;
        this.fromName = fromName;
        this.resendApiKey = resendApiKey;
        this.resendApiUrl = resendApiUrl;
        this.resendFrom = resendFrom;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    @Async("mailTaskExecutor")
    public void enviarCodigoVerificacion(String destinatario, String codigo) {
        enviarCodigo(
                destinatario,
                "Codigo de verificacion",
                "Verifica tu correo",
                "Usa este codigo para verificar tu cuenta en el Subsistema de Seguridad.",
                codigo
        );
    }

    @Override
    @Async("mailTaskExecutor")
    public void enviarCodigoRecuperacionPassword(String destinatario, String codigo) {
        enviarCodigo(
                destinatario,
                "Codigo para recuperar contrasena",
                "Recupera tu contrasena",
                "Usa este codigo para cambiar tu contrasena.",
                codigo
        );
    }

    private void enviarCodigo(String destinatario, String asunto, String titulo, String descripcion, String codigo) {
        if (!mailEnabled) {
            logger.info("Mail deshabilitado. Codigo para {}: {}", destinatario, codigo);
            return;
        }

        validarConfiguracion();

        if ("resend".equalsIgnoreCase(mailProvider)) {
            enviarPorResend(destinatario, asunto, titulo, descripcion, codigo);
            return;
        }

        enviarPorSmtp(destinatario, asunto, titulo, descripcion, codigo);
    }

    private void enviarPorSmtp(String destinatario, String asunto, String titulo, String descripcion, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(resolveFrom());
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(buildPlainText(descripcion, codigo), buildHtml(titulo, descripcion, codigo));
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException ex) {
            logger.error("Error enviando correo de seguridad a {}", destinatario, ex);
            throw new MailDeliveryException("No se pudo enviar el correo de seguridad. Revisa la configuracion SMTP.", ex);
        }
    }

    private void enviarPorResend(String destinatario, String asunto, String titulo, String descripcion, String codigo) {
        try {
            Map<String, Object> payload = Map.of(
                    "from", resolveResendFrom(),
                    "to", List.of(destinatario),
                    "subject", asunto,
                    "html", buildHtml(titulo, descripcion, codigo),
                    "text", buildPlainText(descripcion, codigo)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(resendApiUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new MailDeliveryException(
                        "No se pudo enviar el correo de seguridad por Resend. Estado "
                                + response.statusCode() + ": " + response.body(),
                        null
                );
            }
        } catch (JsonProcessingException ex) {
            throw new MailDeliveryException("No se pudo preparar el correo de seguridad para Resend.", ex);
        } catch (IOException ex) {
            throw new MailDeliveryException("No se pudo enviar el correo de seguridad por Resend.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new MailDeliveryException("No se pudo enviar el correo de seguridad por Resend.", ex);
        }
    }

    private InternetAddress resolveFrom() throws AddressException, UnsupportedEncodingException {
        String address = isBlank(from) ? username : from;
        return isBlank(fromName) ? new InternetAddress(address) : new InternetAddress(address, fromName);
    }

    private String resolveResendFrom() {
        if (!isBlank(resendFrom)) {
            return resendFrom;
        }

        String address = isBlank(from) ? username : from;
        if (isBlank(address)) {
            return "";
        }
        return isBlank(fromName) ? address : fromName + " <" + address + ">";
    }

    private String buildPlainText(String descripcion, String codigo) {
        return descripcion + "\n\nCodigo: " + codigo + "\n\nEl codigo vence en " + expirationMinutes
                + " minutos. Si no solicitaste esta operacion, ignora este correo.";
    }

    private String buildHtml(String titulo, String descripcion, String codigo) {
        return """
                <!doctype html>
                <html>
                  <body style="margin:0;background:#f5f7fb;font-family:Arial,sans-serif;color:#111827;">
                    <div style="max-width:520px;margin:0 auto;padding:32px 18px;">
                      <div style="background:#ffffff;border:1px solid #dbe3ef;border-radius:14px;padding:28px;">
                        <p style="margin:0 0 10px;font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:#0b4aa2;font-weight:700;">Subsistema Seguridad PALA</p>
                        <h1 style="margin:0 0 12px;font-size:24px;line-height:1.2;color:#111827;">%s</h1>
                        <p style="margin:0 0 22px;font-size:15px;line-height:1.6;color:#475569;">%s</p>
                        <div style="font-size:32px;letter-spacing:.35em;font-weight:800;color:#111827;background:#eef4ff;border:1px solid #c9daf8;border-radius:12px;padding:16px 18px;text-align:center;">%s</div>
                        <p style="margin:22px 0 0;font-size:13px;line-height:1.6;color:#64748b;">El codigo vence en %s minutos. Si no solicitaste esta operacion, ignora este correo.</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(titulo, descripcion, codigo, expirationMinutes);
    }

    private void validarConfiguracion() {
        if ("resend".equalsIgnoreCase(mailProvider)) {
            if (isBlank(resendApiKey)) {
                throw new MailDeliveryException("MAIL_PROVIDER=resend requiere RESEND_API_KEY.", null);
            }
            if (isBlank(resolveResendFrom())) {
                throw new MailDeliveryException("MAIL_PROVIDER=resend requiere RESEND_FROM o MAIL_FROM.", null);
            }
            return;
        }

        if (isBlank(username) || isBlank(password)) {
            throw new MailDeliveryException(
                    "MAIL_ENABLED=true requiere GMAIL_USERNAME y GMAIL_APP_PASSWORD.",
                    null
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
