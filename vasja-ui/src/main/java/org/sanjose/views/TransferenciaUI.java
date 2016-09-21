package org.sanjose.views;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;
import tm.kod.widgets.numberfield.NumberField;

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
public class TransferenciaUI extends CssLayout {
	protected Label operacion_header_label;
	protected FormLayout cuenta_form;
	protected TextField numVoucher;
	protected PopupDateField dataFechaComprobante;
	protected ComboBox selProyecto;
	protected ComboBox selFuente;
	protected ComboBox selTercero;
	protected TextField saldoProyPEN;
	protected TextField saldoProyUSD;
	protected TextField saldoProyEUR;
	protected FormLayout billing_form2;
	protected OptionGroup selMoneda;
	protected NumberField numIngreso;
	protected NumberField numEgreso;
	protected ComboBox selCaja;
	protected TextField saldoCajaPEN;
	protected TextField saldoCajaUSD;
	protected TextField glosa;
	protected ComboBox selResponsable;
	protected Button btnResponsable;
	protected ComboBox selLugarGasto;
	protected ComboBox selTipoMov;
	protected ComboBox selCtaContable;
	protected ComboBox selRubroInst;
	protected ComboBox selRubroProy;
	protected FormLayout billing_form5;
	protected Label documento_header_label4;
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
	protected Grid gridTrans;
	protected Label saldoTotal;
	protected FormLayout billing_form6;
	protected Button guardarBtn;
	protected Button modificarBtn;
	protected Button eliminarBtn;
	protected Button nuevoComprobante;
	protected Button imprimirBtn;
	protected Button nuevaTransBtn;
	protected Button finalizarTransBtn;
	protected Button cerrarBtn;
	protected Button imprimirTotalBtn;

	public TransferenciaUI() {
		Design.read(this);
	}
}
