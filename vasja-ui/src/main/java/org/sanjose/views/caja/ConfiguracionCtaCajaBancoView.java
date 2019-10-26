package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.model.VsjConfiguractacajabanco;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
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
public class ConfiguracionCtaCajaBancoView extends ConfiguracionCtaCajaBancoUI implements Viewing {

    public static final String VIEW_NAME = "Movimientos";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(ConfiguracionCtaCajaBancoView.class);
    private final ConfiguracionCtaCajaBancoLogic viewLogic = new ConfiguracionCtaCajaBancoLogic(this);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "activo", "id", "codTipocuenta", "txtTipocuenta", "codCtacontablecaja",
            "codCtacontablegasto", "codCtaespecial", "paraCaja", "paraBanco", "paraProyecto", "paraTercero"
    };
    private final int[] FILTER_WIDTH = new int[]{
            3, 3, 3, 12, 6,
            6, 6, 3, 3, 3, 3
    };
    private PersistanceService service;

    public ConfiguracionCtaCajaBancoView(PersistanceService comprobanteService) {
        this.service = comprobanteService;
        setSizeFull();
    }

    @Override
    public void init() {
        ComboBox selCtacontablecaja = new ComboBox();
        ComboBox selCtacontablegasto = new ComboBox();
        fechaAno.setValue(new Date());
        fechaAno.addValueChangeListener(ev -> {
                    DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", service.getPlanRepo().
                            findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                                    '0', 'N', GenUtil.getYear(fechaAno.getValue()), "101"), "Sel cta contable", "txtDescctacontable");
                    DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable",
                            service.getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                                    '0', 'N', GenUtil.getYear(fechaAno.getValue()), "101%", "102%", "104%", "106%")
                            , "Sel cta contable", "txtDescctacontable");

                }
        );
        @SuppressWarnings("unchecked") BeanItemContainer<VsjConfiguractacajabanco> container = new BeanItemContainer(VsjConfiguractacajabanco.class, service.getConfiguractacajabancoRepo().findAll());
        gridConfigCtaCajaBanco
        	.setContainerDataSource(container);
        gridConfigCtaCajaBanco.setColumnOrder(VISIBLE_COLUMN_IDS);

        gridConfigCtaCajaBanco.getDefaultHeaderRow().getCell("codTipocuenta").setText("Codigo");
        gridConfigCtaCajaBanco.getColumn("txtTipocuenta").setWidth(120);
        gridConfigCtaCajaBanco.getColumn("id").setEditable(false);

        gridConfigCtaCajaBanco.setSelectionMode(SelectionMode.MULTI);

        gridConfigCtaCajaBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjConfiguractacajabanco.class));

        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", service.getPlanRepo().
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        '0','N', GenUtil.getYear(fechaAno.getValue()), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablecaja").setEditorField(selCtacontablecaja);


        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable",
                service.getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        '0', 'N', GenUtil.getYear(fechaAno.getValue()), "101%", "102%", "104%", "106%")
                , "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);

        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", service.getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridConfigCtaCajaBanco.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        gridConfigCtaCajaBanco.getColumn("activo").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraProyecto").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraTercero").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraBanco").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraCaja").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

        ViewUtil.setupColumnFilters(gridConfigCtaCajaBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        viewLogic.init();
    }


    public void refreshData() {
        gridConfigCtaCajaBanco.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) gridConfigCtaCajaBanco.getContainerDataSource()).addAll(service.getConfiguractacajabancoRepo().findAll());
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        gridConfigCtaCajaBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCtaCajaBanco.getSelectedRows();
    }

    public void removeRow(VsjConfiguractacajabanco vsj) {
        service.getConfiguractacajabancoRepo().delete(vsj);
        gridConfigCtaCajaBanco.getContainerDataSource().removeItem(vsj);
    }

    public PersistanceService getService() {
        return service;
    }
}
