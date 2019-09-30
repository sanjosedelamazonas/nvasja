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
import org.sanjose.MainUI;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.SumFooter;
import org.sanjose.views.sys.Viewing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.sanjose.util.GenUtil.PEN;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CajaManejoView extends CajaManejoUI implements CajaViewing, NavigatorViewing, Viewing, GridViewing, SumFooter {

    public static final String VIEW_NAME = "Manejo de Caja";

    public String getWindowTitle() {
        return "Registro de caja diario";
    }

    private final CajaManejoLogic viewLogic = new CajaManejoLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "scpDestino.txtNombredestino", "txtGlosaitem", "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codCtacontable", "codContraparte", "codDestinoitem", "codContracta", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "codCtacontable", "codContraparte", "codDestinoitem", "codContracta", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Responsable", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Cta Cont.", "Lug. Gasto", "Cod. Aux", "Cuenta", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Env", "Origen", "Comprobante"
    };
    private final int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 15, 6, 6, 6, 6, 6, 6, //
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2, 2, 5
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", /*"flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"*/};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<ScpCajabanco> container;

    private ComprobanteService comprobanteService;

    public CajaManejoView(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridCajaFooter;

    public Character getMoneda() {
        return moneda;
    }

    private Character moneda ='0';

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(ScpCajabanco.class, getService().getCajabancoRep().findByFecFechaBetween(filterInitialDate, new Date()));
        container.addNestedContainerBean("scpDestino");
        gridCaja.setContainerDataSource(container);
        gridCaja.setEditorEnabled(false);
        gridCaja.setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  gridCaja.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta, this);

        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.select(moneda);
        DataFilterUtil.bindComboBox(selFiltroCaja, "id.codCtacontable",
                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                "txtDescctacontable");

        selMoneda.setNullSelectionAllowed(false);
        selMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(gridCaja, moneda);

                DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
                        DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                        "txtDescctacontable");
            }
            viewLogic.setSaldoDelDia();
            viewLogic.setSaldosFinal();
        });
        container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
        selFiltroCaja.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codCtacontable");
            }
            viewLogic.setSaldoDelDia();
            viewLogic.setSaldosFinal();
            calcFooterSums();
        });

        gridCaja.addItemClickListener(this::setItemLogic);
        //gridCaja.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //gridCaja.getColumn("flgEnviado").setHidden(true);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic, this);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRows(gridCaja);

        gridCajaFooter = gridCaja.appendFooterRow();
        calcFooterSums();

        // Set Saldos Inicial
        fechaDesde.addValueChangeListener(ev -> refreshCajas());
        fechaHasta.addValueChangeListener(ev -> refreshCajas());

        viewLogic.init(this);
    }

    private void refreshCajas() {
        DataFilterUtil.refreshComboBox(selFiltroCaja, "id.codCtacontable",
                DataUtil.getCajas(fechaDesde.getValue(), getService().getPlanRepo(), moneda),
                "txtDescctacontable");
        calcFooterSums();
        viewLogic.setSaldosFinal();
    }

    public void refreshData() {
        SortOrder[] sortOrders = gridCaja.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridCaja.setSortOrder(Arrays.asList(sortOrders));
        calcFooterSums();
        viewLogic.setSaldosFinal();
    }

    @Override
    public void selectMoneda(Character moneda) {
        selMoneda.select(moneda);
    }

    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (ScpCajabanco scp : container.getItemIds()) {
            sumDebesol = sumDebesol.add(scp.getNumDebesol());
            sumHabersol = sumHabersol.add(scp.getNumHabersol());
            sumDebedolar = sumDebedolar.add(scp.getNumDebedolar());
            sumHaberdolar = sumHaberdolar.add(scp.getNumHaberdolar());
            sumDebemo = sumDebemo.add(scp.getNumDebemo());
            sumHabermo = sumHabermo.add(scp.getNumHabermo());
        }
        gridCajaFooter.getCell("numDebesol").setText(df.format(sumDebesol));
        gridCajaFooter.getCell("numHabersol").setText(df.format(sumHabersol));
        gridCajaFooter.getCell("numDebedolar").setText(df.format(sumDebedolar));
        gridCajaFooter.getCell("numHaberdolar").setText(df.format(sumHaberdolar));
        gridCajaFooter.getCell("numDebemo").setText(df.format(sumDebemo));
        gridCajaFooter.getCell("numHabermo").setText(df.format(sumHabermo));

        Arrays.asList(new String[] { "numDebesol", "numDebesol", "numHabersol", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> gridCajaFooter.getCell(e).setStyleName("v-align-right strong"));
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        gridCaja.setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());
        calcFooterSums();
    }

    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codCajabanco").getValue();
            ScpCajabanco vcb = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
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

    @Override
    public Collection<Object> getSelectedRows() {
        return gridCaja.getSelectedRows();
    }

    public ScpCajabanco getSelectedRow() {
        if (gridCaja.getSelectedRows().toArray().length>0)
            return (ScpCajabanco) gridCaja.getSelectedRows().toArray()[0];
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
