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
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.VsjView;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.sanjose.util.GenUtil.PEN;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class TransferenciaView extends TransferenciaUI implements IComprobanteView, VsjView {

    public static final String VIEW_NAME = "Transferencia";
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
    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{};
    private static final Logger log = LoggerFactory.getLogger(TransferenciaView.class);

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };
    TransferenciaLogic viewLogic = null;
    private BeanItemContainer<VsjCajabanco> container;
    private ComprobanteService comprobanteService;

    public TransferenciaView(ComprobanteService comprobanteService) {
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
        container = new BeanItemContainer(VsjCajabanco.class, new ArrayList());
        gridTrans.setContainerDataSource(container);
        gridTrans.setEditorEnabled(false);
        gridTrans.sort("fecFregistro", SortDirection.DESCENDING);

        gridTrans.getColumn("txtGlosaitem").setWidth(150);

        gridTrans.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                VsjCajabanco vcb = (VsjCajabanco) selectionEvent.getSelected().toArray()[0];
                viewLogic.viewComprobante();

                if (vcb.isAnula() || vcb.isEnviado()) {
                    modificarBtn.setEnabled(false);
                    eliminarBtn.setEnabled(false);
                } else {
                    modificarBtn.setEnabled(true);
                    eliminarBtn.setEnabled(true);
                }
            }
        });

        ViewUtil.setColumnNames(gridTrans, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridTrans);

        ViewUtil.colorizeRows(gridTrans);

        gridTrans.setSelectionMode(Grid.SelectionMode.SINGLE);

        setSaldoTrans();
    }

    public VsjCajabanco getSelectedRow() {
        return (VsjCajabanco) gridTrans.getSelectedRow();
    }

    @Override
    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            if (f!=selMoneda || !enabled) f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public BeanItemContainer<VsjCajabanco> getContainer() {
        return container;
    }

    private BigDecimal calcDifference() {
        BigDecimal total = new BigDecimal(0.00);

        for (VsjCajabanco cajabanco : container.getItemIds()) {
            if (isPEN())
                total = total.add(cajabanco.getNumDebesol()).subtract(cajabanco.getNumHabersol());
            else
                total = total.add(cajabanco.getNumDebedolar()).subtract(cajabanco.getNumHaberdolar());
        }
        return total;
    }

    public void setSaldoTrans() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        } else  {
            order_summary_layout.addStyleName("order-summary-layout-usd");
        }

        saldoTotal.setContentMode(ContentMode.HTML);
        saldoTotal.setValue("Differencia:" +
                "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + calcDifference().toString() + "</span>");

        if (!container.getItemIds().isEmpty() && calcDifference().compareTo(new BigDecimal(0.00)) == 0 && !guardarBtn.isEnabled())
            finalizarTransBtn.setEnabled(true);
        else
            finalizarTransBtn.setEnabled(false);
    }


    @Override
    public void refreshData() {
        MainUI.get().getCajaManejoView().refreshData();
    }

    @Override
    public void setSaldoDeCajas() {
    }

    private boolean isPEN() {
        if (selMoneda.getValue() == null && container.getItemIds().isEmpty()) return true;
        if (selMoneda.getValue() == null) {
            return PEN.equals(container.getItemIds().get(0).getCodTipomoneda());
        } else {
            return PEN.equals(selMoneda.getValue().toString().charAt(0));
        }
    }

    @Override
    public ComboBox getSelProyecto() {
        return selProyecto;
    }


    public TextField getNumVoucher() {
        return numVoucher;
    }

    public ComboBox getSelFuente() {
        return selFuente;
    }

    public ComboBox getSelTercero() {
        return selTercero;
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

    public TextField getSaldoCajaPEN() {
        return saldoCajaPEN;
    }

    public TextField getSaldoCajaUSD() {
        return saldoCajaUSD;
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

    @Override
    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    @Override
    public ComprobanteService getService() {
        return comprobanteService;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}
