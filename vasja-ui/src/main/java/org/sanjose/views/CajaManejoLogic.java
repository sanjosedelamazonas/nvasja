package org.sanjose.views;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanjose.MainUI;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.PrintHelper;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.*;

import java.io.Serializable;
import java.math.BigDecimal;

import static org.sanjose.views.ComprobanteLogic.USD;
import static org.sanjose.views.ComprobanteView.PEN;

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

    private Grid.FooterRow saldosFooterInicial;

    private Grid.FooterRow saldosFooterFinal;

    public CajaManejoLogic(CajaManejoView cajaManejoView) {
        view = cajaManejoView;
    }

    public void init() {

        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.btnEditar.addClickListener(e -> {
            for (Object obj : view.getSelectedRow()) {
                log.info("selected: " + obj);
                editarComprobante((VsjCajabanco) obj);
                break;
            }
        });
        view.btnVerVoucher.addClickListener(e -> generateComprobante());
        view.btnImprimir.addClickListener(e -> printComprobante());
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

    public void editarComprobante(VsjCajabanco vcb) {
        if (!"1".equals(vcb.getFlgEnviado()) && !"1".equals(vcb.getFlg_Anula())) {
            view.getComprobanteView().viewLogic.editarComprobante(vcb);
            MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
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
            ViewUtil.printComprobante(vcb);
        }
    }


    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        //if (saldosFooter!=null) grid.removeFooterRow(saldosFooter);
        BeanItemContainer<Caja> c = new BeanItemContainer<Caja>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder("codigo", "descripcion", "soles", "dolares");
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        for (ScpPlancontable caja : DataUtil.getCajas(view.planRepo)) {
            String moneda = "N".equals(caja.getIndTipomoneda()) ? "0" : "1";
            BigDecimal saldo = new ProcUtil(view.getEm()).getSaldoCaja(
                    (isInicial ? view.fechaDesde.getValue() : view.fechaHasta.getValue()),
                    caja.getId().getCodCtacontable()
                    , moneda);
            Caja cajaItem = new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    ("N".equals(caja.getIndTipomoneda()) ? saldo : new BigDecimal(0.00)),
                    ("D".equals(caja.getIndTipomoneda()) ? saldo : new BigDecimal(0.00))
            );
            c.addItem(cajaItem);
            totalSoles = totalSoles.add("N".equals(caja.getIndTipomoneda()) ? saldo : new BigDecimal(0));
            totalUsd = totalUsd.add("D".equals(caja.getIndTipomoneda()) ? saldo : new BigDecimal(0));
        }
        grid.getColumn("soles").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("dolares").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.setCellStyleGenerator(( Grid.CellReference cellReference ) -> {
            if ( "soles".equals( cellReference.getPropertyId() ) ||
                    "dolares".equals( cellReference.getPropertyId())) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });

        grid.setFooterVisible(true);
        if (isInicial) {
            if (saldosFooterInicial == null) saldosFooterInicial = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterInicial.getCell("codigo").setText("TOTAL:");
            saldosFooterInicial.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterInicial.getCell("soles").setStyleName("v-align-right");
            saldosFooterInicial.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterInicial.getCell("dolares").setStyleName("v-align-right");
        } else {
            if (saldosFooterFinal == null) saldosFooterFinal = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterFinal.getCell("codigo").setText("TOTAL:");
            saldosFooterFinal.getCell("soles").setStyleName("v-align-right");
            saldosFooterFinal.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterFinal.getCell("dolares").setStyleName("v-align-right");
            saldosFooterFinal.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
        }
        //grid.addFooterRowAt(numCajas-1);
    }
}
