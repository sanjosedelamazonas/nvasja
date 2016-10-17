package org.sanjose.views.banco;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.INavigatorView;
import org.sanjose.views.sys.VsjView;

import java.util.Collection;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoOperacionesView extends BancoOperacionesUI implements INavigatorView, VsjView {

    public static final String VIEW_NAME = "Operaciones de Cheques";
    private final BancoOperacionesLogic viewLogic = new BancoOperacionesLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "codMescobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
            "codDestino", "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Mes", "Fecha", "Numero", "Cuenta",
            "Auxiliar", "Nombre", "Cheque", "Glosa",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Orig", "Comprob.", "Env"
    };
    private final int[] FILTER_WIDTH = new int[]{
            2, 4, 4, 4,
            6, 10, 4, 12,
            3, 3, 3, 3, 3, 3,
            1, 4, 1
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private BeanItemContainer<VsjBancocabecera> container;

    private BancoService bancoService;

    public BancoOperacionesView(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(VsjBancocabecera.class, getService().findAll());
        container.addNestedContainerBean("scpDestino");
        gridBanco.setContainerDataSource(container);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null);

        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);
        ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta);

        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        gridBanco.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());

        //gridBanco.addItemClickListener(this::setItemLogic);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        ViewUtil.colorizeRows(gridBanco, VsjBancocabecera.class);

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");

        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codCtacontable");
            }
            //viewLogic.setSaldoDelDia();
        });

        viewLogic.init(this);
    }

    public void refreshData() {
        container.removeAllItems();
        container.addAll(getService().findAll());
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
    }
/*
    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("cod").getValue();
            //VsjCajabanco vcb = getService().getBancocabeceraRep().findByCodCajabanco((Integer) id);
            //viewLogic.editarCheque(vcb);
        }
    }*/

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public void clearSelection() {
        gridBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
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

}
