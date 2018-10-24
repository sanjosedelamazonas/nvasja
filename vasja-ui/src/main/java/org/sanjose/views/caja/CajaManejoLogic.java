package org.sanjose.views.caja;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.v7.ui.Grid;
import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.NonEditableException;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjItem;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class CajaManejoLogic implements Serializable, SaldoDelDia {


	private static final Logger log = LoggerFactory.getLogger(CajaManejoLogic.class);
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares"};
    private CajaManejoView view;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;

    public void init(CajaManejoView cajaManejoView) {
        view = cajaManejoView;
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.btnEditar.addClickListener(e -> editarComprobante(view.getSelectedRow()));
        view.btnVerVoucher.addClickListener(e -> generateComprobante());
        view.btnImprimir.addClickListener(e -> printComprobante());
        view.btnReporteCaja.addClickListener(e -> {
            ReportHelper.generateDiarioCaja(view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridCaja());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
            } else {

                gridContextMenu.addItem(!GenUtil.strNullOrEmpty(((VsjCajabanco) itemId).getCodTranscorrelativo()) ? "Ver detalle" : "Editar",
                        k -> editarComprobante((VsjCajabanco) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Ver Voucher", k -> generateComprobante());
                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> printComprobante());

                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        List<Object> cajabancos = new ArrayList<>();
                        cajabancos.add(itemId);
                        MainUI.get().getProcUtil().enviarContabilidad(cajabancos, view.getService());
                        view.refreshData();
                    });
                }
            }
        });
    }

    private void newComprobante() {
        view.clearSelection();
        MainUI.get().getComprobanteView().viewLogic.nuevoComprobante();
        MainUI.get().getComprobanteView().viewLogic.setNavigatorView(view);
        MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    public void editarComprobante(VsjCajabanco vcb) {
        // Transferencia
        if (!GenUtil.strNullOrEmpty(vcb.getCodTranscorrelativo())) {
            try {
                MainUI.get().getTransferenciaView().viewLogic.editarTransferencia(vcb);
                MainUI.get().getTransferenciaView().viewLogic.setNavigatorView(view);
                MainUI.get().getNavigator().navigateTo(TransferenciaView.VIEW_NAME);
            } catch (NonEditableException e) {
                Notification.show("No es editable", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        } else {
            MainUI.get().getComprobanteView().viewLogic.editarComprobante(vcb);
            MainUI.get().getComprobanteView().viewLogic.setNavigatorView(view);
            MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
        }
    }


    private void generateComprobante() {
        ReportHelper.generateComprobante(view.getSelectedRow());
    }

    private void printComprobante() {
        ViewUtil.printComprobante(view.getSelectedRow());
    }


    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder(COL_VIS_SALDO);
        grid.setColumns(COL_VIS_SALDO);
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getCajasList(view.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(view.fechaDesde.getValue())
                        : GenUtil.getEndOfDay(view.fechaHasta.getValue())))) {
            c.addItem(caja);
            totalSoles = totalSoles.add(caja.getSoles());
            totalUsd = totalUsd.add(caja.getDolares());
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
        setSaldoDelDia();
    }

    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);

        for (Object item : view.gridCaja.getContainerDataSource().getItemIds()) {
            VsjCajabanco cajabanco = (VsjCajabanco) item;
            // PEN
            totalSolesDiaEgr = totalSolesDiaEgr.add(cajabanco.getNumHabersol());
            totalSolesDiaIng = totalSolesDiaIng.add(cajabanco.getNumDebesol());
            // USD
            totalUsdDiaEgr = totalUsdDiaEgr.add(cajabanco.getNumHaberdolar());
            totalUsdDiaIng = totalUsdDiaIng.add(cajabanco.getNumDebedolar());
        }
        DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                null, ConfigurationUtil.get("DECIMAL_FORMAT"));

        // PEN
        view.getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        view.getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        view.getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        view.getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        view.getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        view.getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));

        view.gridSaldoDelDia.setColumnExpandRatio(0, 0);
    }

}
