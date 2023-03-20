package org.sanjose.views.terceros;

import com.vaadin.data.Property;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import jakarta.activation.DataSource;
import net.sf.jasperreports.engine.JRException;
import org.sanjose.MainUI;
import org.sanjose.helper.CustomReport;
import org.sanjose.helper.EmailAttachment;
import org.sanjose.mail.EmailDescription;
import org.sanjose.mail.EmailStatus;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.*;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.email.EmailBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**          A
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class EnviarDiarioTercerosView extends EnviarDiarioTercerosUI implements Viewing {

    public static final String VIEW_NAME = "Enviar Diarios de Terceros";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(EnviarDiarioTercerosView.class);
    private PersistanceService service;
    private Map<String,CustomReport> customReportMap = new TreeMap<>();
    private StringBuilder logRes;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    public EnviarDiarioTercerosView(PersistanceService service) {
        this.service = service;
        setSizeFull();
        addStyleName("crud-view");
    }

    @Override
    public void init() {

//        ReportHelper.get().loadCustomReports();
//        List<CustomReport> customReportList = ReportHelper.get().getCustomReports();
//        Map<String,String> customReportNameMap  = new TreeMap<>();
//        for (CustomReport customReport : customReportList) {
//            customReportNameMap.put(customReport.getName(),customReport.getName());
//            customReportMap.put(customReport.getName(),customReport);
//        }
//        DataFilterUtil.bindFixedStringValComboBox(selReporte, "selReporte", "Sellecione Reporte", customReportNameMap);
//        selReporte.addValueChangeListener(this::setReporteParameters);
        List<ScpDestino> terceros = DataUtil.loadDestinos(service, true);

        Map<String, String> usuariosMap = new HashMap<>();
        terceros.forEach(trc -> {
            if (!GenUtil.strNullOrEmpty(trc.getTxtUsuario())) {
                MsgUsuario usuario = service.getMsgUsuarioRep().findByTxtUsuario(trc.getTxtUsuario());
                if (usuario!=null)
                    usuariosMap.put(trc.getTxtUsuario(), trc.getTxtUsuario() + " (" + usuario.getTxtNombre() + ")");
            }
        });
        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", DataUtil.loadDestinos(service, true), "Sel Tercero",
                "txtNombre");
        selTercero.addValueChangeListener(this::setTerceroLogic);

        DataFilterUtil.bindFixedStringValComboBox(selUsuario, "selUsuario", "Seleccione Usuario", usuariosMap);
        selUsuario.addValueChangeListener(this::setUsuarioLogic);
        checkTodos.addValueChangeListener(this::setTodosLogic);
        txtUsuariosList.addValueChangeListener(this::setUsuarioListLogic);
        fechaInicial.addValueChangeListener(val -> fechaFinal.setValue(GenUtil.getEndOfMonth(fechaInicial.getValue())));
        fechaInicial.setValue(filterInitialDate);

        btnEnviar.addClickListener( e -> doEnviar(prepareListOfTerceros()));
        btnImprimir.addClickListener( e -> doImprimir(selTercero.getValue().toString()));
        btnClear.addClickListener( e -> {
            logRes = new StringBuilder();
            txtLog.setValue("");
        });

        btnEnviar.setEnabled(true);
        logRes = new StringBuilder();
    }

    private void doEnviar(Map<MsgUsuario, List<ScpDestino>> trcMap) {

        StringBuilder sb = new StringBuilder();
        List<String> codigosTerc = new ArrayList<>();
        //tercerosList.forEach(trc -> codigosTerc.add(trc.getCodDestino()));
        //tercerosList.forEach(trc -> sb.append(trc.getCodDestino()).append(", "));
        //log.info("Got list of terceros to send: " + sb.toString());
        List<EmailStatus> sendResults;
        List<String> usuariosErrorList = new ArrayList<>();
        try {
            sendResults = ((MainUI) UI.getCurrent()).getMailerSender().sendEmails(generateEmails(trcMap, fechaInicial.getValue(), fechaFinal.getValue()));
            List<CompletableFuture<String>> sendErrorsList = new ArrayList<>();
            for (EmailStatus es : sendResults) {
                CompletableFuture<String> sendErrors =
                        es.getStatus().handle((String, ex) -> {
                            if (ex != null) {
                                //logRes.append("Problema al enviar mensaje a :"+ es.getTo() + "\n" + ex.getMessage() + "\n");
                                logRes.append(es.getTo() + ": Problema: " + ex.getMessage() + "\n");
                                txtLog.setValue(logRes.toString());
                                usuariosErrorList.add(es.getUsuario());
                                return es.getTo() + ": Problema: " + ex.getMessage() + "\n";
                            } else {
                                logRes.append(es.getTo() + ": Enviado!\n");
                                return es.getTo() + ": Enviado!\n";
                            }
                        });
                sendErrorsList.add(sendErrors);
            }

            for (CompletableFuture<String> se: sendErrorsList) {
                se.join();
                //logRes.append(se.get());
                //txtLog.setValue(logRes.toString());
            }
            logRes.append("Todos reportes han sido procesados!\n\n");
            if (!usuariosErrorList.isEmpty()) {
                logRes.append("No se podia enviar reportes a los siguientes usuarios:\n");
                logRes.append(String.join(",", usuariosErrorList));
            }
            txtLog.setValue(logRes.toString());
//            try {
//                String error = sendErrors.get();
//                if (error!=null) {
//                    showNotification(new Notification("Huvo un problema al enviar el reset link a: " + email.getValue(),
//                            Notification.Type.WARNING_MESSAGE));
//                    Notification.show(error, Notification.Type.WARNING_MESSAGE);
//                    log.warn("Couldn't send password reset link to: " + email.getValue() + "\n" + error);
//                } else {
//                    showNotification(new Notification("Reset link ha sido enviado correctamente a: " + email.getValue()));
//                    log.info("Sent password reset link to: " + email.getValue());
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//

        } catch (JRException | FileNotFoundException e) {
            Notification.show("Problema al generar reportes a enviar \n" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
//        catch (InterruptedException | ExecutionException e) {
//            Notification.show("Problema al enviar \n" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
//        }
    }

    private List<EmailDescription> generateEmails(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta) throws JRException, FileNotFoundException{
        List<EmailDescription> emails = new ArrayList<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            List<AttachmentResource> atres = new ArrayList<>();
            for (ScpDestino dst : trcMap.get(usuario)) {
                EmailAttachment ea = TercerosUtil.generateTerceroOperacionesReport(fechaInicial.getValue(), fechaFinal.getValue(),
                        dst.getCodDestino(), service, false);
                atres.add(ea.asAttachmentResource());
            }
            emails.add(new EmailDescription(usuario.getTxtCorreo(), usuario.getTxtUsuario(), EmailBuilder.startingBlank()
                    .to(usuario.getTxtCorreo())
                    .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                    .withSubject("VASJA Reporte")
                    .withPlainText("Hola " + usuario.getTxtNombre() + "!\nSu reporte Diario de Cuenta adjuntado.\nSaludos\nVASJA")
                    .withAttachments(atres)
                    .buildEmail()));
        }
        return emails;
    }


    private void doImprimir(String codDestino) {
        try {
            TercerosUtil.generateTerceroOperacionesReport(fechaInicial.getValue(), fechaFinal.getValue(),
                    codDestino, service, true);
        } catch (JRException | FileNotFoundException e) {
            Notification.show("Problema al generar reportes a enviar \n" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private Map<MsgUsuario, List<ScpDestino>> prepareListOfTerceros() {
        Map<MsgUsuario, List<ScpDestino>> trcMap = new HashMap<>();
        if (checkTodos.getValue()) {
            List<ScpDestino> dsts = service.getDestinoRepo().findByIndTipodestinoAndActivoAndEnviarreporteAndTxtUsuarioNotLikeOrderByTxtNombre(
                    '3', true, true, "");
            Map<String, List<ScpDestino>> trcUsuarioMap = new HashMap<>();

            for (ScpDestino dst : dsts) {
                if (trcUsuarioMap.containsKey(dst.getTxtUsuario())) {
                    trcUsuarioMap.get(dst.getTxtUsuario()).add(dst);
                } else {
                    List<ScpDestino> locDsts = new ArrayList<>();
                    locDsts.add(dst);
                    trcUsuarioMap.put(dst.getTxtUsuario(), locDsts);
                }
            }
            trcUsuarioMap.forEach((k, v) -> {
                MsgUsuario us = service.getMsgUsuarioRep().findByTxtUsuario(k);
                trcMap.put(us, v);
            });
        } else if (selUsuario.getValue()!=null) {
            MsgUsuario us = service.getMsgUsuarioRep().findByTxtUsuario(selUsuario.getValue().toString());
            trcMap.put(us, service.getDestinoRepo().findByIndTipodestinoAndActivoAndTxtUsuarioLike(
                    '3', true, selUsuario.getValue().toString()));
        } else if (selTercero.getValue()!=null) {
            List<ScpDestino> dests = new ArrayList<>();
            ScpDestino dst = service.getDestinoRepo().findByCodDestino(selTercero.getValue().toString());
            MsgUsuario us = service.getMsgUsuarioRep().findByTxtUsuario(dst.getTxtUsuario());
            List<ScpDestino> dsts = new ArrayList<>();
            dsts.add(dst);
            trcMap.put(us, dsts);
        } else if (txtUsuariosList.getValue()!=null) {
            String[] usuarios = txtUsuariosList.getValue().split(",");
            Set<String> usuariosSet = new HashSet<>();
            for (String u : usuarios) {
                MsgUsuario us = service.getMsgUsuarioRep().findByTxtUsuario(selUsuario.getValue().toString());
                trcMap.put(us, service.getDestinoRepo().findByIndTipodestinoAndActivoAndTxtUsuarioLike(
                        '3', true, u.trim().toLowerCase()));
            }
        }
        return trcMap;
    }


    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selUsuario.setValue(null);
            checkTodos.setValue(false);
            btnImprimir.setEnabled(true);
        }
    }

    private void setUsuarioLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            checkTodos.setValue(false);
            btnImprimir.setEnabled(false);
        }
    }

    private void setUsuarioListLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            checkTodos.setValue(false);
            btnImprimir.setEnabled(false);
        }
    }

    private void setTodosLogic(Property.ValueChangeEvent event) {
        if ((Boolean)event.getProperty().getValue()) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            txtUsuariosList.setEnabled(true);
            btnImprimir.setEnabled(false);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
    }
}
