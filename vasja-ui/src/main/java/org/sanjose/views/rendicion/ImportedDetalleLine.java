package org.sanjose.views.rendicion;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.ComboBox;
import tm.kod.widgets.numberfield.NumberField;

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
public class ImportedDetalleLine extends VerticalLayout {
    protected TextField numItem;
    protected DateField fecFechaDoc;
    protected ComboBox selPartidaP;
    protected ComboBox selDestino;
    protected ComboBox selRubroInst;
    protected TextField txtGlosaItem;
    protected NumberField numMonto;

    public ImportedDetalleLine() {
        Design.read(this);
    }
}
