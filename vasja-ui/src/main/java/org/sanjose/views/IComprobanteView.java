package org.sanjose.views;

import com.vaadin.ui.*;
import org.sanjose.repo.*;
import tm.kod.widgets.numberfield.NumberField;

import javax.persistence.EntityManager;

/**
 * VASJA
 * User: prubach
 * Date: 20.09.16
 */
interface IComprobanteView {

    void setSaldoDeCajas();

    void setEnableFields(boolean enabled);

    void refreshData();

    ComboBox getSelProyecto();

    TextField getNumVoucher();

    ComboBox getSelFuente();

    ComboBox getSelTercero();

    TextField getSaldoProyPEN();

    TextField getSaldoProyUSD();

    TextField getSaldoProyEUR();

    OptionGroup getSelMoneda();

    NumberField getNumIngreso();

    NumberField getNumEgreso();

    ComboBox getSelCaja();

    TextField getSaldoCajaPEN();

    TextField getSaldoCajaUSD();

    TextField getGlosa();

    ComboBox getSelResponsable();

    Button getBtnResponsable();

    ComboBox getSelLugarGasto();

    ComboBox getSelTipoMov();

    ComboBox getSelCtaContable();

    ComboBox getSelRubroInst();

    ComboBox getSelRubroProy();

    ComboBox getSelCodAuxiliar();

    Button getBtnDestino();

    PopupDateField getFechaDoc();

    ComboBox getSelTipoDoc();

    TextField getSerieDoc();

    TextField getNumDoc();

    Label getSaldoTotal();

    Button getCerrarBtn();

    Button getGuardarBtn();

    Button getModificarBtn();

    Button getEliminarBtn();

    Button getNuevoComprobante();

    Button getImprimirBtn();

    PopupDateField getDataFechaComprobante();

    // Repos


    VsjCajabancoRep getRepo();

    ScpPlanproyectoRep getPlanproyectoRepo();

    ScpFinancieraRep getFinancieraRepo();

    Scp_ProyectoPorFinancieraRep getProyectoPorFinancieraRepo();

    VsjConfiguractacajabancoRep getConfiguractacajabancoRepo();

    VsjConfiguracioncajaRep getConfiguracioncajaRepo();

    ScpProyectoRep getProyectoRepo();

    ScpDestinoRep getDestinoRepo();

    ScpPlanespecialRep getPlanespecialRep();

    ScpCargocuartaRep getCargocuartaRepo();

    ScpTipodocumentoRep getTipodocumentoRepo();

    ScpPlancontableRep getPlanRepo();

    Scp_ContraparteRep getContraparteRepo();

    ScpComprobantepagoRep getComprobantepagoRepo();

    EntityManager getEm();
}