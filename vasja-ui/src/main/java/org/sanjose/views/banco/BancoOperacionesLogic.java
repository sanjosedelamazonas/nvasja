package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.model.VsjBancocabecera;

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
public class BancoOperacionesLogic implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(BancoOperacionesLogic.class);
    private BancoOperacionesView view;
    private BancoGridLogic gridLogic;

    public void init(BancoOperacionesView bancoOperacionesView) {
        view = bancoOperacionesView;
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
            //ReportHelper.generateDiarioCaja(view.fechaDesde.getValue(), view.fechaHasta.getValue(), null);
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
            } else {
                gridContextMenu.addItem("Editar", k -> gridLogic.editarCheque((VsjBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> gridLogic.nuevoCheque());
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
            }
        });

    }
}
