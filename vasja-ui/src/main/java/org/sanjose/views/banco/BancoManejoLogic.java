package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.VsjBancocabecera;
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

    public void init(BancoManejoView bancoManejoView) {
        view = bancoManejoView;
        gridLogic = new BancoGridLogic(view);
        view.btnNuevoCheque.addClickListener(e -> gridLogic.nuevoCheque());
        view.btnEditar.addClickListener(e -> {
            for (Object obj : view.getSelectedRows()) {
                gridLogic.editarCheque((VsjBancocabecera) obj);
                break;
            }
        });
        view.btnVerVoucher.addClickListener(e -> gridLogic.generateComprobante());
        view.btnImprimir.addClickListener(e -> gridLogic.printComprobante());
        view.btnReporte.addClickListener(e -> {
            ReportHelper.generateDiarioBanco(view.getSelRepMoneda().getValue().toString().charAt(0),
                    view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });

        view.gridBanco.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Object item = view.gridBanco.getContainerDataSource().getItem(view.gridBanco.getEditedItemId());
                if (item != null) {
                    VsjBancocabecera vcb = (VsjBancocabecera) ((BeanItem) item).getBean();
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
                gridContextMenu.addItem("Editar", k -> gridLogic.editarCheque((VsjBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
                if (!((VsjBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> gridLogic.anularCheque((VsjBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        if (!view.getSelectedRows().isEmpty()) {
                            MainUI.get().getProcUtil().enviarContabilidadBanco(view.getSelectedRows(), view.getService());
                        } else {
                            List<Object> bancocabeceras = new ArrayList<>();
                            bancocabeceras.add(itemId);
                            MainUI.get().getProcUtil().enviarContabilidadBanco(bancocabeceras, view.getService());
                        }
                        view.refreshData();
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjItem) itemId));
                gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjItem) itemId));
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

    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);
        BigDecimal totalEurosDiaIng = new BigDecimal(0.00);
        BigDecimal totalEurosDiaEgr = new BigDecimal(0.00);

        for (Object item : view.getGridBanco().getContainerDataSource().getItemIds()) {
            VsjBancocabecera bancocabecera = (VsjBancocabecera) item;
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
        view.getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        view.getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        view.getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        view.getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        view.getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        view.getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        view.getValEuroEgr().setValue(dpf.format(totalEurosDiaEgr.doubleValue()));
        view.getValEuroIng().setValue(dpf.format(totalEurosDiaIng.doubleValue()));
        view.getValEuroSaldo().setValue(dpf.format(totalEurosDiaIng.subtract(totalEurosDiaEgr).doubleValue()));

        view.gridSaldoDelDia.setColumnExpandRatio(0, 0);
    }
}
