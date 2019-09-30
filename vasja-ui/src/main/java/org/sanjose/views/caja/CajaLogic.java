package org.sanjose.views.caja;

import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;

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

    public void enviarContabilidad(VsjCajaBancoItem scpCajabanco) {
        Collection<Object> cajabancosParaEnviar = cajaView.getSelectedRows();
        if (cajabancosParaEnviar.isEmpty() && scpCajabanco!=null)
            cajabancosParaEnviar.add(scpCajabanco);
        MainUI.get().getProcUtil().enviarContabilidad(cajabancosParaEnviar, cajaView.getService(),this);
        cajaView.getGridCaja().deselectAll();
    }

    @Override
    public void refreshItems(Collection<ScpCajabanco> cajabancosToRefresh) {
        cajabancosToRefresh.forEach(scb -> {
            VsjCajaBancoItem newScb = cajaView.getService().getCajabancoRep().findByCodCajabanco(scb.getCodCajabanco());
            cajaView.getGridCaja().getContainerDataSource().removeItem(scb);
            cajaView.getGridCaja().getContainerDataSource().addItem(newScb);
        });
        cajaView.refreshData();
    }

    void eliminarComprobante(ScpCajabanco savedCajabanco) {
        if (savedCajabanco == null)
            return;
        if (savedCajabanco.isEnviado()) {
            MessageBox
                    .createInfo()
                    .withCaption("Ya enviado a contabilidad")
                    .withMessage("No se puede eliminar porque ya esta enviado a la contabilidad.")
                    .withOkButton()
                    .open();
            return;
        }
        MessageBox
                .createQuestion()
                .withCaption("Eliminar")
                .withMessage("?Esta seguro que quiere eliminar este comprobante?")
                .withYesButton(() ->  doEliminarComprobante(savedCajabanco))
                .withNoButton()
                .open();
    }

    void doEliminarComprobante(ScpCajabanco savedCajabanco) {
        try {
            ScpCajabanco item = savedCajabanco.prepareToEliminar();
            savedCajabanco = cajaView.getService().getCajabancoRep().save(item);
            savedCajabanco = null;
            cajaView.selectMoneda(item.getCodTipomoneda());
            cajaView.refreshData();
            MessageBox
                    .createInfo()
                    .withCaption("Elminado correctamente")
                    .withMessage("El comprobante ha sido eliminado.")
                    .withOkButton()
                    .open();
        } catch (Exception ce) {
            //log.info("Got Exception al eliminar comprobante: " + ce.getMessage());
            MessageBox
                    .createError()
                    .withCaption("Error al anular el comprobante:")
                    .withMessage(ce.getLocalizedMessage())
                    .withOkButton()
                    .open();
        }
    }

}
