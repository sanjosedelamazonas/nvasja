package org.sanjose.views.rendicion;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.sanjose.converter.ZeroOneToBooleanConverter;
import org.sanjose.model.*;
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

import static org.sanjose.util.GenUtil.EUR;
import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also ... for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class RendicionOperView extends RendicionOperUI implements Viewing, SubWindowing {

    public static final String VIEW_NAME = "Rendiciones Avanc.";
    public String getWindowTitle() {
        return VIEW_NAME;
    }

    private Window subWindow;
    static final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{
            "id.numNroitem", "codProyecto", "codFinanciera", "codCtaproyecto",
            "codContraparte", "codCtacontable", "codCtaactividad", "codCtaarea", "codCtaespecial",
            "fecComprobantepago", "fecPagocomprobantepago",
            "codTipomoneda", "numTcvdolar",
            "numDebesol", "numHabersol",
            "numDebedolar", "numHaberdolar",
            "numTcmo", "numDebemo", "numHabermo"
    };
    static final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{
            "It", "Proyecto", "Fuente", "Partida P.",
            "Lug. Gst.", "Contable", "Actividad", "Area", "Rubro Inst",
            "Fecha Doc", "Fecha Pago",
            "Mon", "TC $",
            "Gast S/.", "Ingr S/.",
            "Gast $", "Ingr $",
            "TC €", "Gast €", "Ingr €"
    };
    static final String[] HIDDEN_COLUMN_NAMES_PEN = new String[]{
            "codCtaarea", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo", "codTipomoneda", "numTcvdolar", "numTcmo"
    };

    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private static final Logger log = LoggerFactory.getLogger(RendicionOperView.class);

    private final Field[] allFields = new Field[]{ selTipoMov, selCodAuxiliar, fechaPago,
            selTipoDoc, fechaDoc, txtSerieDoc, txtNumDoc, txtGlosaDetalle
    };
    private final Field[] cabezeraFields = new Field[]{dataFechaComprobante, txtGlosaCabeza, selResponsable1, selMoneda,
            numTotalAnticipo, dataFechaRegistro};
    
    private RendicionLogic viewLogic = null;
    private BeanItemContainer<ScpRendiciondetalle> container;
    //private GeneratedPropertyContainer gpContainer;
    private org.sanjose.views.sys.PersistanceService PersistanceService;

    public boolean isVistaFull = false;

    public Grid.FooterRow gridFooter;

    public RendicionOperView() {
    }

    public RendicionOperView(PersistanceService PersistanceService) {
        this.PersistanceService = PersistanceService;
        setSizeFull();
    }

    public void init(PersistanceService PersistanceService) {
        this.PersistanceService = PersistanceService;
        init();
    }

    @Override
    public void init() {
        viewLogic = new RendicionLogic();
        viewLogic.init(this);
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numTotalAnticipo);

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
        getNumTotalAnticipo().setValue(GenUtil.numFormat(new BigDecimal(0.00)));
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

    public void toggleVista() {
        isVistaFull = !isVistaFull;
        if (!isVistaFull)
            ViewUtil.filterColumnsByMoneda(grid, (Character)getSelMoneda().getValue());
        else
            ViewUtil.filterColumnsByMoneda(grid, 'A');
        //getAjusteForm().setVisible(isVistaFull);
    }

    public ScpRendiciondetalle getSelectedRow() {
        if (grid.getSelectedRows().isEmpty()) return null;
        return grid.getSelectedRows().toArray(new ScpRendiciondetalle[0])[0];
    }

    public void setEnableDetalleFields(boolean enabled) {
        for (Field f : allFields) f.setEnabled(enabled);
        btnAuxiliar.setEnabled(enabled);
    }


    public void setEnableCabezeraFields(boolean enabled) {
        for (Field f : cabezeraFields) f.setEnabled(enabled);
        btnResponsable.setEnabled(enabled);
        btnAuxiliar.setEnabled(enabled);
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

    private BigDecimal calcTotal(Character locMoneda) {
        BigDecimal total = new BigDecimal(0.00);
        for (ScpRendiciondetalle det: container.getItemIds()) {
            switch (locMoneda) {
                case '0':
                    total = total.add(det.getNumDebesol()).subtract(det.getNumHabersol());
                    break;
                case '1':
                    total = total.add(det.getNumDebedolar()).subtract(det.getNumHaberdolar());
                    break;
                case '2':
                    total = total.add(det.getNumDebemo()).subtract(det.getNumHabermo());
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

    public NumberField getNumTotalAnticipo() {
        return numTotalAnticipo;
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

    public TextField getNumItem() {
        return numItem;
    }

    public TextField getTxtGlosaDetalle() {
        return txtGlosaDetalle;
    }

    public ComboBox getSelTipoMov() {
        return selTipoMov;
    }

    public PopupDateField getFechaPago() {
        return fechaPago;
    }

    public ComboBox getSelCodAuxiliar() {
        return selCodAuxiliar;
    }

    public Button getBtnAuxiliar() {
        return btnAuxiliar;
    }

    public PopupDateField getFechaDoc() {
        return fechaDoc;
    }

    public ComboBox getSelTipoDoc() {
        return selTipoDoc;
    }

    public TextField getTxtSerieDoc() {
        return txtSerieDoc;
    }

    public TextField getTxtNumDoc() {
        return txtNumDoc;
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

    public Button getBtnToggleVista() {
        return btnToggleVista;
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

    public Button getBtnSetAll() {
        return btnSetAll;
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


    @Override
    public void enter(ViewChangeEvent event) {
    }

    public RendicionLogic getViewLogic() {
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
