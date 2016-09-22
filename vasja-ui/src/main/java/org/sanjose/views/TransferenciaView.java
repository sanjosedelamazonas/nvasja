package org.sanjose.views;

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
import org.sanjose.repo.*;
import org.sanjose.util.ViewUtil;
import org.sanjose.model.*;
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
public class TransferenciaView extends TransferenciaUI implements View, IComprobanteView {

	private static final Logger log = LoggerFactory.getLogger(TransferenciaView.class);

    public static final String VIEW_NAME = "Transferencia";

    public static final String PEN="0";

    public static final String USD="1";

    TransferenciaLogic viewLogic = null;

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

    private final EntityManager em;

    private final BeanItemContainer<VsjCajabanco> container;

    private final Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };

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

    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{  };

    @Autowired
    private TransferenciaView(VsjCajabancoRep repo, VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
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
        viewLogic = new TransferenciaLogic(this);
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirBtn.setEnabled(false);
        nuevoComprobante.setEnabled(false);

        viewLogic.setupEditComprobanteView();

        // Grid
        //noinspection unchecked
        container = new BeanItemContainer(VsjCajabanco.class, new ArrayList());
        gridTrans.setContainerDataSource(container);
        gridTrans.setEditorEnabled(false);
        gridTrans.sort("fecFregistro", SortDirection.DESCENDING);

        gridTrans.getColumn("txtGlosaitem").setWidth(150);

        ViewUtil.setColumnNames(gridTrans, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridTrans);

        ViewUtil.colorizeRows(gridTrans);

        gridTrans.setSelectionMode(Grid.SelectionMode.SINGLE);

        setSaldoTrans();
        viewLogic.init();
    }

    public VsjCajabanco getSelectedRow() {
        return (VsjCajabanco) gridTrans.getSelectedRow();
    }

    @Override
    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            if (f!=selMoneda || !enabled) f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public BeanItemContainer<VsjCajabanco> getContainer() {
        return container;
    }

    private BigDecimal calcDifference() {
        BigDecimal total = new BigDecimal(0.00);

        for (VsjCajabanco cajabanco : container.getItemIds()) {
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
        saldoTotal.setValue("Differencia:" +
                "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + calcDifference().toString() + "</span>");

        if (!container.getItemIds().isEmpty() && calcDifference().compareTo(new BigDecimal(0.00))==0)
            finalizarTransBtn.setEnabled(true);
        else
            finalizarTransBtn.setEnabled(false);
    }


    @Override
    public void refreshData() {
        MainUI.get().getCajaManejoView().refreshData();
    }

    @Override
    public void setSaldoDeCajas() {
    }

    private boolean isPEN() {
        return selMoneda.getValue() == null || PEN.equals(selMoneda.getValue().toString());
    }

    @Override
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

    @Override
    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    //
    @Override
    public VsjCajabancoRep getRepo() {
        return repo;
    }

    @Override
    public ScpPlanproyectoRep getPlanproyectoRepo() {
        return planproyectoRepo;
    }

    @Override
    public ScpFinancieraRep getFinancieraRepo() {
        return financieraRepo;
    }

    @Override
    public Scp_ProyectoPorFinancieraRep getProyectoPorFinancieraRepo() {
        return proyectoPorFinancieraRepo;
    }

    @Override
    public VsjConfiguractacajabancoRep getConfiguractacajabancoRepo() {
        return configuractacajabancoRepo;
    }

    @Override
    public VsjConfiguracioncajaRep getConfiguracioncajaRepo() {
        return configuracioncajaRepo;
    }

    @Override
    public ScpProyectoRep getProyectoRepo() {
        return proyectoRepo;
    }

    @Override
    public ScpDestinoRep getDestinoRepo() {
        return destinoRepo;
    }

    @Override
    public ScpPlanespecialRep getPlanespecialRep() {
        return planespecialRep;
    }

    @Override
    public ScpCargocuartaRep getCargocuartaRepo() {
        return cargocuartaRepo;
    }

    @Override
    public ScpTipodocumentoRep getTipodocumentoRepo() {
        return tipodocumentoRepo;
    }

    @Override
    public ScpPlancontableRep getPlanRepo() {
        return planRepo;
    }

    @Override
    public Scp_ContraparteRep getContraparteRepo() {
        return contraparteRepo;
    }

    @Override
    public ScpComprobantepagoRep getComprobantepagoRepo() {
        return comprobantepagoRepo;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }


    public EntityManager getEm() {
        return em;
    }

}
