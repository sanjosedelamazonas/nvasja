package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.shared.data.sort.SortDirection;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;

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
                //  gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
            } else {
                // gridContextMenu.addItem("Editar", k -> gridLogic.editarCheque((VsjBancocabecera) itemId));
                // gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
                if (!((VsjBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> gridLogic.anularCheque((VsjBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        List<Object> bancocabeceras = new ArrayList<>();
                        List<VsjBancocabecera> vsjBancocabecerasEnviadas = null;
                        if (!view.getSelectedRows().isEmpty()) {
                            bancocabeceras.addAll(view.getSelectedRows());
                        } else {
                            bancocabeceras.add(itemId);
                        }
                        vsjBancocabecerasEnviadas = MainUI.get().getProcUtil().enviarContabilidadBanco(bancocabeceras, view.getService());
                        for (VsjBancocabecera vcb : vsjBancocabecerasEnviadas) {
                            Object cabeceraToRemove = null;
                            for (Object objVcb : bancocabeceras) {
                                if (((VsjBancocabecera) objVcb).getCodBancocabecera().equals(vcb.getCodBancocabecera())) {
                                    cabeceraToRemove = objVcb;
                                    break;
                                }
                            }
                            if (cabeceraToRemove != null) {
                                view.getGridBanco().getContainerDataSource().removeItem(cabeceraToRemove);
                                view.getGridBanco().getContainerDataSource().addItem(vcb);
                            }
                        }
                        //view.refreshData();
                        //view.getGridBanco().clearSortOrder();
                        view.getGridBanco().sort("fecFecha", SortDirection.DESCENDING);
                    });
                }
                gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjItem) itemId));
            }
        });
    }
}
