package org.sanjose.views.caja;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.DateField;

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
public class ConfiguracionCtaCajaBancoUI extends VerticalLayout {
    protected HorizontalLayout horizontalCabeza;
    protected Button btnNuevaConfig;
    protected Button btnEliminar;
    protected DateField fechaAno;
    protected Grid gridConfigCtaCajaBanco;

    public ConfiguracionCtaCajaBancoUI() {
		Design.read(this);
	}
}
