package org.sanjose.views.terceros;

import com.vaadin.data.Property;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import org.sanjose.helper.CustomReport;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.ReportesUI;
import org.sanjose.views.sys.Viewing;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.util.*;

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
    private PersistanceService comprobanteService;
    private Map<String,CustomReport> customReportMap = new TreeMap<>();

    public EnviarDiarioTercerosView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
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


        List<ScpDestino> terceros = DataUtil.loadDestinos(comprobanteService, true);

        Map<String, String> usuariosMap = new HashMap<>();
        terceros.forEach(trc -> {
            if (!GenUtil.strNullOrEmpty(trc.getTxtUsuario())) {
                MsgUsuario usuario = comprobanteService.getMsgUsuarioRep().findByTxtUsuario(trc.getTxtUsuario());
                if (usuario!=null)
                    usuariosMap.put(trc.getTxtUsuario(), trc.getTxtUsuario() + " (" + usuario.getTxtNombre() + ")");
            }
        });
        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", DataUtil.loadDestinos(comprobanteService, true), "Sel Tercero",
                "txtNombre");
        selTercero.addValueChangeListener(this::setTerceroLogic);

        DataFilterUtil.bindFixedStringValComboBox(selUsuario, "selUsuario", "Seleccione Usuario", usuariosMap);
        selUsuario.addValueChangeListener(this::setUsuarioLogic);

        checkTodos.addValueChangeListener(this::setTodosLogic);


        fechaInicial.addValueChangeListener(val -> fechaFinal.setValue(GenUtil.getEndOfMonth(fechaInicial.getValue())));

///        DataFilterUtil.bindComboBox(selUsuario, "txtUsuario", comprobanteService.getMsgUsuarioRep().findAll(), "Sel Cat Proyecto", "txtDescripcion");

        btnEnviar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                CustomReport cr = customReportMap.get(selReporte.getValue());
//                ReportHelper.generateCustomReport(
//                        cr.getFileName(),
//                        toStringIfNN(selTercero.getValue()),
//                        toStringIfNN(selCategoria.getValue()),
//                        fechaInicial.getValue(),
//                        fechaFinal.getValue()
//                        );
            }
        });
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

    private void setTodosLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selTercero.setValue(null);
            selUsuario.setValue(null);
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
}
