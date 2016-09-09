package org.sanjose.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.model.VsjCajabanco;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import org.sanjose.model.VsjConfiguractacajabanco;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class CajaGridLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(CajaGridLogic.class);
	
    private CajaGridView view;

    public CajaGridLogic(CajaGridView cajaGridView) {
        view = cajaGridView;
    }

    public void init() {

        view.nuevoComprobante.addClickListener(e -> newComprobante());
        // register save listener
        //view.gridCaja.getEditorFieldGroup().


        view.gridCaja.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                // You can persist your data here
                //Notification.show("Item " + view.gridCaja.getEditedItemId() + " was edited.");
                Object item = view.gridCaja.getContainerDataSource().getItem(view.gridCaja.getEditedItemId());
                if (item!=null) 
                	view.repo.save((VsjCajabanco)((BeanItem)item).getBean());
            }
        });
               
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
     //       	newConfiguracion();
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

    public void newComprobante() {
        view.clearSelection();
        setFragmentParameter("new");
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setCodDestino("TEST");
        vcb.setCodMes("03");

        vcb.setTxtAnoproceso("2016");
        vcb.setFlgEnviado("0");
        vcb.setCodDestino("000");
        vcb.setCodTipomoneda("0");
        vcb.setIndTipocuenta("0");

        view.gridCaja.getContainerDataSource().addItem(vcb);
    }


    public void deleteConfiguracion() {
        List<VsjCajabanco> rows = new ArrayList<VsjCajabanco>();
    	
        for (Object vsj : view.getSelectedRow()) {
        	log.info("Got selected: " + vsj);
        	if (vsj instanceof VsjCajabanco)
        		rows.add((VsjCajabanco)vsj);
        }
        view.clearSelection();
        //setFragmentParameter("new");
        for (VsjCajabanco vsj : rows) {
        	log.info("Removing: " + vsj.getCodCajabanco());        	
        	view.removeRow(vsj);
        }
        //view.gridCaja.getContainerDataSource().removeItem(itemId)
    }    
}
