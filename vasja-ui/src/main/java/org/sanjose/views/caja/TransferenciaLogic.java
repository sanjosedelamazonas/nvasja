package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.repo.VsjCajabancoRep;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.util.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.sanjose.util.GenUtil.PEN;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
@UIScope
@Service
@Transactional
public class TransferenciaLogic extends ComprobanteLogic {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaLogic.class);

    private TransferenciaView tView;

    private Character moneda;

    private boolean isEdited = false;

    VsjCajabancoRep cajabancoRep;

    @Autowired
    public TransferenciaLogic(VsjCajabancoRep cajabancoRep) {
        this.cajabancoRep = cajabancoRep;
    }

    @Override
    public void init(IComprobanteView  comprobanteView) {
        super.init(comprobanteView);
        tView = (TransferenciaView) comprobanteView;
        tView.nuevaTransBtn.addClickListener(ev -> nuevaTrans());
        tView.finalizarTransBtn.addClickListener(ev -> saveTransferencia());
        tView.finalizarTransBtn.setEnabled(false);
        tView.imprimirTotalBtn.setEnabled(false);
    }

    private void nuevaTrans() {
        if (!tView.getContainer().getItemIds().isEmpty() && isEdited)
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

    @Transactional(readOnly = false)
    public List<VsjCajabanco> saveVsjCajabancos(List<VsjCajabanco> cajabancos) {
        assert TransactionSynchronizationManager.isActualTransactionActive();
        List<VsjCajabanco> savedOperaciones = new ArrayList<>();

        String transCorrelativo = null;
        // Find at least one operation with transCorrelativo set
        for (VsjCajabanco oper : cajabancos) {
            if (!GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo())) {
                transCorrelativo = oper.getCodTranscorrelativo();
                break;
            }
        }
        if (transCorrelativo==null) transCorrelativo = GenUtil.getUuid();
        for (VsjCajabanco oper : cajabancos) {
            if (GenUtil.strNullOrEmpty(oper.getCodTranscorrelativo()))
                oper.setCodTranscorrelativo(transCorrelativo);
        }
        for (VsjCajabanco oper : cajabancoRep.save(cajabancos)) {
            // Tested saving each element using entityManager directly but then an Exception is raised:
            // javax.persistence.TransactionRequiredException: No EntityManager with actual transaction available for
            // current thread - cannot reliably process 'merge' call
            //
//            VsjCajabanco savedCajabanco = em.merge(oper);
            if (GenUtil.strNullOrEmpty(oper.getTxtCorrelativo())) {
                oper.setTxtCorrelativo(GenUtil.getTxtCorrelativo(oper.getCodCajabanco()));
                // TEST transactionality - causes org.springframework.dao.DataIntegrityViolationException
                // because codMes is NOT NULL in the database
                if (oper.getTxtGlosaitem().equals("abc")) {
                    throw new RuntimeException("Test transactions");
                }
                //oper.setCodMes(null);
                oper = cajabancoRep.save(oper);
                log.info("Saved cajabanco from transferencia: " + oper);
//                oper = em.merge(oper);
            }
            savedOperaciones.add(oper);
        }
        return savedOperaciones;
    }

    private void resetTrans() {
        tView.getContainer().removeAllItems();
        moneda = null;
        tView.setSaldoTrans();
        nuevoComprobante();
    }

    @Override
    public void nuevoComprobante() {
        isEdited = true;
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
        if (tView.getSelectedRow()!=null
                && !tView.getSelectedRow().isAnula()) {
            isEdited = true;
            editarComprobante(tView.getSelectedRow());
            view.getSelMoneda().setEnabled(false);
            view.getModificarBtn().setEnabled(true);
        }
    }

    @Override
    public void eliminarComprobante() {
        if (GenUtil.strNullOrEmpty(tView.getSelectedRow().getCodTranscorrelativo()))
            tView.getContainer().removeItem(tView.getSelectedRow());
        else {
            VsjCajabanco anuladoVcb = prepareToEliminar(tView.getSelectedRow());
            VsjCajabanco vcbOld = null;
            for (VsjCajabanco vcb : tView.getContainer().getItemIds()) {
                if (anuladoVcb .getFecFregistro().equals(vcb.getFecFregistro())) {
                    vcbOld = anuladoVcb ;
                    break;
                }
            }
            tView.getContainer().removeItem(vcbOld);
            tView.getContainer().addBean(anuladoVcb);
        }
        tView.setSaldoTrans();
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
                .withYesButton(this::executeSaveTransferencia)
                .withNoButton()
                .open();
    }

    private void executeSaveTransferencia() {
        List<VsjCajabanco> savedOperaciones = saveVsjCajabancos(tView.getContainer().getItemIds());

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
        isEdited = false;
    }

    public void editarTransferencia(VsjCajabanco vcb) throws NonEditableException {
        if (vcb.getCodTranscorrelativo()==null) return;
        tView.getContainer().removeAllItems();
        if (PEN.equals(vcb.getCodTipomoneda()))
            ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_PEN,
                    TransferenciaView.VISIBLE_COLUMN_IDS_PEN, TransferenciaView.NONEDITABLE_COLUMN_IDS);
        else
            ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_USD,
                    TransferenciaView.VISIBLE_COLUMN_IDS_USD, TransferenciaView.NONEDITABLE_COLUMN_IDS);

        List<VsjCajabanco> operaciones = tView.getRepo().findByCodTranscorrelativo(vcb.getCodTranscorrelativo());

        for (VsjCajabanco oper : operaciones) {
            if (oper.isEnviado())
                throw new NonEditableException("No se puede editar porque una de los operaciones ya esta enviada a contabilidad: " + oper.getCodCajabanco());
        }
        for (VsjCajabanco oper : operaciones) {
            tView.getContainer().addBean(oper);
        }
        tView.setSaldoTrans();
        tView.getModificarBtn().setEnabled(true);
        tView.getEliminarBtn().setEnabled(true);
        tView.getGuardarBtn().setEnabled(false);
        tView.getNuevoComprobante().setEnabled(true);
        isEdited = false;
    }
}
