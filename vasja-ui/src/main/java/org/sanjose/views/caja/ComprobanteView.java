package org.sanjose.views.caja;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class ComprobanteView extends ComprobanteUI implements ComprobanteViewing, Viewing {

    public static final String VIEW_NAME = "Caja";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(ComprobanteView.class);

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyectoTercero, tipoProyectoTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };
    ComprobanteLogic viewLogic;
    private PersistanceService comprobanteService;
    private Window subWindow;

    public ComprobanteView(){
    }

    public ComprobanteView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public void init(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
        init();
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        modificarBtn.setVisible(false);
        cerrarBtn.setVisible(false);

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
        //TODO DISABLED Saldos in Comprobante View
        boolean disable = true;
        if (disable) return;
        //

/*
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
        cajaSaldosLayout.removeAllComponents();
        if (dataFechaComprobante.getValue() != null && selMoneda.getValue() != null) {
            BigDecimal total = new BigDecimal(0.00);
            for (ScpPlancontable caja : DataUtil.getCajas(getDataFechaComprobante().getValue(), getService().getPlanRepo(), selMoneda.getValue().toString().charAt(0))) {

                BigDecimal saldo = MainUI.get().getProcUtil().getSaldoCaja(
                        //TODO - change to saldo final del dia!!!
                        //GenUtil.getEndOfDay(GenUtil.dateAddDays(dataFechaComprobante.getValue(),-1)),
                        GenUtil.getEndOfDay(dataFechaComprobante.getValue()),
                        caja.getId().getCodCtacontable()
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
                    "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : isUSD() ? "$ " : "€") + total.toString() + "</span>");
        }
*/
    }

    @Override
    public void refreshData(Character moneda) {
        //if (moneda!=null) MainUI.get().getCajaManejoView().selectMoneda(moneda);
        if (moneda!=null) viewLogic.navigatorView.selectMoneda(moneda);
        viewLogic.navigatorView.refreshData();
        //MainUI.get().getCajaManejoView().refreshData();
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    @Override
    public ComboBox getSelProyectoTercero() {
        return selProyectoTercero;
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

    public OptionGroup getTipoProyectoTercero() {
        return tipoProyectoTercero;
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
/*
    public CssLayout getCajaSaldosLayout() {
        return cajaSaldosLayout;
    }

    public Label getSaldoTotal() {
        return saldoTotal;
    }*/

    public Button getCerrarBtn() {
        return cerrarBtn;
    }

    public Button getGuardarBtn() {
        return guardarBtn;
    }

    @Override
    public Button getAnularBtn() {
        return anularBtn;
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

    @Override
    public Button getImprimirTotalBtn() {
        return null;
    }

    @Override
    public Button getFinalizarTransBtn() {
        return null;
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

    public Label getLblSaldo() {
        return this.lblSaldo;
    }

    public PersistanceService getService() {
        return comprobanteService;
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
