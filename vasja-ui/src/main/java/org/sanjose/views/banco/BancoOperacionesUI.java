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
import org.sanjose.views.banco.BancoOperView;

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
	protected ComboBox selFiltroCuenta;
	protected Button btnNuevoCheque;
	protected Button btnEditar;
	protected Button btnImprimir;
	protected Button btnVerVoucher;
	protected ComboBox selRepMoneda;
	protected Button btnReporte;
	protected Grid gridBanco;
	protected BancoOperView bancoOperView;

	public BancoOperacionesUI() {
		Design.read(this);
	}
}
