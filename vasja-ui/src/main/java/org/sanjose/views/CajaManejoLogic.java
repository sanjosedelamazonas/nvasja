package org.sanjose.views;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import org.sanjose.MainUI;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.VsjCajabanco;

import java.io.Serializable;
import java.sql.Timestamp;
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
public class CajaManejoLogic implements Serializable {


	private static final Logger log = LoggerFactory.getLogger(CajaManejoLogic.class);

    private CajaManejoView view;

    public CajaManejoLogic(CajaManejoView cajaManejoView) {
        view = cajaManejoView;
    }

    public void init() {

        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.btnEditar.addClickListener(e -> editarComprobante());
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
        view.getComprobanteView().viewLogic.nuevoComprobante();
        MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    public void editarComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjCajabanco vcb = (VsjCajabanco)obj;
            if (!"1".equals(vcb.getFlgEnviado())) {
                view.getComprobanteView().viewLogic.editarComprobante(vcb);
                MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
                break;
            }
        }
    }
}
