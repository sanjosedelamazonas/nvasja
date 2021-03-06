package org.sanjose.views.caja;

import com.vaadin.ui.*;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.views.sys.PersistanceService;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;

/**
 * VASJA
 * User: prubach
 * Date: 20.09.16
 */
public interface ComprobanteViewing {

    void setSaldoDeCajas();

    void setEnableFields(boolean enabled);

    void refreshData(Character moneda);

    ComboBox getSelProyectoTercero();

    TextField getNumVoucher();

    ComboBox getSelFuente();

    OptionGroup getTipoProyectoTercero();

    TextField getSaldoProyPEN();

    TextField getSaldoProyUSD();

    TextField getSaldoProyEUR();

    OptionGroup getSelMoneda();

    NumberField getNumIngreso();

    NumberField getNumEgreso();

    ComboBox getSelCaja();

    TextField getSaldoCaja();

    TextField getGlosa();

    ComboBox getSelResponsable();

    Button getBtnResponsable();

    ComboBox getSelLugarGasto();

    ComboBox getSelTipoMov();

    ComboBox getSelCtaContable();

    ComboBox getSelRubroInst();

    ComboBox getSelRubroProy();

    ComboBox getSelCodAuxiliar();

    Button getBtnDestino();

    PopupDateField getFechaDoc();

    ComboBox getSelTipoDoc();

    TextField getSerieDoc();

    TextField getNumDoc();

/*    Label getSaldoTotal();*/

    Button getCerrarBtn();

    Button getGuardarBtn();

    Button getAnularBtn();

    Button getModificarBtn();

    Button getEliminarBtn();

    Button getNuevoComprobante();

    Button getImprimirBtn();

    Button getImprimirTotalBtn();

    Button getFinalizarTransBtn();

    CssCheckBox getChkEnviado();

    TextField getTxtOrigen();

    TextField getTxtNumCombrobante();

    PopupDateField getDataFechaComprobante();

    Label getLblSaldo();

    // Repos

    PersistanceService getService();

    // Sub Window

    Window getSubWindow();

    void setSubWindow(Window window);
}