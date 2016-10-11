package org.sanjose.views.caja;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.VsjView;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;

import static org.sanjose.util.GenUtil.PEN;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class ComprobanteView extends ComprobanteUI implements IComprobanteView, VsjView {

    public static final String VIEW_NAME = "Caja";
    private static final Logger log = LoggerFactory.getLogger(ComprobanteView.class);

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };
    ComprobanteLogic viewLogic;
    private ComprobanteService comprobanteService;

    public ComprobanteView(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirBtn.setEnabled(false);

        viewLogic = new ComprobanteLogic();
        viewLogic.init(this);
        viewLogic.setupEditComprobanteView();
    }

    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public void setSaldoDeCajas() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        } else  {
            order_summary_layout.addStyleName("order-summary-layout-usd");
        }
        cajaSaldosLayout.removeAllComponents();
        if (dataFechaComprobante.getValue() != null && selMoneda.getValue() != null) {
            BigDecimal total = new BigDecimal(0.00);
            for (ScpPlancontable caja : DataUtil.getCajas(getDataFechaComprobante().getValue(), getService().getPlanRepo(), PEN.equals(selMoneda.getValue().toString().charAt(0)))) {

                BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(dataFechaComprobante.getValue(), caja.getId().getCodCtacontable()
                        , selMoneda.getValue().toString().charAt(0));
                Label salLbl = new Label();
                salLbl.setContentMode(ContentMode.HTML);
                salLbl.setValue(
                    caja.getId().getCodCtacontable() + " " + caja.getTxtDescctacontable() + ": <span class=\"order-sum\">"+  saldo + "</span");
                salLbl.setStyleName("order-item");
                cajaSaldosLayout.addComponent(salLbl);
                total = total.add(saldo);
            }
            saldoTotal.setContentMode(ContentMode.HTML);
            saldoTotal.setValue("Total :" +
                    "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + total.toString() + "</span>");
        }
    }

    private boolean isPEN() {
        return PEN.equals(selMoneda.getValue().toString().charAt(0));
    }

    @Override
    public void refreshData() {
        MainUI.get().getCajaManejoView().refreshData();
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    @Override
    public ComboBox getSelProyecto() {
        return selProyecto;
    }

    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
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

    public CssLayout getCajaSaldosLayout() {
        return cajaSaldosLayout;
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


    public ComprobanteService getService() {
        return comprobanteService;
    }
}
