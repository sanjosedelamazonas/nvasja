package org.sanjose.views.rendicion;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.sanjose.converter.ZeroOneToBooleanConverter;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.SubWindowing;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.sanjose.util.GenUtil.*;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also ... for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class RendicionSimpleOperView extends RendicionSimpleOperUI implements Viewing, SubWindowing {

    public static final String VIEW_NAME = "Rendiciones";
    public String getWindowTitle() {
        return VIEW_NAME;
    }

    private Window subWindow;
    static final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{
            "id.numNroitem", "codProyecto", "fecComprobantepago", "codTipocomprobantepago", "txtSeriecomprobantepago",
            "txtComprobantepago", "codDestino", "txtGlosaitem",
            "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar", "numHabermo", "numDebemo",
            "codCtacontable", "codContraparte", "codCtaespecial", "codFinanciera", "codCtaproyecto"
    };
    static final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{
            "It", "Proyecto", "Fecha Doc", "Tipo Doc", "Nro de serie",
            "Nro de doc", "Razon social/Nombre", "Glosa por detalle",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Cuenta", "Lug. Gasto", "Rubro Inst", "Fuente", "Partida P."

    };
    static final String[] HIDDEN_COLUMN_NAMES_PEN = new String[]{
            //"codCtaarea", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo", "codTipomoneda", "numTcvdolar", "numTcmo"
    };

    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private static final Logger log = LoggerFactory.getLogger(RendicionSimpleOperView.class);

    private final Field[] allFields = new Field[]{ fechaPago,
    };
    private final Field[] cabezeraFields = new Field[]{dataFechaComprobante, txtGlosaCabeza, selResponsable1, selMoneda,
            dataFechaRegistro};

    private RendicionSimpleLogic viewLogic = null;
    private BeanItemContainer<ScpRendiciondetalle> container;
    //private GeneratedPropertyContainer gpContainer;
    private org.sanjose.views.sys.PersistanceService PersistanceService;

    public boolean isVistaFull = false;

    public Grid.FooterRow gridFooter;

    public RendicionSimpleOperView() {
    }

    public RendicionSimpleOperView(PersistanceService PersistanceService) {
        this.PersistanceService = PersistanceService;
        setSizeFull();
    }

    public void init(PersistanceService PersistanceService) {
        this.PersistanceService = PersistanceService;
        init();
    }

    @Override
    public void init() {
        viewLogic = new RendicionSimpleLogic();
        viewLogic.init(this);
        addStyleName("crud-view");
        //txtTotalAnticipo

        // Grid
        initGrid();

        viewLogic.setupEditComprobanteView();

        chkEnviado.setSimpleMode(false);
        chkEnviado.setConverter(new ZeroOneToBooleanConverter());

        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                viewLogic.viewComprobante();
            }
        });

        gridFooter = grid.appendFooterRow();
        //getNumTotalAnticipo().setValue(GenUtil.numFormat(new BigDecimal(0.00)));
        getNumTotalAnticipo().setEnabled(false);
    }

    public void initGrid(){
        // Grid
        //noinspection unchecked
        container = new BeanItemContainer(ScpRendiciondetalle.class, new ArrayList());
        container.addNestedContainerBean("id");
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.sort("fecFregistro", SortDirection.DESCENDING);

        grid.sort("id.numNroitem", SortDirection.ASCENDING);
        //grid.getColumn("Item").setWidth(36);
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_NAMES_PEN)
                .forEach( e -> grid.getColumn(e).setHidden(true));

        ViewUtil.alignMontosInGrid(grid);

        ViewUtil.colorizeRows(grid, ScpRendiciondetalle.class);
        grid.setWidth("100%");
        setTotal(null);
    }

    public ScpRendiciondetalle getSelectedRow() {
        if (grid.getSelectedRows().isEmpty()) return null;
        return grid.getSelectedRows().toArray(new ScpRendiciondetalle[0])[0];
    }

    public void setEnableDetalleFields(boolean enabled) {
        for (Field f : allFields) f.setEnabled(enabled);
    }


    public void setEnableCabezeraFields(boolean enabled) {
        for (Field f : cabezeraFields) f.setEnabled(enabled);
        btnResponsable.setEnabled(enabled);
    }

    public void setTotal(Character locMoneda) {
        if (locMoneda == null) {
            return;
        }
        if (locMoneda.equals(PEN)) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
            order_summary_layout.removeStyleName("order-summary-layout-eur");
        } else if (locMoneda.equals(USD)) {
            order_summary_layout.removeStyleName("order-summary-layout-eur");
            order_summary_layout.addStyleName("order-summary-layout-usd");
        } else {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
            order_summary_layout.addStyleName("order-summary-layout-eur");
        }

        if (!isVistaFull) ViewUtil.filterColumnsByMoneda(grid, locMoneda);
        ViewUtil.alignMontosInGrid(grid);
        getContainer().sort(new Object[]{"txtCorrelativo"}, new boolean[]{true});

        BigDecimal gastoTotal = calcTotal(locMoneda);
        getTxtGastoTotal().setValue(GenUtil.numFormat(gastoTotal));
        viewLogic.setGastoTotal(gastoTotal);
        NumberFormat nf = NumberFormat.getInstance(ConfigurationUtil.getLocale());
        try {
            BigDecimal totalAnticipo = null;
            if (getNumTotalAnticipo().getValue()==null)
                totalAnticipo = new BigDecimal(0.00);
            else
                totalAnticipo = new BigDecimal(nf.parse(getNumTotalAnticipo().getValue()).toString());
            if (viewLogic.rendicioncabecera!=null) {
                viewLogic.rendicioncabecera.setNumSaldopendiente(totalAnticipo.subtract(gastoTotal));
            }
            getTxtSaldoPendiente().setValue(GenUtil.numFormat(totalAnticipo.subtract(gastoTotal)));
        } catch (ParseException pe) {
            log.debug("Problem parsing total anticipo");
        }
        getNumDifsol().setValue(GenUtil.numFormat(new BigDecimal(-1).multiply(calcTotal(PEN))));
        getNumDifdolar().setValue(GenUtil.numFormat(new BigDecimal(-1).multiply(calcTotal(USD))));
        getNumDifmo().setValue(GenUtil.numFormat(new BigDecimal(-1).multiply(calcTotal(EUR))));
    }

    public BigDecimal calcTotal(Character locMoneda) {
        BigDecimal total = new BigDecimal(0.00);
        for (ScpRendiciondetalle det: container.getItemIds()) {
            switch (locMoneda) {
                case '0':
                    total = total.add(det.getNumDebesol());
                    break;
                case '1':
                    total = total.add(det.getNumDebedolar());
                    break;
                case '2':
                    total = total.add(det.getNumDebemo());
                    break;
            }
        }
        return total;
    }

    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (ScpRendiciondetalle scp : getContainer().getItemIds()) {
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

        Arrays.asList(new String[] { "numDebesol", "numDebesol", "numHabersol", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> getGridFooter().getCell(e).setStyleName("v-align-right strong"));
    }

    public BeanItemContainer<ScpRendiciondetalle> getContainer() {
        return container;
    }

    public void refreshData() {
        viewLogic.navigatorView.refreshData();
    }

    public PersistanceService getService() {
        return PersistanceService;
    }

    public TextField getTxtOrigen() {
        return txtOrigen;
    }

    public TextField getNumVoucher() {
        return numVoucher;
    }

    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    public TextField getTxtGlosaCabeza() {
        return txtGlosaCabeza;
    }

    public ComboBox getSelResponsable1() {
        return selResponsable1;
    }

    public Button getBtnResponsable() {
        return btnResponsable;
    }

    public OptionGroup getSelMoneda() {
        return selMoneda;
    }

    public TextField getNumTotalAnticipo() {
        return txtTotalAnticipo;
    }

    public TextField getTxtGastoTotal() {
        return txtGastoTotal;
    }

    public TextField getTxtSaldoPendiente() {
        return txtSaldoPendiente;
    }

    public TextField getTxtIngresadoPor() {
        return txtIngresadoPor;
    }

    public PopupDateField getDataFechaRegistro() {
        return dataFechaRegistro;
    }

    public CssCheckBox getChkEnviado() {
        return chkEnviado;
    }

    public Grid getGrid() {
        return grid;
    }


    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnNewItem() {
        return btnNewItem;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Button getBtnCerrar() {
        return btnCerrar;
    }

    public Button getBtnVerVoucher() {
        return btnVerVoucher;
    }

    public TextField getTxtOrigenlace() {
        return txtOrigenlace;
    }

    public TextField getTxtComprobenlace() {
        return txtComprobenlace;
    }

    public ComboBox getSetAllProyecto() {
        return setAllProyecto;
    }

    public ComboBox getSetAllFuente() {
        return setAllFuente;
    }

    public ComboBox getSetAllPartida() {
        return setAllPartida;
    }

    public ComboBox getSetAllLugarGasto() {
        return setAllLugarGasto;
    }

    public ComboBox getSetAllContable() {
        return setAllContable;
    }

    public ComboBox getSetAllRubrInst() {
        return setAllRubrInst;
    }

    public DateField getSetAllFechaDoc() {
        return setAllFechaDoc;
    }

    public DateField getSetAllFechaPago() {
        return setAllFechaPago;
    }

    public TextField getNumDifsol() {
        return numDifsol;
    }

    public TextField getNumDifdolar() {
        return numDifdolar;
    }

    public TextField getNumDifmo() {
        return numDifmo;
    }

    public Button getBtnAjustar() {
        return btnAjustar;
    }

    public TextField getSetAllTcambioText() {
        return setAllTcambioText;
    }

    public Button getBtnEliminarRend() {
        return btnEliminarRend;
    }

    public Button getBtnImportar() {
        return btnImportar;
    }

    public Button getBtnRegAnticipo() {
        return btnRegAnticipo;
    }

    public Button getBtnGuardarExcel() {
        return btnGuardarExcel;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public RendicionSimpleLogic getViewLogic() {
        return viewLogic;
    }

    @Override
    public Window getSubWindow() {
        return subWindow;
    }

    @Override
    public void setSubWindow(Window subWindow) {
        this.subWindow = subWindow;
    }

    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }


}
