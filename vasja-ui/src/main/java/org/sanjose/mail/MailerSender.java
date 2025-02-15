package org.sanjose.mail;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.apache.commons.text.StringSubstitutor;
import org.sanjose.helper.ChunkList;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.sys.PropiedadService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class MailerSender {

    private Map<String, TransportStrategy> authTypes = new HashMap<>();
    private Map<String, String> emailTemplates = new HashMap<>();
    private Map<String, String> emailTemplatesLoaded = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(MailerSender.class);

    private Mailer mailer;


    @Autowired
    private PropiedadService propiedadService;
    //private VsjPropiedadRep propRepo;

    public MailerSender(PropiedadService propiedadService) {
        this.authTypes.put("OAUTH2", TransportStrategy.SMTP_OAUTH2);
        this.authTypes.put("SSL", TransportStrategy.SMTPS);
        this.authTypes.put("NO", TransportStrategy.SMTP);
        this.authTypes.put("TLS", TransportStrategy.SMTP_TLS);
        this.emailTemplates.put("INVITACION", "email_invitacion.html");
        this.emailTemplates.put("RESET_PASS", "email_reset_pass.html");
        this.emailTemplates.put("REPORTE_TERCERO", "email_reporte_tercero.html");
        ConfigurationUtil.setPropiedadRepo(propiedadService.getPropiedadRep());
        this.mailer = MailerBuilder
                .withSMTPServer(ConfigurationUtil.get("MAIL_SMTP_SERVER"),
                        Integer.parseInt(ConfigurationUtil.get("MAIL_SMTP_SERVER_PORT")),
                        ConfigurationUtil.get("MAIL_SMTP_USER"),
                        ConfigurationUtil.get("MAIL_SMTP_PASS"))
                .withTransportStrategy(authTypes.get(ConfigurationUtil.get("MAIL_SMTP_AUTH")))
                .withSessionTimeout(20 * 1000)
                .clearEmailValidator()
                .withDebugLogging(ConfigurationUtil.is("MAIL_DEBUG"))
                .async()
                .buildMailer();
        try {
            for (String et : emailTemplates.keySet()) {
                Resource resource = new ClassPathResource(emailTemplates.get(et));
                InputStream is = resource.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length; (length = is.read(buffer)) != -1; ) {
                    result.write(buffer, 0, length);
                }
                emailTemplatesLoaded.put(et, result.toString("UTF-8"));
            }
        } catch (IOException e) {
            log.error("Could not load email templates from html files");
            e.printStackTrace();
        }
        log.info("Mailer Sender started and configured to send from " + ConfigurationUtil.get("MAIL_SMTP_SERVER"));
    }

    public CompletableFuture<Void> sendEmail(Email email) {
        return this.mailer.sendMail(email);
    }


    public List<EmailStatus> sendEmails(List<EmailDescription> emails) throws InterruptedException {
        List<List<EmailDescription>> chunkedEmailList = new ChunkList().chunkList(emails, Integer.valueOf(ConfigurationUtil.get("EMAILS_SENDING_BATCH_SIZE")));
        List<EmailStatus> emailStatuses = new ArrayList<>();

        for (List<EmailDescription> chunk : chunkedEmailList) {
            for (EmailDescription ed : chunk) {
                emailStatuses.add(new EmailStatus(ed.getTo(), ed.getUsuario(), this.mailer.sendMail(ed.getEmail())));
            }
            Thread.sleep(Integer.valueOf(ConfigurationUtil.get("EMAILS_SENDING_DELAY_MS")));
        }
        return emailStatuses;
    }

    public CompletableFuture<Void> sendInvitation(String to) {
        Email email = EmailBuilder.startingBlank()
                .to(to)
                .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                .withSubject("Invitacion a los servicios del Sistema de Gestion de Caja y bancos")
                .withHTMLText(emailTemplatesLoaded.get("INVITACION"))
                //.withPlainText("Hola!\nSu reporte adjuntado.\nSaludos\nVASJA")
                //.withAttachment("mypdf.pdf", pdfByteArray, "application/pdf")
                .buildEmail();
        return this.mailer.sendMail(email);
    }

    public CompletableFuture<Void> sendPasswordResetLink(String to, String link) {
        Map<String, String> toReplace = new HashMap<>();
        toReplace.put("LINK", link);

        Email email = EmailBuilder.startingBlank()
                .to(to)
                .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                .withSubject("Reset clave")
                .withHTMLText(genFromTemplate("RESET_PASS", toReplace))
                .buildEmail();
        return this.mailer.sendMail(email);
    }


    public String genFromTemplate(String templName, Map<String, String> replaceMap) {
        String emailContent = emailTemplatesLoaded.get(templName);
        StringSubstitutor sub = new StringSubstitutor(replaceMap);
        return sub.replace(emailContent);
    }

    @PreDestroy
    public void close() {
        log.info("Closing Mailer Sender");
        mailer.shutdownConnectionPool();
    }
}
