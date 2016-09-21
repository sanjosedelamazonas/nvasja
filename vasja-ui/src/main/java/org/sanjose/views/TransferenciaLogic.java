package org.sanjose.views;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.GenUtil;
import org.sanjose.helper.ViewUtil;
import org.sanjose.model.TransactionUtil;
import org.sanjose.model.VsjCajabanco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * SORCER class
 * User: prubach
 * Date: 20.09.16
 */
public class TransferenciaLogic extends ComprobanteLogic {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaLogic.class);

    private TransferenciaView tView;

    private String moneda;

    private TransactionUtil transactionUtil;

    public TransferenciaLogic(IComprobanteView comprobanteView) {
        super(comprobanteView);
        tView = (TransferenciaView) comprobanteView;
        transactionUtil = new TransactionUtil(view.getRepo(), tView.getEm());
    }

    @Override
    public void init() {
        super.init();
        tView.nuevaTransBtn.addClickListener(ev -> nuevaTrans());
        tView.finalizarTransBtn.addClickListener(ev -> saveTransferencia());
        tView.finalizarTransBtn.setEnabled(false);
        tView.imprimirTotalBtn.setEnabled(false);
    }

    public void nuevaTrans() {
        if (!tView.getContainer().getItemIds().isEmpty())
            MessageBox
                .createQuestion()
                .withCaption("Nueva transferencia")
                .withMessage("?Esta seguro que quiere eliminar esta transferencia y crear una nueva?")
                .withYesButton(() -> {
                    resetTrans();
                })
                .withNoButton()
                .open();
        else
            resetTrans();
    }

    private void resetTrans() {
        tView.getContainer().removeAllItems();
        moneda = null;
        tView.setSaldoTrans();
        nuevoComprobante();
    }

    @Override
    public void nuevoComprobante() {
        if (moneda!=null) {
            super.nuevoComprobante(moneda);
            view.getSelMoneda().setEnabled(false);
        }
        else {
            super.nuevoComprobante();
            view.getSelMoneda().setEnabled(true);
        }
        view.getModificarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
    }

    @Override
    public void editarComprobante() {
        if (tView.getSelectedRow()!=null) editarComprobante(tView.getSelectedRow());
        view.getSelMoneda().setEnabled(false);
        view.getModificarBtn().setEnabled(true);
    }

    @Override
    public void eliminarComprobante() {
        tView.getContainer().removeItem(tView.getSelectedRow());
        tView.setSaldoTrans();
    }

    @Override
    public void cerrarAlManejo() {
        MessageBox
                .createQuestion()
                .withCaption("Quitar la transferencia")
                .withMessage("?Esta seguro que quiere eliminar todos operaciones de esta transferencia \n" +
                        "y regresar al Manejo de Caja?\n")
                .withYesButton(() -> {
                    MainUI.get().getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
                })
                .withNoButton()
                .open();
    }

    @Override
    public void saveComprobante() {
        try {
            boolean isNew = getVsjCajabanco().getFecFregistro()==null;
            VsjCajabanco item = prepareToSave();
            moneda = item.getCodTipomoneda();
            if (isNew) {
                tView.getContainer().addBean(item);
                if (PEN.equals(moneda))
                    ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_PEN,
                        TransferenciaView.VISIBLE_COLUMN_IDS_PEN, TransferenciaView.NONEDITABLE_COLUMN_IDS);
                else
                    ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_USD,
                            TransferenciaView.VISIBLE_COLUMN_IDS_USD, TransferenciaView.NONEDITABLE_COLUMN_IDS);
            }
            else {
                VsjCajabanco vcbOld = null;
                for (VsjCajabanco vcb : tView.getContainer().getItemIds()) {
                    if (item.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = item;
                        break;
                    }
                }
                tView.getContainer().removeItem(vcbOld);
                tView.getContainer().addBean(item);
            }
            tView.setSaldoTrans();
            tView.getGuardarBtn().setEnabled(false);
            tView.getNuevoComprobante().setEnabled(true);
        } catch (FieldGroup.CommitException ce) {
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    private void saveTransferencia() {
        MessageBox
                .createQuestion()
                .withCaption("Guardar la transferencia")
                .withMessage("?Esta seguro que quiere guardar todos operaciones de esta transferencia?\n" +
                        "!Despues no se puede regresar a editarlos en esta pantalla!")
                .withYesButton(() -> {
                    executeSaveTransferencia();
                })
                .withNoButton()
                .open();
    }

    private void executeSaveTransferencia() {
        List<VsjCajabanco> savedOperaciones = transactionUtil.saveVsjCajabancos(tView.getContainer().getItemIds());

        tView.getContainer().removeAllItems();
        tView.getContainer().addAll(savedOperaciones);
        tView.finalizarTransBtn.setEnabled(false);
        tView.imprimirTotalBtn.setEnabled(true);
        view.getGuardarBtn().setEnabled(false);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getNuevoComprobante().setEnabled(false);
        tView.nuevaTransBtn.setEnabled(true);
        view.refreshData();
    }
}
