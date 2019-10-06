package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.MainUI;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjItem;
import org.sanjose.util.*;
import org.sanjose.views.caja.CajaSaldoView;
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
public class BancoManejoView extends BancoManejoUI implements Viewing, BancoViewing, GridViewing {

    public static final String VIEW_NAME = "Manejo de Cheques";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private BancoManejoLogic viewLogic;
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "checkMesCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula", "codBancocabecera"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Cobr", "Fecha", "Numero", "Cuenta",
            "Nombre", "Cheque", "Glosa",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Orig", "Comprob.", "Env", "Anul", "Codigo"
    };

    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "codOrigenenlace", "flgEnviado", "flg_Anula", "codBancocabecera"
    };

    private final int[] FILTER_WIDTH = new int[]{
            1, 4, 4, 4,
            10, 4, 12,
            3, 3, 3, 3, 3, 3,
            1, 4, 1, 1, 4
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula", "codBancocabecera"};

    private BeanItemContainer<ScpBancocabecera> container;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BancoService bancoService;

    public BancoManejoView(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    private CajaSaldoView saldosView = new CajaSaldoView();

    private Character moneda = GenUtil.PEN;

    public Grid.FooterRow gridFooter;

    private GeneratedPropertyContainer gpContainer;

    private BancoOperView bancoOperView;

    private ScpPlancontable bancoCuenta;

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

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

        gridBanco.setSelectionMode(SelectionMode.MULTI);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);
        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  gridBanco.getColumn(colName).setHidden(true));
        ViewUtil.alignMontosInGrid(gridBanco);
        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta, this);
        //ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta, this);

        fechaDesde.addValueChangeListener(e -> {
            viewLogic.calcFooterSums();
            DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                    DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                    "txtDescctacontable");
            viewLogic.setSaldoCuenta(bancoCuenta);
        });

        fechaHasta.addValueChangeListener(e -> {
            viewLogic.calcFooterSums();
            viewLogic.setSaldoCuenta(bancoCuenta);
        });

        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridBanco.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());

        gridBanco.getColumn("txtGlosa").setMaximumWidth(400);

        gridBanco.getColumn("scpDestino.txtNombredestino").setMaximumWidth(200);

        gridBanco.getColumn("checkMesCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //gridBanco.getColumn("codBancocabecera").setHidden(true);

        // Single click selects, double click opens
        gridBanco.addItemClickListener(e -> viewLogic.setItemLogic(e));

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(gridBanco, ScpBancocabecera.class);

        // CABECA
        getFecMesCobrado().setResolution(Resolution.MONTH);
        getFecMesCobrado().setValue(new Date());

        DataFilterUtil.bindTipoMonedaComboBox(selRepMoneda, "moneda", "", false);
        selRepMoneda.select('0');
        selRepMoneda.setNullSelectionAllowed(false);

        DataFilterUtil.bindTipoMonedaComboBox(selRepMoneda, "cod_tipomoneda", "Moneda", false);
        selRepMoneda.select(moneda);
        ViewUtil.filterColumnsByMoneda(gridBanco, moneda);

        selRepMoneda.setNullSelectionAllowed(false);
        selRepMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(gridBanco, moneda);
                viewLogic.calcFooterSums();
                DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                        DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                        "txtDescctacontable");
            }
            viewLogic.setSaldoDelDia();
        });

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                "txtDescctacontable");
        selFiltroCuenta.setEnabled(true);
        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
                bancoCuenta = getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                        GenUtil.getYear(fechaDesde.getValue()), selFiltroCuenta.getValue().toString());
                //selRepMoneda.select(GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
                gridBanco.getColumn("txtGlosa").setMaximumWidth(500);
                viewLogic.calcFooterSums();

            } else {
                bancoCuenta = null;
                container.removeContainerFilters("codCtacontable");
                ViewUtil.filterColumnsByMoneda(gridBanco, moneda);
                gridBanco.getColumn("txtGlosa").setMaximumWidth(400);
            }
            viewLogic.setSaldoCuenta(bancoCuenta);
            viewLogic.setSaldoDelDia();
        });
        selFiltroCuenta.setPageLength(20);

        gridFooter = gridBanco.appendFooterRow();
        // Initialize Comprobante View
        bancoOperView = new BancoOperView();
        bancoOperView.init(getService());

        viewLogic = new BancoManejoLogic(this);
        viewLogic.setSaldos(getSaldosView().getGridSaldoInicial(), true);
        viewLogic.setSaldos(getSaldosView().getGridSaldoFinal(), false);
        viewLogic.calcFooterSums();
    }

    @Override
    public void refreshData() {
        SortOrder[] sortOrders = gridBanco.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridBanco.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectItem(VsjItem item) {
        for (Object vcb : container.getItemIds()) {
            if (((ScpBancocabecera)vcb).getCodBancocabecera().equals(((ScpBancocabecera)item).getCodBancocabecera())) {
                gridBanco.select(vcb);
                return;
            }
        }
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

    public Button getBtnMarcarCobrado() {
        return btnMarcarCobrado;
    }

    public Grid getGridBanco() {
        return gridBanco;
    }

    public ComboBox getSelRepMoneda() {
        return selRepMoneda;
    }

    @Override
    public BancoOperView getBancoOperView() {
        return bancoOperView;
    }

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date fecha) {
        this.filterInitialDate = fecha;
    }

    public Button getBtnDetallesSaldos() {
        return btnDetallesSaldos;
    }

    public CajaSaldoView getSaldosView() {
        return saldosView;
    }

    public DateField getFechaDesde() {
        return fechaDesde;
    }

    public DateField getFechaHasta() {
        return fechaHasta;
    }

    public TextField getNumSaldoInicialSegBancos() {
        return numSaldoInicialSegBancos;
    }

    public TextField getNumSaldoInicialLibro() {
        return numSaldoInicialLibro;
    }

    public TextField getNumSaldoFinalSegBancos() {
        return numSaldoFinalSegBancos;
    }

    public TextField getNumSaldoFinalLibro() {
        return numSaldoFinalLibro;
    }

    public Button getBtnMarcarNoCobrado() {
        return btnMarcarNoCobrado;
    }

    public BeanItemContainer<ScpBancocabecera> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }

    public DateField getFecMesCobrado() {
        return fecMesCobrado;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public ScpPlancontable getBancoCuenta() {
        return bancoCuenta;
    }
}
