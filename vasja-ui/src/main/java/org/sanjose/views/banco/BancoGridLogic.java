package org.sanjose.views.banco;

import org.sanjose.MainUI;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.views.sys.INavigatorView;

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

    public void anularCheque() {
        view.clearSelection();
        for (Object obj : view.getSelectedRows()) {
            VsjBancocabecera vcb = (VsjBancocabecera) obj;
            //ViewUtil.printComprobante(vcb);
        }
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

