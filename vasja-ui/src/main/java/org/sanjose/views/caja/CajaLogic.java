package org.sanjose.views.caja;

import com.vaadin.ui.Notification;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.GridViewing;

import java.util.Collection;

public abstract class CajaLogic implements ItemsRefreshing<ScpCajabanco> {

    protected CajaViewing cajaView;

    protected void newComprobante() {
        cajaView.clearSelection();
        MainUI.get().getComprobanteView().viewLogic.nuevoComprobante();
        MainUI.get().getComprobanteView().viewLogic.setNavigatorView(cajaView);
        ViewUtil.openInNewWindow(MainUI.get().getComprobanteView());
        //MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    protected void newTransferencia() {
        cajaView.clearSelection();
        MainUI.get().getTransferenciaView().viewLogic.nuevaTrans();
        MainUI.get().getTransferenciaView().viewLogic.setNavigatorView(cajaView);
        ViewUtil.openInNewWindow(MainUI.get().getTransferenciaView());
        //MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

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
            MainUI.get().getComprobanteView().viewLogic.setNavigatorView(cajaView);
            MainUI.get().getComprobanteView().viewLogic.editarComprobante(vcb);
            //MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
            ViewUtil.openInNewWindow(MainUI.get().getComprobanteView());
        }
    }

    public void enviarContabilidad(ScpCajabanco scpCajabanco) {
        Collection<Object> cajabancosParaEnviar = cajaView.getSelectedRows();
        if (cajabancosParaEnviar.isEmpty() && scpCajabanco!=null)
            cajabancosParaEnviar.add(scpCajabanco);
        MainUI.get().getProcUtil().enviarContabilidad(cajabancosParaEnviar, cajaView.getService(),this);
        cajaView.getGridCaja().deselectAll();
    }

    @Override
    public void refreshItems(Collection<ScpCajabanco> cajabancosToRefresh) {
        cajabancosToRefresh.forEach(scb -> {
            ScpCajabanco newScb = cajaView.getService().getCajabancoRep().findByCodCajabanco(scb.getCodCajabanco());
            cajaView.getGridCaja().getContainerDataSource().removeItem(scb);
            cajaView.getGridCaja().getContainerDataSource().addItem(newScb);
        });
        cajaView.refreshData();
    }
}
