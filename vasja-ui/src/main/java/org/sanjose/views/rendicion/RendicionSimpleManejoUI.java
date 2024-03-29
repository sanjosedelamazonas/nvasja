package org.sanjose.views.rendicion;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class RendicionSimpleManejoUI extends VerticalLayout {
    protected HorizontalLayout horiz1;
    protected DateField fechaDesde;
    protected DateField fechaHasta;
    protected ComboBox filtroResponsable;
    protected ComboBox selMoneda;
    protected ComboBox filtroEnviadasCombo;
    protected HorizontalLayout horiz4;
    protected HorizontalLayout horiz3;
    protected Grid grid;
    protected Button btnNueva;
    protected Button btnVerImprimir;
    protected Button btnModificar;
    protected Button btnEliminar;
    protected Button btnEnviar;
    protected Button btnNoEnviado;

    public RendicionSimpleManejoUI() {
        Design.read(this);
    }
}
