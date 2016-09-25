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
import org.sanjose.helper.NonEditableException;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class BancoLogic extends BancoItemLogic {

    private static final Logger log = LoggerFactory.getLogger(BancoLogic.class);

    //private final BancoOperView view;

    private FieldGroup fieldGroupCabezera;


    private BeanItem<VsjBancocabecera> beanItem;

    private VsjBancocabecera bancocabecera;

    //private final TransactionUtil transactionUtil;

    private boolean isEdited = false;

    public BancoLogic(BancoOperView comprobanteView) {
        super(comprobanteView);
        view = comprobanteView;
        //transactionUtil = new TransactionUtil(view.getBancodetalleRep(), view.getEm());
    }

    @Override
    public void init() {
        super.init();
        view.newChequeBtn.addClickListener(ev -> nuevaTrans());
        view.getGuardarBtn().addClickListener(event -> saveCabecera());
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
        log.info("new cheque");
        view.getContainer().removeAllItems();
        moneda = null;
        view.setTotal();
        VsjBancocabecera vcb = new VsjBancocabecera();
        bindForm(vcb);
        view.setEnableCabezeraFields(true);
        view.setEnableDetalleFields(false);
        //view.getGuardarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getImprimirTotalBtn().setEnabled(false);
        //bindForm(vcb);
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
                && !view.getSelectedRow().isAnula()) {
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
        view.setTotal();
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

        isEdit = !GenUtil.strNullOrEmpty(item.getCodUregistro());
        clearSaldos();
        //getSelMoneda().setValue(null);
        beanItem = new BeanItem<VsjBancocabecera>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        fieldGroupCabezera.bind(view.getNumVoucher(), "codBancocabecera");
        fieldGroupCabezera.bind(view.getDataFechaComprobante(), "fecFecha");
        fieldGroupCabezera.bind(view.getSelCuenta(), "codCtacontable");
        /*
        if (isEdit && _PEN.equals(item.getCodTipomoneda())) {
            fieldGroupCabezera.bind(view.getNumEgreso(), "numHabersol");
            fieldGroupCabezera.bind(view.getNumIngreso(), "numDebesol");
        } else if (isEdit && _USD.equals(item.getCodTipomoneda())) {
            fieldGroupCabezera.bind(view.getNumEgreso(), "numHaberdolar");
            fieldGroupCabezera.bind(view.getNumIngreso(), "numDebedolar");
        } else if (isEdit && _EUR.equals(item.getCodTipomoneda())) {

        }
*/
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
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            view.setEnableDetalleFields(true);
            view.setTotal();
            setCuentaLogic();
        } else {
            //setCuentaLogic();
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    @Transactional(readOnly = false)
    public void saveCabecera() {
        log.info("saving Cabecera");
        try {
            fieldGroupCabezera.commit();
            view.setEnableCabezeraFields(false);
            VsjBancocabecera cabecera = beanItem.getBean();
            boolean isNew = cabecera.getFecFregistro()==null;
            log.info("cabezera ready: " + cabecera);
            cabecera.setCodTipomoneda(moneda);
            cabecera = cabecera.prepareToSave();
            cabecera = view.getBancocabeceraRep().save(cabecera);
            if (GenUtil.strNullOrEmpty(cabecera.getTxtCorrelativo())) {
                cabecera.setTxtCorrelativo(GenUtil.getTxtCorrelativo(cabecera.getCodBancocabecera()));
                cabecera = view.getBancocabeceraRep().save(cabecera);
            }
            //
            VsjBancodetalle bancoItem = saveItem(cabecera);
            log.info("detalle ready: " + bancoItem);
            bancoItem.setVsjBancocabecera(cabecera);
            bancoItem = view.getBancodetalleRep().save(bancoItem);
            if (GenUtil.strNullOrEmpty(bancoItem.getTxtCorrelativo())) {
                bancoItem.setTxtCorrelativo(GenUtil.getTxtCorrelativo(bancoItem.getId().getNumItem()));
                bancoItem = view.getBancodetalleRep().save(bancoItem);
            }
            if (cabecera.getVsjBancodetalles()==null)
                cabecera.setVsjBancodetalles(new ArrayList<VsjBancodetalle>());
            if (!cabecera.getVsjBancodetalles().contains(bancoItem)) {
                cabecera.addVsjBancodetalle(bancoItem);
                cabecera = view.getBancocabeceraRep().save(cabecera);
            }
            log.info("cabecera after save: " + cabecera);

            //
            moneda = item.getCodTipomoneda();
            if (isNew) {
                view.getContainer().addBean(bancoItem);
                if (PEN.equals(moneda))
                    ViewUtil.setColumnNames(view.gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_PEN,
                        BancoOperView.VISIBLE_COLUMN_IDS_PEN, BancoOperView.NONEDITABLE_COLUMN_IDS);
                else if (USD.equals(moneda))
                    ViewUtil.setColumnNames(view.gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_USD,
                            BancoOperView.VISIBLE_COLUMN_IDS_USD, BancoOperView.NONEDITABLE_COLUMN_IDS);
                else
                    ViewUtil.setColumnNames(view.gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_EUR,
                            BancoOperView.VISIBLE_COLUMN_IDS_EUR, BancoOperView.NONEDITABLE_COLUMN_IDS);
            } else {
                /*VsjCajabanco vcbOld = null;
                for (VsjCajabanco vcb : view.getContainer().getItemIds()) {
                    if (item.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = item;
                        break;
                    }
                }*/
                //view.getContainer().removeItem(vcbOld);
                view.getContainer().addBean(bancoItem);
            }
            view.setTotal();
            view.getGuardarBtn().setEnabled(false);
            view.getNewItemBtn().setEnabled(true);
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

        List<VsjCajabanco> operaciones = view.getBancodetalleRep().findByCodTranscorrelativo(vcb.getCodTranscorrelativo());

        for (VsjCajabanco oper : operaciones) {
            if ("1".equals(oper.getFlgEnviado()))
                throw new NonEditableException("No se puede editar porque una de los operaciones ya esta enviada a contabilidad: " + oper.getId());
        }
        for (VsjCajabanco oper : operaciones) {
            view.getContainer().addBean(oper);
        }
        view.setTotal();
        view.getModificarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
        view.getGuardarBtn().setEnabled(false);
        view.getNuevoComprobante().setEnabled(true);
        isEdited = false;
*/
    }
}
