package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;

import java.util.Collection;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public class BancoGridLogic implements ItemsRefreshing<ScpBancocabecera> {

    private BancoViewing view;

    public BancoGridLogic(BancoViewing view) {
        this.view = view;
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
    }

    public void nuevoCheque() {
        view.clearSelection();
        view.getBancoOperView().getViewLogic().nuevoCheque();
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
        ViewUtil.openViewInNewWindow(view.getBancoOperView());
        //MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void editarCheque(ScpBancocabecera vcb) {
        view.getBancoOperView().getViewLogic().editarCheque(vcb);
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
        ViewUtil.openViewInNewWindow(view.getBancoOperView());
        //MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void anularCheque(ScpBancocabecera cabeceraToAnular) {
        if (cabeceraToAnular.isEnviado() && !Role.isPrivileged()) {
            Notification.show("!No se puede eliminar este cheque porque ya esta enviado a contabilidad!", Notification.Type.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("?Esta seguro que quiere eliminar cheque numero: \n"
                + cabeceraToAnular.getTxtCheque() + " cod operacion: " + cabeceraToAnular.getCodBancocabecera());
        MessageBox
                .createQuestion()
                .withCaption("Eliminar cheque")
                .withMessage(sb.toString())
                .withYesButton(() -> {
                    try {
                        view.getService().anularCheque(cabeceraToAnular);
                        view.refreshData();
                    } catch (FieldGroup.CommitException ce) {
                        Notification.show("Error al anular: " + ce.getMessage());
                    }
                })
                .withNoButton()
                .open();
    }

    public void enviarContabilidad(ScpBancocabecera scpBancocabecera) {
        Collection<Object> cabecerasParaEnviar = view.getSelectedRows();
        if (cabecerasParaEnviar.isEmpty() && scpBancocabecera!=null)
            cabecerasParaEnviar.add(scpBancocabecera);
        MainUI.get().getProcUtil().enviarContabilidadBanco(cabecerasParaEnviar, view.getService(),this);
        view.getGridBanco().deselectAll();
    }

    public void generateComprobante() {
        for (Object obj : view.getSelectedRows()) {
            ScpBancocabecera vcb = (ScpBancocabecera) obj;
            ReportHelper.generateComprobante(vcb);
        }
    }

    public void printComprobante() {
        for (Object obj : view.getSelectedRows()) {
            ScpBancocabecera vcb = (ScpBancocabecera) obj;
            ViewUtil.printComprobante(vcb);
        }
    }

    @Override
    public void refreshItems(Collection<ScpBancocabecera> items) {
        items.forEach(scb -> {
            ScpBancocabecera newScb = view.getService().getBancocabeceraRep().findByCodBancocabecera(scb.getCodBancocabecera());
            view.getGridBanco().getContainerDataSource().removeItem(scb);
            view.getGridBanco().getContainerDataSource().addItem(newScb);
        });
        view.refreshData();
    }
}

