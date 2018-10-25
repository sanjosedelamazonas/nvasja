package org.sanjose.views.caja;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
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
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.Viewing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CajaManejoView extends CajaManejoUI implements NavigatorViewing, Viewing, GridViewing {

    public static final String VIEW_NAME = "Manejo de Caja";
    private final CajaManejoLogic viewLogic = new CajaManejoLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codDestino", "codContraparte", "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Responsable", "Lug. Gasto", "Cod. Aux", "Cta Cont.", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Env", "Origen", "Comprobante"
    };
    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 10, 6, 6, 6, 6, 6, 6, //
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2, 2, 5
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", "flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

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
        container = new BeanItemContainer(VsjCajabanco.class, getService().getCajabancoRep().findByFecFechaBetween(filterInitialDate, new Date()));
        gridCaja.setContainerDataSource(container);
        gridCaja.setEditorEnabled(false);
        gridCaja.setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta, this);

        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindComboBox(selFiltroCaja, "id.codCtacontable",
                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), PEN),
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
        gridCaja.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridCaja.getColumn("flgEnviado").setHidden(true);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(gridCaja);

        // Set Saldos Inicial
        fechaDesde.addValueChangeListener(ev -> {viewLogic.setSaldos(gridSaldoInicial, true); refreshCajas();});
        fechaHasta.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoFInal, false));

        viewLogic.init(this);
        viewLogic.setSaldos(gridSaldoInicial, true);
        viewLogic.setSaldos(gridSaldoFInal, false);

    }

    private void refreshCajas() {
        DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), PEN),
                "txtDescctacontable");
    }

    public void refreshData() {
        filter(filterInitialDate, new Date());
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        gridCaja.setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());
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
        refreshData();
    }

    public void clearSelection() {
        gridCaja.getSelectionModel().reset();
    }

    public VsjCajabanco getSelectedRow() {
        if (gridCaja.getSelectedRows().toArray().length>0)
            return (VsjCajabanco) gridCaja.getSelectedRows().toArray()[0];
        else
            return null;
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

    public Label getValEurIng() {
        return valEurIng;
    }

    public Label getValEurEgr() {
        return valEurEgr;
    }

    public Label getValEurSaldo() {
        return valEurSaldo;
    }


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
