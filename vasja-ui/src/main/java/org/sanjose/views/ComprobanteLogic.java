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
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.VsjCajabanco;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        view.guardarBtn.addClickListener(event -> saveComprobante());
        view.anularBtn.addClickListener(event -> anularComprobante());
        view.nuevoComprobante.addClickListener(event -> nuevoComprobante());
        //nuevoComprobante();
    }

    public void saveComprobante() {
        try {
            VsjCajabanco item = view.getVsjCajabanco();
            if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
                throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            item.setCodMes(sdf.format(item.getFecFecha()));
            sdf = new SimpleDateFormat("yyyy");
            item.setTxtAnoproceso(sdf.format(item.getFecFecha()));
            if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
                item.setIndTipocuenta("0");
            } else {
                item.setIndTipocuenta("1");
            }
            item.setCodUregistro(CurrentUser.get());
            item.setFecFregistro(new Timestamp(System.currentTimeMillis()));

            log.info("Ready to save: " + item);
        } catch (CommitException ce) {
            log.info("Got Commit Exception: " + ce.getMessage());
        }
        //view.repo.save(item);
        // You can persist your data here
        //Notification.show("Item " + view.gridCaja.getEditedItemId() + " was edited.");
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
        vcb.setFlgEnviado("0");
        vcb.setIndTipocuenta("0");
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));

        view.bindForm(vcb);
    }
}
