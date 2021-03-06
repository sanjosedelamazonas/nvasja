package org.sanjose.views.banco;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.TextField;

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
public class BancoOperacionesUI extends VerticalLayout {
    protected HorizontalLayout horizontalFechasLayout;
    protected DateField fechaDesde;
    protected DateField fechaHasta;
    protected ComboBox selRepMoneda;
    protected TextField numSaldoInicialSegBancos;
    protected TextField numSaldoInicialLibro;
    protected TextField numSaldoFinalSegBancos;
    protected TextField numSaldoFinalLibro;
    protected Button btnDetallesSaldos;
    protected Button btnReporte;
    protected ComboBox selFiltroCuenta;
    protected DateField fecMesCobrado;
    protected Button btnMarcarCobrado;
    protected Button btnMarcarNoCobrado;
    protected Grid gridBanco;
    protected BancoOperView bancoOperView;

    public BancoOperacionesUI() {
		Design.read(this);
	}
}
