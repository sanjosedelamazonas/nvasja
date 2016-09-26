package org.sanjose.views.banco;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
@SpringComponent
@UIScope
@Service
@Transactional
public class BancoLogic extends BancoItemLogic {

    private static final Logger log = LoggerFactory.getLogger(BancoLogic.class);

    //private final BancoOperView view;

    private FieldGroup fieldGroupCabezera;


    private BeanItem<VsjBancocabecera> beanItem;

    //private final TransactionUtil transactionUtil;

    private boolean isEdited = false;

    //@Autowired
    public BancoLogic() {
    }

    @Override
    public void init(BancoOperView view) {
        super.init(view);
        view.newChequeBtn.addClickListener(ev -> nuevaTrans());
        view.getGuardarBtn().addClickListener(event -> saveCabecera());
        view.getNewItemBtn().addClickListener(event -> nuevoComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
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
        if (bancocabecera!=null) {
            // cabecera in edit mode
            log.info("nuevo Item, cabecera: " + bancocabecera);
            bindForm(bancocabecera);
            super.nuevoComprobante(bancocabecera.getCodTipomoneda());
        } else {
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
        //getSelMoneda().setValue(null);
        beanItem = new BeanItem<>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        //fieldGroupCabezera.bind(view.getNumVoucher(), "txtCorrelativo");
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
                log.info("isEdit cabecera, setting num voucher: " + item.getTxtCorrelativo());
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            view.setEnableCabezeraFields(true);
            view.getNumVoucher().setEnabled(false);
            view.setTotal();
            setCuentaLogic();
        } else {
            //setCuentaLogic();
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    private void eliminarComprobante() {
        try {
            VsjBancodetalle bancoItem = getVsjBancodetalle();
            if (bancoItem == null) {
                log.info("no se puede eliminar si no esta ya guardado");
                return;
            }
            if (bancoItem.getVsjBancocabecera().isEnviado()) {
                Notification.show("Problema al eliminar", "No se puede eliminar porque ya esta enviado a la contabilidad",
                        Notification.Type.WARNING_MESSAGE);
                return;
            }
            bancoItem = prepareToEliminar(bancoItem);

            view.getGlosaDetalle().setValue(bancoItem.getTxtGlosaitem());
            log.info("Ready to ANULAR: " + bancoItem);
            saveCabecera();


            //bancoItem= view.getBancodetalleRep().save(item);
            /*view.getNumVoucher().setValue(Integer.toString(savedBancodetalle.getVsjBancocabecera().getCodBancocabecera()) + "-" + savedBancodetalle.getId().getNumItem());
            savedBancodetalle = null;
            view.getGuardarBtn().setEnabled(false);
            //view.getModificarBtn().setEnabled(false);
            view.getNewItemBtn().setEnabled(true);
            view.refreshData();
            view.getImprimirTotalBtn().setEnabled(false);
            view.getEliminarBtn().setEnabled(true);*/
        } catch (FieldGroup.CommitException ce) {
            Notification.show("Error al anular el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    @Transactional()
    private void saveCabecera() {
        log.info("saving Cabecera");
        try {
            fieldGroupCabezera.commit();
            view.setEnableCabezeraFields(false);
            log.info("saved in class: " + bancocabecera);
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
            BigDecimal saldoHabersol = new BigDecimal(0);
            BigDecimal saldoHaberdolar = new BigDecimal(0);
            BigDecimal saldoHabermo = new BigDecimal(0);
            BigDecimal saldoDebesol = new BigDecimal(0);
            BigDecimal saldoDebedolar = new BigDecimal(0);
            BigDecimal saldoDebemo = new BigDecimal(0);
            for (VsjBancodetalle it : view.getBancodetalleRep()
                    .findById_CodBancocabecera(cabecera.getCodBancocabecera())) {
                saldoDebedolar = saldoDebedolar.add(it.getNumDebedolar());
                saldoDebemo = saldoDebemo.add(it.getNumDebemo());
                saldoDebesol = saldoDebesol.add(it.getNumDebesol());
                saldoHaberdolar = saldoHaberdolar.add(it.getNumHaberdolar());
                saldoHabermo = saldoHabermo.add(it.getNumHabermo());
                saldoHabersol = saldoHabersol.add(it.getNumHabersol());
            }
            cabecera.setNumDebesol(saldoDebesol);
            cabecera.setNumHabersol(saldoHabersol);
            cabecera.setNumDebedolar(saldoDebedolar);
            cabecera.setNumHaberdolar(saldoHaberdolar);
            cabecera.setNumDebemo(saldoDebemo);
            cabecera.setNumDebemo(saldoHabermo);
            cabecera = view.getBancocabeceraRep().save(cabecera);
            log.info("cabecera after save: " + cabecera);
            bancocabecera = cabecera;
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
                VsjBancodetalle vcbOld = null;
                for (VsjBancodetalle vcb : view.getContainer().getItemIds()) {
                    if (bancoItem.getFecFregistro().equals(vcb.getFecFregistro())) {
                        vcbOld = bancoItem;
                        break;
                    }
                }
                view.getContainer().removeItem(vcbOld);
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

    /*private void saveTransferencia() {
        MessageBox
                .createQuestion()
                .withCaption("Guardar la transferencia")
                .withMessage("?Esta seguro que quiere guardar todos operaciones de esta transferencia?\n" +
                        "!Despues no se puede regresar a editarlos en esta pantalla!")
                .withYesButton(this::executeSaveTransferencia)
                .withNoButton()
                .open();
    }*/
}
