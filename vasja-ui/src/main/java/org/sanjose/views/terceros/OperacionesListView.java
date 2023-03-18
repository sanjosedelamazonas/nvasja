package org.sanjose.views.terceros;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import net.sf.jasperreports.engine.JRException;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.bean.VsjOperaciontercero;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjItem;
import org.sanjose.util.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static org.sanjose.util.TercerosUtil.getAllSaldoPorFecha;

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

    private static final Logger log = LoggerFactory.getLogger(OperacionesListView.class.getName());

    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id", "codVoucher", "fecComprobante", "txtGlosaitem",
            "txtDestinonombre", "codCtacontable",
            "numHabersol", "numDebesol", "numSaldosol", "numHaberdolar", "numDebedolar",  "numSaldodolar",
            "numHabermo", "numDebemo", "numSaldomo",
            "codContraparte"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {

    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "It", "Numero", "Fecha", "Descripcion",
            "Entregado a/por", "Contra Cta,",
            "Ing S/.", "Egr S/.", "Saldo S/.", "Ing $", "Egr $", "Saldo $",
            "Ing €", "Egr €", "Saldo €",
            "Cuenta"
    };
    private final int[] FILTER_WIDTH = new int[]{
            2, 5, 4, 15,
            10, 6, //
            5, 5, 5, 5, 5, 5, // Fuente
            6
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<VsjOperaciontercero> container;

    private PersistanceService comprobanteService;

    public OperacionesListView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridFooter;

    private Character moneda ='0';

    private List<String> codigosTerc = new ArrayList<>();

    private String curCodTercero = null;

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        container = new BeanItemContainer(VsjOperaciontercero.class, new ArrayList());
        container.addNestedContainerBean("id");
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(false);
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  grid.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(grid);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersRendicionesPreviousMonth(container, fechaDesde, fechaHasta, this);
        fechaDesde.setValue(filterInitialDate);

        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        grid.getColumn("fecComprobante").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridFooter = grid.appendFooterRow();

        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.setNullSelectionAllowed(false);
        selMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(grid, moneda);
                setSaldos();
            }
        });
        // Run date filter
        //ViewUtil.filterComprobantes(container, "fecComprobante", fechaDesde, fechaHasta, this);

        // Set Saldos Inicial
        fechaDesde.addValueChangeListener(ev -> filter(fechaDesde.getValue(), fechaHasta.getValue()));
        fechaHasta.addValueChangeListener(ev -> filter(fechaDesde.getValue(), fechaHasta.getValue()));

        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        getBtnReporteImprimirCaja().addClickListener(clickEvent -> {
            try {
                TercerosUtil.generateTerceroOperacionesReport(
                        fechaDesde.getValue(), fechaHasta.getValue(), "PDF", curCodTercero, getService());
            } catch (JRException jre) {
                log.error("Problem: " + jre.toString());
            } catch (FileNotFoundException e) {
                log.error("Problem: " + e.toString());
            }
        });
    }

    private void setSaldos() {
        getSaldoInicial().setValue(getSaldoPorFecha(GenUtil.getBeginningOfDay(getFechaDesde().getValue())));
        getSaldoFinal().setValue(getSaldoPorFecha(GenUtil.getEndOfDay(getFechaHasta().getValue())));
        calcFooterSums();
    }

    private String getSaldoPorFecha(Date fecha) {
        if (fecha==null || curCodTercero==null) return "";
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        ProcUtil.Saldos res = getAllSaldoPorFecha(fecha, curCodTercero);
        Map<Character, BigDecimal> saldos = new HashMap<>();
        saldos.put('0', res.getSaldoPEN());
        saldos.put('1', res.getSaldoUSD());
        saldos.put('2', res.getSaldoEUR());
        return df.format(saldos.get(moneda));
    }

    
    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (VsjOperaciontercero scp : getContainer().getItemIds()) {
            sumDebesol = sumDebesol.add(scp.getNumDebesol());
            sumHabersol = sumHabersol.add(scp.getNumHabersol());
            sumDebedolar = sumDebedolar.add(scp.getNumDebedolar());
            sumHaberdolar = sumHaberdolar.add(scp.getNumHaberdolar());
            sumDebemo = sumDebemo.add(scp.getNumDebemo());
            sumHabermo = sumHabermo.add(scp.getNumHabermo());
        }
        getGridFooter().getCell("numDebesol").setText(df.format(sumDebesol));
        getGridFooter().getCell("numHabersol").setText(df.format(sumHabersol));
        getGridFooter().getCell("numDebedolar").setText(df.format(sumDebedolar));
        getGridFooter().getCell("numHaberdolar").setText(df.format(sumHaberdolar));
        getGridFooter().getCell("numDebemo").setText(df.format(sumDebemo));
        getGridFooter().getCell("numHabermo").setText(df.format(sumHabermo));

        Arrays.asList(new String[] { "numDebesol", "numHabersol", "numHaberdolar", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> getGridFooter().getCell(e).setStyleName("v-align-right strong"));
    }


    @Override
    public void refreshData() {
        if (codigosTerc.isEmpty()) {
            List<ScpDestino> destinosTerc = getService().getDestinoRepo().findByTxtUsuario(CurrentUser.get());
            destinosTerc.forEach(destino -> codigosTerc.add(destino.getCodDestino()));
            Map<String, String> codigosTercMap = new HashMap<>();
            codigosTerc.forEach(codigo -> codigosTercMap.put(codigo, codigo));
            DataFilterUtil.bindFixedStringValComboBox(selCuenta, "codTercero",
                    "Cuenta", codigosTercMap);
            selCuenta.setNullSelectionAllowed(false);
            selCuenta.addValueChangeListener(e -> {
                if (e.getProperty().getValue() != null) {
                    String codTercero = (String) e.getProperty().getValue();
                    container.removeContainerFilters("codTercero");
                    if (!GenUtil.strNullOrEmpty(codTercero))
                        container.addContainerFilter(new Compare.Equal("codTercero", codTercero));
                    //ViewUtil.filterColumnsByMoneda(grid, moneda);
                    //viewLogic.calcFooterSums();
                    curCodTercero = codTercero;
                    setSaldos();
                }
            });
            selCuenta.select(codigosTerc.get(0));
            selMoneda.select(moneda);
        }
        container.removeAllItems();
        filter(filterInitialDate, new Date());
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
        container.addAll(
                TercerosUtil.getAll(
                        fechaDesde,
                        fechaHasta,
                        codigosTerc,
                        curCodTercero,
                        getService(),
                        false)
        );
        //grid.sort("fecComprobante", SortDirection.ASCENDING);
        setSaldos();
    }

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

    public Character getMoneda() {
        return moneda;
    }

    public BeanItemContainer<VsjOperaciontercero> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }

    public ComprobanteView getComprobView() {
        throw new NotImplementedException();
    }

    public ComboBox getSelCuenta() {
        return selCuenta;
    }

    public Button getBtnReporteImprimirCaja() {
        return btnReporteImprimirCaja;
    }

    public TextField getSaldoInicial() {
        return saldoInicial;
    }

    public TextField getSaldoFinal() {
        return saldoFinal;
    }
}
