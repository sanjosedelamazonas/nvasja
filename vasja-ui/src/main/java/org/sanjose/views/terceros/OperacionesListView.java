package org.sanjose.views.terceros;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.VsjItem;
import org.sanjose.util.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class OperacionesListView extends OperacionesListUI implements NavigatorViewing, Viewing, GridViewing  {

    public static final String VIEW_NAME = "Operaciones de la cuenta";

    public String getWindowTitle() {
        return "Operaciones de la cuenta";
    }

    private final String[] VISIBLE_COLUMN_IDS = new String[]{"txtCorrelativo", "fecFecha", "txtGlosaitem",
            "codTercero", "codDestino", "numDebesol", "codContracta",
            "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codCtacontable"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {

    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Numero", "Fecha", "Descripcion",
            "Entregado a/por", "Contra Cta,",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Cuenta"
    };
//    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
//            5, 15, 6, 6, 6, 6, 6, 6, //
//            6, 4, 6, 5, 5, 2, // Tipo Doc
//            4, 5, 5, 5, 4, // Fuente
//            2, 2, 5
//    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", /*"flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"*/};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<ScpCajabanco> container;

    private PersistanceService comprobanteService;

    public OperacionesListView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridCajaFooter;

    private Character moneda ='0';

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(ScpComprobantedetalle.class, getService().getScpComprobantedetalleRep().findByFecFechaBetween(filterInitialDate, new Date()));
        container.addNestedContainerBean("scpDestino");
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(false);
        grid.setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());

        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  grid.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(grid);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta, this);

        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        grid.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.select(moneda);

//        DataFilterUtil.bindComboBox(selFiltroCaja, "id.codCtacontable",
//                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
//                "txtDescctacontable");

        selMoneda.setNullSelectionAllowed(false);
//        selMoneda.addValueChangeListener(e -> {
//            if (e.getProperty().getValue() != null) {
//                moneda = (Character)e.getProperty().getValue();
//                container.removeContainerFilters("codTipomoneda");
//                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
//                ViewUtil.filterColumnsByMoneda(gridCaja, moneda);
//                viewLogic.calcFooterSums();
//                DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
//                        DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
//                        "txtDescctacontable");
//            }
//            viewLogic.setSaldoDelDia();
//            viewLogic.setSaldosFinal();
//        });
//        container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
//        selFiltroCaja.addValueChangeListener(e -> {
//            if (e.getProperty().getValue() != null) {
//                container.removeContainerFilters("codCtacontable");
//                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
//            } else {
//                container.removeContainerFilters("codCtacontable");
//            }
//            viewLogic.setSaldoDelDia();
//            viewLogic.setSaldosFinal();
//            viewLogic.calcFooterSums();
//        });

        //gridCaja.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //gridCaja.getColumn("flgEnviado").setHidden(true);

       /* gridCaja.getColumn("txtCorrelativo").setWidth(100);
        gridCaja.getColumn("codProyecto").setWidth(100);
        gridCaja.getColumn("codTercero").setWidth(100);
*/
        // Add filters
        //ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(grid);

        gridCajaFooter = grid.appendFooterRow();

        // Set Saldos Inicial
//    fechaDesde.addValueChangeListener(ev -> refreshCajas());
//    fechaHasta.addValueChangeListener(ev -> refreshCajas());

        //viewLogic.init(this);
    }

//    private void refreshCajas() {
//        if (fechaDesde.getValue()!=null) {
//            DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
//                    DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
//                    "txtDescctacontable");
//            viewLogic.calcFooterSums();
//            viewLogic.setSaldosFinal();
//        }
//    }
    @Override
    public void refreshData() {
        SortOrder[] sortOrders = grid.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        grid.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectMoneda(Character moneda) {
        selMoneda.select(moneda);
    }

    @Override
    public void selectItem(VsjItem item) {
        //return null;

    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        grid.sort("fecFecha", SortDirection.DESCENDING);
    }
//
//    private void setItemLogic(ItemClickEvent event) {
//        if (event.isDoubleClick()) {
//            Object id = event.getItem().getItemProperty("codCajabanco").getValue();
//            ScpCajabanco vcb = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
//            viewLogic.editarComprobante(vcb);
//        }
//    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRows() {
        return grid.getSelectedRows();
    }

    public ScpCajabanco getSelectedRow() {
        if (grid.getSelectedRows().toArray().length>0)
            return (ScpCajabanco) grid.getSelectedRows().toArray()[0];
        else
            return null;
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public PersistanceService getService() {
        return comprobanteService;
    }

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date filterInitialDate) {
        this.filterInitialDate = filterInitialDate;
    }

    public DateField getFechaDesde() {
        return fechaDesde;
    }

    public DateField getFechaHasta() {
        return fechaHasta;
    }

    public ComboBox getSelMoneda() {
        return selMoneda;
    }

    public TextField getSaldoCaja() {
        return saldoCaja;
    }

    public Character getMoneda() {
        return moneda;
    }

    public BeanItemContainer<ScpCajabanco> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridCajaFooter() {
        return gridCajaFooter;
    }

    public ComprobanteView getComprobView() {
        throw new NotImplementedException();
    }

}
