package org.sanjose.views.banco;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.repo.*;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.caja.TransferenciaLogic;
import org.sanjose.views.caja.TransferenciaUI;
import org.springframework.beans.factory.annotation.Autowired;
import tm.kod.widgets.numberfield.NumberField;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class BancoOperView extends BancoOperUI implements View {

	private static final Logger log = LoggerFactory.getLogger(BancoOperView.class);

    public static final String VIEW_NAME = "Cheques";

    BancoItemLogic viewLogic = null;

    private final VsjBancodetalleRep repo;

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

    private final EntityManager em;

    private final BeanItemContainer<VsjBancodetalle> container;

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosaCabeza, glosaDetalle, serieDoc, numDoc,
            selCodAuxCabeza, cheque };

    static final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol"
    };
    static final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/."
    };

    static final String[] VISIBLE_COLUMN_IDS_USD = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebedolar", "numHaberdolar"
    };

    static final String[] VISIBLE_COLUMN_NAMES_USD = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing $", "Egr $"
    };

    static final String[] VISIBLE_COLUMN_IDS_EUR = new String[]{"txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebemo", "numHabermo"
    };

    static final String[] VISIBLE_COLUMN_NAMES_EUR = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing €", "Egr €"
    };

    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{  };

    @Autowired
    private BancoOperView(VsjBancodetalleRep repo, VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
                          ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                          ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                          ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                          Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                          ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, EntityManager em) {
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

        this.em = em;
        viewLogic = new BancoItemLogic(this);
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirTotalBtn.setEnabled(false);
        newItemBtn.setEnabled(false);

        viewLogic.setupEditComprobanteView();

        // Grid
        //noinspection unchecked
        container = new BeanItemContainer(VsjBancodetalle.class, new ArrayList());
        gridBanco.setContainerDataSource(container);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFregistro", SortDirection.DESCENDING);

        gridBanco.getColumn("txtGlosaitem").setWidth(150);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridBanco);

        ViewUtil.colorizeRows(gridBanco);

        gridBanco.setSelectionMode(Grid.SelectionMode.SINGLE);

        setSaldoTrans();
        viewLogic.init();
    }

    public VsjBancodetalle getSelectedRow() {
        return (VsjBancodetalle) gridBanco.getSelectedRow();
    }

    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            //if (f!=selMoneda || !enabled)
            f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public BeanItemContainer<VsjBancodetalle> getContainer() {
        return container;
    }

    private BigDecimal calcDifference() {
        BigDecimal total = new BigDecimal(0.00);

        for (VsjBancodetalle cajabanco : container.getItemIds()) {
            if (isPEN())
                total = total.add(cajabanco.getNumDebesol()).subtract(cajabanco.getNumHabersol());
            else
                total = total.add(cajabanco.getNumDebedolar()).subtract(cajabanco.getNumHaberdolar());
        }
        return total;
    }

    public void setSaldoTrans() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        } else  {
            order_summary_layout.addStyleName("order-summary-layout-usd");
        }

        saldoTotal.setContentMode(ContentMode.HTML);
        saldoTotal.setValue("Total:" +
                "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + calcDifference().toString() + "</span>");

        /*if (!container.getItemIds().isEmpty() && calcDifference().compareTo(new BigDecimal(0.00))==0)
            finalizarTransBtn.setEnabled(true);
        else
            finalizarTransBtn.setEnabled(false);*/
    }


    public void refreshData() {
        MainUI.get().getCajaManejoView().refreshData();
    }

    public void setSaldoDeCajas() {
    }

    private boolean isPEN() {
//        return selMoneda.getValue() == null || PEN.equals(selMoneda.getValue().toString());
        return true;
    }

    public ComboBox getSelProyecto() {
        return selProyecto;
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

    public NumberField getNumIngreso() {
        return numIngreso;
    }

    public NumberField getNumEgreso() {
        return numEgreso;
    }

    public TextField getGlosaCabeza() {
        return glosaCabeza;
    }

    public TextField getGlosaDetalle() {
        return glosaDetalle;
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

    public Button getNewItemBtn() {
        return newItemBtn;
    }

    public Button getImprimirTotalBtn() {
        return imprimirTotalBtn;
    }

    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    public ComboBox getSelCuenta() {
        return selCuenta;
    }

    public TextField getSaldoCuenta() {
        return saldoCuenta;
    }

    public ComboBox getSelCodAuxCabeza() {
        return selCodAuxCabeza;
    }



    @Override
    public void enter(ViewChangeEvent event) {
    }


    public EntityManager getEm() {
        return em;
    }

    public VsjBancodetalleRep getRepo() {
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
