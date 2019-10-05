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
public class BancoManejoUI extends VerticalLayout {
    protected HorizontalLayout horiz1;
    protected DateField fechaDesde;
    protected DateField fechaHasta;
    protected ComboBox selRepMoneda;
    protected TextField numSaldoInicialSegBancos;
    protected TextField numSaldoInicialLibro;
    protected TextField numSaldoFinalSegBancos;
    protected TextField numSaldoFinalLibro;
    protected Button btnDetallesSaldos;
    protected Button btnReporte;
    protected VerticalLayout espacio;
    protected ComboBox selFiltroCuenta;
    protected DateField fecMesCobrado;
    protected Button btnMarcarCobrado;
    protected Button btnMarcarNoCobrado;
    protected Button btnNuevoCheque;
    protected Grid gridBanco;
    protected Button btnImprimir;
    protected Button btnEditar;
    protected Button btnVerVoucher;
    protected Button btnEliminar;

    public BancoManejoUI() {
        Design.read(this);
    }
}
