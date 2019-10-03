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
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.SaldoDelDia;

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
public class BancoManejoLogic implements Serializable, SaldoDelDia {

    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private BancoManejoView view;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;
    private BancoGridLogic gridLogic;
    private static final Logger log = LoggerFactory.getLogger(BancoManejoLogic.class);


    public void init(BancoManejoView bancoManejoView) {
        view = bancoManejoView;
        gridLogic = new BancoGridLogic(view);
        view.btnNuevoCheque.addClickListener(e -> gridLogic.nuevoCheque());
        view.btnEditar.addClickListener(e -> {
            for (Object obj : view.getSelectedRows()) {
                gridLogic.editarCheque((ScpBancocabecera) obj);
                break;
            }
        });
        view.btnVerVoucher.addClickListener(e -> gridLogic.generateComprobante());
        view.btnImprimir.addClickListener(e -> gridLogic.printComprobante());
        view.btnReporte.addClickListener(e -> {
            ReportHelper.generateDiarioBanco(view.getSelRepMoneda().getValue().toString().charAt(0),
                    view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });

        view.getBtnDetallesSaldos().addClickListener(e -> {
            setSaldos(view.getSaldosView().getGridSaldoInicial(), true);
            setSaldos(view.getSaldosView().getGridSaldoFinal(), false);
            setSaldoDelDia();
            ViewUtil.openCajaSaldosInNewWindow(view.getSaldosView(), view.getFechaDesde().getValue(), view.getFechaHasta().getValue());
        });


        view.gridBanco.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Object item = view.gridBanco.getContainerDataSource().getItem(view.gridBanco.getEditedItemId());
                if (item != null) {
                    ScpBancocabecera vcb = (ScpBancocabecera) ((BeanItem) item).getBean();
                    vcb.setCodMescobrado(new MesCobradoToBooleanConverter(vcb)
                            .convertToModel(vcb.getFlgCobrado(), String.class, ConfigurationUtil.LOCALE));
                    view.getService().updateCobradoInCabecera(vcb);
                }
            }
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
            } else {
                gridContextMenu.addItem("Ver detalle", k -> gridLogic.editarCheque((ScpBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
                if (!((ScpBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> gridLogic.anularCheque((ScpBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        gridLogic.enviarContabilidad((ScpBancocabecera)itemId);
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjCajaBancoItem) itemId));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajaBancoItem) itemId));
            }
        });
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

        for (Object item : view.getGridBanco().getContainerDataSource().getItemIds()) {
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
        view.getSaldosView().getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        view.getSaldosView().getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        view.getSaldosView().getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        view.getSaldosView().getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        view.getSaldosView().getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        view.getSaldosView().getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        view.getSaldosView().getValEurEgr().setValue(dpf.format(totalEurosDiaEgr.doubleValue()));
        view.getSaldosView().getValEurIng().setValue(dpf.format(totalEurosDiaIng.doubleValue()));
        view.getSaldosView().getValEurSaldo().setValue(dpf.format(totalEurosDiaIng.subtract(totalEurosDiaEgr).doubleValue()));

        view.getSaldosView().getGridSaldoDelDia().setColumnExpandRatio(0, 0);
    }

    @Override
    public void calcFooterSums() {
    }
}
