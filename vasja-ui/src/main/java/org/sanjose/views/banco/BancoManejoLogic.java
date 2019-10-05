package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.*;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class BancoManejoLogic extends BancoGridLogic implements Serializable, SaldoDelDia {

    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private BancoManejoView mView;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;
    private static final Logger log = LoggerFactory.getLogger(BancoManejoLogic.class);

    public BancoManejoLogic(BancoManejoView view) {
        super(view);
        mView = view;
        init();
    }

    public void init() {
        mView.btnNuevoCheque.addClickListener(e -> nuevoCheque(mView.getBancoCuenta()));
        mView.btnEditar.addClickListener(e -> {
            for (Object obj : mView.getSelectedRows()) {
                editarCheque((ScpBancocabecera) obj);
                break;
            }
        });
        mView.btnVerVoucher.addClickListener(e -> generateComprobante());
        mView.btnImprimir.addClickListener(e -> printComprobante());
        mView.btnReporte.addClickListener(e -> {
            ReportHelper.generateDiarioBanco(mView.getSelRepMoneda().getValue().toString().charAt(0),
                    mView.fechaDesde.getValue(), mView.fechaHasta.getValue(), null);
        });

        mView.getBtnDetallesSaldos().addClickListener(e -> {
            setSaldos(mView.getSaldosView().getGridSaldoInicial(), true);
            setSaldos(mView.getSaldosView().getGridSaldoFinal(), false);
            setSaldoDelDia();
            ViewUtil.openCajaSaldosInNewWindow(mView.getSaldosView(), mView.getFechaDesde().getValue(), mView.getFechaHasta().getValue());
        });
        mView.getSaldosView().getBtnReporte().addClickListener(e -> {
            ReportHelper.generateDiarioBanco(mView.getSelRepMoneda().getValue().toString().charAt(0),
                    mView.fechaDesde.getValue(), mView.fechaHasta.getValue(), null);
        });
        mView.getBtnEliminar().addClickListener(clickEvent -> {
            for (Object row : mView.getSelectedRows()) {
                anularCheque((ScpBancocabecera)row);
                return;
            }
        });
        mView.getBtnMarcarCobrado().addClickListener(clickEvent -> { setMesCobrado(true); });
        mView.getBtnMarcarNoCobrado().addClickListener(clickEvent -> { setMesCobrado(false); });

        GridContextMenu gridContextMenu = new GridContextMenu(mView.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> nuevoCheque(mView.getBancoCuenta()));
            } else {
                gridContextMenu.addItem("Ver detalle", k -> editarCheque((ScpBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo cheque", k -> nuevoCheque(mView.getBancoCuenta()));
                if (!((ScpBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> anularCheque((ScpBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        enviarContabilidad((ScpBancocabecera)itemId);
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjCajaBancoItem) itemId));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajaBancoItem) itemId));
            }
        });
    }

    public void setSaldoCuenta(ScpPlancontable cuenta) {
        if (cuenta==null) {
            mView.getNumSaldoFinalLibro().setValue("");
            mView.getNumSaldoFinalSegBancos().setValue("");
            mView.getNumSaldoInicialLibro().setValue("");
            mView.getNumSaldoInicialSegBancos().setValue("");
        } else {
            ProcUtil.SaldosBanco saldosIni = DataUtil.getBancoCuentaSaldos(cuenta, mView.getFechaDesde().getValue());
            ProcUtil.SaldosBanco saldosFin = DataUtil.getBancoCuentaSaldos(cuenta, mView.getFechaHasta().getValue());
            mView.getNumSaldoInicialLibro().setValue(GenUtil.numFormat(saldosIni.getSegLibro()));
            mView.getNumSaldoInicialSegBancos().setValue(GenUtil.numFormat(saldosIni.getSegBanco()));
            mView.getNumSaldoFinalLibro().setValue(GenUtil.numFormat(saldosFin.getSegLibro()));
            mView.getNumSaldoFinalSegBancos().setValue(GenUtil.numFormat(saldosFin.getSegBanco()));
        }
    }

    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder(COL_VIS_SALDO);
        grid.setColumns(COL_VIS_SALDO);
        grid.getColumn("descripcion").setWidth(200);
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        BigDecimal totalEuros = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getBancoCuentasList(mView.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(mView.fechaDesde.getValue())
                        : GenUtil.getEndOfDay(mView.fechaHasta.getValue())))) {
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
        setSaldoDelDia();
    }

    @Override
    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);
        BigDecimal totalEurosDiaIng = new BigDecimal(0.00);
        BigDecimal totalEurosDiaEgr = new BigDecimal(0.00);

        for (Object item : mView.getGridBanco().getContainerDataSource().getItemIds()) {
            ScpBancocabecera bancocabecera = (ScpBancocabecera) item;
            // PEN
            totalSolesDiaEgr = totalSolesDiaEgr.add(bancocabecera.getNumHabersol());
            totalSolesDiaIng = totalSolesDiaIng.add(bancocabecera.getNumDebesol());
            // USD
            totalUsdDiaEgr = totalUsdDiaEgr.add(bancocabecera.getNumHaberdolar());
            totalUsdDiaIng = totalUsdDiaIng.add(bancocabecera.getNumDebedolar());
            // EUR
            totalEurosDiaEgr = totalEurosDiaEgr.add(bancocabecera.getNumHabermo());
            totalEurosDiaIng = totalEurosDiaIng.add(bancocabecera.getNumDebemo());
        }
        DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                null, ConfigurationUtil.get("DECIMAL_FORMAT"));
        // PEN
        mView.getSaldosView().getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        mView.getSaldosView().getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        mView.getSaldosView().getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        mView.getSaldosView().getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        mView.getSaldosView().getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        mView.getSaldosView().getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        mView.getSaldosView().getValEurEgr().setValue(dpf.format(totalEurosDiaEgr.doubleValue()));
        mView.getSaldosView().getValEurIng().setValue(dpf.format(totalEurosDiaIng.doubleValue()));
        mView.getSaldosView().getValEurSaldo().setValue(dpf.format(totalEurosDiaIng.subtract(totalEurosDiaEgr).doubleValue()));

        mView.getSaldosView().getGridSaldoDelDia().setColumnExpandRatio(0, 0);
    }

    @Override
    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (ScpBancocabecera scp : mView.getContainer().getItemIds()) {
            sumDebesol = sumDebesol.add(scp.getNumDebesol());
            sumHabersol = sumHabersol.add(scp.getNumHabersol());
            sumDebedolar = sumDebedolar.add(scp.getNumDebedolar());
            sumHaberdolar = sumHaberdolar.add(scp.getNumHaberdolar());
            sumDebemo = sumDebemo.add(scp.getNumDebemo());
            sumHabermo = sumHabermo.add(scp.getNumHabermo());
        }
        mView.getGridFooter().getCell("numDebesol").setText(df.format(sumDebesol));
        mView.getGridFooter().getCell("numHabersol").setText(df.format(sumHabersol));
        mView.getGridFooter().getCell("numDebedolar").setText(df.format(sumDebedolar));
        mView.getGridFooter().getCell("numHaberdolar").setText(df.format(sumHaberdolar));
        mView.getGridFooter().getCell("numDebemo").setText(df.format(sumDebemo));
        mView.getGridFooter().getCell("numHabermo").setText(df.format(sumHabermo));

        Arrays.asList(new String[] { "numDebesol", "numHabersol", "numHaberdolar", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> mView.getGridFooter().getCell(e).setStyleName("v-align-right strong"));
    }
}
