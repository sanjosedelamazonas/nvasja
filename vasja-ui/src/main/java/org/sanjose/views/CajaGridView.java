package org.sanjose.views;

import java.sql.Timestamp;
import java.util.*;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.helper.*;
import org.sanjose.model.*;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjCajabancoRep;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.HtmlRenderer;

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
            "codContracta", "txtGlosaitem", "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar",
            "codDestinoitem", "codCtacontable", "codCtaespecial", "codTipocomprobantepago", "txtSeriecomprobantepago",
            "txtComprobantepago", "fecComprobantepago", "codCtaproyecto", "codFinanciera",
            "flgEnviado", "codOrigenenlace", "codComprobanteenlace"
    };
    String[] VISIBLE_COLUMN_NAMES = new String[]{"Fecha", "Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/.", "Ing $", "Egr $",
            "Cod. Aux", "Cta Contable", "Rubro Inst.", "Tipo Doc", "Serie",
            "Num Doc", "Fecha Doc", "Rubro Proy", "Fuente",
            "Enviado", "Origen", "Comprobante"
    };
    String[] NONEDITABLE_COLUMN_IDS = new String[]{/*"fecFecha",*/ "txtCorrelativo", "flgEnviado" };
    
    @Autowired
    public CajaGridView(VsjCajabancoRep repo, ScpPlancontableRep planRepo,
                        ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                        ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                        ScpPlanproyectoRep planproyectoRepo) {
    	this.repo = repo;
        setSizeFull();
        addStyleName("crud-view");

        BeanItemContainer<VsjCajabanco> container = new BeanItemContainer(VsjCajabanco.class, repo.findAll());
        
        gridCaja.setContainerDataSource(container);

        Map<String, String> colNames = new HashMap<>();
        for (int i=0;i<VISIBLE_COLUMN_NAMES.length;i++) {
            colNames.put(VISIBLE_COLUMN_IDS[i], VISIBLE_COLUMN_NAMES[i]);
        }

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

        gridCaja.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridCaja.appendHeaderRow();
        
        gridCaja.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjCajabanco>(VsjCajabanco.class));

        /*PopupDateField pdf = new PopupDateField();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecFecha").setEditorField(pdf);*/

        PopupDateField pdf = new PopupDateField();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        gridCaja.getColumn("fecFecha").setEditorField(pdf);



        gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));


        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()), "Sel Proyecto", "txtDescproyecto");
        gridCaja.getColumn("codProyecto").setEditorField(selProyecto);

        ComboBox selTercero = new ComboBox();
        DataFilterUtil.bindComboBox(selTercero, "codDestino", destinoRepo.findByIndTipodestino("3"), "Sel Tercero", "txtNombredestino");
        gridCaja.getColumn("codTercero").setEditorField(selTercero);

        ComboBox selCtacontablecaja = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codContracta").setEditorField(selCtacontablecaja);

        ComboBox selCtacontable = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontable").setEditorField(selCtacontable);

        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial",
                planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");
        gridCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        ComboBox selResponsable = new ComboBox();
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Sel Responsable", "txtNombredestino");
        gridCaja.getColumn("codDestinoitem").setEditorField(selResponsable);

        ComboBox selComprobantepago = new ComboBox();
        DataFilterUtil.bindComboBox(selComprobantepago, "codTipocomprobantepago", comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");
        gridCaja.getColumn("codTipocomprobantepago").setEditorField(selComprobantepago);


        ComboBox selFinanciera = new ComboBox();
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");
        gridCaja.getColumn("codFinanciera").setEditorField(selFinanciera);


        ComboBox selPlanproyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
                planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        gridCaja.getColumn("codCtaproyecto").setEditorField(selPlanproyecto);

        //financieraRepo

       /* ComboBox selCtacontablegasto = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta contable", "txtDescctacontable");
        gridCaja.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);
        
        ComboBox selCtaespecial = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);
         */
        // Set up a filter for all columns
	     for (Grid.Column column: gridCaja.getColumns()) {
	         Object pid = column.getPropertyId();
	         HeaderCell cell = filterRow.getCell(pid);
	
	         // Have an input field to use for filter
	         TextField filterField = new TextField();
	         if (pid.toString().contains("flgEnviado") || pid.toString().contains("codTipocomprobantepago"))
	        	 filterField.setColumns(2);
	         else
	        	 filterField.setColumns(6);
	
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
