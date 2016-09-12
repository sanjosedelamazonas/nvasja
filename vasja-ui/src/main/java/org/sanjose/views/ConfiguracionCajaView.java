package org.sanjose.views;

import java.util.Collection;

import org.sanjose.helper.BooleanTrafficLight;
import org.sanjose.helper.DataFilterUtil;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.*;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.HtmlRenderer;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class ConfiguracionCajaView extends ConfiguracionCajaUI implements View {

	private static final Logger log = LoggerFactory.getLogger(ConfiguracionCajaView.class);
	
    public static final String VIEW_NAME = "Configuracion";

    private ConfiguracionCajaLogic viewLogic = new ConfiguracionCajaLogic(this);
    
    public VsjConfiguracioncajaRep repo;
    
    @Autowired
    public ConfiguracionCajaView(VsjConfiguracioncajaRep repo, ScpPlancontableRep planRepo, ScpPlanespecialRep planEspRepo) {
    	this.repo = repo;
        setSizeFull();
        //addStyleName("crud-view");

        BeanItemContainer<VsjConfiguractacajabanco> container = new BeanItemContainer(VsjConfiguractacajabanco.class, repo.findAll());
        gridConfigCaja
        	.setContainerDataSource(container);
        gridConfigCaja.setColumnOrder("activo", "codTipocuenta", "txtTipocuenta", "codCtacontablecaja",
                "codCtacontablegasto", "codCtaespecial", "paraCaja", "paraBanco", "paraProyecto", "paraTercero");
        
        
        gridConfigCaja.getDefaultHeaderRow().getCell("codTipocuenta").setText("Codigo");
        
        gridConfigCaja.getColumn("txtTipocuenta").setWidth(120);
        //gridConfigCaja.setCol
        
        gridConfigCaja.getColumn("codTipocuenta").setEditable(false);
               
        gridConfigCaja.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridConfigCaja.appendHeaderRow();
        
        gridConfigCaja.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjConfiguractacajabanco>(VsjConfiguractacajabanco.class));
        
        ComboBox selCtacontablecaja = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCaja.getColumn("codCtacontablecaja").setEditorField(selCtacontablecaja);
        
        ComboBox selCtacontablegasto = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta contable", "txtDescctacontable");
        gridConfigCaja.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);
        
        ComboBox selCtaespecial = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridConfigCaja.getColumn("codCtaespecial").setEditorField(selCtaespecial);
        
        gridConfigCaja.getColumn("activo").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCaja.getColumn("paraProyecto").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCaja.getColumn("paraTercero").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCaja.getColumn("paraBanco").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCaja.getColumn("paraCaja").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        
        // Grey out inactive rows
        gridConfigCaja.setRowStyleGenerator(rowRef -> {// Java 8
		  if (! ((Boolean) rowRef.getItem()
					.getItemProperty("activo")
					.getValue()).booleanValue())
		      return "grayed";
		  else
		      return null;
		});
        
        // Set up a filter for all columns
	     for (Object pid: gridConfigCaja.getContainerDataSource()
	                          .getContainerPropertyIds()) {
	         HeaderCell cell = filterRow.getCell(pid);
	
	         // Have an input field to use for filter
	         TextField filterField = new TextField();
	         if (pid.toString().contains("para") || pid.toString().contains("activo"))
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
    
    public ConfiguracionCajaView() {
        this(null, null, null);
    }


    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void clearSelection() {
        gridConfigCaja.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCaja.getSelectedRows();
    }

    public void removeRow(VsjConfiguracioncaja vsj) {
    	repo.delete(vsj);    	
    	gridConfigCaja.getContainerDataSource().removeItem(vsj);
    }
}
