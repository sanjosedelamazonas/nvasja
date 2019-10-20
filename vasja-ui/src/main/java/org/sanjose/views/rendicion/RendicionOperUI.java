package org.sanjose.views.rendicion;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.vaadin.addons.CssCheckBox;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button;
import tm.kod.widgets.numberfield.NumberField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Grid;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.DateField;

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
public class RendicionOperUI extends CssLayout {
    protected TextField txtOrigen;
    protected TextField numVoucher;
    protected PopupDateField dataFechaComprobante;
    protected TextField txtGlosaCabeza;
    protected ComboBox selResponsable1;
    protected Button btnResponsable;
    protected OptionGroup selMoneda;
    protected NumberField numTotalAnticipio;
    protected TextField txtGastoTotal;
    protected TextField txtSaldoPendiente;
    protected CssCheckBox chkEnviado;
    protected TextField txtOrigenlace;
    protected TextField txtComprobenlace;
    protected PopupDateField dataFechaRegistro;
    protected TextField txtIngresadoPor;
    protected Button btnGuardar;
    protected Button eliminarRendBtn;
    protected Button btnVerVoucher;
    protected Button btnCerrar;
    protected TextField numItem;
    protected TextField txtGlosaDetalle;
    protected ComboBox selTipoMov;
    protected HorizontalLayout name_wrapper9;
    protected ComboBox selCodAuxiliar;
    protected Button btnAuxiliar;
    protected HorizontalLayout name_wrapper10;
    protected PopupDateField fechaPago;
    protected ComboBox selTipoDoc;
    protected PopupDateField fechaDoc;
    protected TextField txtSerieDoc;
    protected TextField txtNumDoc;
    protected ComboBox setAllProyecto;
    protected ComboBox setAllFuente;
    protected ComboBox setAllPartida;
    protected ComboBox setAllLugarGasto;
    protected ComboBox setAllContable;
    protected ComboBox setAllRubrInst;
    protected TextField tcambioText;
    protected DateField setAllFechaDoc;
    protected DateField setAllFechaPago;
    protected Button btnSetAll;
    protected Button btnToggleVista;
    protected FormLayout order_summary_layout;
    protected Label order_heading;
    protected Grid grid;
    protected TextField numDifsol;
    protected TextField numDifdolar;
    protected TextField numDifmo;
    protected Button btnAjustar;
    protected Button btnNewItem;
    protected Button btnEliminar;
    protected Button btnModificar;
    protected Button btnAnular;


    public RendicionOperUI() {
        Design.read(this);
    }
}
