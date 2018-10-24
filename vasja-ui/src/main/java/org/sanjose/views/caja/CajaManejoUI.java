package org.sanjose.views.caja;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;

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
public class CajaManejoUI extends VerticalLayout {
	protected HorizontalLayout horizontalFechas;
	protected DateField fechaDesde;
	protected DateField fechaHasta;
	protected ComboBox selFiltroCaja;
	protected Button nuevoComprobante;
	protected Button btnEditar;
	protected Button btnImprimir;
	protected Button btnVerVoucher;
	protected Button btnReporteCaja;
	protected Grid gridCaja;
	protected HorizontalLayout horizontalSaldos;
	protected VerticalLayout verticalSaldoInicial;
	protected Grid gridSaldoInicial;
	protected VerticalLayout verticalSaldoFinal;
	protected Grid gridSaldoFInal;
	protected VerticalLayout verticalButtons;
	protected GridLayout gridSaldoDelDia;
	protected Label lblIng;
	protected Label lblEgr;
	protected Label lblSaldo;
	protected Label lblSol;
	protected Label valSolIng;
	protected Label valSolEgr;
	protected Label valSolSaldo;
	protected Label lblDolar;
	protected Label valDolIng;
	protected Label valDolEgr;
	protected Label valDolSaldo;
	protected Label lblEur;
	protected Label valEurIng;
	protected Label valEurEgr;
	protected Label valEurSaldo;

	public CajaManejoUI() {
		Design.read(this);
	}
}
