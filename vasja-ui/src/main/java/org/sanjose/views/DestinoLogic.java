package org.sanjose.views;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjCajabanco;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class DestinoLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(DestinoLogic.class);
	
    private DestinoView view;

    public DestinoLogic(DestinoView DestinoView) {
        view = DestinoView;
    }

    public void init() {
        view.btnGuardar.addClickListener(event -> saveDestino());
        view.btnAnular.addClickListener(event -> anularDestino());
        //view.nuevoDestino.addClickListener(event -> nuevoDestino());
        //view.cerrarBtn.addClickListener(event -> cerrarAlManejo());
    }

    public void saveDestino() {
        try {
            ScpDestino item = view.getScpDestino();

            if (item.getCodUregistro()==null) item.setCodUregistro(CurrentUser.get());
            if (item.getFecFregistro()==null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
            item.setCodUactualiza(CurrentUser.get());
            item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

            view.btnGuardar.setEnabled(false);
            view.btnAnular.setEnabled(false);

            log.info("Ready to save: " + item);
            //ScpDestino saved = view.destinoRepo.save(item);

            //view.nuevoDestino.setEnabled(true);
//            view.cajaManejoView.refreshData();
        } catch (CommitException ce) {
            Notification.show("Error al guardar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    public void anularDestino() {
        view.anularDestino();
        //view.nuevoDestino.setEnabled(true);
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


    public void nuevoDestino() {
        ScpDestino vcb = new ScpDestino();
        view.bindForm(vcb);
//        view.nuevoDestino.setEnabled(false);
        view.btnGuardar.setEnabled(true);
        view.btnAnular.setEnabled(true);
    }


    public void editarDestino(ScpDestino vcb) {
        view.bindForm(vcb);
        //view.nuevoDestino.setEnabled(false);
        view.btnGuardar.setEnabled(true);
        view.btnAnular.setEnabled(true);
    }
}
