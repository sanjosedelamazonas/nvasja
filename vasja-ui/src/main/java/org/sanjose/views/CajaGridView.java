package org.sanjose.views;

import java.sql.Timestamp;
import java.util.*;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjCajabancoRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.validator.TwoCombosValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid.SelectionMode;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class CajaGridView extends CajaGridUI implements View {

	private static final Logger log = LoggerFactory.getLogger(CajaGridView.class);
	
    public static final String VIEW_NAME = "Operaciones de Caja";

    private CajaGridLogic viewLogic = new CajaGridLogic(this);
    
    public VsjCajabancoRep repo;

    String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem",  "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "codTipomoneda",
            "codDestino", "codContraparte", "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };
    String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $", "S/$",
            "Responsable", "Lug. Gasto", "Cod. Aux", "Cta Cont.", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Env", "Origen", "Comprobante"
    };
    int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 10, 6, 6, 6, 6, 2, // S/$
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2, 6, 6
    };
    String[] NONEDITABLE_COLUMN_IDS = new String[]{/*"fecFecha",*/ "txtCorrelativo", "flgEnviado" };

    public ScpPlanproyectoRep planproyectoRepo;

    public ScpFinancieraRep financieraRepo;

    public Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo;

    private ScpCargocuartaRep cargocuartaRepo;

    private ScpDestinoRep destinoRepo;

    private ScpTipodocumentoRep tipodocumentoRepo;

    private ScpTipocambioRep tipocambioRepo;

    @PersistenceContext
    private EntityManager em;

    private BeanItemContainer<VsjCajabanco> container;

    @Autowired
    public CajaGridView(VsjCajabancoRep repo, ScpPlancontableRep planRepo,
                        ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                        ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                        ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                        Scp_ContraparteRep contraparteRepo, ScpCargocuartaRep cargocuartaRepo,
                        ScpTipodocumentoRep tipodocumentoRepo, ScpTipocambioRep tipocambioRepo, EntityManager em) {
    	this.repo = repo;
        this.planproyectoRepo = planproyectoRepo;
        this.financieraRepo = financieraRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        this.destinoRepo = destinoRepo;
        this.tipodocumentoRepo = tipodocumentoRepo;
        this.cargocuartaRepo = cargocuartaRepo;
        this.tipocambioRepo = tipocambioRepo;
        this.em = em;
        setSizeFull();
        addStyleName("crud-view");

        container = new BeanItemContainer(VsjCajabanco.class, repo.findAll());
        
        gridCaja.setContainerDataSource(container);
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.getColumn("txtGlosaitem").setWidth(120);

        gridCaja.setSelectionMode(SelectionMode.MULTI);

        gridCaja.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjCajabanco>(VsjCajabanco.class));

        // Fecha
        PopupDateField pdf = new PopupDateField();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecFecha").setEditorField(pdf);
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Fecha Doc
        pdf = new PopupDateField();
        prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecComprobantepago").setEditorField(pdf);
        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Proyecto
        ComboBox selTercero = new ComboBox();
        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()), "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(event -> setProyectoLogic(event));
        selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
        gridCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", destinoRepo.findByIndTipodestino("3"), "Sel Tercero", "txtNombredestino");
        selTercero.addValueChangeListener(event -> setTerceroLogic(event));
        selTercero.addValidator(new TwoCombosValidator(selProyecto, true, null));
        gridCaja.getColumn("codTercero").setEditorField(selTercero);

        // Cta Caja
        ComboBox selCtacontablecaja = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codContracta").setEditorField(selCtacontablecaja);

        // Tipo Moneda
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "codTipomoneda", "Moneda");
        gridCaja.getColumn("codTipomoneda").setEditorField(selTipomoneda);

        // Cta Contable
        ComboBox selCtacontable = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontable").setEditorField(selCtacontable);

        // Rubro inst
        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial",
                planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");
        gridCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        // Responsable
        ComboBox selResponsable = new ComboBox();
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        gridCaja.getColumn("codDestino").setEditorField(selResponsable);

        ComboBox selLugarGasto = new ComboBox();
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte", contraparteRepo.findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");
        gridCaja.getColumn("codContraparte").setEditorField(selLugarGasto);

        // Cod. Auxiliar
        ComboBox selAuxiliar = new ComboBox();
        DataFilterUtil.bindComboBox(selAuxiliar, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
        gridCaja.getColumn("codDestinoitem").setEditorField(selAuxiliar);

        // Tipo doc
        ComboBox selComprobantepago = new ComboBox();
        DataFilterUtil.bindComboBox(selComprobantepago, "codTipocomprobantepago", comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");
        gridCaja.getColumn("codTipocomprobantepago").setEditorField(selComprobantepago);

        // Rubro Proy
        ComboBox selPlanproyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
                planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        gridCaja.getColumn("codCtaproyecto").setEditorField(selPlanproyecto);

        // Fuente
        ComboBox selFinanciera = new ComboBox();
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");
        gridCaja.getColumn("codFinanciera").setEditorField(selFinanciera);

        gridCaja.addItemClickListener(event ->  setItemLogic(event));

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta);
        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        viewLogic.init();
    }

    public void setProyectoLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        ComboBox selProyecto = (ComboBox)gridCaja.getColumn("codProyecto").getEditorField();
        selProyecto.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    public void setTerceroLogic(Property.ValueChangeEvent event) {
        ComboBox selTercero = (ComboBox)gridCaja.getColumn("codTercero").getEditorField();
        selTercero.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }


    public void setItemLogic(ItemClickEvent event) {
        String proyecto = null;
        Object objProyecto = event.getItem().getItemProperty("codProyecto").getValue();
        if (objProyecto !=null && !objProyecto.toString().isEmpty())
            proyecto = objProyecto.toString();

        setEditorLogic(proyecto);
    }

    public void setEditorLogic(String codProyecto) {
        ComboBox selFinanciera = (ComboBox)gridCaja.getColumn("codFinanciera").getEditorField();
        ComboBox selPlanproyecto = (ComboBox) gridCaja.getColumn("codCtaproyecto").getEditorField();

        if (codProyecto!=null && !codProyecto.isEmpty()) {
            DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
                    planproyectoRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "Sel Rubro proy", "txtDescctaproyecto");
            List<Scp_ProyectoPorFinanciera>
                    proyectoPorFinancieraList = proyectoPorFinancieraRepo.findById_CodProyecto(codProyecto);

            // Filter financiera if exists in Proyecto Por Financiera
            List<ScpFinanciera> financieraList = financieraRepo.findAll();
            List<ScpFinanciera> financieraEfectList = new ArrayList<>();
            if (proyectoPorFinancieraList!=null && !proyectoPorFinancieraList.isEmpty()) {
                List<String> codFinancieraList = new ArrayList<>();
                for (Scp_ProyectoPorFinanciera proyectoPorFinanciera : proyectoPorFinancieraList)
                    codFinancieraList.add(proyectoPorFinanciera.getId().getCodFinanciera());

                for (ScpFinanciera financiera : financieraList) {
                    if (financiera.getCodFinanciera()!=null &&
                            codFinancieraList.contains(financiera.getCodFinanciera())) {
                        financieraEfectList.add(financiera);
                    }
                }
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraEfectList,
                    "Sel Fuente", "txtDescfinanciera");
        } else {
            DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", new ArrayList<ScpFinanciera>(),
                    "-------", null);
            DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto", new ArrayList<ScpPlanproyecto>(),
                    "-------", null);
        }
    }

    public void refreshData() {
        container.removeAllItems();
        container.addAll(repo.findAll());
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void clearSelection() {
        gridCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridCaja.getSelectedRows();
    }

    public void removeRow(VsjCajabanco vsj) {
    	repo.delete(vsj);    	
    	gridCaja.getContainerDataSource().removeItem(vsj);
    }

    public ScpCargocuartaRep getCargocuartaRepo() {
        return cargocuartaRepo;
    }

    public ScpDestinoRep getDestinoRepo() {
        return destinoRepo;
    }

    public ScpTipodocumentoRep getTipodocumentoRepo() {
        return tipodocumentoRepo;
    }

    public VsjCajabancoRep getRepo() {
        return repo;
    }

    public ScpTipocambioRep getTipocambioRepo() {
        return tipocambioRepo;
    }

    public EntityManager getEm() {
        return em;
    }
}
