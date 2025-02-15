package org.sanjose.views.terceros;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.vaadin.data.Property;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.util.TempFile;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
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
import org.simplejavamail.email.EmailBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.sanjose.util.ConfigurationUtil.get;

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

    private ch.qos.logback.classic.Logger emaillog = null;
    private PersistanceService service;
    private StringBuilder logRes;

    private FileDownloader allDownloader;
    private FileDownloader noenvDownloader;

    private String format = "Un PDF";

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    public EnviarDiarioTercerosView(PersistanceService service) {
        this.service = service;
        setSizeFull();
        addStyleName("crud-view");
    }

    public EnviarDiarioTercerosView() {
        this.service = service;
    }

    @Override
    public void init() {

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

        selFormato.setNullSelectionAllowed(false);
        selFormato.setValue("Un PDF");
        selFormato.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                format = (String)valueChangeEvent.getProperty().getValue();
                updateBtnGenerarSetResource(true);
                updateBtnGenerarSetResource(false);
            }
        });

        btnEnviar.addClickListener( e -> {
               CompletableFuture<String> test = doEnviarAsync();
                    test.handle((String, ex) -> {
                        if (ex != null) {
                            //logRes.append("Problema al enviar mensaje a :"+ es.getTo() + "\n" + ex.getMessage() + "\n");
                            logRes.append("problem");
                            txtLog.setValue(logRes.toString());
                            return "Problema: " + ex.getMessage() + "\n";
                        } else {
                            //logRes.append("add: " + );
                            return ": Enviado!\n";
                        }
                    });
        });
        //btnImprimir.addClickListener( e -> doImprimir(selTercero.getValue().toString()));
        btnClear.addClickListener( e -> {
            logRes = new StringBuilder();
            txtLog.setValue("");
            btnEnviar.setEnabled(true);
        });
        btnEnviar.setEnabled(true);
        btnGenerarNoEnviados.setEnabled(false);
        btnImprimir.setEnabled(true);
        logRes = new StringBuilder();
        setupBtnGenerarNoEnviados();
        setupBtnGenerarSeleccionados();
        //btnGenerarNoEnviados.addClickListener( o -> generateNoEnviadosAsOneReport());
    }

    private void outLog(String txt) {
        outLog(txt, "info");
    }

    private void outLog(String txt, String level) {
        logRes.append(txt);
        if (txt.endsWith("\n"))
            txt = txt.substring(0,txt.length() - 1);{
        }
        if (emaillog!=null) {
            if (level=="err") {
                emaillog.error(txt);
            }
            else {
                emaillog.info(txt);
            }
        }
    }


    public CompletableFuture<String> doEnviarAsync() {
        // Setup email logger:
        emaillog = setupLogFile();
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        UI ui = UI.getCurrent();
        showProgress.setVisible(true);
        btnEnviar.setEnabled(false);
        Executors.newFixedThreadPool(2).submit(() -> {
            List<EmailStatus> sendResults;
            List<String> usuariosErrorList = new ArrayList<>();
            Map<MsgUsuario, List<ScpDestino>> terceros = prepareListOfTerceros(true);
            ui.access(() -> {
                outLog("Generado lista de terceros para enviar... " + terceros.keySet().size() + " usuarios"+"\n");
                txtLog.setValue(logRes.toString());
                showProgress.setValue(0.1f);
            });
            List<EmailDescription> emailDescs = generateEmails(terceros, fechaInicial.getValue(), fechaFinal.getValue(), service, ui);
            ui.access(() -> {
                outLog("Generado " + emailDescs.size() + " mensajes."+"\n");
                txtLog.setValue(logRes.toString());
                showProgress.setValue(0.5f);
            });
            try {
                sendResults = ((MainUI) UI.getCurrent()).getMailerSender().sendEmails(emailDescs);
                showProgress.setValue(0.7f);
                List<CompletableFuture<String>> sendErrorsList = new ArrayList<>();
                for (EmailStatus es : sendResults) {
                    CompletableFuture<String> sendErrors =
                            es.getStatus().handle((String, ex) -> {
                                if (ex != null) {
                                    //logRes.append("Problema al enviar mensaje a :"+ es.getTo() + "\n" + ex.getMessage() + "\n");
                                    //logRes.append(es.getTo() + ": Problema: " + ex.getMessage() + "\n");
                                    txtLog.setValue(logRes.toString());
                                    usuariosErrorList.add(es.getUsuario());
                                    return es.getTo() + ": Problema: " + ex.getMessage() + "\n";
                                } else {
                                    //logRes.append(es.getTo() + ": Enviado!\n");
                                    return es.getTo() + ": Enviado!\n";
                                }
                            });
                    sendErrorsList.add(sendErrors);
                }
                ui.access(() -> {
                    outLog("Enviando " + emailDescs.size() + " mensajes."+"\n");
                    txtLog.setValue(logRes.toString());
                    showProgress.setValue(0.5f);
                });
                for (CompletableFuture<String> se: sendErrorsList) {
                    se.join();
                    try {
                        String msg = se.get();
                        outLog(msg);
                        ui.access(() -> {
                            txtLog.setValue(logRes.toString());
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        ui.access(() -> {
                            outLog("Problem: " + e.getLocalizedMessage(), "err");
                            txtLog.setValue(logRes.toString());
                        });
                        e.printStackTrace();
                    }
                }
                outLog("Todos reportes han sido procesados!\n\n");
                if (!usuariosErrorList.isEmpty()) {
                    logRes.append("Error al enviar reportes a los siguientes usuarios:\n");
                    logRes.append(String.join(",", usuariosErrorList) + "\n\n");
                }
                ui.access(() -> {
                    btnEnviar.setEnabled(true);
                    txtLog.setValue(logRes.toString());
                    showProgress.setVisible(false);
                });
                log.info("Finished sending reports");
                completableFuture.complete("Hello");
                return null;
            } catch (InterruptedException ie) {
                log.error("Problem waiting in-between sending messages in a bulk!");
                outLog("Error al enviar reportes, Tiempo de espera interrompio.\n", "err");
                return null;
            }
        });

        return completableFuture;
    }

    private List<EmailDescription> generateEmails(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta, PersistanceService service, UI ui) throws JRException {
        List<EmailDescription> emails = new ArrayList<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            ui.access(() -> {
                outLog("Generando para usuario: " + usuario.getTxtUsuario()+"\n");
                txtLog.setValue(logRes.toString());
            });
            log.info("Generating report for user: " + usuario.getTxtUsuario() + " " + fechaDesde + " " + fechaHasta);
            List<AttachmentResource> atres = new ArrayList<>();
            for (ScpDestino dst : trcMap.get(usuario)) {
                ui.access(() -> {
                    outLog("Tercero: " + dst.getCodDestino()+"\n");
                    txtLog.setValue(logRes.toString());
                });
                EmailAttachment ea = TercerosUtil.generateTerceroOperacionesReport(fechaDesde, fechaHasta,
                        dst.getCodDestino(), service, false);
                atres.add(ea.asAttachmentResource());
            }
            Map<String, String> toReplace = new HashMap<>();
            toReplace.put("USUARIO", usuario.getTxtNombre());
            //log.info("Generating report for: " + dst.getCodDestino() + " " + fechaDesde + " " + fechaHasta);
            emails.add(new EmailDescription(usuario.getTxtCorreo(), usuario.getTxtUsuario(), EmailBuilder.startingBlank()
                    .to(usuario.getTxtCorreo())
                    .from("Vicariato San Jose del Amazonas", get("MAIL_FROM"))
                    .withSubject("VASJA Reporte")
                    .withHTMLText(((MainUI) UI.getCurrent()).getMailerSender().genFromTemplate("REPORTE_TERCERO", toReplace))
                    .withAttachments(atres)
                    .buildEmail()));
            log.info("Finished for user: " + usuario.getTxtUsuario()+"\n");
        }
        return emails;
    }

//    public void generateNoEnviadosAsOneReport() {
//        try {
//            Map<MsgUsuario, List<ScpDestino>> terceros = prepareListOfTerceros(false);
//            TercerosUtil.generateTerceroOperacionesAllInOneReport(fechaInicial.getValue(), fechaFinal.getValue(), terceros, service, true);
//        } catch (JRException e) {
//            Notification.show("Problema al generar reportes a enviar \n" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//

    public ch.qos.logback.classic.Logger setupLogFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String fileName = ConfigurationUtil.getEmailLogDirectory() + File.separator + "emails_" + CurrentUser.get() + "_" + sdf.format(System.currentTimeMillis())+ ".log";
        return setupEmailLogger(fileName);
    }

    public ch.qos.logback.classic.Logger setupEmailLogger(String logFilePath) {
        LoggerContext context = new LoggerContext();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        try {
            configurator.doConfigure(createLogbackConfig(logFilePath));
        } catch (JoranException je) {
            System.err.println("Error configuring logback: " + je.getMessage());
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        emaillog = context.getLogger("Emails");
        return emaillog;
    }

    private static File createLogbackConfig(String logFilePath) {
        String config = "<configuration>" +
                        "  <appender name=\"FILE\" class=\"ch.qos.logback.core.FileAppender\">" +
                        "    <file>" + logFilePath + "</file>" +
                        "    <encoder>" +
                        "      <pattern>%d{HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>" +
                        "    </encoder>" +
                        "  </appender>" +
                        "  <root level=\"debug\">" +
                        "    <appender-ref ref=\"FILE\" />" +
                        "  </root>" +
                        "</configuration>";
        File configFile = new File("logback.xml");
        try {
            java.nio.file.Files.write(configFile.toPath(), config.getBytes());
        } catch (java.io.IOException e) {
            System.err.println("Error creating logback configuration file: " + e.getMessage());
        }
        return configFile;
    }

    public void setupBtnGenerarNoEnviados() {
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return null;
            }
        }, "no");
        noenvDownloader = new FileDownloader(resource);
        noenvDownloader.extend(btnGenerarNoEnviados);
    }

    public void setupBtnGenerarSeleccionados() {
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return null;
            }
        }, "no");
        allDownloader = new FileDownloader(resource);
        allDownloader.extend(btnImprimir);
    }

    public void updateBtnGenerarSetResource(boolean isNoEnviados) {
        updateBtnGenerarSetResource(isNoEnviados, "TercerosDiarios_");
    }

    public void updateBtnGenerarSetResource(boolean isNoEnviados, String filePrefix) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String exportFileName = (filePrefix!=null ? "Diario_" + filePrefix + "_" : "Diarios_Terceros_")
                + sdf.format(new Date(System.currentTimeMillis()))
                + (format.endsWith("ZIP") ? ".zip" : ".pdf");
        final UI ui = UI.getCurrent();
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    Map<MsgUsuario, List<ScpDestino>> terceros = prepareListOfTerceros(!isNoEnviados);
                    if (format.endsWith("ZIP"))
                        return generateReportesZip(terceros, fechaInicial.getValue(), fechaFinal.getValue(), service, ui);
                    else
                        return generateReportesOnePdf(terceros, fechaInicial.getValue(), fechaFinal.getValue(), service, ui);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, exportFileName);
        resource.setMIMEType(format.endsWith("ZIP") ? "application/zip" : "application/pdf");
        if (isNoEnviados)
            noenvDownloader.setFileDownloadResource(resource);
        else
            allDownloader.setFileDownloadResource(resource);
    }

    private InputStream generateReportesOnePdf(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta, PersistanceService service, UI ui) throws JRException, IOException {
        showProgress.setVisible(true);
        showProgress.setValue(0.1f);
        btnGenerarNoEnviados.setEnabled(false);
        btnImprimir.setEnabled(false);
        Map<String, byte[]> mapReporte = new HashMap<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            for (ScpDestino dst : trcMap.get(usuario)) {
                logRes.append("Generando reporte para usuario: " + dst.getTxtUsuario() + ""+"\n");
                txtLog.setValue(logRes.toString());
                //showProgress.setValue(0.1f);
                EmailAttachment ea = TercerosUtil.generateTerceroOperacionesReport(fechaDesde, fechaHasta,
                        dst.getCodDestino(), service, false);
                mapReporte.put(ea.getFilename(), ea.getData());
            }
        }
        PDFMergerUtility ut = new PDFMergerUtility();
        File outFile = TempFile.createTempFile("diario_cuenta", "pdf");
        ut.setDestinationFileName(outFile.getAbsolutePath());
                //TempFile.createTempFile("")"");
        for (String pdfName : mapReporte.keySet()) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mapReporte.get(pdfName));
            ut.addSource(byteArrayInputStream);
        }
        showProgress.setValue(0.9f);
        showProgress.setVisible(false);
        btnGenerarNoEnviados.setEnabled(true);
        btnImprimir.setEnabled(true);
        ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return new FileInputStream(outFile);
    }

    private InputStream generateReportesZip(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta, PersistanceService service, UI ui) throws JRException, IOException {
        ui.access(() ->{
            showProgress.setVisible(true);
            showProgress.setValue(0.1f);
            btnGenerarNoEnviados.setEnabled(false);
            btnImprimir.setEnabled(false);
        });
        Map<String, byte[]> mapReporte = new HashMap<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            for (ScpDestino dst : trcMap.get(usuario)) {
                EmailAttachment ea = TercerosUtil.generateTerceroOperacionesReport(fechaDesde, fechaHasta,
                        dst.getCodDestino(), service, false);
                mapReporte.put(ea.getFilename(), ea.getData());
            }
        }
        ui.access(() -> {
            showProgress.setValue(0.9f);
            showProgress.setVisible(false);
            btnGenerarNoEnviados.setEnabled(true);
            btnImprimir.setEnabled(true);
        });
        return listBytesToZip(mapReporte);
    }

    protected static InputStream listBytesToZip(Map<String, byte[]> mapReporte) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (Map.Entry<String, byte[]> reporte : mapReporte.entrySet()) {
            ZipEntry entry = new ZipEntry(reporte.getKey());
            entry.setSize(reporte.getValue().length);
            zos.putNextEntry(entry);
            zos.write(reporte.getValue());
        }
        zos.closeEntry();
        zos.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }


    private void doImprimir(String codDestino) {
        try {
            TercerosUtil.generateTerceroOperacionesReport(fechaInicial.getValue(), fechaFinal.getValue(),
                    codDestino, service, true);
        } catch (JRException e) {
            Notification.show("Problema al generar reportes a enviar \n" + e.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Map<MsgUsuario, List<ScpDestino>> prepareListOfTerceros(boolean isReporteEnviar) {
        showProgress.setVisible(true);
        showProgress.setValue(0.1f);
        Map<MsgUsuario, List<ScpDestino>> trcMap = new HashMap<>();
        if (checkTodos.getValue()) {
            List<ScpDestino> dsts = null;
            if (isReporteEnviar) {
                dsts = service.getDestinoRepo().findByIndTipodestinoAndActivoAndEnviarreporteAndTxtUsuarioNotLikeOrderByTxtNombre(
                        '3', true, isReporteEnviar, "");
            } else {
                dsts = service.getDestinoRepo().findByIndTipodestinoAndActivoAndEnviarreporteOrderByTxtNombre(
                        '3', true, false);
            }
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
            List<String> usuariosSet = new ArrayList<>();
            for (String u : usuarios) {
                if (!usuariosSet.contains(u.trim()))
                    usuariosSet.add(u.trim());
            }
            for (String u : usuariosSet) {
                MsgUsuario us = service.getMsgUsuarioRep().findByTxtUsuario(u);
                trcMap.put(us, service.getDestinoRepo().findByIndTipodestinoAndActivoAndTxtUsuarioLike(
                        '3', true, u.trim().toLowerCase()));
            }
        }
        showProgress.setValue(0.2f);
        return trcMap;
    }


    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selUsuario.setValue(null);
            checkTodos.setValue(false);
            btnGenerarNoEnviados.setEnabled(true);
            updateBtnGenerarSetResource(true, event.getProperty().getValue().toString());
            updateBtnGenerarSetResource(false, event.getProperty().getValue().toString());
        } else {
            updateBtnGenerarSetResource(true);
            updateBtnGenerarSetResource(false);
        }
    }

    private void setUsuarioLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            checkTodos.setValue(false);
            btnGenerarNoEnviados.setEnabled(false);
            updateBtnGenerarSetResource(true, event.getProperty().getValue().toString());
            updateBtnGenerarSetResource(false, event.getProperty().getValue().toString());
        } else {
            updateBtnGenerarSetResource(true);
            updateBtnGenerarSetResource(false);
        }
    }

    private void setUsuarioListLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            checkTodos.setValue(false);
            btnGenerarNoEnviados.setEnabled(false);
        }
        updateBtnGenerarSetResource(true);
        updateBtnGenerarSetResource(false);
    }

    private void setTodosLogic(Property.ValueChangeEvent event) {
        if ((Boolean)event.getProperty().getValue()) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            txtUsuariosList.setEnabled(true);
            btnGenerarNoEnviados.setEnabled(true);
        }
        updateBtnGenerarSetResource(true);
        updateBtnGenerarSetResource(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
        btnGenerarNoEnviados.setEnabled(false);
        //TODO update list of usuarios
        updateBtnGenerarSetResource(true);
        updateBtnGenerarSetResource(false);
    }

//    public static void main(String[] args) {
//        String customLogFilePath = "my-custom-log_2.log";
//        EnviarDiarioTercerosView enviarDiarioTercerosView = new EnviarDiarioTercerosView();
//        enviarDiarioTercerosView.setupEmailLogger(customLogFilePath);
//        enviarDiarioTercerosView.emaillog.info("This is a test log message to {}", customLogFilePath);
//    }
}
