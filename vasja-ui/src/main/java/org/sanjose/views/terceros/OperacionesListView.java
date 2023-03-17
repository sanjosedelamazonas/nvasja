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
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpComprobantedetalle;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjItem;
import org.sanjose.util.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

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

    private final String[] VISIBLE_COLUMN_IDS = new String[]{"id.codComprobante",
            //"id.num_nroitem",
            "fecComprobante", "txtGlosaitem",
            //"scpDestino.txtNombredestino",
            "codDestino",
            "codCtacontable",
            "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar",  "numHabermo", "numDebemo"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {

    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Numero",
            //"Item",
            "Fecha", "Descripcion",
            "Entregado a/por", "Contra Cta,",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            //"Cuenta"
    };
//    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
//            5, 15, 6, 6, 6, 6, 6, 6, //
//            6, 4, 6, 5, 5, 2, // Tipo Doc
//            4, 5, 5, 5, 4, // Fuente
//            2, 2, 5
//    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<ScpComprobantedetalle> container;

    private PersistanceService comprobanteService;

    public OperacionesListView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridCajaFooter;

    private Character moneda ='0';

    private List<String> codigosTerc = new ArrayList<>();

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(ScpComprobantedetalle.class, new ArrayList());
        container.addNestedContainerBean("id");
        //container.addNestedContainerBean("scpDestino");
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(false);
        //grid.setSortOrder(Sort.by("fecComprobante", SortDirection.DESCENDING).then("id.codComprobante", SortDirection.ASCENDING).build());

        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  grid.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(grid);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersRendicionesPreviousMonth(container, fechaDesde, fechaHasta, this);

        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        grid.getColumn("fecComprobante").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.select(moneda);

//        DataFilterUtil.bindComboBox(selFiltroCaja, "id.codCtacontable",
//                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
//                "txtDescctacontable");

        selMoneda.setNullSelectionAllowed(false);
        selMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(grid, moneda);
                //viewLogic.calcFooterSums();
                //DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
                //        DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                //        "txtDescctacontable");
            }
            //view.setSaldoDelDia();
            //view.setSaldosFinal();
        });
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
        ViewUtil.filterComprobantes(container, "fecComprobante", fechaDesde, fechaHasta, this);

        //ViewUtil.colorizeRows(grid);

        gridCajaFooter = grid.appendFooterRow();

        // Set Saldos Inicial
        fechaDesde.addValueChangeListener(ev -> filter(fechaDesde.getValue(), fechaHasta.getValue()));
        fechaHasta.addValueChangeListener(ev -> filter(fechaDesde.getValue(), fechaHasta.getValue()));

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
        container.removeAllItems();
        if (codigosTerc.isEmpty()) {
            List<ScpDestino> destinosTerc = getService().getDestinoRepo().findByTxtUsuario(CurrentUser.get());
            destinosTerc.forEach(destino -> codigosTerc.add(destino.getCodDestino()));
        }
        container.addAll(getService().getScpComprobantedetalleRep().findByFecComprobanteBetweenAndCodTerceroIsIn(filterInitialDate, new Date(), codigosTerc));

        //SortOrder[] sortOrders = grid.getSortOrder().toArray(new SortOrder[1]);
        //filter(fechaDesde.getValue(), fechaHasta.getValue());
        //grid.setSortOrder(Arrays.asList(sortOrders));
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
        container.addAll(getService().getScpComprobantedetalleRep().findByFecComprobanteBetweenAndCodTerceroIsInAndCodCtacontableStartingWith(fechaDesde, fechaHasta, codigosTerc, "4"));

        //container.addAll(getService().getScpComprobantedetalleRep().findByFecComprobanteBetweenAndCodTerceroIsIn(filterInitialDate, new Date(), codigosTerc));
        //container.addAll(getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        grid.sort("fecComprobante", SortDirection.DESCENDING);
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

    public BeanItemContainer<ScpComprobantedetalle> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridCajaFooter() {
        return gridCajaFooter;
    }

    public ComprobanteView getComprobView() {
        throw new NotImplementedException();
    }

}
