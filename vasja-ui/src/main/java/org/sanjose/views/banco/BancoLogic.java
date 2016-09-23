package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.GenUtil;
import org.sanjose.util.TransactionUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.TransferenciaView;

import java.util.List;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class BancoLogic extends BancoItemLogic {

    private static final Logger log = LoggerFactory.getLogger(BancoLogic.class);

    //private final BancoOperView view;

    private String moneda;

    //private final TransactionUtil transactionUtil;

    private boolean isEdited = false;

    public BancoLogic(BancoOperView comprobanteView) {
        super(comprobanteView);
        view = comprobanteView;
        //transactionUtil = new TransactionUtil(view.getRepo(), view.getEm());
    }

    @Override
    public void init() {
        super.init();
        view.newChequeBtn.addClickListener(ev -> nuevaTrans());
        //view.gu.addClickListener(ev -> saveTransferencia());
        //view.finalizarTransBtn.setEnabled(false);
        view.imprimirTotalBtn.setEnabled(false);
    }

    private void nuevaTrans() {
        if (!view.getContainer().getItemIds().isEmpty() && isEdited)
            MessageBox
                .createQuestion()
                .withCaption("Nueva transferencia")
                .withMessage("?Esta seguro que quiere eliminar esta transferencia y crear una nueva?")
                .withYesButton(this::resetTrans)
                .withNoButton()
                .open();
        else
            resetTrans();
    }

    private void resetTrans() {
        view.getContainer().removeAllItems();
        moneda = null;
        view.setSaldoTrans();
        nuevoComprobante();
    }

    @Override
    public void nuevoComprobante() {
        isEdited = true;
        if (moneda!=null) {
            super.nuevoComprobante(moneda);
            //view.getSelCuenta().setEnabled(false);
        }
        else {
            super.nuevoComprobante();
            //view.getSelMoneda().setEnabled(true);
        }
        view.getModificarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
    }

    @Override
    public void editarComprobante() {
        if (view.getSelectedRow()!=null
                && "0".equals(view.getSelectedRow().getFlg_Anula())) {
            isEdited = true;
            editarComprobante(view.getSelectedRow());
            //view.getSelMoneda().setEnabled(false);
            view.getModificarBtn().setEnabled(true);
        }
    }

    @Override
    public void eliminarComprobante() {
    /*    if (GenUtil.strNullOrEmpty(view.getSelectedRow().getCodTranscorrelativo()))
            view.getContainer().removeItem(view.getSelectedRow());
        else {
            VsjCajabanco anuladoVcb = prepareToEliminar(view.getSelectedRow());
            VsjCajabanco vcbOld = null;
            for (VsjCajabanco vcb : view.getContainer().getItemIds()) {
                if (anuladoVcb .getFecFregistro().equals(vcb.getFecFregistro())) {
                    vcbOld = anuladoVcb ;
                    break;
                }
            }
            view.getContainer().removeItem(vcbOld);
            view.getContainer().addBean(anuladoVcb);
        }*/
        view.setSaldoTrans();
    }

    @Override
    public void cerrarAlManejo() {
        if (isEdited)
            MessageBox
                .createQuestion()
                .withCaption("Quitar la transferencia")
                .withMessage("?Esta seguro que quiere eliminar todos operaciones de esta transferencia \n" +
                        "y regresar al Manejo de Caja?\n")
                .withYesButton(() -> MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName()))
                .withNoButton()
                .open();
        else
            MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    @Override
    public void saveComprobante() {
/*        try {

            boolean isNew = getVsjCajabanco().getFecFregistro()==null;
            VsjCajabanco item = prepareToSave();
            moneda = item.getCodTipomoneda();
            if (isNew) {
                view.getContainer().addBean(item);
                if (PEN.equals(moneda))
                    ViewUtil.setColumnNames(view.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_PEN,
                        TransferenciaView.VISIBLE_COLUMN_IDS_PEN, TransferenciaView.NONEDITABLE_COLUMN_IDS);
                else
                    ViewUtil.setColumnNames(view.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_USD,
                            TransferenciaView.VISIBLE_COLUMN_IDS_USD, TransferenciaView.NONEDITABLE_COLUMN_IDS);
            }
            else {
                VsjCajabanco vcbOld = null;
                for (VsjCajabanco vcb : view.getContainer().getItemIds()) {
                    if (item.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = item;
                        break;
                    }
                }
                view.getContainer().removeItem(vcbOld);
                view.getContainer().addBean(item);
            }
            view.setSaldoTrans();
            view.getGuardarBtn().setEnabled(false);
            view.getNuevoComprobante().setEnabled(true);
        } catch (FieldGroup.CommitException ce) {
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
*/
    }

    private void saveTransferencia() {
        MessageBox
                .createQuestion()
                .withCaption("Guardar la transferencia")
                .withMessage("?Esta seguro que quiere guardar todos operaciones de esta transferencia?\n" +
                        "!Despues no se puede regresar a editarlos en esta pantalla!")
                .withYesButton(this::executeSaveTransferencia)
                .withNoButton()
                .open();
    }

    private void executeSaveTransferencia() {
/*
        List<VsjCajabanco> savedOperaciones = transactionUtil.saveVsjCajabancos(view.getContainer().getItemIds());

        view.getContainer().removeAllItems();
        view.getContainer().addAll(savedOperaciones);
        view.finalizarTransBtn.setEnabled(false);
        view.imprimirTotalBtn.setEnabled(true);
        view.getGuardarBtn().setEnabled(false);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getNuevoComprobante().setEnabled(false);
        view.nuevaTransBtn.setEnabled(true);
        view.refreshData();
        isEdited = false;
*/
    }

    public void editarTransferencia(VsjCajabanco vcb) throws NonEditableException {
/*
        if (vcb.getCodTranscorrelativo()==null) return;
        view.getContainer().removeAllItems();
        if (PEN.equals(vcb.getCodTipomoneda()))
            ViewUtil.setColumnNames(view.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_PEN,
                    TransferenciaView.VISIBLE_COLUMN_IDS_PEN, TransferenciaView.NONEDITABLE_COLUMN_IDS);
        else
            ViewUtil.setColumnNames(view.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_USD,
                    TransferenciaView.VISIBLE_COLUMN_IDS_USD, TransferenciaView.NONEDITABLE_COLUMN_IDS);

        List<VsjCajabanco> operaciones = view.getRepo().findByCodTranscorrelativo(vcb.getCodTranscorrelativo());

        for (VsjCajabanco oper : operaciones) {
            if ("1".equals(oper.getFlgEnviado()))
                throw new NonEditableException("No se puede editar porque una de los operaciones ya esta enviada a contabilidad: " + oper.getCodCajabanco());
        }
        for (VsjCajabanco oper : operaciones) {
            view.getContainer().addBean(oper);
        }
        view.setSaldoTrans();
        view.getModificarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
        view.getGuardarBtn().setEnabled(false);
        view.getNuevoComprobante().setEnabled(true);
        isEdited = false;
*/
    }
}
