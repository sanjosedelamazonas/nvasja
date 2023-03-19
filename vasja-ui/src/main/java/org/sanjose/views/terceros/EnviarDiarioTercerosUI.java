package org.sanjose.views.terceros;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;

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
public class EnviarDiarioTercerosUI extends VerticalLayout {
    protected ComboBox selReporte;
    protected DateField fechaInicial;
    protected DateField fechaFinal;
    protected CheckBox checkTodos;
    protected ComboBox selTercero;
    protected ComboBox selUsuario;
    protected Button btnEnviar;
    protected TextArea txtLog;

    public EnviarDiarioTercerosUI() {
        Design.read(this);
    }
}
