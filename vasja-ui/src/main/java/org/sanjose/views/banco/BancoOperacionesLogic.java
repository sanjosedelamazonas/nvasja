package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.model.VsjBancocabecera;

import java.io.Serializable;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class BancoOperacionesLogic implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(BancoOperacionesLogic.class);
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private BancoOperacionesView view;
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;

    public void init(BancoOperacionesView bancoOperacionesView) {
        view = bancoOperacionesView;
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

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> nuevoCheque());
            } else {
                gridContextMenu.addItem("Editar", k -> editarCheque((VsjBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> nuevoCheque());
/*
                gridContextMenu.addItem("Enviar a contabilidad", k -> {
                    if (!view.getSelectedRows().isEmpty()) {
                        enviarContabilidad(view.getSelectedRows());
                    } else {
                        List<Object> cajabancos = new ArrayList<>();
                        cajabancos.add(itemId);
                        enviarContabilidad(cajabancos);
                    }
                });
*/
                //gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajabanco)itemId));
            }
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
}
