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
import org.sanjose.helper.ConfigurationUtil;
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
        ConfigurationUtil.setPropiedadRepo(repo);
        if (ConfigurationUtil.getProperty("LOCALE")==null) {
            Notification.show("Initializing Configuracion del Sistema", Notification.Type.ERROR_MESSAGE);
            ConfigurationUtil.storeDefaultProperties();
        }

        setSizeFull();
        addStyleName("crud-view");

        BeanItemContainer<VsjPropiedad> container = new BeanItemContainer(VsjPropiedad.class, repo.findAll());

        gridPropiedad.setSelectionMode(SelectionMode.MULTI);
        gridPropiedad
        	.setContainerDataSource(container);
        Object[] VISIBLE_COLUMN_IDS = new String[]{"nombre", "valor"};
        gridPropiedad.setColumns(VISIBLE_COLUMN_IDS);
        gridPropiedad.setColumnOrder("nombre", "valor");

        gridPropiedad.setSelectionMode(SelectionMode.MULTI);
        HeaderRow filterRow = gridPropiedad.appendHeaderRow();
        
        gridPropiedad.setEditorFieldGroup(
        	    new BeanFieldGroup<VsjPropiedad>(VsjPropiedad.class));
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
