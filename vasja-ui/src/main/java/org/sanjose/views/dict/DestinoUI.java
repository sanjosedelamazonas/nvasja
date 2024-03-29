package org.sanjose.views.dict;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.HorizontalLayout;

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
public class DestinoUI extends HorizontalLayout {
    protected ComboBox tipoDePersona;
    protected ComboBox selCodigo;
    protected ComboBox clasificacion;
    protected TextField ruc;
    protected ComboBox selNombreCompleta;
    protected TextField txtNombreCompleta;
    protected TextField telefono1;
    protected TextField telefono2;
    protected ComboBox dependencia;
    protected ComboBox cargo;
    protected TextField direccion;
    protected ComboBox genero;
    protected TextField apellidoMaterno;
    protected TextField apellidoPaterno;
    protected ComboBox tipoDocumento;
    protected TextField numDocumento;
    protected Button btnGuardar;
    protected Button btnEliminar;
    protected Button btnAnular;
    protected Button btnNuevo;

    public DestinoUI() {
		Design.read(this);
	}
}
