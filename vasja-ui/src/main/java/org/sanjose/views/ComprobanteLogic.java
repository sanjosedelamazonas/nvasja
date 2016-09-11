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
public class ComprobanteLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(ComprobanteLogic.class);
	
    private ComprobanteView view;

    public ComprobanteLogic(ComprobanteView ComprobanteView) {
        view = ComprobanteView;
    }

    public void init() {

        //view.nuevoComprobante.addClickListener(e -> newComprobante());
        // register save listener
        //view.gridCaja.getEditorFieldGroup().

        view.guardarBtn.addClickListener(event -> saveComprobante());
        view.anularBtn.addClickListener(event -> anularComprobante());
    }

    public void saveComprobante() {

        //log.info("Pre commit" + item);
            VsjCajabanco vcb = new VsjCajabanco();
            //log.info("Proy " + vcb.getCodProyecto() + " " + vcb.getCodTercero());
            if (GenUtil.strNullOrEmpty(vcb.getCodProyecto()) && GenUtil.strNullOrEmpty(vcb.getCodTercero()))
                throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");
            //throw new CommitException("Codigo Proyecto o Codigo Tercero debe ser rellenado",
            //        view.gridCaja.getEditorFieldGroup(),
            //        new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado")
            //);
        // You can persist your data here
        //Notification.show("Item " + view.gridCaja.getEditedItemId() + " was edited.");
            if (vcb.getCodProyecto()==null || "".equals(vcb.getCodProyecto()))
                vcb.setIndTipocuenta("1");
            else
                vcb.setIndTipocuenta("0");
            //view.repo.save(vcb);
    }

    public void anularComprobante() {
        setFragmentParameter("");
        //view.clearSelection();
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

    public void nuevoComprobante() {
        setFragmentParameter("new");
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setCodMes("03");

        vcb.setTxtAnoproceso("2016");
        vcb.setFlgEnviado("0");
        vcb.setCodDestino("000");
        vcb.setCodTipomoneda("0");
        vcb.setIndTipocuenta("0");

        //view.gridCaja.getContainerDataSource().addItem(vcb);
    }
}
