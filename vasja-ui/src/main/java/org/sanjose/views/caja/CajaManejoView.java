package org.sanjose.views.caja;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.INavigatorView;
import org.sanjose.views.sys.VsjView;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CajaManejoView extends CajaManejoUI implements INavigatorView, VsjView {

    public static final String VIEW_NAME = "Manejo de Caja";
    private final CajaManejoLogic viewLogic = new CajaManejoLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar",
            "codDestino", "codContraparte", "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $",
            "Responsable", "Lug. Gasto", "Cod. Aux", "Cta Cont.", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Env", "Origen", "Comprobante"
    };
    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 10, 6, 6, 6, 6, //
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2, 2, 5
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", "flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"};

    private BeanItemContainer<VsjCajabanco> container;

    private ComprobanteService comprobanteService;

    public CajaManejoView(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(VsjCajabanco.class, getService().getCajabancoRep().findAll());
        gridCaja.setContainerDataSource(container);
        gridCaja.setEditorEnabled(false);
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);

        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindComboBox(selFiltroCaja, "id.codCtacontable",
                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), true),
                "txtDescctacontable");

        selFiltroCaja.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codCtacontable");
            }
            viewLogic.setSaldoDelDia();
        });

        gridCaja.addItemClickListener(this::setItemLogic);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        ViewUtil.colorizeRows(gridCaja);

        gridCaja.getColumn("flgEnviado").setHidden(true);

        // Set Saldos Inicial
        fechaDesde.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoInicial, true));
        fechaHasta.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoFInal, false));

        viewLogic.init(this);
        viewLogic.setSaldos(gridSaldoInicial, true);
        viewLogic.setSaldos(gridSaldoFInal, false);

    }

    public void refreshData() {
        container.removeAllItems();
        container.addAll(getService().getCajabancoRep().findAll());
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);
    }

    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codCajabanco").getValue();
            VsjCajabanco vcb = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
            viewLogic.editarComprobante(vcb);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public void clearSelection() {
        gridCaja.getSelectionModel().reset();
    }

    public VsjCajabanco getSelectedRow() {
        return (VsjCajabanco) gridCaja.getSelectedRows().toArray()[0];
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public ComprobanteService getService() {
        return comprobanteService;
    }

    public ComboBox getSelFiltroCaja() {
        return selFiltroCaja;
    }

    public Label getValSolIng() {
        return valSolIng;
    }

    public Label getValSolEgr() {
        return valSolEgr;
    }

    public Label getValSolSaldo() {
        return valSolSaldo;
    }

    public Label getValDolIng() {
        return valDolIng;
    }

    public Label getValDolEgr() {
        return valDolEgr;
    }

    public Label getValDolSaldo() {
        return valDolSaldo;
    }

    public Grid getGridCaja() {
        return gridCaja;
    }
}
