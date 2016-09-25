package org.sanjose.views.caja;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.model.*;
import org.sanjose.repo.*;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.util.TransactionUtil;
import org.sanjose.util.ViewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import tm.kod.widgets.numberfield.NumberField;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class ComprobanteView extends ComprobanteUI implements View, IComprobanteView {

	private static final Logger log = LoggerFactory.getLogger(ComprobanteView.class);
	
    public static final String VIEW_NAME = "Caja";

    ComprobanteLogic viewLogic;

    private final VsjCajabancoRep repo;

    private final ScpPlanproyectoRep planproyectoRepo;

    private final ScpFinancieraRep financieraRepo;

    private final Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo;

    private final VsjConfiguractacajabancoRep configuractacajabancoRepo;

    private final VsjConfiguracioncajaRep configuracioncajaRepo;

    private final ScpProyectoRep proyectoRepo;

    private final ScpDestinoRep destinoRepo;

    private final ScpPlanespecialRep planespecialRep;

    private final ScpCargocuartaRep cargocuartaRepo;

    private final ScpTipodocumentoRep tipodocumentoRepo;

    private final ScpPlancontableRep planRepo;

    private final Scp_ContraparteRep contraparteRepo;

    private final ScpComprobantepagoRep comprobantepagoRepo;

    @PersistenceContext
    private final EntityManager em;

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };

    @Autowired
    private ComprobanteView(VsjCajabancoRep repo, VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
                            ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                            ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                            ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                            Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                            ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, EntityManager em,
                            ComprobanteLogic comprobanteLogic) {
    	this.repo = repo;
        this.planproyectoRepo = planproyectoRepo;
        this.financieraRepo = financieraRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        this.configuractacajabancoRepo = configuractacajabancoRepo;
        this.configuracioncajaRepo = configuracioncajaRepo;
        this.proyectoRepo = proyectoRepo;
        this.destinoRepo = destinoRepo;
        this.cargocuartaRepo = cargocuartaRepo;
        this.tipodocumentoRepo = tipodocumentoRepo;
        this.planespecialRep = planEspRepo;
        this.contraparteRepo = contraparteRepo;
        this.comprobantepagoRepo = comprobantepagoRepo;
        this.planRepo = planRepo;
        this.viewLogic = comprobanteLogic;

        this.em = em;
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirBtn.setEnabled(false);

        viewLogic.init(this);
        viewLogic.setupEditComprobanteView();
    }

    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public void setSaldoDeCajas() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        } else  {
            order_summary_layout.addStyleName("order-summary-layout-usd");
        }
        cajaSaldosLayout.removeAllComponents();
        if (dataFechaComprobante.getValue() != null && selMoneda.getValue() != null) {
            BigDecimal total = new BigDecimal(0.00);
            for (ScpPlancontable caja : DataUtil.getCajas(getDataFechaComprobante().getValue(), planRepo, PEN.equals(selMoneda.getValue().toString().charAt(0)))) {

                BigDecimal saldo = new ProcUtil(em).getSaldoCaja(dataFechaComprobante.getValue(), caja.getId().getCodCtacontable()
                        , selMoneda.getValue().toString().charAt(0));
                Label salLbl = new Label();
                salLbl.setContentMode(ContentMode.HTML);
                salLbl.setValue(
                    caja.getId().getCodCtacontable() + " " + caja.getTxtDescctacontable() + ": <span class=\"order-sum\">"+  saldo + "</span");
                salLbl.setStyleName("order-item");
                cajaSaldosLayout.addComponent(salLbl);
                total = total.add(saldo);
            }
            saldoTotal.setContentMode(ContentMode.HTML);
            saldoTotal.setValue("Total :" +
                    "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + total.toString() + "</span>");
        }
    }

    private boolean isPEN() {
        return PEN.equals(selMoneda.getValue().toString().charAt(0));
    }

    @Override
    public void refreshData() {
        MainUI.get().getCajaManejoView().refreshData();
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }


    public EntityManager getEm() {
        return em;
    }

    @Override
    public ComboBox getSelProyecto() {
        return selProyecto;
    }

    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    public TextField getNumVoucher() {
        return numVoucher;
    }

    public ComboBox getSelFuente() {
        return selFuente;
    }

    public ComboBox getSelTercero() {
        return selTercero;
    }

    public TextField getSaldoProyPEN() {
        return saldoProyPEN;
    }

    public TextField getSaldoProyUSD() {
        return saldoProyUSD;
    }

    public TextField getSaldoProyEUR() {
        return saldoProyEUR;
    }

    public OptionGroup getSelMoneda() {
        return selMoneda;
    }

    public NumberField getNumIngreso() {
        return numIngreso;
    }

    public NumberField getNumEgreso() {
        return numEgreso;
    }

    public ComboBox getSelCaja() {
        return selCaja;
    }

    public TextField getSaldoCajaPEN() {
        return saldoCajaPEN;
    }

    public TextField getSaldoCajaUSD() {
        return saldoCajaUSD;
    }

    public TextField getGlosa() {
        return glosa;
    }

    public ComboBox getSelResponsable() {
        return selResponsable;
    }

    public Button getBtnResponsable() {
        return btnResponsable;
    }

    public ComboBox getSelLugarGasto() {
        return selLugarGasto;
    }

    public ComboBox getSelTipoMov() {
        return selTipoMov;
    }

    public ComboBox getSelCtaContable() {
        return selCtaContable;
    }

    public ComboBox getSelRubroInst() {
        return selRubroInst;
    }

    public ComboBox getSelRubroProy() {
        return selRubroProy;
    }

    public ComboBox getSelCodAuxiliar() {
        return selCodAuxiliar;
    }

    public Button getBtnDestino() {
        return btnDestino;
    }

    public PopupDateField getFechaDoc() {
        return fechaDoc;
    }

    public ComboBox getSelTipoDoc() {
        return selTipoDoc;
    }

    public TextField getSerieDoc() {
        return serieDoc;
    }

    public TextField getNumDoc() {
        return numDoc;
    }

    public CssLayout getCajaSaldosLayout() {
        return cajaSaldosLayout;
    }

    public Label getSaldoTotal() {
        return saldoTotal;
    }

    public Button getCerrarBtn() {
        return cerrarBtn;
    }

    public Button getGuardarBtn() {
        return guardarBtn;
    }

    public Button getModificarBtn() {
        return modificarBtn;
    }

    public Button getEliminarBtn() {
        return eliminarBtn;
    }

    public Button getNuevoComprobante() {
        return nuevoComprobante;
    }

    public Button getImprimirBtn() {
        return imprimirBtn;
    }


    public VsjCajabancoRep getRepo() {
        return repo;
    }

    public ScpPlanproyectoRep getPlanproyectoRepo() {
        return planproyectoRepo;
    }

    public ScpFinancieraRep getFinancieraRepo() {
        return financieraRepo;
    }

    public Scp_ProyectoPorFinancieraRep getProyectoPorFinancieraRepo() {
        return proyectoPorFinancieraRepo;
    }

    public VsjConfiguractacajabancoRep getConfiguractacajabancoRepo() {
        return configuractacajabancoRepo;
    }

    public VsjConfiguracioncajaRep getConfiguracioncajaRepo() {
        return configuracioncajaRepo;
    }

    public ScpProyectoRep getProyectoRepo() {
        return proyectoRepo;
    }

    public ScpDestinoRep getDestinoRepo() {
        return destinoRepo;
    }

    public ScpPlanespecialRep getPlanespecialRep() {
        return planespecialRep;
    }

    public ScpCargocuartaRep getCargocuartaRepo() {
        return cargocuartaRepo;
    }

    public ScpTipodocumentoRep getTipodocumentoRepo() {
        return tipodocumentoRepo;
    }

    public ScpPlancontableRep getPlanRepo() {
        return planRepo;
    }

    public Scp_ContraparteRep getContraparteRepo() {
        return contraparteRepo;
    }

    public ScpComprobantepagoRep getComprobantepagoRepo() {
        return comprobantepagoRepo;
    }

}
