package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.model.VsjBancocabecera;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public class BancoGridLogic {

    private BancoView view;

    public BancoGridLogic(BancoView view) {
        this.view = view;
    }

    public void nuevoCheque() {
        view.clearSelection();
        MainUI.get().getBancoOperView().getViewLogic().nuevoCheque();
        MainUI.get().getBancoOperView().getViewLogic().setNavigatorView(view);
        MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void editarCheque(VsjBancocabecera vcb) {
        MainUI.get().getBancoOperView().getViewLogic().editarCheque(vcb);
        MainUI.get().getBancoOperView().getViewLogic().setNavigatorView(view);
        MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void anularCheque(VsjBancocabecera cabeceraToAnular) {
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

    public void generateComprobante() {
        for (Object obj : view.getSelectedRows()) {
            VsjBancocabecera vcb = (VsjBancocabecera) obj;
            //ReportHelper.generateComprobante(vcb);
        }
    }

    public void printComprobante() {
        for (Object obj : view.getSelectedRows()) {
            VsjBancocabecera vcb = (VsjBancocabecera) obj;
            //ViewUtil.printComprobante(vcb);
        }
    }


}

