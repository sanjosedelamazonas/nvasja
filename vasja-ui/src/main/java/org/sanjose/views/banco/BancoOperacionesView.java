package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.MainUI;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.VsjItem;
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
    private final BancoOperacionesLogic viewLogic = new BancoOperacionesLogic();
    private final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{
            "flgCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
             "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"
    };
    private final String[] VISIBLE_COLUMN_IDS_USD = new String[]{
            "flgCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
             "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
         "numDebedolar", "numHaberdolar",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"
    };
    private final String[] VISIBLE_COLUMN_IDS_EUR = new String[]{
            "flgCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
             "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
             "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"
    };
    private final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{
            "Cobr.", "Fecha", "Numero", "Cuenta",
            "Auxiliar",  "Cheque", "Glosa",
            "Ing S/.", "Egr S/.",
            "Orig", "Comprob.", "Env", "Anul."
    };
    private final String[] VISIBLE_COLUMN_NAMES_USD = new String[]{
            "Cobr.", "Fecha", "Numero", "Cuenta",
            "Auxiliar", "Cheque", "Glosa",
             "Ing $", "Egr $",
            "Orig", "Comprob.", "Env", "Anul."
    };
    private final String[] VISIBLE_COLUMN_NAMES_EUR = new String[]{
            "Cobr.", "Fecha", "Numero", "Cuenta",
            "Auxiliar", "Cheque", "Glosa",
            "Ing €", "Egr €",
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

    public BancoOperacionesView(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @Override
    public void init() {
        setSizeFull();
        setHeight(102, Unit.PERCENTAGE);

        //noinspection unchecked
        container = new BeanItemContainer(ScpBancocabecera.class, getService().findByFecFechaBetween(filterInitialDate, new Date()));
        container.addNestedContainerBean("scpDestino");
        gridBanco.setContainerDataSource(container);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS_PEN);

        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta);
        ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta, this);
        fechaDesde.addValueChangeListener(ev -> {
            DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                    DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
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

        gridBanco.getColumn("flgCobrado").setEditorField(cobradoChkBox);
        gridBanco.getColumn("flgCobrado").setEditable(true);
        gridBanco.getColumn("flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS_PEN, FILTER_WIDTH, null);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(gridBanco, ScpBancocabecera.class);

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");

        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
                ScpPlancontable cuenta = getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                        GenUtil.getYear(fechaDesde.getValue()), selFiltroCuenta.getValue().toString());
                gridBanco.getColumn("numHabersol").setHidden(true);
                gridBanco.getColumn("numDebesol").setHidden(true);
                gridBanco.getColumn("numHaberdolar").setHidden(true);
                gridBanco.getColumn("numDebedolar").setHidden(true);
                gridBanco.getColumn("numHabermo").setHidden(true);
                gridBanco.getColumn("numDebemo").setHidden(true);
                switch (GenUtil.getNumMoneda(cuenta.getIndTipomoneda()).charValue()) {
                    case '0':
                        gridBanco.getColumn("numHabersol").setHidden(false);
                        gridBanco.getColumn("numDebesol").setHidden(false);
                        break;
                    case '1':
                        gridBanco.getColumn("numHaberdolar").setHidden(false);
                        gridBanco.getColumn("numDebedolar").setHidden(false);
                        break;
                    case '2':
                        gridBanco.getColumn("numHabermo").setHidden(false);
                        gridBanco.getColumn("numDebemo").setHidden(false);
                        break;
                }
                selRepMoneda.select(GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
            } else {
                gridBanco.getColumn("numHabersol").setHidden(false);
                gridBanco.getColumn("numDebesol").setHidden(false);
                gridBanco.getColumn("numHaberdolar").setHidden(false);
                gridBanco.getColumn("numDebedolar").setHidden(false);
                gridBanco.getColumn("numHabermo").setHidden(false);
                gridBanco.getColumn("numDebemo").setHidden(false);
                container.removeContainerFilters("codCtacontable");
                selRepMoneda.select('0');
            }
            //viewLogic.setSaldoDelDia();
        });
        selFiltroCuenta.setPageLength(20);
        bancoOperView.init(MainUI.get().getBancoManejoView().getService());
        // Make the top buttons panel invisible if in this grid view
        bancoOperView.getTopButtons().setVisible(false);
        bancoOperView.getViewLogic().nuevoCheque();
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
        DataFilterUtil.bindTipoMonedaComboBox(selRepMoneda, "moneda", "", false);
        selRepMoneda.select('0');
        selRepMoneda.setNullSelectionAllowed(false);
        bancoOperView.getCerrarBtn().setVisible(false);
        viewLogic.init(this);
    }


    public void refreshData() {
        SortOrder[] sortOrders = gridBanco.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridBanco.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectItem(VsjItem item) {
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

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date filterInitialDate) {
        this.filterInitialDate = filterInitialDate;
    }
}
