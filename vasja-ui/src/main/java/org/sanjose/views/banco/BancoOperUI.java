package org.sanjose.views.banco;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.OptionGroup;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class BancoOperUI extends CssLayout {
    protected Label operacion_header_label;
    protected FormLayout cuenta_form;
    protected TextField numVoucher;
    protected TextField saldoCuenta;
    protected CssCheckBox chkCobrado;
    protected CssCheckBox chkEnviado;
    protected TextField txtOrigen;
    protected TextField txtNumCombrobante;
    protected PopupDateField dataFechaComprobante;
    protected ComboBox selCuenta;
    protected ComboBox selCodAuxCabeza;
    protected Button btnAuxiliar;
    protected TextField montoTotal;
    protected TextField glosaCabeza;
    protected TextField cheque;
    protected FormLayout billing_form2;
    protected TextField numItem;
    protected OptionGroup tipoProyectoTercero;
    protected TextField saldoProyPEN;
    protected TextField saldoProyUSD;
    protected TextField saldoProyEUR;
    protected ComboBox selProyectoTercero;
    protected ComboBox selFuente;
    protected TextField glosaDetalle;
    protected NumberField numIngreso;
    protected NumberField numEgreso;
    protected ComboBox selResponsable;
    protected Button btnResponsable;
    protected ComboBox selLugarGasto;
    protected ComboBox selTipoMov;
    protected ComboBox selCtaContable;
    protected ComboBox selRubroInst;
    protected ComboBox selRubroProy;
    protected FormLayout billing_form5;
    protected HorizontalLayout name_wrapper9;
    protected ComboBox selCodAuxiliar;
    protected Button btnDestino;
    protected HorizontalLayout name_wrapper10;
    protected PopupDateField fechaDoc;
    protected ComboBox selTipoDoc;
    protected TextField serieDoc;
    protected TextField numDoc;
    protected FormLayout order_summary_layout;
    protected Label order_heading;
    protected Grid gridBanco;
    protected Label saldoTotal;
    protected FormLayout billing_form6;
    protected Button guardarBtn;
    protected Button newItemBtn;
    protected Button modificarBtn;
    protected Button anularBtn;
    protected Button eliminarBtn;
    protected Button newChequeBtn;
    protected Button cerrarBtn;
    protected Button imprimirVoucherBtn;
    protected Button verVoucherBtn;

    public BancoOperUI() {
		Design.read(this);
	}
}
