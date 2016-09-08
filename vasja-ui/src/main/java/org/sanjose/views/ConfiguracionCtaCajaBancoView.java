package org.sanjose.views;

import java.util.Collection;

import org.sanjose.helper.BooleanTrafficLight;
import org.sanjose.helper.DataFilterUtil;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.ScpPlancontableRep;
import org.sanjose.model.ScpPlanespecialRep;
import org.sanjose.model.VsjConfiguractacajabanco;
import org.sanjose.model.VsjConfiguractacajabancoRep;
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
public class ConfiguracionCtaCajaBancoView extends ConfiguracionCtaCajaBancoUI implements View {

	private static final Logger log = LoggerFactory.getLogger(ConfiguracionCtaCajaBancoView.class);
	
    public static final String VIEW_NAME = "Configuracion";

    private ConfiguracionCtaCajaBancoLogic viewLogic = new ConfiguracionCtaCajaBancoLogic(this);
    
    public VsjConfiguractacajabancoRep repo;
    
    @Autowired
    public ConfiguracionCtaCajaBancoView(VsjConfiguractacajabancoRep repo, ScpPlancontableRep planRepo, ScpPlanespecialRep planEspRepo) {
    	this.repo = repo;
        setSizeFull();
        //addStyleName("crud-view");

        BeanItemContainer<VsjConfiguractacajabanco> container = new BeanItemContainer(VsjConfiguractacajabanco.class, repo.findAll());
        gridConfigCtaCajaBanco
        	.setContainerDataSource(container);
        gridConfigCtaCajaBanco.setColumnOrder("activo", "codTipocuenta", "txtTipocuenta", "codCtacontablecaja",
                "codCtacontablegasto", "codCtaespecial", "paraCaja", "paraBanco", "paraProyecto", "paraTercero");
        
        
        gridConfigCtaCajaBanco.getDefaultHeaderRow().getCell("codTipocuenta").setText("Codigo");
        
        gridConfigCtaCajaBanco.getColumn("txtTipocuenta").setWidth(120);
        //gridConfigCtaCajaBanco.setCol
        
        gridConfigCtaCajaBanco.getColumn("codTipocuenta").setEditable(false);
               
        gridConfigCtaCajaBanco.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridConfigCtaCajaBanco.appendHeaderRow();
        
        gridConfigCtaCajaBanco.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjConfiguractacajabanco>(VsjConfiguractacajabanco.class));
        
        ComboBox selCtacontablecaja = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtacontablecaja, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), "101"), "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablecaja").setEditorField(selCtacontablecaja);
        
        ComboBox selCtacontablegasto = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtacontablegasto, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta contable", "txtDescctacontable");
        gridConfigCtaCajaBanco.getColumn("codCtacontablegasto").setEditorField(selCtacontablegasto);
        
        ComboBox selCtaespecial = new ComboBox();  
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial", planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()), "Sel cta especial", "txtDescctaespecial");
        gridConfigCtaCajaBanco.getColumn("codCtaespecial").setEditorField(selCtaespecial);
        
        gridConfigCtaCajaBanco.getColumn("activo").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraProyecto").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraTercero").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraBanco").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        gridConfigCtaCajaBanco.getColumn("paraCaja").setConverter(new BooleanTrafficLight()).setRenderer(new HtmlRenderer());
        
        // Grey out inactive rows
        gridConfigCtaCajaBanco.setRowStyleGenerator(rowRef -> {// Java 8
		  if (! ((Boolean) rowRef.getItem()
					.getItemProperty("activo")
					.getValue()).booleanValue())
		      return "grayed";
		  else
		      return null;
		});
        
        // Set up a filter for all columns
	     for (Object pid: gridConfigCtaCajaBanco.getContainerDataSource()
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
    
    public ConfiguracionCtaCajaBancoView() {
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
        gridConfigCtaCajaBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridConfigCtaCajaBanco.getSelectedRows();
    }

    public void removeRow(VsjConfiguractacajabanco vsj) {
    	repo.delete(vsj);    	
    	gridConfigCtaCajaBanco.getContainerDataSource().removeItem(vsj);
    }
}
