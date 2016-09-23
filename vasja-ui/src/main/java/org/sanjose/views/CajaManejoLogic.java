package org.sanjose.views;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.NonEditableException;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.*;

import java.io.Serializable;
import java.math.BigDecimal;

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

    private final CajaManejoView view;

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
        view.btnReporteCaja.addClickListener(e -> {
            ReportHelper.generateDiarioCaja(view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });
    }

    public void enter(String productId) {
    }

    private void newComprobante() {
        view.clearSelection();
        MainUI.get().getComprobanteView().viewLogic.nuevoComprobante();
        MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    public void editarComprobante(VsjCajabanco vcb) {
        if (!vcb.isEnviado() && !vcb.isAnula()) {
            // Transferencia
            if (!GenUtil.strNullOrEmpty(vcb.getCodTranscorrelativo())) {
                try {
                    MainUI.get().getTransferenciaView().viewLogic.editarTransferencia(vcb);
                    MainUI.get().getNavigator().navigateTo(TransferenciaView.VIEW_NAME);
                } catch (NonEditableException e) {
                    Notification.show("No es editable", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            } else {
                MainUI.get().getComprobanteView().viewLogic.editarComprobante(vcb);
                MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
            }
        }
    }


    private void generateComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjCajabanco vcb = (VsjCajabanco)obj;
            ReportHelper.generateComprobante(vcb);
        }
    }

    private void printComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjCajabanco vcb = (VsjCajabanco) obj;
            ViewUtil.printComprobante(vcb);
        }
    }


    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder("codigo", "descripcion", "soles", "dolares");
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        for (ScpPlancontable caja : DataUtil.getCajas(view.planRepo)) {
            char moneda = caja.getIndTipomoneda().equals('N') ? '0' : '1';
            BigDecimal saldo = new ProcUtil(view.getEm()).getSaldoCaja(
                    (isInicial ? view.fechaDesde.getValue() : view.fechaHasta.getValue()),
                    caja.getId().getCodCtacontable()
                    , moneda);
            Caja cajaItem = new Caja(caja.getId().getCodCtacontable(), caja.getTxtDescctacontable(),
                    (caja.getIndTipomoneda().equals('N') ? saldo : new BigDecimal(0.00)),
                    (caja.getIndTipomoneda().equals('D') ? saldo : new BigDecimal(0.00))
            );
            c.addItem(cajaItem);
            totalSoles = totalSoles.add(caja.getIndTipomoneda().equals('N') ? saldo : new BigDecimal(0));
            totalUsd = totalUsd.add(caja.getIndTipomoneda().equals('D') ? saldo : new BigDecimal(0));
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
