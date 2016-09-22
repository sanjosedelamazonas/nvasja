package org.sanjose.views;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import org.sanjose.MainUI;
import org.sanjose.model.VsjConfiguracioncaja;
import org.sanjose.model.VsjConfiguractacajabanco;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class ConfiguracionCajaLogic implements Serializable {


	private static final Logger log = LoggerFactory.getLogger(ConfiguracionCajaLogic.class);

    private final ConfiguracionCajaView view;

    public ConfiguracionCajaLogic(ConfiguracionCajaView configuracionCtaCajaBancoView) {
        view = configuracionCtaCajaBancoView;
    }

    public void init() {
    	view.btnNuevaConfig.addClickListener(e -> newConfiguracion());
    	
        // register save listener
        view.gridConfigCaja.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            	//Notification.show("Item " + view.gridConfigCaja.getEditedItemId() + " was edited PRE.");                
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                // You can persist your data here            	
                //Notification.show("Item " + view.gridConfigCaja.getEditedItemId() + " was edited.");
                Object item = view.gridConfigCaja.getContainerDataSource().getItem(view.gridConfigCaja.getEditedItemId());
                if (item!=null) 
                	view.repo.save((VsjConfiguracioncaja)((BeanItem)item).getBean());
            }
        });
        
        view.btnEliminar.addClickListener(e -> deleteConfiguracion());
    }

    public void enter(String productId) {
    }
    
    private void newConfiguracion() {
        view.clearSelection();
        view.gridConfigCaja.getContainerDataSource().addItem(new VsjConfiguracioncaja());
    }

    private void deleteConfiguracion() {
        List<VsjConfiguracioncaja> rows = new ArrayList<>();
    	
        for (Object vsj : view.getSelectedRow()) {
        	log.info("Got selected: " + vsj);
        	if (vsj instanceof VsjConfiguractacajabanco)
        		rows.add((VsjConfiguracioncaja)vsj);
        }
        view.clearSelection();
        for (VsjConfiguracioncaja vsj : rows) {
        	log.info("Removing: " + vsj.getCodConfiguracion());
        	view.removeRow(vsj);
        }
    }

}
