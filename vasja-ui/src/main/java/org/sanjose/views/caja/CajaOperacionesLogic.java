package org.sanjose.views.caja;

import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;

public class CajaOperacionesLogic extends CajaManejoLogic {

    protected void editarComprobante(ScpCajabanco vcb) {
        if (vcb==null) return;
        // Transferencia
        if (!GenUtil.strNullOrEmpty(vcb.getCodTranscorrelativo())) {
            try {
                MainUI.get().getTransferenciaView().viewLogic.editarTransferencia(vcb);
                MainUI.get().getTransferenciaView().viewLogic.setNavigatorView(cajaView);
                //MainUI.get().getNavigator().navigateTo(TransferenciaView.VIEW_NAME);
                ViewUtil.openInNewWindow(MainUI.get().getTransferenciaView());
            } catch (NonEditableException e) {
                Notification.show("No es editable", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        } else {
            view.getComprobView().viewLogic.setNavigatorView(view);
            view.getComprobView().viewLogic.editarComprobante(vcb);
        }
    }
}
