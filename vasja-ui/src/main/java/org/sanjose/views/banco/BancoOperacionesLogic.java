package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.SaldoDelDia;

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
public class BancoOperacionesLogic extends BancoGridLogic implements Serializable, SaldoDelDia {


    private BancoOperacionesView oView;

    public BancoOperacionesLogic(BancoOperacionesView view) {
        super(view);
        oView = view;
        init();
    }

    public void init() {
        oView.btnNuevoCheque.addClickListener(e -> nuevoCheque());
        oView.btnEditar.addClickListener(e -> {
            for (Object obj : oView.getSelectedRows()) {
                editarCheque((ScpBancocabecera) obj);
                break;
            }
        });
        oView.btnVerVoucher.addClickListener(e -> generateComprobante());
        oView.btnImprimir.addClickListener(e -> printComprobante());
        oView.btnReporte.addClickListener(e -> {
            ReportHelper.generateDiarioBanco(oView.getSelRepMoneda().getValue().toString().charAt(0),
                    oView.fechaDesde.getValue(), oView.fechaHasta.getValue(), null);
        });
        oView.getBtnMarcarCobrado().addClickListener(clickEvent -> { setMesCobrado(true); });
        oView.getBtnMarcarNoCobrado().addClickListener(clickEvent -> { setMesCobrado(false); });

        GridContextMenu gridContextMenu = new GridContextMenu(oView.getGridBanco());

        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                //  gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevaRendicion());
            } else {
                // gridContextMenu.addItem("Editar", k -> gridLogic.editarRendicion((ScpBancocabecera) itemId));
                // gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevaRendicion());
                if (!((ScpBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> anularCheque((ScpBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        enviarContabilidad((ScpBancocabecera)itemId);
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjCajaBancoItem)itemId));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajaBancoItem) itemId));
            }
        });
        oView.btnImprimir.setVisible(false);
        oView.btnVerVoucher.setVisible(false);
        oView.btnEditar.setVisible(false);
        oView.btnNuevoCheque.setVisible(false);
    }

    // Single click - select row in grids
    @Override
    void setItemLogic(ItemClickEvent event) {
        oView.getGridBanco().deselectAll();
        oView.getGridBanco().select(event.getItemId());
    }

    @Override
    public void setSaldoDelDia() {
    }

    @Override
    public void calcFooterSums() {
    }
}
