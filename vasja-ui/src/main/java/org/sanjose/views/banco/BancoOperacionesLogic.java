package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;

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
    private BancoOperacionesView view;
    private BancoGridLogic gridLogic;


    public void init(BancoOperacionesView bancoOperacionesView) {
        view = bancoOperacionesView;
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
                //  gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevaRendicion());
            } else {
                // gridContextMenu.addItem("Editar", k -> gridLogic.editarRendicion((ScpBancocabecera) itemId));
                // gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevaRendicion());
                if (!((ScpBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> gridLogic.anularCheque((ScpBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        gridLogic.enviarContabilidad((ScpBancocabecera)itemId);
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjCajaBancoItem)itemId));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajaBancoItem) itemId));
            }
        });
        view.btnImprimir.setVisible(false);
        view.btnVerVoucher.setVisible(false);
        view.btnEditar.setVisible(false);
        view.btnNuevoCheque.setVisible(false);
    }

}
