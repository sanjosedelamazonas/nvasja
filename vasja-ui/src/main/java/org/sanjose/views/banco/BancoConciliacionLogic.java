package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.sys.ISaldoDelDia;

import java.io.Serializable;
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
public class BancoConciliacionLogic implements Serializable, ISaldoDelDia {

    private static final Logger log = LoggerFactory.getLogger(BancoConciliacionLogic.class);
    private BancoConciliacionView view;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;
    private BancoGridLogic gridLogic;

    public void init(BancoConciliacionView bancoConciliacionView) {
        view = bancoConciliacionView;
        gridLogic = new BancoGridLogic(view);
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
                //gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajabanco)itemId));
            }
        });

    }

    @Override
    public void setSaldoDelDia() {

    }

/*    public void setSaldos(Grid grid, boolean isInicial) {
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
    }*/

}
