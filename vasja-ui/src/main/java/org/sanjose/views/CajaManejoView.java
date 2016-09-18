package org.sanjose.views;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ItemSorter;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import org.sanjose.MainUI;
import org.sanjose.helper.*;
import org.sanjose.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class CajaManejoView extends CajaManejoUI implements View {

    public static final String VIEW_NAME = "Manejo de Caja";

    private CajaManejoLogic viewLogic = new CajaManejoLogic(this);

    private ComprobanteView comprobanteView;
    
    public VsjCajabancoRep repo;

    private BeanItemContainer<VsjCajabanco> container;

    private Container.Filter fechaFilter;

    String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "codTipomoneda",
            "codDestino", "codContraparte", "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago",
            "txtSeriecomprobantepago", "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado"
    };
    String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $", "S/$",
            "Responsable", "Lug. Gasto", "Cod. Aux", "Cta Cont.", "Rubro Inst.", "TD",
            "Serie", "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Env"
    };
    int[] FILTER_WIDTH = new int[]{ 5, 6, 4, 4,
            5, 10, 6, 6, 6, 6, 2, // S/$
            6, 4, 6, 5, 5, 2, // Tipo Doc
            4, 5, 5, 5, 4, // Fuente
            2
    };
    String[] NONEDITABLE_COLUMN_IDS = new String[]{/*"fecFecha",*/ "txtCorrelativo", "flgEnviado" };

    public ScpPlanproyectoRep planproyectoRepo;

    public ScpFinancieraRep financieraRepo;

    public Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo;
    
    @Autowired
    public CajaManejoView(VsjCajabancoRep repo, ScpPlancontableRep planRepo,
                          ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                          ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                          ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                          Scp_ContraparteRep contraparteRepo) {
    	this.repo = repo;
        this.planproyectoRepo = planproyectoRepo;
        this.financieraRepo = financieraRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        setSizeFull();
        addStyleName("crud-view");

        container = new BeanItemContainer(VsjCajabanco.class, repo.findAll());
        gridCaja.setContainerDataSource(container);
        gridCaja.setEditorEnabled(false);
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);

        ViewUtil.setColumnNames(gridCaja, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridCaja, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ViewUtil.alignMontosInGrid(gridCaja);

        gridCaja.setSelectionMode(SelectionMode.SINGLE);

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);

        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        gridCaja.addItemClickListener(event ->  setItemLogic(event));

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        viewLogic.init();

    }

    public void setComprobanteView(ComprobanteView comprobanteView) {
        this.comprobanteView = comprobanteView;
    }

    public ComprobanteView getComprobanteView() {
        return comprobanteView;
    }

    public void refreshData() {
        container.removeAllItems();
        container.addAll(repo.findAll());
        gridCaja.sort("fecFecha", SortDirection.DESCENDING);
    }

    public void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codCajabanco").getValue();
            VsjCajabanco vcb = repo.findByCodCajabanco((Integer)id);
            if (!"1".equals(vcb.getFlgEnviado())) {
                getComprobanteView().viewLogic.editarComprobante(vcb);
                MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
            }
        }
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
}
