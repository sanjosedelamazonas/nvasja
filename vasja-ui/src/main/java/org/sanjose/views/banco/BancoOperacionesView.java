package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.MainUI;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoOperacionesView extends BancoOperacionesUI implements Viewing, BancoViewing, GridViewing {

    public static final String VIEW_NAME = "Operaciones de Cheques";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private BancoOperacionesLogic viewLogic;
    private final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{
            "checkMesCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"
    };
    private final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{
            "Cobr.", "Fecha", "Numero", "Cuenta",
            "Auxiliar",  "Cheque", "Glosa",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Orig", "Comprob.", "Env", "Anul."
    };

    private final int[] FILTER_WIDTH = new int[]{
            2, 4, 4, 4,
            10, 6, 14,
            3, 3,
            1, 4, 1, 1
    };
    private final String[] NONEDITABLE_COLUMN_IDS_PEN = new String[]{"fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"};

    private BeanItemContainer<ScpBancocabecera> container;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BancoService bancoService;

    private GeneratedPropertyContainer gpContainer;

    public BancoOperacionesView(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    private Character moneda = GenUtil.PEN;

    @Override
    public void init() {
        setSizeFull();
        setHeight(102, Unit.PERCENTAGE);

        //noinspection unchecked
        container = new BeanItemContainer(ScpBancocabecera.class, getService().findByFecFechaBetween(filterInitialDate, new Date()));
        container.addNestedContainerBean("scpDestino");

        gpContainer = new GeneratedPropertyContainer(container);
        gpContainer.addGeneratedProperty("checkMesCobrado",
                new PropertyValueGenerator<Boolean>() {
                    @Override
                    public Boolean getValue(Item item, Object itemId,
                                           Object propertyId) {

                        return DataUtil.isCobrado((ScpBancocabecera) ((BeanItem)item).getBean(), getService());
                    }
                    @Override
                    public Class<Boolean> getType() {
                        return Boolean.class;
                    }
                });


        gridBanco.setContainerDataSource(gpContainer);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS_PEN);

        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.getColumn("txtGlosa").setMaximumWidth(200);
        gridBanco.getColumn("scpDestino.txtNombredestino").setMaximumWidth(130);

        gridBanco.setSelectionMode(SelectionMode.MULTI);

        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta);
        ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta, this);
        fechaDesde.addValueChangeListener(ev -> {
            DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                    DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                    "txtDescctacontable");
        });

        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridBanco.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("txtCorrelativo").setHidden(true);
        gridBanco.getColumn("flgEnviado").setHidden(true);
        gridBanco.getColumn("flg_Anula").setHidden(true);

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);
        //gridBanco.getColumn("flgCobrado").setRenderer(new CheckboxRenderer());
        gridBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(ScpBancocabecera.class));
        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);

        gridBanco.getColumn("checkMesCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

        gridBanco.addItemClickListener(e -> viewLogic.setItemLogic(e));

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS_PEN, FILTER_WIDTH, null);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(gridBanco, ScpBancocabecera.class);

        // CABECA

        getFecMesCobrado().setResolution(Resolution.MONTH);
        getFecMesCobrado().setValue(new Date());

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");
        DataFilterUtil.bindTipoMonedaComboBox(selRepMoneda, "moneda", "", false);
        selRepMoneda.setNullSelectionAllowed(false);
        selRepMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(gridBanco, moneda);
                //viewLogic.calcFooterSums();
                DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                        DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                        "txtDescctacontable");
            }
            //viewLogic.setSaldoDelDia();
        });
        selRepMoneda.select(moneda);

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                "txtDescctacontable");
        selFiltroCuenta.setEnabled(true);
        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
                ScpPlancontable cuenta = getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                        GenUtil.getYear(fechaDesde.getValue()), selFiltroCuenta.getValue().toString());
                //selRepMoneda.select(GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
                gridBanco.getColumn("txtGlosa").setMaximumWidth(500);
              //  viewLogic.calcFooterSums();
              //  viewLogic.setSaldoCuenta(cuenta);
            } else {
                container.removeContainerFilters("codCtacontable");
                ViewUtil.filterColumnsByMoneda(gridBanco, moneda);
                gridBanco.getColumn("txtGlosa").setMaximumWidth(400);
               // viewLogic.setSaldoCuenta(null);
            }
            //viewLogic.setSaldoDelDia();
        });

        selFiltroCuenta.setPageLength(20);
        bancoOperView.init(MainUI.get().getBancoManejoView().getService());
        // Make the top buttons panel invisible if in this grid view
        bancoOperView.getTopButtons().setVisible(false);
        bancoOperView.getViewLogic().nuevoCheque(null);
        BancoOperacionesView bancoOperacionesView = this;
        gridBanco.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                ScpBancocabecera cabeceraSelected = (ScpBancocabecera) selectionEvent.getSelected().toArray()[0];
                bancoOperView.getViewLogic().setNavigatorView(bancoOperacionesView);
                bancoOperView.getViewLogic().editarCheque(cabeceraSelected);
            }
        });
        bancoOperView.getCerrarBtn().setVisible(false);
        viewLogic = new BancoOperacionesLogic(this);
    }

    public void refreshData() {
        SortOrder[] sortOrders = gridBanco.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridBanco.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectItem(VsjItem item) {
        if (container.containsId(item))
            gridBanco.select(item);
    }

    @Override
    public void selectMoneda(Character moneda) {
        selRepMoneda.select(moneda);
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().findByFecFechaBetween(fechaDesde, fechaHasta));
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        gridBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRows() {
        return gridBanco.getSelectedRows();
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public BancoService getService() {
        return bancoService;
    }

    public Grid getGridBanco() {
        return gridBanco;
    }

    public BancoOperView getBancoOperView() {
        return bancoOperView;
    }

    public ComboBox getSelRepMoneda() {
        return selRepMoneda;
    }

    public DateField getFecMesCobrado() {
        return fecMesCobrado;
    }

    public Button getBtnMarcarCobrado() {
        return btnMarcarCobrado;
    }

    public Button getBtnMarcarNoCobrado() {
        return btnMarcarNoCobrado;
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
