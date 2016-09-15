package org.sanjose.views;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.VsjCajabanco;

import java.io.Serializable;
import java.math.BigDecimal;
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
        view.cerrarBtn.addClickListener(event -> cerrarAlManejo());
    }


    public void cerrarAlManejo() {
        MainUI.get().getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
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
            if (item.getCodUregistro()==null) item.setCodUregistro(CurrentUser.get());
            if (item.getFecFregistro()==null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
            item.setCodUactualiza(CurrentUser.get());
            item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

            // Verify moneda and fields
            if ("0".equals(item.getCodTipomoneda())) {
                if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
                    throw new CommitException("Selected SOL but values are zeros or nulls");
                if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
                    throw new CommitException("Selected SOL but values for Dolar are not zeros or nulls");
                item.setNumHaberdolar(new BigDecimal(0.00));
                item.setNumDebedolar(new BigDecimal(0.00));
            } else {
                if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
                    throw new CommitException("Selected USD but values are zeros or nulls");
                if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
                    throw new CommitException("Selected USD but values for SOL are not zeros or nulls");
                item.setNumHabersol(new BigDecimal(0.00));
                item.setNumDebesol(new BigDecimal(0.00));
            }
            log.info("Ready to save: " + item);
            VsjCajabanco saved = view.repo.save(item);
            view.numVoucher.setValue(new Integer(saved.getCodCajabanco()).toString());
            view.guardarBtn.setEnabled(false);
            view.anularBtn.setEnabled(false);
            view.nuevoComprobante.setEnabled(true);
            view.cajaManejoView.refreshData();
        } catch (CommitException ce) {
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    public void anularComprobante() {
        view.anularComprobante();
        view.nuevoComprobante.setEnabled(true);
    }


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
//        setFragmentParameter("new");
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setFlgEnviado("0");
        vcb.setIndTipocuenta("0");
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        view.bindForm(vcb);
        view.nuevoComprobante.setEnabled(false);
        view.guardarBtn.setEnabled(true);
        view.anularBtn.setEnabled(true);
    }

    public void editarComprobante(VsjCajabanco vcb) {
  //      setFragmentParameter("edit");
        view.bindForm(vcb);
        view.nuevoComprobante.setEnabled(false);
        view.guardarBtn.setEnabled(true);
        view.anularBtn.setEnabled(true);
    }
}
