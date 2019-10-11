package org.sanjose.views.rendicion;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.model.VsjItem;
import org.sanjose.util.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.Viewing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class RendicionManejoView extends RendicionManejoUI implements NavigatorViewing, Viewing, GridViewing  {

    public static final String VIEW_NAME = "Manejo de Rendiciones";

    public String getWindowTitle() {
        return "Registro de rendiciones";
    }

    private final RendicionManejoLogic viewLogic = new RendicionManejoLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{ "codOrigen", "codComprobante", "fecComprobante", "txtGlosa",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Origen", "Comprobante", "Fecha", "Glosa", "Enviado", "Origen Contab", "Voucher Contab", "Ingresado por"
    };
    private final int[] FILTER_WIDTH = new int[]{
            3, 3, 4, 15, 3, 3, 2
//            5, 6, 4, 4,
//            5, 15, 6, 6, 6, 6, 6, 6, //
//            6, 4, 6, 5, 5, 2, // Tipo Doc
//            4, 5, 5, 5, 4, // Fuente
//            2, 2, 5
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{/*"txtCorrelativo"*/ /*"flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"*/};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<ScpRendicioncabecera> container;

    private RendicionService comprobanteService;

    public RendicionManejoView(RendicionService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridFooter;

    private Character moneda ='0';

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(ScpRendicioncabecera.class, getService().getRendicioncabeceraRep().findByFecComprobanteBetween(filterInitialDate, new Date()));
        //container.addNestedContainerBean("scpDestino");
        //container.addNestedContainerBean("msgUsuario");
        gridCaja.setContainerDataSource(container);
        gridCaja.setEditorEnabled(false);
        gridCaja.setSortOrder(Sort.by("fecComprobante", SortDirection.DESCENDING).then("codComprobante", SortDirection.DESCENDING).build());

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  gridCaja.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersRendicionesPreviousMonth(container, fechaDesde, fechaHasta, this);

        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecComprobante").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

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
                ViewUtil.filterColumnsByMoneda(gridCaja, moneda);

//                DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
//                        DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
//                        "txtDescctacontable");
            }
        });
        container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
        gridCaja.addItemClickListener(this::setItemLogic);
        gridCaja.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridCaja.getColumn("flgEnviado").setHidden(true);

        viewLogic.init(this);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecComprobante", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRowsRendiciones(gridCaja);

        gridFooter = gridCaja.appendFooterRow();
    }

    public void refreshData() {
        viewLogic.refreshData();
    }

    @Override
    public void selectItem(VsjItem item) {
        gridCaja.select(item);        for (Object vcb : container.getItemIds()) {
            if (((ScpRendicioncabecera)vcb).getCodRendicioncabecera().equals(((ScpRendicioncabecera)item).getCodRendicioncabecera())) {
                gridCaja.select(vcb);
                return;
            }
        }
    }


    @Override
    public void selectMoneda(Character moneda) {
        selMoneda.select(moneda);
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
       viewLogic.filter(fechaDesde, fechaHasta);
    }

    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codRendicioncabecera").getValue();
            ScpRendicioncabecera vcb = getService().getRendicioncabeceraRep().findByCodRendicioncabecera((Integer) id);
            viewLogic.editarRendicion(vcb);
        }
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

    public ScpRendicioncabecera getSelectedRow() {
        if (gridCaja.getSelectedRows().toArray().length>0)
            return (ScpRendicioncabecera) gridCaja.getSelectedRows().toArray()[0];
        else
            return null;
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public RendicionService getService() {
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

    public Grid getGridCaja() {
        return gridCaja;
    }

    public Button getBtnVerImprimir() {
        return btnVerImprimir;
    }

    public Button getBtnModificar() {
        return btnModificar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Character getMoneda() {
        return moneda;
    }

    public BeanItemContainer<ScpRendicioncabecera> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }

    public Button getBtnNueva() {
        return btnNueva;
    }

}
