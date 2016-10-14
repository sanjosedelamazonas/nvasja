package org.sanjose.views.banco;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.bean.Caja;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class BancoManejoLogic implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(BancoManejoLogic.class);
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private BancoManejoView view;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;

    public void init(BancoManejoView bancoManejoView) {
        view = bancoManejoView;
        view.btnNuevoCheque.addClickListener(e -> nuevoCheque());
        view.btnEditar.addClickListener(e -> {
            for (Object obj : view.getSelectedRow()) {
                editarCheque((VsjBancocabecera) obj);
                break;
            }
        });
        view.btnVerVoucher.addClickListener(e -> generateComprobante());
        view.btnImprimir.addClickListener(e -> printComprobante());
        view.btnReporte.addClickListener(e -> {
            //ReportHelper.generateDiarioCaja(view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });
    }

    private void nuevoCheque() {
        view.clearSelection();
        MainUI.get().getBancoOperView().getViewLogic().nuevoCheque();
        MainUI.get().getBancoOperView().getViewLogic().setNavigatorView(view);
        MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void editarCheque(VsjBancocabecera vcb) {
        if (!vcb.isEnviado()) {
            MainUI.get().getBancoOperView().getViewLogic().editarCheque(vcb);
            MainUI.get().getBancoOperView().getViewLogic().setNavigatorView(view);
            MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
        }
    }


    private void generateComprobante() {
        for (Object obj : view.getSelectedRow()) {
            log.info("selected: " + obj);
            VsjBancocabecera vcb = (VsjBancocabecera) obj;
            //ReportHelper.generateComprobante(vcb);
        }
    }

    private void printComprobante() {
        for (Object obj : view.getSelectedRow()) {
            VsjBancocabecera vcb = (VsjBancocabecera) obj;
            //ViewUtil.printComprobante(vcb);
        }
    }


    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder(COL_VIS_SALDO);
        grid.setColumns(COL_VIS_SALDO);
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        BigDecimal totalEuros = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getBancoCuentasList(view.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(view.fechaDesde.getValue())
                        : GenUtil.getEndOfDay(view.fechaHasta.getValue())))) {
            c.addItem(caja);
            totalSoles = totalSoles.add(caja.getSoles());
            totalUsd = totalUsd.add(caja.getDolares());
            totalEuros = totalEuros.add(caja.getEuros());
        }
        grid.getColumn("soles").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("dolares").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("euros").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.setCellStyleGenerator((Grid.CellReference cellReference) -> {
            if ("soles".equals(cellReference.getPropertyId()) ||
                    "dolares".equals(cellReference.getPropertyId()) ||
                    "euros".equals(cellReference.getPropertyId())) {
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
            saldosFooterInicial.getCell("euros").setText(dpf.format(totalEuros.doubleValue()));
            saldosFooterInicial.getCell("euros").setStyleName("v-align-right");
        } else {
            if (saldosFooterFinal == null) saldosFooterFinal = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterFinal.getCell("codigo").setText("TOTAL:");
            saldosFooterFinal.getCell("soles").setStyleName("v-align-right");
            saldosFooterFinal.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterFinal.getCell("dolares").setStyleName("v-align-right");
            saldosFooterFinal.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterFinal.getCell("euros").setText(dpf.format(totalEuros.doubleValue()));
            saldosFooterFinal.getCell("euros").setStyleName("v-align-right");
        }
        //grid.addFooterRowAt(numCajas-1);
    }
}
