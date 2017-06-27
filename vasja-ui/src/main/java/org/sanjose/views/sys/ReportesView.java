package org.sanjose.views.sys;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.SelectionMode;
import org.sanjose.helper.CustomReport;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.VsjPropiedad;
import org.sanjose.repo.VsjPropiedadRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ComprobanteService;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;

import java.util.*;

/**          A
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class ReportesView extends ReportesUI implements Viewing {

    public static final String VIEW_NAME = "Reportes";
    private static final Logger log = LoggerFactory.getLogger(ReportesView.class);
    private ComprobanteService comprobanteService;
    private Map<String,CustomReport> customReportMap = new TreeMap<>();

    public ReportesView(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
        setSizeFull();
        addStyleName("crud-view");
    }

    @Override
    public void init() {

        ReportHelper.get().loadCustomReports();
        List<CustomReport> customReportList = ReportHelper.get().getCustomReports();
        Map<String,String> customReportNameMap  = new TreeMap<>();
        for (CustomReport customReport : customReportList) {
            customReportNameMap.put(customReport.getName(),customReport.getName());
            customReportMap.put(customReport.getName(),customReport);
        }
        DataFilterUtil.bindFixedStringValComboBox(selReporte, "selReporte", "Sellecione Reporte", customReportNameMap);
        selReporte.addValueChangeListener(this::setReporteParameters);


        // Proyecto
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", comprobanteService.getProyectoRepo().findByFecFinalGreaterThan(new Date()),
                "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(this::setProyectoLogic);

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", comprobanteService.getDestinoRepo().findByIndTipodestino('3'), "Sel Tercero",
                "txtNombredestino");
        selTercero.addValueChangeListener(this::setTerceroLogic);

        DataFilterUtil.bindComboBox(selCategoria, "codCategoriaproyecto", comprobanteService.getScpCategoriaproyectoRep().findAll(), "Sel Cat Proyecto", "txtDescripcion");

        btnAceptar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                CustomReport cr = customReportMap.get(selReporte.getValue());
                ReportHelper.generateCustomReport(
                        cr.getFileName(),
                        toStringIfNN(selProyecto.getValue()),
                        toStringIfNN(selTercero.getValue()),
                        toStringIfNN(selCategoria.getValue()),
                        fechaInicial.getValue(),
                        fechaFinal.getValue()
                        );
            }
        });
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
            selProyecto.setEnabled(cr.isProyTercero());
            selTercero.setEnabled(cr.isProyTercero());
            fechaInicial.setEnabled(cr.isFecha());
            if (!cr.isFecha()) {
                fechaInicial.setValue(null);
                fechaFinal.setValue(null);
            }
            fechaFinal.setEnabled(cr.isFecha());
            selCategoria.setEnabled(cr.isCategoria());
            btnAceptar.setEnabled(true);

        } else {
            selProyecto.setEnabled(false);
            selTercero.setEnabled(false);
            fechaInicial.setEnabled(false);
            fechaFinal.setEnabled(false);
            selCategoria.setEnabled(false);
            btnAceptar.setEnabled(false);
        }
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null) {
            selTercero.setValue(null);
        }
    }

    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            selProyecto.setValue(null);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
    }
}
