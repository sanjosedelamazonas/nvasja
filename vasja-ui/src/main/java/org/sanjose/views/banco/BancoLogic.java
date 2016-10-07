package org.sanjose.views.banco;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.util.GenUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.sanjose.util.GenUtil.verifyNumMoneda;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class BancoLogic extends BancoItemLogic {

    private static final Logger log = LoggerFactory.getLogger(BancoLogic.class);

    private FieldGroup fieldGroupCabezera;


    private BeanItem<VsjBancocabecera> beanItem;

    private boolean isEdited = false;

    @Override
    public void init(BancoOperView view) {
        super.init(view);
        view.newChequeBtn.addClickListener(ev -> nuevoCheque());
        view.getGuardarBtn().addClickListener(event -> saveCabecera());
        view.getNewItemBtn().addClickListener(event -> nuevoComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
        view.imprimirTotalBtn.setEnabled(false);
    }

    private void nuevoCheque() {
        if (!view.getContainer().getItemIds().isEmpty() && isEdited)
            MessageBox
                .createQuestion()
                    .withCaption("Nuevo Cheque")
                    .withMessage("?Esta seguro que quiere eliminar este cheque y crear un nuevo?")
                    .withYesButton(this::resetCheque)
                .withNoButton()
                .open();
        else
            resetCheque();
    }

    private void resetCheque() {
        log.info("new cheque");
        view.getContainer().removeAllItems();
        if (fieldGroupCabezera != null) {
            Collection<Field<?>> cabeceraFields = fieldGroupCabezera.getFields();
            List<Field> cabeceraList = new ArrayList<>(cabeceraFields);
            for (Field f : cabeceraList) {
                fieldGroupCabezera.unbind(f);
            }
        }
        if (fieldGroup != null) {
            Collection<Field<?>> detalleFields = fieldGroup.getFields();
            for (Field f : new ArrayList<>(detalleFields)) {
                fieldGroup.unbind(f);
            }
        }
        moneda = null;
        view.setTotal(null);
        item = null;
        bancocabecera = null;
        VsjBancocabecera vcb = new VsjBancocabecera();
        bindForm(vcb);
        view.setEnableCabezeraFields(true);
        view.resetDetalleFields();
        view.setEnableDetalleFields(false);
        //view.getGuardarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getImprimirTotalBtn().setEnabled(false);
    }

    @Override
    public void nuevoComprobante() {
        isEdited = true;
        if (bancocabecera!=null) {
            // cabecera in edit mode
            log.info("nuevo Item, cabecera: " + bancocabecera);
            bindForm(bancocabecera);
            super.nuevoComprobante(bancocabecera.getCodTipomoneda());
        } else {
            super.nuevoComprobante();
        }
        view.getModificarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
    }

    @Override
    public void editarComprobante() {
        if (view.getSelectedRow()!=null
                && !view.getSelectedRow().isAnula()) {
            isEdited = true;
            editarComprobante(view.getSelectedRow());
            //view.getSelMoneda().setEnabled(false);
            view.getModificarBtn().setEnabled(true);
            view.setEnableCabezeraFields(true);
        }
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

    private void bindForm(VsjBancocabecera item) {
        isLoading = true;

        isEdit = item.getCodBancocabecera()!=null;
        clearSaldos();
        beanItem = new BeanItem<>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        fieldGroupCabezera.bind(view.getDataFechaComprobante(), "fecFecha");
        fieldGroupCabezera.bind(view.getSelCuenta(), "codCtacontable");
        fieldGroupCabezera.bind(view.getSelCodAuxCabeza(), "codDestino");
        fieldGroupCabezera.bind(view.getGlosaCabeza(), "txtGlosa");
        fieldGroupCabezera.bind(view.getCheque(), "txtCheque");

        for (Field f: fieldGroupCabezera.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox)f).setPageLength(20);
        }
        view.setEnableDetalleFields(true);
        view.getSelProyecto().setEnabled(true);
        view.getSelTercero().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                log.info("isEdit cabecera, setting num voucher: " + item.getTxtCorrelativo());
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            view.setEnableCabezeraFields(true);
            view.getNumVoucher().setEnabled(false);
            setCuentaLogic();
            view.setTotal(moneda);
        } else {
            //setCuentaLogic();
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    private void eliminarComprobante() {
        VsjBancodetalle bancoItem = view.getSelectedRow();//getVsjBancodetalle();
            if (bancoItem == null) {
                log.info("no se puede eliminar si no esta ya guardado");
                return;
            }
            if (bancoItem.getVsjBancocabecera().isEnviado()) {
                Notification.show("Problema al eliminar", "No se puede eliminar porque ya esta enviado a la contabilidad",
                        Notification.Type.WARNING_MESSAGE);
                return;
            }
        bancoItem.setFlg_Anula('1');
            log.info("Ready to ANULAR: " + bancoItem);
            saveCabecera();
    }

    private void saveCabecera() {
        log.info("saving Cabecera");
        try {
            fieldGroupCabezera.commit();
            view.setEnableCabezeraFields(false);
            log.info("saved in class: " + bancocabecera);
            VsjBancocabecera cabecera = beanItem.getBean();
            VsjBancodetalle bancoItem = getVsjBancodetalle();
            if (!verifyNumMoneda(moneda))
                throw new FieldGroup.CommitException("Moneda no esta de tipo numeral");
            boolean isNew = cabecera.getFecFregistro()==null;
            log.info("cabezera ready: " + cabecera);

            bancocabecera = view.getService().saveBancoOperacion(cabecera, bancoItem, moneda);

            log.info("cabecera after save: " + bancocabecera);
            //
            moneda = item.getCodTipomoneda();
            log.info("Moneda set in saveCabecera: " + moneda);
            if (isNew) {
                view.getContainer().addBean(bancoItem);
            } else {
                VsjBancodetalle vcbOld = null;
                for (VsjBancodetalle vcb : view.getContainer().getItemIds()) {
                    if (bancoItem.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = bancoItem;
                        break;
                    }
                }
                view.getContainer().removeItem(vcbOld);
                view.getContainer().addBean(bancoItem);
                view.getContainer().sort(new Object[]{"txtCorrelativo"}, new boolean[]{true});
            }
            view.setTotal(moneda);
            view.getGuardarBtn().setEnabled(false);
            view.getNewItemBtn().setEnabled(true);
            view.getModificarBtn().setEnabled(true);
            view.refreshData();
            view.getImprimirTotalBtn().setEnabled(true);
        } catch (Validator.InvalidValueException e) {
            Notification.show("Error al guardar: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);

        } catch (FieldGroup.CommitException ce) {
            StringBuilder sb = new StringBuilder();
            Map<Field<?>, Validator.InvalidValueException> fieldMap = ce.getInvalidFields();
            for (Field f : fieldMap.keySet()) {
                sb.append(f.getConnectorId()).append(" ").append(fieldMap.get(f).getHtmlMessage()).append("\n");
            }
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage() + "\n" + sb.toString(), Notification.Type.ERROR_MESSAGE);
            log.warn("Got Commit Exception: " + ce.getMessage() + "\n" + sb.toString());
        }
    }
}
