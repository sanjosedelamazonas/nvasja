package org.sanjose.helper;

import org.sanjose.util.ConfigurationUtil;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class MailerSender {

    private Map<String, TransportStrategy> authTypes = new HashMap<>();

    private Mailer mailer;

    public MailerSender() {
        this.authTypes.put("OAUTH2", TransportStrategy.SMTP_OAUTH2);
        this.authTypes.put("SSL", TransportStrategy.SMTPS);
        this.authTypes.put("NO", TransportStrategy.SMTP);
        this.authTypes.put("TLS", TransportStrategy.SMTP_TLS);
        this.mailer = MailerBuilder

                .withSMTPServer(ConfigurationUtil.get("MAIL_SMTP_SERVER"),
                        Integer.parseInt(ConfigurationUtil.get("MAIL_SMTP_SERVER_PORT")),
                        ConfigurationUtil.get("MAIL_SMTP_USER"),
                        ConfigurationUtil.get("MAIL_SMTP_PASS"))
                .withTransportStrategy(authTypes.get(ConfigurationUtil.get("MAIL_SMTP_AUTH")))
                .withSessionTimeout(10 * 1000)
                .clearEmailValidator()
                .withDebugLogging(true)
                .async()
                .buildMailer();
    }


    public void sendEmail(Email email) {
        this.mailer.sendMail(email);
    }

    public static void main(String[] args) throws IOException {
        Path pdfPath = Paths.get("/pol/ReporteCajaDiario_20230316_192243.pdf");
        byte[] pdfByteArray = Files.readAllBytes(pdfPath);

        Email email = EmailBuilder.startingBlank()
                .to("Pol", "pawel.rubach@gmail.com")
                .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                .withSubject("VASJA Reporte")
                .withPlainText("Hola!\nSu reporte adjuntado.\nSaludos\nVASJA")
                //.withAttachment("mypdf.pdf", pdfByteArray, "application/pdf")
                .buildEmail();

        new MailerSender().sendEmail(email);
    }
}
