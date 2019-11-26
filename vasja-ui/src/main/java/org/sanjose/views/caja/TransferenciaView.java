package org.sanjose.views.caja;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class TransferenciaView extends TransferenciaUI implements ComprobanteViewing, Viewing {

    public static final String VIEW_NAME = "Transferencia";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    static final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol"
    };
    static final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/."
    };
    static final String[] VISIBLE_COLUMN_IDS_USD = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebedolar", "numHaberdolar"
    };
    static final String[] VISIBLE_COLUMN_NAMES_USD = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing $", "Egr $"
    };
    static final String[] VISIBLE_COLUMN_IDS_EUR = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebemo", "numHabermo"
    };
    static final String[] VISIBLE_COLUMN_NAMES_EUR = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing €", "Egr €"
    };
    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{};
    private static final Logger log = LoggerFactory.getLogger(TransferenciaView.class);

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyectoTercero, tipoProyectoTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };
    TransferenciaLogic viewLogic = null;
    private BeanItemContainer<ScpCajabanco> container;
    private PersistanceService comprobanteService;

    private Window subWindow;

    public TransferenciaView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @Override
    public void init() {
        viewLogic = new TransferenciaLogic();
        viewLogic.init(this);
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirBtn.setEnabled(false);
        nuevoComprobante.setEnabled(false);

        viewLogic.setupEditComprobanteView();

        // Grid
        //noinspection unchecked
        container = new BeanItemContainer(ScpCajabanco.class, new ArrayList());
        gridTrans.setContainerDataSource(container);
        gridTrans.setEditorEnabled(false);
        gridTrans.sort("fecFregistro", SortDirection.DESCENDING);

        gridTrans.getColumn("txtGlosaitem").setWidth(150);

        gridTrans.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                viewLogic.editarComprobante();
                //viewLogic.viewComprobante();
            }
        });

        ViewUtil.setColumnNames(gridTrans, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridTrans);

        ViewUtil.colorizeRows(gridTrans);

        gridTrans.setSelectionMode(Grid.SelectionMode.SINGLE);

        setSaldoTrans();
    }

    public ScpCajabanco getSelectedRow() {
        return (ScpCajabanco) gridTrans.getSelectedRow();
    }

    @Override
    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            if (f!=selMoneda || !enabled) f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public BeanItemContainer<ScpCajabanco> getContainer() {
        return container;
    }

    private BigDecimal calcDifference() {
        BigDecimal total = new BigDecimal(0.00);
        if (container != null) {
            for (ScpCajabanco cajabanco : container.getItemIds()) {
                if (isPEN())
                    total = total.add(cajabanco.getNumDebesol()).subtract(cajabanco.getNumHabersol());
                else if (isUSD())
                    total = total.add(cajabanco.getNumDebedolar()).subtract(cajabanco.getNumHaberdolar());
                else
                    total = total.add(cajabanco.getNumDebemo()).subtract(cajabanco.getNumHabermo());
            }
        }
        return total;
    }

    public void setSaldoTrans() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
            order_summary_layout.removeStyleName("order-summary-layout-eur");
        } else if (isUSD()) {
            order_summary_layout.addStyleName("order-summary-layout-usd");
            order_summary_layout.removeStyleName("order-summary-layout-eur");
        } else {
            order_summary_layout.addStyleName("order-summary-layout-eur");
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        }
        saldoTotal.setContentMode(ContentMode.HTML);
        saldoTotal.setValue("Differencia:" +
                "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : isUSD() ? "$ " : "€") + calcDifference().toString() + "</span>");

        if (container != null && !container.getItemIds().isEmpty() && calcDifference().compareTo(new BigDecimal(0.00)) == 0
                && !guardarBtn.isEnabled() && viewLogic.getState().isEdited())
            finalizarTransBtn.setEnabled(true);
        else
            finalizarTransBtn.setEnabled(false);
        ViewUtil.alignMontosInGrid(gridTrans);
    }


    @Override
    public void refreshData(Character moneda) {
        //if (moneda!=null) MainUI.get().getCajaManejoView().selectMoneda(moneda);
        //MainUI.get().getCajaManejoView().refreshData();
        if (moneda!=null) viewLogic.navigatorView.selectMoneda(moneda);
        viewLogic.navigatorView.refreshData();
    }

    @Override
    public void setSaldoDeCajas() {
    }


    private Character getMonedaActiva() {
        if (selMoneda.getValue() == null && (container == null || container.getItemIds().isEmpty())) return PEN;
        if (selMoneda.getValue() == null) {
            return container.getItemIds().get(0).getCodTipomoneda();
        } else {
            return selMoneda.getValue().toString().charAt(0);
        }
    }

    private boolean isPEN() {
        return PEN.equals(getMonedaActiva());
    }

    private boolean isUSD() {
        return USD.equals(getMonedaActiva());
    }

    public OptionGroup getTipoProyectoTercero() {
        return tipoProyectoTercero;
    }

    @Override
    public ComboBox getSelProyectoTercero() {
        return selProyectoTercero;
    }

    public TextField getNumVoucher() {
        return numVoucher;
    }

    public ComboBox getSelFuente() {
        return selFuente;
    }

    public TextField getSaldoProyPEN() {
        return saldoProyPEN;
    }

    public TextField getSaldoProyUSD() {
        return saldoProyUSD;
    }

    public TextField getSaldoProyEUR() {
        return saldoProyEUR;
    }

    public OptionGroup getSelMoneda() {
        return selMoneda;
    }

    public NumberField getNumIngreso() {
        return numIngreso;
    }

    public NumberField getNumEgreso() {
        return numEgreso;
    }

    public ComboBox getSelCaja() {
        return selCaja;
    }

    public TextField getSaldoCaja() {
        return saldoCaja;
    }

    public TextField getGlosa() {
        return glosa;
    }

    public ComboBox getSelResponsable() {
        return selResponsable;
    }

    public Button getBtnResponsable() {
        return btnResponsable;
    }

    public ComboBox getSelLugarGasto() {
        return selLugarGasto;
    }

    public ComboBox getSelTipoMov() {
        return selTipoMov;
    }

    public ComboBox getSelCtaContable() {
        return selCtaContable;
    }

    public ComboBox getSelRubroInst() {
        return selRubroInst;
    }

    public ComboBox getSelRubroProy() {
        return selRubroProy;
    }

    public ComboBox getSelCodAuxiliar() {
        return selCodAuxiliar;
    }

    public Button getBtnDestino() {
        return btnDestino;
    }

    public PopupDateField getFechaDoc() {
        return fechaDoc;
    }

    public ComboBox getSelTipoDoc() {
        return selTipoDoc;
    }

    public TextField getSerieDoc() {
        return serieDoc;
    }

    public TextField getNumDoc() {
        return numDoc;
    }

    public Label getSaldoTotal() {
        return saldoTotal;
    }

    public Button getCerrarBtn() {
        return cerrarBtn;
    }

    public Button getGuardarBtn() {
        return guardarBtn;
    }

    public Button getModificarBtn() {
        return modificarBtn;
    }

    public Button getEliminarBtn() {
        return eliminarBtn;
    }

    public Button getNuevoComprobante() {
        return nuevoComprobante;
    }

    public Button getImprimirBtn() {
        return imprimirBtn;
    }

    public Button getFinalizarTransBtn() {
        return finalizarTransBtn;
    }

    public Button getEliminarTransfBtn() {
        return eliminarTransfBtn;
    }

    public CssCheckBox getChkEnviado() {
        return chkEnviado;
    }

    public TextField getTxtOrigen() {
        return txtOrigen;
    }

    public TextField getTxtNumCombrobante() {
        return txtNumCombrobante;
    }

    public Grid getGridTrans() {
        return gridTrans;
    }

    public Button getAnularBtn() {
        return anularBtn;
    }

    public Button getImprimirTotalBtn() {
        return imprimirTotalBtn;
    }

    public Button getNuevaTransBtn() {
        return nuevaTransBtn;
    }

    public Label getLblSaldo() {
        return this.lblSaldo;
    }

    @Override
    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    @Override
    public PersistanceService getService() {
        return comprobanteService;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    @Override
    public Window getSubWindow() {
        return subWindow;
    }

    @Override
    public void setSubWindow(Window subWindow) {
        this.subWindow = subWindow;
    }
}
