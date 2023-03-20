package org.sanjose.views.terceros;

import com.vaadin.data.Property;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import net.sf.jasperreports.engine.JRException;
import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.bean.VsjTercerofactory;
import org.sanjose.helper.CustomReport;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.TercerosUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

///        DataFilterUtil.bindComboBox(selUsuario, "txtUsuario", service.getMsgUsuarioRep().findAll(), "Sel Cat Proyecto", "txtDescripcion");

        btnEnviar.addClickListener( e -> doEnviar(prepareListOfTerceros()));

        btnImprimir.addClickListener( e -> doImprimir(prepareListOfTerceros()));

        btnEnviar.setEnabled(true);
    }

    private void doEnviar(List<ScpDestino> tercerosList) {
        StringBuilder sb = new StringBuilder();
        List<String> codigosTerc = new ArrayList<>();
        tercerosList.forEach(trc -> codigosTerc.add(trc.getCodDestino()));
        tercerosList.forEach(trc -> sb.append(trc.getCodDestino()).append(", "));
        log.info("Got list of terceros to send: " + sb.toString());
        try {
            TercerosUtil.generateTerceroOperacionesReport(fechaInicial.getValue(), fechaFinal.getValue(),
                    codigosTerc.get(0), service, false);
        } catch (JRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void doImprimir(List<ScpDestino> tercerosList) {
        try {
            TercerosUtil.generateTerceroOperacionesReport(fechaInicial.getValue(), fechaFinal.getValue(),
                    tercerosList.get(0).getCodDestino(), service, true);
        } catch (JRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private List<ScpDestino> prepareListOfTerceros() {
        if (checkTodos.getValue()) {
            return service.getDestinoRepo().findByIndTipodestinoAndActivoAndEnviarreporteAndTxtUsuarioNotOrderByTxtNombre(
                    '3', true, true, "");
        }
        if (selUsuario.getValue()!=null) {
            return service.getDestinoRepo().findByIndTipodestinoAndActivoAndTxtUsuarioLike(
                        '3', true, selUsuario.getValue().toString());
        }
        if (selTercero.getValue()!=null) {
            List<ScpDestino> dests = new ArrayList<>();
            dests.add(service.getDestinoRepo().findByCodDestino(selTercero.getValue().toString()));
            return dests;
        }
        if (txtUsuariosList.getValue()!=null) {
            String[] usuarios = txtUsuariosList.getValue().split(",");
            Set<String> usuariosSet = new HashSet<>();
            for (String u : usuarios)
                usuariosSet.add(u.trim().toLowerCase());
            return service.getDestinoRepo().findByIndTipodestinoAndActivoAndTxtUsuarioIn(
                    '3', true, usuariosSet);
        }
        return new ArrayList<>();
    }


    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selUsuario.setValue(null);
            checkTodos.setValue(false);
        }
    }

    private void setUsuarioLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            checkTodos.setValue(false);
        }
    }

    private void setUsuarioListLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            checkTodos.setValue(false);
        }
    }


    private void setTodosLogic(Property.ValueChangeEvent event) {
        if ((Boolean)event.getProperty().getValue()) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            txtUsuariosList.setEnabled(true);
        } else {
            selTercero.setValue(null);
            selUsuario.setValue(null);
            txtUsuariosList.setEnabled(true);
        }
    }


    private static String toStringIfNN(Object value) {
        if (value==null) return null;
        else return value.toString();
    }

    private void setReporteParameters(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null) {
            selTercero.setValue(null);
            String repName = event.getProperty().getValue().toString();
            log.debug("selected: " +repName);
            CustomReport cr = customReportMap.get(repName);
            selTercero.setEnabled(cr.isProyTercero());
            fechaInicial.setEnabled(cr.isFecha());
            if (!cr.isFecha()) {
                fechaInicial.setValue(null);
                fechaFinal.setValue(null);
            }
            fechaFinal.setEnabled(cr.isFecha());
            btnEnviar.setEnabled(true);

        } else {
            selTercero.setEnabled(false);
            fechaInicial.setEnabled(false);
            fechaFinal.setEnabled(false);
            btnEnviar.setEnabled(false);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
    }

    public ComboBox getSelUsuario() {
        return selUsuario;
    }

    public ComboBox getSelReporte() {
        return selReporte;
    }

    public DateField getFechaInicial() {
        return fechaInicial;
    }

    public DateField getFechaFinal() {
        return fechaFinal;
    }

    public CheckBox getCheckTodos() {
        return checkTodos;
    }

    public ComboBox getSelTercero() {
        return selTercero;
    }

    public Button getBtnEnviar() {
        return btnEnviar;
    }

    public TextArea getTxtLog() {
        return txtLog;
    }

    public TextArea getTxtUsuariosList() {
        return txtUsuariosList;
    }
}
