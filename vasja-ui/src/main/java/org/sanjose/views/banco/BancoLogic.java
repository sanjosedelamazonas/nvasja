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
import java.util.List;
import java.util.Map;

import static org.sanjose.util.GenUtil.PEN;
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

    @Override
    public void init(BancoOperView view) {
        super.init(view);
        view.getNewChequeBtn().addClickListener(ev -> nuevoCheque());
        view.getGuardarBtn().addClickListener(event -> saveCabecera());
        view.getNewItemBtn().addClickListener(event -> nuevoComprobante());
        view.getModificarBtn().addClickListener(event -> editarComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
        view.getAnularBtn().addClickListener(event -> anularComprobante());
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirTotalBtn().addClickListener(event -> {
            //  if (savedBancodetalle!=null) ViewUtil.printComprobante(savedBancodetalle);
        });
        switchMode(Mode.EMPTY);
    }

    private void anularComprobante() {
        if (view.gridBanco.getSelectedRow() != null) {
            viewComprobante();
            fieldGroupCabezera.discard();
        } else {
            clearFields();
            switchMode(Mode.VIEW);
        }
    }

    public void nuevoCheque() {
        switchMode(Mode.NEW);
        editarCheque(new VsjBancocabecera());
    }

    private void clearFields() {
        if (fieldGroupCabezera != null) {
            new ArrayList<>(fieldGroupCabezera.getFields()).stream().forEach(f -> {
                f.removeAllValidators();
                fieldGroupCabezera.unbind(f);
                f.setValue(null);
            });
        }
        if (fieldGroup != null) {
            new ArrayList<>(fieldGroup.getFields()).stream().forEach(f -> {
                f.removeAllValidators();
                fieldGroup.unbind(f);
                f.setValue(null);
            });
        }
    }

    public void editarCheque(VsjBancocabecera vsjBancocabecera) {
        view.getContainer().removeAllItems();
        view.gridBanco.select(null);
        clearFields();
        moneda = null;
        clearSaldos();
        view.setTotal(null);
        item = null;
        bancocabecera = vsjBancocabecera;
        bindForm(vsjBancocabecera);
        addValidators();
        if (vsjBancocabecera.getCodBancocabecera() != null) {
            List<VsjBancodetalle> bancodetalleList = view.getService().getBancodetalleRep()
                    .findById_CodBancocabecera(vsjBancocabecera.getCodBancocabecera());
            if (!bancodetalleList.isEmpty()) {
                view.getContainer().addAll(bancodetalleList);
                view.gridBanco.select(bancodetalleList.toArray()[0]);
                viewComprobante();
            }
            switchMode(Mode.VIEW);
        }
        view.setEnableDetalleFields(false);
    }

    private void nuevoComprobante() {
        clearSaldos();
        if (bancocabecera != null) {
            // cabecera in edit mode
            log.info("nuevo Item, cabecera: " + bancocabecera);
            bindForm(bancocabecera);
            super.nuevoComprobante(bancocabecera.getCodTipomoneda());
        } else {
            super.nuevoComprobante(PEN);
        }
        switchMode(Mode.NEW);
    }

    private void editarComprobante() {
        if (view.getSelectedRow() != null
                && !view.getSelectedRow().isAnula()) {
            clearSaldos();
            bindForm(view.getSelectedRow());
            view.setEnableCabezeraFields(true);
            switchMode(Mode.EDIT);
        }
    }

    public void viewComprobante() {
        if (view.getSelectedRow() != null) {
            clearSaldos();
            bindForm(view.getSelectedRow());
        }
        switchMode(Mode.VIEW);
        view.setEnableCabezeraFields(false);
        view.setEnableDetalleFields(false);
    }

    @Override
    public void cerrarAlManejo() {
        if (navigatorView == null) navigatorView = MainUI.get().getBancoManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    private void bindForm(VsjBancocabecera item) {
        isLoading = true;

        isEdit = item.getCodBancocabecera() != null;
        clearSaldos();
        beanItem = new BeanItem<>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        fieldGroupCabezera.bind(view.getDataFechaComprobante(), "fecFecha");
        fieldGroupCabezera.bind(view.getSelCuenta(), "codCtacontable");
        fieldGroupCabezera.bind(view.getSelCodAuxCabeza(), "codDestino");
        fieldGroupCabezera.bind(view.getGlosaCabeza(), "txtGlosa");
        fieldGroupCabezera.bind(view.getCheque(), "txtCheque");

        for (Field f : fieldGroupCabezera.getFields()) {
            if (f instanceof TextField)
                ((TextField) f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox) f).setPageLength(20);
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
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    private void eliminarComprobante() {
        VsjBancodetalle bancoItem = view.getSelectedRow();
        MessageBox
                .createQuestion()
                .withCaption("Eliminar operacion")
                .withMessage("?Esta seguro que quiere eliminar operacion: \n" +
                        bancoItem.getVsjBancocabecera().getTxtCorrelativo() + "-" + bancoItem.getId().getNumItem() + " ?\n")
                .withYesButton(() -> {
                    eliminarRealmenteComprobante(bancoItem);
                })
                .withNoButton()
                .open();
    }

    private void eliminarRealmenteComprobante(VsjBancodetalle bancoItem) {
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
            //view.setEnableCabezeraFields(false);
            log.info("saved in class: " + bancocabecera);
            VsjBancocabecera cabecera = beanItem.getBean();
            VsjBancodetalle bancoItem = getVsjBancodetalle();
            if (!verifyNumMoneda(moneda))
                throw new FieldGroup.CommitException("Moneda no esta de tipo numeral");
            boolean isNew = cabecera.getFecFregistro() == null;
            log.info("cabezera ready: " + cabecera);

            bancoItem = view.getService().saveBancoOperacion(cabecera, bancoItem, moneda);
            bancocabecera = bancoItem.getVsjBancocabecera();
            log.info("cabecera after save: " + bancoItem.getVsjBancocabecera());
            setNumVoucher(bancoItem);
            moneda = item.getCodTipomoneda();
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
            view.refreshData();
            switchMode(Mode.VIEW);
        } catch (Validator.InvalidValueException e) {
            Notification.show("Error al guardar: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);

        } catch (FieldGroup.CommitException ce) {
            StringBuilder sb = new StringBuilder();
            Map<Field<?>, Validator.InvalidValueException> fieldMap = ce.getInvalidFields();
            for (Field f : fieldMap.keySet()) {
                sb.append(f.getConnectorId()).append(" ").append(fieldMap.get(f).getHtmlMessage()).append("\n");
            }
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage() + "\n" + sb.toString(), Notification.Type.ERROR_MESSAGE);
            log.warn("Error al guardar: " + ce.getMessage() + "\n" + sb.toString());
            ce.printStackTrace();
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        } catch (RuntimeException re) {
            log.warn("Error al guardar: " + re.getMessage() + "\n" + re.toString());
            Notification.show("Error al guardar: " + re.getLocalizedMessage() + "\n" + re.toString(), Notification.Type.ERROR_MESSAGE);
            re.printStackTrace();
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        }
    }

    private void switchMode(Mode newMode) {
        switch (newMode) {
            case EMPTY:
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(true);
                view.getCerrarBtn().setEnabled(true);
                break;

            case NEW:
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                view.getEliminarBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(false);
                view.getCerrarBtn().setEnabled(false);
                break;

            case EDIT:
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                view.getEliminarBtn().setEnabled(true);
                view.getModificarBtn().setEnabled(false);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(false);
                view.getCerrarBtn().setEnabled(false);
                break;

            case VIEW:
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                if (view.getSelectedRow() != null && view.getSelectedRow().isAnula()) {
                    view.getModificarBtn().setEnabled(false);
                    view.getEliminarBtn().setEnabled(false);
                } else {
                    view.getModificarBtn().setEnabled(true);
                    view.getEliminarBtn().setEnabled(true);
                }
                view.getCerrarBtn().setEnabled(true);
                view.getImprimirTotalBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(true);
                view.getNewChequeBtn().setEnabled(true);
                view.setEnableCabezeraFields(false);
                view.setEnableDetalleFields(false);
                break;
        }
    }

    public enum Mode {
        NEW, EDIT, VIEW, EMPTY
    }

}
