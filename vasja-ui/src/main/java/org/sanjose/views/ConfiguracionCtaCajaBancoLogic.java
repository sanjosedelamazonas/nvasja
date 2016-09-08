package org.sanjose.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sanjose.MainUI;
import org.sanjose.model.VsjConfiguractacajabanco;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class ConfiguracionCtaCajaBancoLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(ConfiguracionCtaCajaBancoLogic.class);
	
    private ConfiguracionCtaCajaBancoView view;

    public ConfiguracionCtaCajaBancoLogic(ConfiguracionCtaCajaBancoView configuracionCtaCajaBancoView) {
        view = configuracionCtaCajaBancoView;
    }

    public void init() {
    	view.btnNuevaConfig.addClickListener(e -> newConfiguracion());
    	
        // register save listener
        view.gridConfigCtaCajaBanco.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            	//Notification.show("Item " + view.gridConfigCtaCajaBanco.getEditedItemId() + " was edited PRE.");                
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                // You can persist your data here            	
                //Notification.show("Item " + view.gridConfigCtaCajaBanco.getEditedItemId() + " was edited.");
                Object item = view.gridConfigCtaCajaBanco.getContainerDataSource().getItem(view.gridConfigCtaCajaBanco.getEditedItemId());
                if (item!=null) 
                	view.repo.save((VsjConfiguractacajabanco)((BeanItem)item).getBean());
            }
        });
        
        view.btnEliminar.addClickListener(e -> deleteConfiguracion());
    }

    public void cancelProduct() {
        setFragmentParameter("");
        view.clearSelection();
//        view.editProduct(null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String productId) {
        String fragmentParameter;
        if (productId == null || productId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = productId;
        }

        Page page = MainUI.get().getPage();
  /*      page.setUriFragment("!" + SampleCrudView.VIEW_NAME + "/"
                + fragmentParameter, false);
  */  }

    public void enter(String productId) {
        if (productId != null && !productId.isEmpty()) {
        	log.info("Configuracion Logic getting: " + productId);
            if (productId.equals("new")) {
            	newConfiguracion();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(productId);
  //                  Product product = findProduct(pid);
    //                view.selectRow(product);
                } catch (NumberFormatException e) {
                }
            }
        }
    }
    
    public void newConfiguracion() {
        view.clearSelection();
        setFragmentParameter("new");
        view.gridConfigCtaCajaBanco.getContainerDataSource().addItem(new VsjConfiguractacajabanco());              
    }
    
    
    public void deleteConfiguracion() {
        List<VsjConfiguractacajabanco> rows = new ArrayList<VsjConfiguractacajabanco>();
    	
        for (Object vsj : view.getSelectedRow()) {
        	log.info("Got selected: " + vsj);
        	if (vsj instanceof VsjConfiguractacajabanco)
        		rows.add((VsjConfiguractacajabanco)vsj);
        }
        view.clearSelection();
        //setFragmentParameter("new");
        for (VsjConfiguractacajabanco vsj : rows) {
        	log.info("Removing: " + vsj.getCodTipocuenta());        	
        	view.removeRow(vsj);
        }
        //view.gridConfigCtaCajaBanco.getContainerDataSource().removeItem(itemId)
    }
    
/*
    private Product findProduct(int productId) {
        return DataService.get().getProductById(productId);
    }

    public void saveProduct(Product product) {
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") updated");
        view.clearSelection();
//        view.editProduct(null);
//        view.refreshProduct(product);
        setFragmentParameter("");
    }

    public void deleteProduct(Product product) {
        DataService.get().deleteProduct(product.getId());
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") removed");

        view.clearSelection();
  //      view.editProduct(null);
   //     view.removeProduct(product);
        setFragmentParameter("");
    }

    public void editProduct(Product product) {
        if (product == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(product.getId() + "");
        }
   //     view.editProduct(product);
    }

    public void newProduct() {
        view.clearSelection();
        setFragmentParameter("new");
    //    view.editProduct(new Product());
    }

    public void rowSelected(Product product) {
        if (MainUI.get().getAccessControl().isUserInRole("admin")) {
     //       view.editProduct(product);
        }
    }
    */
}
