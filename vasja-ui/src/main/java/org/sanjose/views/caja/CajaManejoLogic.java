package org.sanjose.views.caja;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.NonEditableException;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class CajaManejoLogic extends CajaLogic implements ItemsRefreshing<ScpCajabanco>,Serializable, SaldoDelDia {


	private static final Logger log = LoggerFactory.getLogger(CajaManejoLogic.class);
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private CajaManejoView view;
    private CajaSaldoView saldosView = new CajaSaldoView();
    private Grid.FooterRow saldosFooterInicial=null;
    private Grid.FooterRow saldosFooterFinal=null;

    public void init(CajaManejoView cajaManejoView) {
        view = cajaManejoView;
        cajaView = view;
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.nuevaTransferencia.addClickListener(e -> newTransferencia());
        view.btnEditar.addClickListener(e -> editarComprobante(view.getSelectedRow()));
        view.btnVerVoucher.addClickListener(e -> generateComprobante());
        //
        view.btnImprimir.setVisible(ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT"));
        view.btnImprimir.addClickListener(e -> printComprobante());
        saldosView.getBtnReporte().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(view.fechaDesde.getValue(), view.fechaHasta.getValue(), null));
        view.btnDetallesSaldos.addClickListener(e -> {
            setSaldos(saldosView.getGridSaldoInicial(), true);
            setSaldos(saldosView.getGridSaldoFInal(), false);
            setSaldoDelDia();
            ViewUtil.openCajaSaldosInNewWindow(saldosView, view.fechaDesde.getValue(), view.fechaHasta.getValue());
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridCaja());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
            } else {

                gridContextMenu.addItem(!GenUtil.strNullOrEmpty(((ScpCajabanco) itemId).getCodTranscorrelativo()) ? "Ver detalle" : "Editar",
                        k -> editarComprobante((ScpCajabanco) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Ver Voucher", k -> generateComprobante());
                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> printComprobante());

                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> { enviarContabilidad((ScpCajabanco)itemId); });
                }
            }
        });
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
        BigDecimal totalEur = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getCajasList(view.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(view.fechaDesde.getValue())
                        : GenUtil.getEndOfDay(view.fechaHasta.getValue())))) {
            c.addItem(caja);
            totalSoles = totalSoles.add(caja.getSoles());
            totalUsd = totalUsd.add(caja.getDolares());
            totalEur = totalEur.add(caja.getEuros());
        }
        grid.getColumn("soles").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("dolares").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("euros").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.setCellStyleGenerator(( Grid.CellReference cellReference ) -> {
            if ( "soles".equals( cellReference.getPropertyId() ) ||
                    "dolares".equals( cellReference.getPropertyId()) ||
                    "euros".equals( cellReference.getPropertyId())) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });

        grid.setFooterVisible(true);
        if (isInicial) {
            if (saldosFooterInicial==null) saldosFooterInicial = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterInicial.getCell("codigo").setText("TOTAL:");
            saldosFooterInicial.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterInicial.getCell("soles").setStyleName("v-align-right");
            saldosFooterInicial.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterInicial.getCell("dolares").setStyleName("v-align-right");
            saldosFooterInicial.getCell("euros").setText(dpf.format(totalEur.doubleValue()));
            saldosFooterInicial.getCell("euros").setStyleName("v-align-right");
        } else {
            if (saldosFooterFinal==null) saldosFooterFinal = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterFinal.getCell("codigo").setText("TOTAL:");
            saldosFooterFinal.getCell("soles").setStyleName("v-align-right");
            saldosFooterFinal.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterFinal.getCell("dolares").setStyleName("v-align-right");
            saldosFooterFinal.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterFinal.getCell("euros").setText(dpf.format(totalEur.doubleValue()));
            saldosFooterFinal.getCell("euros").setStyleName("v-align-right");
        }
    }

    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);
        BigDecimal totalEurDiaIng = new BigDecimal(0.00);
        BigDecimal totalEurDiaEgr = new BigDecimal(0.00);

        for (Object item : view.gridCaja.getContainerDataSource().getItemIds()) {
            ScpCajabanco cajabanco = (ScpCajabanco) item;
            // PEN
            totalSolesDiaEgr = totalSolesDiaEgr.add(cajabanco.getNumHabersol());
            totalSolesDiaIng = totalSolesDiaIng.add(cajabanco.getNumDebesol());
            // USD
            totalUsdDiaEgr = totalUsdDiaEgr.add(cajabanco.getNumHaberdolar());
            totalUsdDiaIng = totalUsdDiaIng.add(cajabanco.getNumDebedolar());
            // EUR
            totalEurDiaEgr = totalEurDiaEgr.add(cajabanco.getNumHabermo());
            totalEurDiaIng = totalEurDiaIng.add(cajabanco.getNumDebemo());
        }
        DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                null, ConfigurationUtil.get("DECIMAL_FORMAT"));

        // PEN
        saldosView.getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        saldosView.getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        saldosView.getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        saldosView.getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        saldosView.getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        saldosView.getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        saldosView.getValEurEgr().setValue(dpf.format(totalEurDiaEgr.doubleValue()));
        saldosView.getValEurIng().setValue(dpf.format(totalEurDiaIng.doubleValue()));
        saldosView.getValEurSaldo().setValue(dpf.format(totalEurDiaIng.subtract(totalEurDiaEgr).doubleValue()));

        saldosView.gridSaldoDelDia.setColumnExpandRatio(0, 0);
    }

}
