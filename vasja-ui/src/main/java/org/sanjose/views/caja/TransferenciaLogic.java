package org.sanjose.views.caja;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.GenUtil;
import org.sanjose.util.StateUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.Viewing;

import java.util.List;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class TransferenciaLogic extends ComprobanteLogic {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaLogic.class);
    private TransferenciaView tView;
    private Character moneda;
    private StateUtil state;

    @Override
    public void init(ComprobanteViewing comprobanteView) {
        super.init(comprobanteView);
        tView = (TransferenciaView) comprobanteView;
        tView.nuevaTransBtn.addClickListener(ev -> nuevaTrans());
        tView.finalizarTransBtn.addClickListener(ev -> saveTransferencia());
        state = new StateUtil();
        switchMode(Viewing.Mode.VIEW);
    }

    public void nuevaTrans() {
        if (!tView.getContainer().getItemIds().isEmpty() && state.isEdited())
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
        switchMode(Viewing.Mode.NEW);
    }

    @Override
    public void editarComprobante() {
        if (tView.getSelectedRow()!=null
                && !tView.getSelectedRow().isAnula()) {
            state.edit();
            editarComprobante(tView.getSelectedRow());
            switchMode(Viewing.Mode.EDIT);
        }
    }

    public void viewComprobante() {
        if (!view.getGuardarBtn().isEnabled()) {
            super.viewComprobante(tView.getSelectedRow());
            tView.setEnableFields(false);
            switchMode(Viewing.Mode.VIEW);
        }
    }

    @Override
    public void doEliminarComprobante() {
        if (GenUtil.strNullOrEmpty(tView.getSelectedRow().getCodTranscorrelativo()))
            tView.getContainer().removeItem(tView.getSelectedRow());
        else {
            ScpCajabanco anuladoVcb = tView.getSelectedRow().prepareToEliminar();
            ScpCajabanco vcbOld = null;
            for (ScpCajabanco vcb : tView.getContainer().getItemIds()) {
                if (anuladoVcb .getFecFregistro().equals(vcb.getFecFregistro())) {
                    vcbOld = anuladoVcb ;
                    break;
                }
            }
            tView.getContainer().removeItem(vcbOld);
            tView.getContainer().addBean(anuladoVcb);
        }
        if (tView.getContainer().getItemIds().isEmpty()) {
            nuevoComprobante();
            moneda = null;
        } else
            tView.getGridTrans().select(tView.getContainer().firstItemId());
        tView.setSaldoTrans();
        switchMode(Viewing.Mode.VIEW);
        state.edit();
    }

    @Override
    public void cerrarAlManejo() {
        if (state.isEdited())
            MessageBox
                .createQuestion()
                .withCaption("Quitar la transferencia")
                .withMessage("?Esta seguro que quiere eliminar todos operaciones de esta transferencia \n" +
                        "y regresar al Manejo de Caja?\n")
                    .withYesButton(() -> {
                        if (navigatorView == null) navigatorView = MainUI.get().getCajaManejoView();
                        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
                    })
                .withNoButton()
                .open();
        else {
            if (navigatorView == null) navigatorView = MainUI.get().getCajaManejoView();
            MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
            if (view.getSubWindow()!=null)
                view.getSubWindow().close();
        }
    }

    private void setColumnsForMoneda(Character moneda) {
        if (PEN.equals(moneda))
            ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_PEN,
                    TransferenciaView.VISIBLE_COLUMN_IDS_PEN, TransferenciaView.NONEDITABLE_COLUMN_IDS);
        else if (USD.equals(moneda))
            ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_USD,
                    TransferenciaView.VISIBLE_COLUMN_IDS_USD, TransferenciaView.NONEDITABLE_COLUMN_IDS);
        else
            ViewUtil.setColumnNames(tView.gridTrans, TransferenciaView.VISIBLE_COLUMN_NAMES_EUR,
                    TransferenciaView.VISIBLE_COLUMN_IDS_EUR, TransferenciaView.NONEDITABLE_COLUMN_IDS);
    }


    @Override
    public void saveComprobante() {
        try {
            boolean isNew = getVsjCajabanco().getFecFregistro()==null;
            ScpCajabanco item = getVsjCajabanco().prepareToSave();
            moneda = item.getCodTipomoneda();
            if (isNew) {
                tView.getContainer().addBean(item);
                setColumnsForMoneda(moneda);
            }
            else {
                ScpCajabanco vcbOld = null;
                for (ScpCajabanco vcb : tView.getContainer().getItemIds()) {
                    if (item.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = item;
                        break;
                    }
                }
                tView.getContainer().removeItem(vcbOld);
                tView.getContainer().addBean(item);
                tView.gridTrans.sort("fecFregistro", SortDirection.DESCENDING);
            }
            tView.setSaldoTrans();
            switchMode(Viewing.Mode.VIEW);
        } catch (FieldGroup.CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            Notification.show("Error al guardar el comprobante: \n" + errMsg, Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    private void saveTransferencia() {
        if (state.isSaved()) log.error("Called Finalizar but is already SAVED");
        List<ScpCajabanco> savedOperaciones = view.getService().saveVsjCajabancos(tView.getContainer().getItemIds());
        tView.getContainer().removeAllItems();
        tView.getContainer().addAll(savedOperaciones);
        view.refreshData(moneda);
        state.save();
        switchMode(Viewing.Mode.VIEW);
    }

    public void editarTransferencia(ScpCajabanco vcb) throws NonEditableException {
        if (vcb.getCodTranscorrelativo()==null) return;
        tView.getContainer().removeAllItems();
        setColumnsForMoneda(moneda);
        ViewUtil.alignMontosInGrid(tView.gridTrans);

        List<ScpCajabanco> operaciones = tView.getService().getCajabancoRep().findByCodTranscorrelativo(vcb.getCodTranscorrelativo());

        for (ScpCajabanco oper : operaciones) {
            tView.getContainer().addBean(oper);
        }
        switchMode(Viewing.Mode.VIEW);
        state.reset();
        if (!tView.getContainer().getItemIds().isEmpty())
            tView.getGridTrans().select(tView.getContainer().getItemIds().toArray()[0]);
    }

    @Override
    protected void switchMode(Viewing.Mode newMode) {
        super.switchMode(newMode);
        switch (newMode) {
            case EMPTY:
                tView.getNuevaTransBtn().setEnabled(true);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getFinalizarTransBtn().setEnabled(false);
                break;

            case NEW:
                state.edit();
                tView.getNuevaTransBtn().setEnabled(false);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getFinalizarTransBtn().setEnabled(false);
                view.getImprimirBtn().setEnabled(false);
                break;

            case EDIT:
                state.edit();
                tView.getNuevaTransBtn().setEnabled(false);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getImprimirBtn().setEnabled(false);
                view.getFinalizarTransBtn().setEnabled(false);
                break;

            case VIEW:
                tView.getNuevaTransBtn().setEnabled(true);
                if (state.isSaved()) view.getImprimirTotalBtn().setEnabled(true);
                if (tView.getContainer() == null || tView.getContainer().getItemIds().isEmpty()
                        || tView.getGridTrans() == null || tView.getGridTrans().getSelectedRow() == null) {
                    view.getModificarBtn().setEnabled(false);
                    view.getEliminarBtn().setEnabled(false);
                    view.getImprimirBtn().setEnabled(false);
                } else {
                    if (state.isSaved()) view.getImprimirBtn().setEnabled(true);
                    else view.getImprimirBtn().setEnabled(false);
                    if (beanItem != null && (beanItem.getBean().isAnula() ||
                            (beanItem.getBean().isEnviado() && !Role.isPrivileged()))) {
                        view.getModificarBtn().setEnabled(false);
                        view.getEliminarBtn().setEnabled(false);
                    } else {
                        view.getModificarBtn().setEnabled(true);
                        view.getEliminarBtn().setEnabled(true);
                    }
                }
                tView.setSaldoTrans();
                break;
        }
    }

    public StateUtil getState() {
        return state;
    }
}
