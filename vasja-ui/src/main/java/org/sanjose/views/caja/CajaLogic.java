package org.sanjose.views.caja;

import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.PersistanceService;

import java.sql.Timestamp;
import java.util.*;

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

    public void enviarContabilidad(VsjCajaBancoItem scpCajabanco, boolean isEnviar) {
        Collection<Object> cajabancosParaEnviar = cajaView.getSelectedRows();
        if (cajabancosParaEnviar.isEmpty() && scpCajabanco!=null)
            cajabancosParaEnviar.add(scpCajabanco);
        if (isEnviar) {
            Set<ScpCajabanco> cajabancosEnviados = new HashSet<>();
            List<String> cajabancoIdsEnviados = new ArrayList<>();
            // Check if already sent and ask if only marcar...
            for (Object objVcb : cajabancosParaEnviar) {
                ScpCajabanco cajabanco = (ScpCajabanco) objVcb;
                if (!cajabanco.isEnviado() && cajaView.getService().checkIfAlreadyEnviado(cajabanco)) {
                    cajabancosEnviados.add(cajabanco);
                    cajabancoIdsEnviados.add(cajabanco.getCodCajabanco().toString());
                }
            }
            for (ScpCajabanco cajabanco : cajabancosEnviados) {
                cajabancosParaEnviar.remove(cajabanco);
            }
            if (cajabancosEnviados.isEmpty()) {
                MainUI.get().getProcUtil().enviarContabilidad(cajabancosParaEnviar, cajaView.getService(), this);
                cajaView.getGridCaja().deselectAll();
            } else {
                MessageBox
                        .createQuestion()
                        .withCaption("!Atencion!")
                        .withMessage("?Estas operaciones ya fueron enviadas ("+ Arrays.toString(cajabancoIdsEnviados.toArray()) +"), quiere solo marcar los como enviadas?")
                        .withYesButton(() -> doMarcarEnviados(cajabancosParaEnviar, cajabancosEnviados))
                        .withNoButton()
                        .open();
            }
        } else {
            for (Object objVcb : cajabancosParaEnviar) {
                ScpCajabanco cajabanco = (ScpCajabanco) objVcb;
                if (!cajabanco.isEnviado()) {
                    Notification.show("!Atencion!", "!Omitiendo operacion " + cajabanco.getTxtCorrelativo() + " - no esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                cajaView.getGridCaja().deselect(cajabanco);
                cajabanco.setFlgEnviado('0');
                cajabanco.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
                cajabanco.setCodUactualiza(CurrentUser.get());
                cajaView.getService().getCajabancoRep().save(cajabanco);
            }
            cajaView.refreshData();
        }
    }

    public void doMarcarEnviados(Collection<Object> cajabancosParaEnviar , Set<ScpCajabanco> cajabancosEnviados) {
        for (ScpCajabanco cajabanco : cajabancosEnviados) {
            cajaView.getGridCaja().deselect(cajabanco);
            cajabanco.setFlgEnviado('1');
            cajabanco.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
            cajabanco.setCodUactualiza(CurrentUser.get());
            cajaView.getService().getCajabancoRep().save(cajabanco);
        }
        cajaView.refreshData();
        if (!cajabancosParaEnviar.isEmpty())
            MainUI.get().getProcUtil().enviarContabilidad(cajabancosParaEnviar, cajaView.getService(), this);
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
