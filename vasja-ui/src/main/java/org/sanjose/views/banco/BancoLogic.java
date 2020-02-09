package org.sanjose.views.banco;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpBancodetalle;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.Viewing;

import java.util.List;
import java.util.Optional;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.verifyNumMoneda;
import static org.sanjose.views.sys.Viewing.Mode.*;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class BancoLogic extends BancoItemLogic {

    private static final Logger log = LoggerFactory.getLogger(BancoLogic.class);

    private FieldGroup fieldGroupCabezera;

    private BeanItem<ScpBancocabecera> beanItem;

    @Override
    public void init(BancoOperView view) {
        super.init(view);
        view.getNewChequeBtn().addClickListener(ev -> nuevoCheque(null));
        view.getGuardarBtn().addClickListener(event -> saveCabecera());
        view.getNewItemBtn().addClickListener(event -> nuevoComprobante());
        view.getModificarBtn().addClickListener(event -> editarComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
        view.getAnularBtn().addClickListener(event -> anularComprobante());
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirVoucherBtn().addClickListener(event -> ViewUtil.printComprobante(beanItem.getBean()));
        view.getVerVoucherBtn().addClickListener(event -> ReportHelper.generateComprobante(beanItem.getBean()));
        view.getAnularChequeBtn().addClickListener(clickEvent -> anularCheque(bancocabecera));
        switchMode(EMPTY);
    }

    private void anularComprobante() {
        if (view.gridBanco.getSelectedRow() != null) {
            //fieldGroupCabezera.discard();
            //fieldGroup.discard();
            // Load again from disk
            loadDetallesToGrid(bancocabecera);
        } else {
            clearFields();
            if (view.getContainer().getItemIds().isEmpty())
                switchMode(EMPTY);
            else
                switchMode(VIEW);
        }
    }

    public void nuevoCheque(ScpPlancontable bancoCuenta) {
        switchMode(NEW);
        editarCheque(new ScpBancocabecera());
        if (bancoCuenta!=null)
            view.getSelCuenta().select(bancoCuenta.getId().getCodCtacontable());
    }

    private void clearFields() {
        ViewUtil.clearFields(fieldGroupCabezera);
        ViewUtil.clearFields(fieldGroup);
    }

    public void editarCheque(ScpBancocabecera vsjBancocabecera) {
        view.getContainer().removeAllItems();
        view.gridBanco.select(null);
        moneda = vsjBancocabecera.getCodTipomoneda();
        isLoading=true;
        clearFields();
        //clearSaldos();
        view.setTotal(null);
        item = null;
        bancocabecera = vsjBancocabecera;
        bindForm(vsjBancocabecera);
        loadDetallesToGrid(vsjBancocabecera).ifPresent(bancodet -> {
            view.gridBanco.select(bancodet);
            //viewComprobante();
        });
        addValidators();
        if (vsjBancocabecera.getCodBancocabecera()!=null)
            switchMode(VIEW);
    }

    private Optional loadDetallesToGrid(ScpBancocabecera vsjBancocabecera) {
        if (vsjBancocabecera.getCodBancocabecera()==null) return Optional.ofNullable(null);
        List<ScpBancodetalle> bancodetalleList = view.getService().getBancodetalleRep()
                .findById_CodBancocabecera(vsjBancocabecera.getCodBancocabecera());
        view.initGrid();
        if (!bancodetalleList.isEmpty()) {
            view.getContainer().addAll(bancodetalleList);
            view.getContainer().sort(new Object[]{"txtCorrelativo"}, new boolean[]{true});
            view.setTotal(moneda);
            return Optional.of(bancodetalleList.toArray()[0]);
        }
        return Optional.ofNullable(null);
    }

    private void nuevoComprobante() {
        clearSaldos();
        switchMode(NEW);
        if (bancocabecera != null) {
            // cabecera in edit mode
            //log.debug("nuevo Item, cabecera: " + bancocabecera);
            bindForm(bancocabecera);
            super.nuevoComprobante(bancocabecera.getCodTipomoneda());
            view.getSelCodAuxiliar().setValue(view.getSelCodAuxCabeza().getValue());
            view.getSelResponsable().setValue(view.getSelCodAuxCabeza().getValue());
            view.getGlosaDetalle().setValue(view.getGlosaCabeza().getValue());
               } else {
            super.nuevoComprobante(PEN);
        }
    }

    public void editarComprobante() {
        if (view.getSelectedRow() != null
                && !view.getSelectedRow().isAnula()) {
            clearSaldos();
            bindForm(view.getSelectedRow());
            switchMode(EDIT);
        }
    }

    public void viewComprobante() {
        if (view.getSelectedRow() != null) {
            clearSaldos();
            bindForm(view.getSelectedRow());
        }
        switchMode(VIEW);
    }

    @Override
    public void cerrarAlManejo() {
        if (navigatorView == null) navigatorView = MainUI.get().getBancoManejoView();
        if (moneda!=null)
            navigatorView.selectMoneda(moneda);
        navigatorView.refreshData();
        view.getSubWindow().close();
        //MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    private void bindForm(ScpBancocabecera item) {
        isLoading = true;
        clearSaldos();
        beanItem = new BeanItem<>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        fieldGroupCabezera.bind(view.getDataFechaComprobante(), "fecFecha");
        fieldGroupCabezera.bind(view.getSelCuenta(), "codCtacontable");
        fieldGroupCabezera.bind(view.getSelCodAuxCabeza(), "codDestino");
        fieldGroupCabezera.bind(view.getGlosaCabeza(), "txtGlosa");
        fieldGroupCabezera.bind(view.getCheque(), "txtCheque");
        fieldGroupCabezera.bind(view.getTxtOrigen(), "codOrigenenlace");
        view.getTxtOrigen().setEnabled(false);
        fieldGroupCabezera.bind(view.getTxtNumCombrobante(), "codComprobanteenlace");
        view.getTxtNumCombrobante().setEnabled(false);
        fieldGroupCabezera.bind(view.getChkEnviado(), "flgEnviado");
        //view.getChkCobrado().setConverter(new MesCobradoToBooleanConverter(item));

        String mesCobr = DataUtil.checkMesCobrado(item, view.getService());
        view.getChkCobrado().setEnabled(false);
        view.getChkCobrado().setValue(DataUtil.isCobrado(mesCobr));
        view.getCodMescobrado().setValue(mesCobr!=null ? mesCobr : "");
        view.getChkEnviado().setEnabled(false);

        ViewUtil.setFieldsNullRepresentation(fieldGroupCabezera);
        isEdit = item.getCodBancocabecera() != null;
        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                //log.debug("isEdit cabecera, setting num voucher: " + item.getTxtCorrelativo());
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            view.getNumVoucher().setEnabled(false);
            //setCuentaLogic();
            //view.setTotal(moneda);
        } else {
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    void eliminarComprobante() {
        ScpBancodetalle bancoItem = view.getSelectedRow();
        if (bancoItem  == null)
            return;
        if (bancoItem.getScpBancocabecera().isEnviado()) {
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
                .withMessage("?Esta seguro que quiere eliminar esta operacion:\n" +
                        bancoItem.getScpBancocabecera().getTxtCorrelativo() + "-" + bancoItem.getId().getNumItem() + " ?\n")
                .withYesButton(this::doEliminarComprobante)
                .withNoButton()
                .open();
    }


    private void doEliminarComprobante() {
        ScpBancodetalle bancoItem = view.getSelectedRow();
        log.info("Eliminando: " + bancocabecera.getTxtCorrelativo() + "-" + bancoItem.getId().getNumItem());
        view.getService().deleteBancoOperacion(bancocabecera, bancoItem);
        loadDetallesToGrid(bancocabecera);
        view.refreshData();
    }

    private void saveCabecera() {
        log.debug("saving Cabecera");
        try {
            fieldGroupCabezera.commit();
            log.debug("saved in class: " + bancocabecera);
            ScpBancocabecera cabecera = beanItem.getBean();
            cabecera.setFlgCobrado(!GenUtil.strNullOrEmpty(cabecera.getCodMescobrado()));
            ScpBancodetalle bancoItem = getScpBancodetalle();
            if (!verifyNumMoneda(moneda))
                throw new FieldGroup.CommitException("Moneda no esta de tipo numeral");
            boolean isNew = cabecera.getFecFregistro() == null;
            log.debug("cabezera ready: " + cabecera);

            bancoItem = view.getService().saveBancoOperacion(cabecera, bancoItem, moneda);
            bancocabecera = bancoItem.getScpBancocabecera();
            log.debug("cabecera after save: " + bancoItem.getScpBancocabecera());
            // Update flg_cobrado y mes_cobrado en Comprobante detalle
            view.getService().updateCobradoInCabecera(bancocabecera);

            setNumVoucher(bancoItem);
            moneda = item.getCodTipomoneda();
            if (isNew) {
                view.getContainer().addBean(bancoItem);
            } else {
                loadDetallesToGrid(cabecera);
                view.gridBanco.select(bancoItem);
            }
            view.setTotal(moneda);
            view.refreshData();
            switchMode(VIEW);
        } catch (Validator.InvalidValueException e) {
            Notification.show("Error al guardar: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        } catch (FieldGroup.CommitException ce) {
            Notification.show("Error al guardar el cheque: \n" + GenUtil.genErrorMessage(ce.getInvalidFields()) + "\n" + ce.getMessage(), Notification.Type.ERROR_MESSAGE);
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

    private void switchMode(Viewing.Mode newMode) {
        switch (newMode) {
            case EMPTY:
                view.getAnularChequeBtn().setEnabled(false);
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                view.getImprimirVoucherBtn().setEnabled(false);
                view.getVerVoucherBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(true);
                view.getCerrarBtn().setEnabled(true);
                view.setEnableCabezeraFields(false);
                view.setEnableDetalleFields(false);
                break;

            case NEW:
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                view.getEliminarBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                view.getImprimirVoucherBtn().setEnabled(false);
                view.getVerVoucherBtn().setEnabled(false);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(false);
                view.getCerrarBtn().setEnabled(false);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(false);
                view.selProyectoTercero.setEnabled(false);
                view.tipoProyectoTercero.setEnabled(false);
                break;

            case EDIT:
                view.getAnularChequeBtn().setEnabled(true);
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                if (view.getContainer().size() > 1) view.getEliminarBtn().setEnabled(true);
                else view.getEliminarBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                if (ViewUtil.isPrinterReady()) view.getImprimirVoucherBtn().setEnabled(true);
                view.getVerVoucherBtn().setEnabled(true);
                view.getNewItemBtn().setEnabled(false);
                view.getNewChequeBtn().setEnabled(false);
                view.getCerrarBtn().setEnabled(false);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(true);
                break;

            case VIEW:
                view.getAnularChequeBtn().setEnabled(true);
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                if ((view.getSelectedRow() != null && view.getSelectedRow().isAnula()) ||
                        (bancocabecera != null && (bancocabecera.isAnula()
                                || (bancocabecera.isEnviado() && !Role.isPrivileged())))) {
                    view.getModificarBtn().setEnabled(false);
                    view.getEliminarBtn().setEnabled(false);
                } else {
                    view.getModificarBtn().setEnabled(true);
                    if (view.getContainer().size() > 1) view.getEliminarBtn().setEnabled(true);
                    else view.getEliminarBtn().setEnabled(false);
                }
                view.getCerrarBtn().setEnabled(true);
                if (ViewUtil.isPrinterReady()) view.getImprimirVoucherBtn().setEnabled(true);
                view.getVerVoucherBtn().setEnabled(true);
                if (bancocabecera != null && ((bancocabecera.isEnviado() && !Role.isPrivileged())
                        || bancocabecera.isAnula())) {
                    view.getNewItemBtn().setEnabled(false);
                } else {
                    view.getNewItemBtn().setEnabled(true);
                }
                view.getNewChequeBtn().setEnabled(true);
                view.setEnableCabezeraFields(false);
                view.setEnableDetalleFields(false);
                break;
        }
        view.getImprimirVoucherBtn().setVisible(ViewUtil.isPrinterReady());
    }

    public void anularCheque(ScpBancocabecera cabeceraToAnular) {
        if (cabeceraToAnular.isEnviado() && !Role.isPrivileged()) {
            Notification.show("!No se puede eliminar este cheque porque ya esta enviado a contabilidad!", Notification.Type.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("?Esta seguro que quiere eliminar cheque numero: \n"
                + cabeceraToAnular.getTxtCheque() + " cod operacion: " + cabeceraToAnular.getCodBancocabecera());
        MessageBox
                .createQuestion()
                .withCaption("Eliminar cheque")
                .withMessage(sb.toString())
                .withYesButton(() -> {
                    try {
                        view.getService().anularCheque(cabeceraToAnular);
                        view.refreshData();
                        editarCheque(cabeceraToAnular);
                    } catch (FieldGroup.CommitException ce) {
                        Notification.show("Error al anular: " + ce.getMessage());
                    }
                })
                .withNoButton()
                .open();
    }
}
