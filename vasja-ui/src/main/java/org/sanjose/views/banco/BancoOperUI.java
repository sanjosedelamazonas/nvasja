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
import com.vaadin.ui.VerticalLayout;

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
    protected VerticalLayout topVertLayout;
    protected HorizontalLayout topButtons;
    protected Button newChequeBtn;
    protected Button guardarChequeBtn;
    protected Button anularChequeBtn;
    protected Button imprimirVoucherBtn;
    protected Button verVoucherBtn;
    protected Button cerrarBtn;
    protected HorizontalLayout datosPrincipalesHorizLayout;
    protected TextField numVoucher;
    protected CssCheckBox chkCobrado;
    protected TextField codMescobrado;
    protected CssCheckBox chkEnviado;
    protected TextField txtOrigen;
    protected TextField txtNumCombrobante;
    protected Button btnPrevio;
    protected Button btnSiguiente;
    protected VerticalLayout bodyVertLayout;
    protected VerticalLayout encabezadoVertLayout;
    protected PopupDateField dataFechaComprobante;
    protected ComboBox selCuenta;
    protected Label monedaSaldoCuentaLabel;
    protected TextField saldoCuenta;
    protected ComboBox selCodAuxCabeza;
    protected Button btnAuxiliar;
    protected TextField cheque;
    protected TextField montoTotal;
    protected TextField glosaCabeza;
    protected FormLayout detallesFormLayout;
    protected TextField numItem;
    protected OptionGroup tipoProyectoTercero;
    protected ComboBox selProyectoTercero;
    protected TextField saldoProyPEN;
    protected TextField saldoProyUSD;
    protected TextField saldoProyEUR;
    protected TextField glosaDetalle;
    protected NumberField numIngreso;
    protected NumberField numEgreso;
    protected ComboBox selResponsable;
    protected Button btnResponsable;
    protected ComboBox selTipoMov;
    protected ComboBox selLugarGasto;
    protected ComboBox selFuente;
    protected ComboBox selCtaContable;
    protected ComboBox selRubroInst;
    protected ComboBox selRubroProy;
    protected VerticalLayout docGastoVertLayout;
    protected HorizontalLayout name_wrapper9;
    protected ComboBox selCodAuxiliar;
    protected Button btnDestino;
    protected HorizontalLayout name_wrapper10;
    protected PopupDateField fechaDoc;
    protected ComboBox selTipoDoc;
    protected TextField serieDoc;
    protected TextField numDoc;
    protected VerticalLayout itemsVertLayout;
    protected FormLayout order_summary_layout;
    protected Label order_heading;
    protected Grid gridBanco;
    protected Button guardarBtn;
    protected Button newItemBtn;
    protected Button modificarBtn;
    protected Button anularBtn;
    protected Button eliminarBtn;

    public BancoOperUI() {
		Design.read(this);
	}

}
