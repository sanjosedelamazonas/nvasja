package org.sanjose.helper;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.sys.PropiedadService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class MailerSender {

    private Map<String, TransportStrategy> authTypes = new HashMap<>();

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
        ConfigurationUtil.setPropiedadRepo(propiedadService.getPropiedadRep());
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
        log.info("Mailer Sender started and configured to send from " + ConfigurationUtil.get("MAIL_SMTP_SERVER"));
    }


    public CompletableFuture<Void> sendEmail(Email email) {
        return this.mailer.sendMail(email);
    }

    public CompletableFuture<Void> sendInvitation(String to) {
        Email email = EmailBuilder.startingBlank()
                .to(to)
                .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                .withSubject("Invitacion a los servicios del Sistema de Gestion de Caja y bancos")
                .withPlainText("Hola!\nSu reporte adjuntado.\nSaludos\nVASJA")
                //.withAttachment("mypdf.pdf", pdfByteArray, "application/pdf")
                .buildEmail();
        return this.mailer.sendMail(email);
    }

    public CompletableFuture<Void> sendPasswordResetLink(String to, String link) {
        Email email = EmailBuilder.startingBlank()
                .to(to)
                .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                .withSubject("Reset clave")
                .withHTMLText("<p>Hello,</p>"
                        + "<p>You have requested to reset your password.</p>"
                        + "<p>Click the link below to change your password:</p>"
                        + "<p><a href=\"" + link + "\">Change my password</a></p>"
                        + "<br>"
                        + "<p>Ignore this email if you do remember your password, "
                        + "or you have not made the request.</p>")
                .buildEmail();
        return this.mailer.sendMail(email);
    }


    @PreDestroy
    public void close() {
        log.info("Closing Mailer Sender");
        mailer.shutdownConnectionPool();
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

//        //MailerSender mailerSender = new MailerSender();
//
//        CompletableFuture<Void> result = mailerSender.sendEmail(email);
//        try {
//
//        } catch (MailException me) {
//            System.out.println("Something went wrong");
//            System.out.println(me.getLocalizedMessage());
//        } catch (RuntimeException e) {
//            System.out.println("Got error");
//            System.out.println(e);
//        }
//
//        CompletableFuture<String> cf1 =
//                result.handle((Void, ex) -> {
//                    if (ex != null) {
//                        return "Recovered from \"" + ex.getMessage() + "\"";
//                    } else {
//                        return "OK";
//                    }
//                });
//
//        try {
//            cf1.join();
//            mailerSender.mailer.shutdownConnectionPool();
//            System.out.println(cf1.get());
//        } catch (CompletionException e) {
//            System.out.println("Error: " + e.getMessage());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//

        //mailerSender.mailer.
//        mailerSender.mailer.
//        try {
//            //mailerSender.mailer.shutdownConnectionPool();
//        } catch (MailException me) {
//            System.out.println("Something went wrong when closing");
//            System.out.println(me.getLocalizedMessage());
//        } catch (RuntimeException e) {
//            System.out.println("Got error closing");
//            System.out.println(e);
//        }
    }
}
