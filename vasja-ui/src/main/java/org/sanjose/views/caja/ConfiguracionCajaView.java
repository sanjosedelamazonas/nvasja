package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import org.sanjose.model.VsjConfiguracioncaja;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.Viewing;

import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class ConfiguracionCajaView extends ConfiguracionCajaUI implements Viewing {

    public static final String VIEW_NAME = "Cajas";
    private static final Logger log = LoggerFactory.getLogger(ConfiguracionCajaView.class);
    private final ConfiguracionCajaLogic viewLogic = new ConfiguracionCajaLogic(this);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "codConfiguracion", "txtConfiguracion", "indTipomoneda",
            "codCtacontable", "codDestino", "codProyecto"
    };
    private final int[] FILTER_WIDTH = new int[]{
            6, 12, 2,
            6, 6, 6
    };
    private ComprobanteService service;

    public ConfiguracionCajaView(ComprobanteService comprobanteService) {
        service = comprobanteService;
        setSizeFull();
    }

    @Override
    public void init() {
        ComboBox selCtacontable = new ComboBox();
        ComboBox selCategoriaproy = new ComboBox();
        fechaAno.setValue(new Date());
        fechaAno.addValueChangeListener(ev ->
                DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", service.getPlanRepo().
                        findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                                '0','N', GenUtil.getYear(fechaAno.getValue()), "101"), "Sel cta contable", "txtDescctacontable")
        );
        @SuppressWarnings("unchecked") BeanItemContainer<VsjConfiguracioncaja> container = new BeanItemContainer(VsjConfiguracioncaja.class, getService().getConfiguracioncajaRepo().findAll());
        gridConfigCaja
        	.setContainerDataSource(container);
        gridConfigCaja.setColumnOrder(VISIBLE_COLUMN_IDS);
        
        gridConfigCaja.getDefaultHeaderRow().getCell("codConfiguracion").setText("Codigo");
        gridConfigCaja.getColumn("txtConfiguracion").setWidth(200);
        gridConfigCaja.getColumn("codConfiguracion").setEditable(false);
               
        gridConfigCaja.setSelectionMode(SelectionMode.MULTI);

        gridConfigCaja.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjConfiguracioncaja.class));

        DataFilterUtil.bindComboBox(selCategoriaproy, "codCategoriaproyecto", getService().getScpCategoriaproyectoRep().findAll(), "Sel Cat Proyecto", "txtDescripcion");
        gridConfigCaja.getColumn("codCategoriaproyecto").setEditorField(selCategoriaproy);

        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                '0', 'N',GenUtil.getYear(fechaAno.getValue()), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCaja.getColumn("codCtacontable").setEditorField(selCtacontable);
        

        ComboBox selDestino = new ComboBox();
        DataFilterUtil.bindComboBox(selDestino, "codDestino", getService().getDestinoRepo().findByIndTipodestino('3'), "Sel Tercero", "txtNombredestino");
        gridConfigCaja.getColumn("codDestino").setEditorField(selDestino);

        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");
        //selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
        gridConfigCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tipo Moneda
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "indTipomoneda", "Moneda");
        gridConfigCaja.getColumn("indTipomoneda").setEditorField(selTipomoneda);

        ViewUtil.setupColumnFilters(gridConfigCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        viewLogic.init();
    }

    public void refreshData() {
        gridConfigCaja.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) gridConfigCaja.getContainerDataSource()).addAll(getService().getConfiguracioncajaRepo().findAll());
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        gridConfigCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCaja.getSelectedRows();
    }

    public void removeRow(VsjConfiguracioncaja vsj) {
        getService().getConfiguracioncajaRepo().delete(vsj);
        gridConfigCaja.getContainerDataSource().removeItem(vsj);
    }

    public ComprobanteService getService() {
        return service;
    }
}
