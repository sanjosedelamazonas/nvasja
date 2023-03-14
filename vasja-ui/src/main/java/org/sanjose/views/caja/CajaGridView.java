package org.sanjose.views.caja;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.TwoCombosValidator;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class CajaGridView extends CajaGridUI implements CajaViewing, NavigatorViewing, Viewing, GridViewing {

    public static final String VIEW_NAME = "Operaciones de Caja - Grid";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(CajaGridView.class);
    private final CajaGridLogic viewLogic;
    private final String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codDestino", "codContraparte", "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flg_Anula", "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };

    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Responsable", "Lug. Gasto", "Cod. Aux", "Cta Cont.", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Anl", "Env", "Orig.", "Comprob."
    };
    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 10, 6, 6, 6, 6, 6, 6, // S/$
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2, 2, 2, 4
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", "flgEnviado", "flg_Anula",
            "codOrigenenlace", "codComprobanteenlace"};

    private BeanItemContainer<ScpCajabanco> container;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private ScpCajabanco itemSeleccionado;
    private PersistanceService comprobanteService;

    public CajaGridView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
        viewLogic = new CajaGridLogic();
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");
        //noinspection unchecked
        container = new BeanItemContainer(ScpCajabanco.class, getService().getCajabancoRep().findByFecFechaBetween(filterInitialDate, new Date()));
        gridCaja.setContainerDataSource(container);
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.getColumn("txtGlosaitem").setWidth(120);

        gridCaja.setSelectionMode(SelectionMode.MULTI);

        gridCaja.setEditorFieldGroup(
                new BeanFieldGroup<>(ScpCajabanco.class));
        // Moneda
        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.setNullSelectionAllowed(false);
        selMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", e.getProperty().getValue()));
                ViewUtil.filterColumnsByMoneda(gridCaja, (Character)e.getProperty().getValue());
            }
        });
        selMoneda.select('0');

        // Fecha
        PopupDateField pdf = new PopupDateField();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecFecha").setEditorField(pdf);
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Fecha Doc
        pdf = new PopupDateField();
        prop = new ObjectProperty<>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecComprobantepago").setEditorField(pdf);
        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Proyecto
        ComboBox selTercero = new ComboBox();
        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(this::setProyectoLogic);
        selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
        gridCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", DataUtil.loadDestinos(getService()), "Sel Tercero", "txtNombre");
        selTercero.addValueChangeListener(this::setTerceroLogic);
        selTercero.addValidator(new TwoCombosValidator(selProyecto, true, null));
        gridCaja.getColumn("codTercero").setEditorField(selTercero);

        // Cta Caja
        ComboBox selCtacontablecaja = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith('0', 'N', GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codContracta").setEditorField(selCtacontablecaja);

        // Cta Contable
        ComboBox selCtacontable = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith('0', 'N', GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontable").setEditorField(selCtacontable);

        // Rubro inst
        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial",
                getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");
        gridCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        // Responsable
        ComboBox selResponsable = new ComboBox();
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", DataUtil.loadDestinos(getService()),
                "Responsable", "txtNombre");
        gridCaja.getColumn("codDestino").setEditorField(selResponsable);

        ComboBox selLugarGasto = new ComboBox();
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte", getService().getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txtDescContraparte");
        gridCaja.getColumn("codContraparte").setEditorField(selLugarGasto);

        // Cod. Auxiliar
        ComboBox selAuxiliar = new ComboBox();
        DataFilterUtil.bindComboBox(selAuxiliar, "codDestino", DataUtil.loadDestinos(getService()),
                "Auxiliar", "txtNombre");
        gridCaja.getColumn("codDestinoitem").setEditorField(selAuxiliar);

        // Tipo doc
        ComboBox selComprobantepago = new ComboBox();
        DataFilterUtil.bindComboBox(selComprobantepago, "codTipocomprobantepago", getService().getComprobantepagoRepo().findAll(),
                "Sel Tipo", "txtDescripcion");
        gridCaja.getColumn("codTipocomprobantepago").setEditorField(selComprobantepago);

        // Rubro Proy
        ComboBox selPlanproyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
                getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        gridCaja.getColumn("codCtaproyecto").setEditorField(selPlanproyecto);

        // Fuente
        ComboBox selFinanciera = new ComboBox();
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", getService().getFinancieraRepo().findAll(),
                "Sel Fuente", "txtDescfinanciera");
        gridCaja.getColumn("codFinanciera").setEditorField(selFinanciera);

        gridCaja.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridCaja.getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ViewUtil.colorizeRows(gridCaja);

        gridCaja.addItemClickListener(this::setItemLogic);

        for (String col : VISIBLE_COLUMN_IDS) {
            if (gridCaja.getColumn(col).getEditorField() instanceof TextField)
                ((TextField)gridCaja.getColumn(col).getEditorField()).setNullRepresentation("");
        }

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta, this);
        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        viewLogic.init(this);
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        ComboBox selProyecto = (ComboBox)gridCaja.getColumn("codProyecto").getEditorField();
        selProyecto.getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    private void setTerceroLogic(Property.ValueChangeEvent event) {
        ComboBox selTercero = (ComboBox)gridCaja.getColumn("codTercero").getEditorField();
        selTercero.getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    private void setItemLogic(ItemClickEvent event) {
        String proyecto = null;
        Object objProyecto = event.getItem().getItemProperty("codProyecto").getValue();
        if (objProyecto !=null && !objProyecto.toString().isEmpty())
            proyecto = objProyecto.toString();

        Object id = event.getItem().getItemProperty("codCajabanco").getValue();
        itemSeleccionado = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
        setEditorLogic(proyecto);
    }

    private void setEditorLogic(String codProyecto) {
        ComboBox selFinanciera = (ComboBox)gridCaja.getColumn("codFinanciera").getEditorField();
        ComboBox selPlanproyecto = (ComboBox) gridCaja.getColumn("codCtaproyecto").getEditorField();

        DataFilterUtil.setEditorLogicPorProyecto(codProyecto, selFinanciera, selPlanproyecto, getService().getPlanproyectoRepo(), getService().getProyectoPorFinancieraRepo(), getService().getFinancieraRepo());
    }

    @Override
    public void refreshData() {
        SortOrder[] sortOrders = gridCaja.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridCaja.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectMoneda(Character moneda) {
        selMoneda.select(moneda);
    }

    @Override
    public void selectItem(VsjItem item) {
        for (Object vcb : container.getItemIds()) {
            if (((ScpCajabanco)vcb).getCodCajabanco().equals(((ScpCajabanco)item).getCodCajabanco())) {
                gridCaja.select(vcb);
                return;
            }
        }
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        gridCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRows() {
        return gridCaja.getSelectedRows();
    }

    public ScpCajabanco getSelectedRow() {
        if (getSelectedRows().isEmpty())
            return itemSeleccionado;
        for (Object obj : gridCaja.getSelectedRows()) {
            log.info("selected: " + obj);
            return (ScpCajabanco) obj;
        }
        return null;
    }


    public ScpCajabanco getItemSeleccionado() {
        return itemSeleccionado;
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public PersistanceService getService() {
        return comprobanteService;
    }

    @Override
    public Grid getGridCaja() {
        return gridCaja;
    }

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date filterInitialDate) {
        this.filterInitialDate = filterInitialDate;
    }
}
