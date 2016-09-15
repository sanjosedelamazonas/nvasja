package org.sanjose.views;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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

	private static final Logger log = LoggerFactory.getLogger(CajaManejoView.class);
	
    public static final String VIEW_NAME = "Manejo de Caja";

    private CajaManejoLogic viewLogic = new CajaManejoLogic(this);

    private ComprobanteView comprobanteView;
    
    public VsjCajabancoRep repo;

    private BeanItemContainer<VsjCajabanco> container;

    private Container.Filter fechaFilter;

    String[] VISIBLE_COLUMN_IDS = new String[]{"fecFecha", "txtCorrelativo", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar", "codTipomoneda",
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

        Map<String, String> colNames = new HashMap<>();
        for (int i=0;i<VISIBLE_COLUMN_NAMES.length;i++) {
            colNames.put(VISIBLE_COLUMN_IDS[i], VISIBLE_COLUMN_NAMES[i]);
        }

        //gridCaja.setH
        gridCaja.setColumns(VISIBLE_COLUMN_IDS);
        gridCaja.setColumnOrder(VISIBLE_COLUMN_IDS);

        //gridCaja.getColumn("fecFecha").setRenderer()

        for (String colId : colNames.keySet()) {
            gridCaja.getDefaultHeaderRow().getCell(colId).setText(colNames.get(colId));
        }

     //   gridCaja.getColumn("txtTipocuenta").setWidth(120);
        for (String colId : NONEDITABLE_COLUMN_IDS) {
            gridCaja.getColumn(colId).setEditable(false);
        }

        gridCaja.setSelectionMode(SelectionMode.SINGLE);
        HeaderRow filterRow = gridCaja.appendHeaderRow();
        
        //gridCaja.setEditorFieldGroup(
       // 	    new BeanFieldGroup<VsjCajabanco>(VsjCajabanco.class));

        // Fecha Desde
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        fechaDesde.setPropertyDataSource(prop);
        fechaDesde.setConverter(DateToTimestampConverter.INSTANCE);
        fechaDesde.setResolution(Resolution.DAY);
        fechaDesde.setValue(GenUtil.getBeginningOfMonth(new Date()));
        fechaDesde.addValueChangeListener(valueChangeEvent -> filterComprobantes());

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<Timestamp>(ts);
        fechaHasta.setPropertyDataSource(prop);
        fechaHasta.setConverter(DateToTimestampConverter.INSTANCE);
        fechaHasta.setResolution(Resolution.DAY);

        fechaHasta.setValue(GenUtil.getEndOfDay(new Date()));
        fechaHasta.addValueChangeListener(valueChangeEvent -> filterComprobantes());

        gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        Map<String, Integer> filCols = new HashMap<>();
        for (int i=0;i<FILTER_WIDTH.length;i++) {
            filCols.put(VISIBLE_COLUMN_IDS[i], FILTER_WIDTH[i]);
        }

        filterComprobantes();

        gridCaja.addItemClickListener(event ->  setItemLogic(event));

        // Set up a filter for all columns
	     for (Grid.Column column: gridCaja.getColumns()) {
	         Object pid = column.getPropertyId();
	         HeaderCell cell = filterRow.getCell(pid);
             // Have an input field to use for filter
	         TextField filterField = new TextField();
             // Set filter width according to table
             filterField.setColumns(filCols.get(pid));
             // Update filter When the filter input is changed
	         filterField.addTextChangeListener(change -> {
	             // Can't modify filters so need to replace
	        	 container.removeContainerFilters(pid);
	
	             // (Re)create the filter if necessary
	             if (! change.getText().isEmpty())
	                 container.addContainerFilter(
	                     new SimpleStringFilter(pid,
	                         change.getText(), true, false));
	         });
	         cell.setComponent(filterField);
	     }
        viewLogic.init();
    }

    public void filterComprobantes() {
        container.removeContainerFilters("fecFecha");
        Date from, to = null;
        if (fechaDesde.getValue()!=null || fechaHasta.getValue()!=null ) {
            from = (fechaDesde.getValue()!=null ? fechaDesde.getValue() : new Date(0));
            to = (fechaHasta.getValue()!=null ? fechaHasta.getValue() : new Date(Long.MAX_VALUE));
            container.addContainerFilter(
                    new Between("fecFecha",
                            from, to));
        }
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
    }

    public void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
               // The item was double-clicked, event.getItem() returns the target.
        }
        //gridCaja.isEditorEnabled()
        String proyecto = null;
        Object objProyecto = event.getItem().getItemProperty("codProyecto").getValue();
        if (objProyecto !=null && !objProyecto.toString().isEmpty())
            proyecto = objProyecto.toString();

           // log.info("Got to item: " + event.getItem() + "\n" + event.getPropertyId());
        //}
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
