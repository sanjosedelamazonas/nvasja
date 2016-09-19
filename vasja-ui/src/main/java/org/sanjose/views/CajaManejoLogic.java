package org.sanjose.views;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanjose.MainUI;
import org.sanjose.helper.PrintHelper;
import org.sanjose.model.ReportHelper;
import org.sanjose.model.VsjCajabanco;

import java.io.Serializable;

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
        view.btnReporteCaja.addClickListener(e -> generateComprobante());
        view.btnEnviar.addClickListener(e -> printComprobante());
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

    public void generateComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjCajabanco vcb = (VsjCajabanco)obj;
            ReportHelper.generateComprobante(vcb);
        }
    }

    public void printComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjCajabanco vcb = (VsjCajabanco) obj;
            try {
                JasperPrint jrPrint = ReportHelper.printComprobante(vcb);
                boolean isPrinted = false;

                PrintHelper ph = ((MainUI)MainUI.getCurrent()).getMainScreen().getPrintHelper();
                isPrinted = ph.print(jrPrint, true);
                if (!isPrinted)
                    throw new JRException("Problema al consequir un servicio de imprimir");
            } catch (JRException e) {
                e.printStackTrace();
                Notification.show("Problema al imprimir el comprobante ID: " + vcb.getCodCajabanco() + " " + e.getMessage());
            }
        }
    }
}
