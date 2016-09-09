package org.sanjose.views;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import org.sanjose.helper.DateToTimestampConverter;
import org.sanjose.model.ScpPlancontableRep;
import org.sanjose.model.ScpPlanespecialRep;
import org.sanjose.model.VsjPropiedad;
import org.sanjose.model.VsjPropiedadRep;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Collection;

/**          A
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class PropiedadView extends PropiedadUI implements View {

	private static final Logger log = LoggerFactory.getLogger(PropiedadView.class);
	
    public static final String VIEW_NAME = "Config del Sistema";

    private PropiedadLogic viewLogic = new PropiedadLogic(this);
    
    public VsjPropiedadRep repo;
    
    @Autowired
    public PropiedadView(VsjPropiedadRep repo) {
    	this.repo = repo;
        setSizeFull();
        addStyleName("crud-view");

        BeanItemContainer<VsjPropiedad> container = new BeanItemContainer(VsjPropiedad.class, repo.findAll());
        
        gridPropiedad
        	.setContainerDataSource(container);
        Object[] VISIBLE_COLUMN_IDS = new String[]{"nombre", "valor"};
        gridPropiedad.setColumns(VISIBLE_COLUMN_IDS);
        gridPropiedad.setColumnOrder("nombre", "valor");

        gridPropiedad.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridPropiedad.appendHeaderRow();
        
        gridPropiedad.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjPropiedad>(VsjPropiedad.class));

        // Set up a filter for all columns
	   /*  for (Object pid: gridPropiedad.getContainerDataSource()
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
	     }*/
        viewLogic.init();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void clearSelection() {
        gridPropiedad.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridPropiedad.getSelectedRows();
    }

    public void removeRow(VsjPropiedad vsj) {
    	repo.delete(vsj);    	
    	gridPropiedad.getContainerDataSource().removeItem(vsj);
    }
}
