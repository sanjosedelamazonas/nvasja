package org.sanjose.views.terceros;

import com.vaadin.data.Property;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
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
import org.sanjose.views.rendicion.RendicionExportXLS;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.email.EmailBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private StringBuilder logRes;

    private FileDownloader zipDownloader;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    public EnviarDiarioTercerosView(PersistanceService service) {
        this.service = service;
        setSizeFull();
        addStyleName("crud-view");
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
        btnImprimir.addClickListener( e -> doImprimir(selTercero.getValue().toString()));
        btnClear.addClickListener( e -> {
            logRes = new StringBuilder();
            txtLog.setValue("");
            btnEnviar.setEnabled(true);
        });
        btnEnviar.setEnabled(true);
        btnGenerarNoEnviados.setEnabled(true);
        logRes = new StringBuilder();
        setupBtnGenerarNoEnviados();
        //btnGenerarNoEnviados.addClickListener();
    }

    public CompletableFuture<String> doEnviarAsync() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        UI ui = UI.getCurrent();
        showProgress.setVisible(true);
        btnEnviar.setEnabled(false);
        Executors.newFixedThreadPool(4).submit(() -> {
            List<EmailStatus> sendResults;
            List<String> usuariosErrorList = new ArrayList<>();
            Map<MsgUsuario, List<ScpDestino>> terceros = prepareListOfTerceros(true);
            ui.access(() -> {
                logRes.append("Generado lista de terceros para enviar... " + terceros.keySet().size() + " usuarios"+"\n");
                txtLog.setValue(logRes.toString());
                showProgress.setValue(0.1f);
            });
            List<EmailDescription> emailDescs = generateEmails(terceros, fechaInicial.getValue(), fechaFinal.getValue(), service, ui);
            ui.access(() -> {
                logRes.append("Generado " + emailDescs.size() + " mensajes."+"\n");
                txtLog.setValue(logRes.toString());
                showProgress.setValue(0.5f);
            });
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
                logRes.append("Enviando" + emailDescs.size() + " mensajes."+"\n");
                txtLog.setValue(logRes.toString());
                showProgress.setValue(0.5f);
            });
            for (CompletableFuture<String> se: sendErrorsList) {
                se.join();
                try {
                    String msg = se.get();
                    logRes.append(msg);
                    ui.access(() -> {
                        txtLog.setValue(logRes.toString());
                    });
                } catch (InterruptedException | ExecutionException e) {
                    ui.access(() -> {
                        logRes.append("Problem: " + e.getLocalizedMessage());
                        txtLog.setValue(logRes.toString());
                    });
                    e.printStackTrace();
                }
            }
            logRes.append("Todos reportes han sido procesados!\n\n");
            if (!usuariosErrorList.isEmpty()) {
                logRes.append("No se podia enviar reportes a los siguientes usuarios:\n");
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
        });

        return completableFuture;
    }

    private List<EmailDescription> generateEmails(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta, PersistanceService service, UI ui) throws JRException {
        List<EmailDescription> emails = new ArrayList<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            ui.access(() -> {
                logRes.append("Generando para usuario: " + usuario.getTxtUsuario()+"\n");
                txtLog.setValue(logRes.toString());
            });
            log.info("Generating report for user: " + usuario.getTxtUsuario() + " " + fechaDesde + " " + fechaHasta);
            List<AttachmentResource> atres = new ArrayList<>();
            for (ScpDestino dst : trcMap.get(usuario)) {
                ui.access(() -> {
                    logRes.append("Tercero: " + dst.getCodDestino()+"\n");
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
                    .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                    .withSubject("VASJA Reporte")
                    .withHTMLText(((MainUI) UI.getCurrent()).getMailerSender().genFromTemplate("REPORTE_TERCERO", toReplace))
                    .withAttachments(atres)
                    .buildEmail()));
            log.info("Finished for user: " + usuario.getTxtUsuario()+"\n");
        }
        return emails;
    }

    public void setupBtnGenerarNoEnviados() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return null;
            }
        }, "no");
        zipDownloader = new FileDownloader(resource);
        //xlsDownloader.setFileDownloadResource(resource);
        zipDownloader.extend(btnGenerarNoEnviados);
    }

    public void updateBtnGenerarNoEnviadosResource() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String exportFileName = "TercerosDiarios_"
                + sdf.format(new Date(System.currentTimeMillis()))
                + ".zip";
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    Map<MsgUsuario, List<ScpDestino>> terceros = prepareListOfTerceros(false);
                    return generateReportesZip(terceros, fechaInicial.getValue(), fechaFinal.getValue(), service);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, exportFileName);
        resource.setMIMEType("application/zip");
        zipDownloader.setFileDownloadResource(resource);
    }


    private static ByteArrayInputStream generateReportesZip(Map<MsgUsuario, List<ScpDestino>> trcMap, Date fechaDesde, Date fechaHasta, PersistanceService service) throws JRException, IOException {
        byte[] zipOut;
        Map<String, byte[]> mapReporte = new HashMap<>();
        for (MsgUsuario usuario : trcMap.keySet()) {
            for (ScpDestino dst : trcMap.get(usuario)) {
                EmailAttachment ea = TercerosUtil.generateTerceroOperacionesReport(fechaDesde, fechaHasta,
                        dst.getCodDestino(), service, false);
                mapReporte.put(ea.getFilename(), ea.getData());
            }
        }
        return listBytesToZip(mapReporte);
    }

    protected static ByteArrayInputStream listBytesToZip(Map<String, byte[]> mapReporte) throws IOException {
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
        showProgress.setValue(0.1f);
        Map<MsgUsuario, List<ScpDestino>> trcMap = new HashMap<>();
        if (checkTodos.getValue()) {
            List<ScpDestino> dsts = null;
            dsts = service.getDestinoRepo().findByIndTipodestinoAndActivoAndEnviarreporteAndTxtUsuarioNotLikeOrderByTxtNombre(
                        '3', true, isReporteEnviar, "");
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
        showProgress.setValue(0.2f);
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
        updateBtnGenerarNoEnviadosResource();
    }
}
