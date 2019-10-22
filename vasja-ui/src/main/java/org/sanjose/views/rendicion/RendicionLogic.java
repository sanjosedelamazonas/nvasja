package org.sanjose.views.rendicion;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.*;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.Viewing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.sanjose.util.GenUtil.verifyNumMoneda;
import static org.sanjose.views.sys.Viewing.Mode.*;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class RendicionLogic extends RendicionItemLogic {

    private static final Logger log = LoggerFactory.getLogger(RendicionLogic.class);

    private FieldGroup fieldGroupCabezera;

    private BeanItem<ScpRendicioncabecera> beanItem;

    @Override
    public void init(RendicionOperView view) {
        super.init(view);
        view.getBtnGuardar().addClickListener(event -> {
            if (beanItem.getBean().isEnviado()) {
                MessageBox
                        .createQuestion()
                        .withCaption("Esta operacion ya esta enviado")
                        .withMessage("?Esta seguro que quiere guardar los cambios?")
                        .withYesButton(() -> {
                            saveCabecera();
                            switchMode(VIEW);
                        })
                        .withNoButton()
                        .open();
            } else {
                saveCabecera();
                switchMode(VIEW);
            }
        });
        view.getBtnAjustar().addClickListener(event -> ajusteTipoCambio());
        view.getBtnNewItem().addClickListener(event -> nuevoItem());
        view.getBtnEliminar().addClickListener(event -> eliminarItem());
        view.getBtnAnular().addClickListener(event -> anular());
        view.getBtnCerrar().addClickListener(event -> cerrarAlManejo());
        view.getBtnVerVoucher().addClickListener(event -> ReportHelper.generateComprobante(beanItem.getBean()));
        view.getBtnToggleVista().addClickListener(event -> view.toggleVista());
        view.getBtnEliminarRend().addClickListener(clickEvent -> eliminarRendicion(beanItem.getBean()));
        switchMode(EMPTY);
    }

    private void anular() {
        if (!view.grid.getSelectedRows().isEmpty()) {
            viewComprobante();
            fieldGroupCabezera.discard();
        } else {
            clearFields();
            if (view.getContainer().getItemIds().isEmpty())
                switchMode(EMPTY);
            else
                switchMode(VIEW);
        }
        cerrarAlManejo();
    }

    public void nuevaRendicion() {
        switchMode(NEW);
        editarRendicion(new ScpRendicioncabecera());
    }

    private void clearFields() {
        ViewUtil.clearFields(fieldGroupCabezera);
        ViewUtil.clearFields(fieldGroup);
    }


    public void editarRendicion(ScpRendicioncabecera rendicioncabecera) {
        view.getContainer().removeAllItems();
        moneda = rendicioncabecera.getCodTipomoneda();
        clearFields();
        view.setTotal(null);
        view.calcFooterSums();
        item = null;
        bindForm(rendicioncabecera);
        addValidators();
        switchMode(NEW);
        view.setEnableDetalleFields(false);
        loadDetallesToGrid(rendicioncabecera).ifPresent(renddet -> {
            view.grid.select(renddet);
            switchMode(EDIT);
        });
        if (rendicioncabecera.getCodRendicioncabecera()!=null)
            switchMode(EDIT);
        addCommitHandlerToGrid();
    }

    private Optional loadDetallesToGrid(ScpRendicioncabecera rendicioncabecera) {
        if (rendicioncabecera.getCodRendicioncabecera()==null) return Optional.ofNullable(null);
        List<ScpRendiciondetalle> bancodetalleList = view.getService().getRendiciondetalleRep()
                .findById_CodRendicioncabecera(rendicioncabecera.getCodRendicioncabecera());
        view.initGrid();
        if (!bancodetalleList.isEmpty()) {
            view.getContainer().addAll(bancodetalleList);
            view.getContainer().sort(new Object[]{"numNritem"}, new boolean[]{true});
            view.getContainer().sort(new Object[]{"id.numNroitem"}, new boolean[]{true});
            view.setTotal(moneda);
            view.calcFooterSums();
            if (view.isVistaFull) ViewUtil.filterColumnsByMoneda(view.getGrid(), 'A');
            return Optional.of(bancodetalleList.toArray()[0]);
        }
        return Optional.ofNullable(null);
    }

    void nuevoItem() {
        switchMode(EDIT);
        // If grid is not empty save current and then create a new one.
        if (item!=null) {
            saveCabecera();
            item = null;
        }
        ScpRendiciondetalle det = saveCabecera();
        if (det != null)
            super.nuevoItem(det);
    }

    public void viewComprobante() {
        if (!view.grid.getSelectedRows().isEmpty()) {
            bindForm((ScpRendiciondetalle)view.grid.getSelectedRows().toArray()[0]);
            addCommitHandlerToGrid();
        }
        switchMode(EDIT);
    }

    @Override
    public void cerrarAlManejo() {
        view.getGrid().deselectAll();
        item = null;
        beanItem = null;
        if (navigatorView == null) navigatorView = MainUI.get().getRendicionManejoView();
        navigatorView.refreshData();
        if (rendicioncabecera!=null) navigatorView.selectMoneda(rendicioncabecera.getCodTipomoneda());
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
        closeWindow();
    }

    private void bindForm(ScpRendicioncabecera item) {
        isLoading = true;
        //clearSaldos();
        setAllFields.forEach(field -> field.setValue(null));
        beanItem = new BeanItem<>(item);
        fieldGroupCabezera = new FieldGroup(beanItem);
        fieldGroupCabezera.setItemDataSource(beanItem);
        fieldGroupCabezera.bind(view.getSelMoneda(), "codTipomoneda");
        view.getTxtOrigen().setValue(item.getCodOrigen());
        fieldGroupCabezera.bind(view.getDataFechaComprobante(), "fecComprobante");
        fieldGroupCabezera.bind(view.getSelResponsable1(), "codDestino");
        fieldGroupCabezera.bind(view.getTxtGlosaCabeza(), "txtGlosa");
        fieldGroupCabezera.bind(view.getSelMoneda(), "codTipomoneda");
        fieldGroupCabezera.bind(view.getDataFechaRegistro(), "fecFregistro");
        fieldGroupCabezera.bind(view.getNumTotalAnticipio(), "numTotalanticipo");

        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        view.getTxtGastoTotal().setValue(df.format(item.getNumGastototal()));
        view.getTxtSaldoPendiente().setValue(df.format(item.getNumSaldopendiente()));
        fieldGroupCabezera.bind(view.getTxtOrigen(), "codOrigen");
        view.getTxtOrigen().setEnabled(false);

        fieldGroupCabezera.bind(view.getNumVoucher(), "codComprobante");
        view.getNumVoucher().setEnabled(false);
        fieldGroupCabezera.bind(view.getChkEnviado(), "flgEnviado");
        view.getChkEnviado().setEnabled(false);

        view.getTxtOrigenlace().setValue(item.getCodOrigenenlace());
        view.getTxtComprobenlace().setValue(item.getCodComprobanteenlace());

        if (item.getCodUregistro()==null)
            item.setCodUregistro(CurrentUser.get());
        MsgUsuario ingresadoPor = view.getService().getMsgUsuarioRep().findByTxtUsuario(item.getCodUregistro().toLowerCase());
        if (ingresadoPor!=null) view.getTxtIngresadoPor().setValue(ingresadoPor.getTxtNombre());
        else view.getTxtIngresadoPor().setValue(item.getCodUregistro());

        ViewUtil.setFieldsNullRepresentation(fieldGroupCabezera);
        isEdit = item.getCodRendicioncabecera() != null;
        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getCodComprobante())) {
                log.debug("isEdit cabecera, setting num voucher: " + item.getCodComprobante());
                view.getNumVoucher().setValue(item.getCodComprobante());
            }
            view.getNumVoucher().setEnabled(false);
            view.setTotal(moneda);
            view.calcFooterSums();
        } else {
            view.getNumVoucher().setValue("");
        }
        isEdit = false;
    }

    private ScpRendiciondetalle saveCabecera() {
        ScpRendiciondetalle rendicionItem = item;
        log.debug("saving Cabecera");
        try {
            fieldGroupCabezera.commit();
            log.debug("saved in class: " + rendicioncabecera);
            ScpRendicioncabecera cabecera = beanItem.getBean();
            if (!verifyNumMoneda(cabecera.getCodTipomoneda()))
                throw new FieldGroup.CommitException("Moneda no esta de tipo numeral");
            log.debug("cabezera ready: " + cabecera);

            // Committing detalle
            if (rendicionItem!=null) {
                BeanItem<ScpRendiciondetalle> beanItem = new BeanItem<>(item);
                final ScpRendiciondetalle rendiItem = prepToSave(beanItem);
                fieldGroup.commit();
                String[] numFields = {"numHaber", "numDebe"};
                Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(rendiItem.getCodTipomoneda())));
                rendicionItem = setEmptyStrings(rendiItem);
            }

            rendicionItem = view.getService().saveRendicionOperacion(cabecera, rendicionItem);
            rendicioncabecera = rendicionItem.getScpRendicioncabecera();
            log.debug("cabecera after save: " + rendicionItem.getScpRendicioncabecera());
            boolean isNew = rendicionItem.getFecFregistro() == null;
            setNumVoucher(rendicionItem);
            moneda = rendicioncabecera.getCodTipomoneda();
            if (isNew) {
                item = rendicionItem;
            } else {
                loadDetallesToGrid(cabecera);
                view.grid.select(rendicionItem);
            }
            view.calcFooterSums();
        } catch (Validator.InvalidValueException e) {
            Notification.show("Error al guardar: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        } catch (FieldGroup.CommitException ce) {
            System.out.println(ce);
            Notification.show("Error al guardar el cheque: \n" + GenUtil.genErrorMessage(ce.getInvalidFields()), Notification.Type.ERROR_MESSAGE);
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        } catch (RuntimeException re) {
            log.warn("Error al guardar: " + re.getMessage() + "\n" + re.toString());
            Notification.show("Error al guardar: " + re.getLocalizedMessage() + "\n" + re.toString(), Notification.Type.ERROR_MESSAGE);
            re.printStackTrace();
            view.setEnableCabezeraFields(true);
            view.setEnableDetalleFields(true);
        }
        return rendicionItem;
    }

    void eliminarItem() {
        ScpRendiciondetalle bancoItem = view.getSelectedRow();
        if (bancoItem  == null)
            return;
        if (bancoItem.getScpRendicioncabecera().isEnviado()) {
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
                        bancoItem.getScpRendicioncabecera().getCodRendicioncabecera() + "-" + bancoItem.getId().getNumNroitem() + " ?\n")
                .withYesButton(this::doEliminarComprobante)
                .withNoButton()
                .open();
    }

    private void doEliminarComprobante() {
        ScpRendiciondetalle rendiciondetalle = view.getSelectedRow();
        ScpRendicioncabecera rendcab = rendiciondetalle.getScpRendicioncabecera();
        view.getService().deleteRendicionOperacion(rendiciondetalle.getScpRendicioncabecera(), rendiciondetalle);
        loadDetallesToGrid(rendcab);
        ViewUtil.clearFields(fieldGroup);
        view.getNumItem().setValue("");
        item = null;
        switchMode(VIEW);
    }

    private void switchMode(Viewing.Mode newMode) {
        switch (newMode) {
            case EMPTY:
                view.getBtnGuardar().setEnabled(false);
                view.getBtnAnular().setEnabled(false);
                view.getBtnEliminar().setEnabled(false);
                view.getBtnVerVoucher().setEnabled(false);
                view.getBtnNewItem().setEnabled(false);
                view.getBtnEliminarRend().setEnabled(false);
                view.setEnableCabezeraFields(false);
                view.setEnableDetalleFields(false);
                break;

            case NEW:
                view.getBtnGuardar().setEnabled(true);
                view.getBtnAnular().setEnabled(true);
                if (view.getContainer().size() > 1) view.getBtnEliminar().setEnabled(true);
                else view.getBtnEliminar().setEnabled(false);
                view.getBtnVerVoucher().setEnabled(false);
                view.getBtnNewItem().setEnabled(true);
                view.getBtnCerrar().setEnabled(false);
                view.getBtnEliminarRend().setEnabled(false);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(false);
                break;

            case EDIT:
                view.getBtnGuardar().setEnabled(true);
                view.getBtnAnular().setEnabled(true);
                if (view.getContainer().size() > 1) view.getBtnEliminar().setEnabled(true);
                else view.getBtnEliminar().setEnabled(false);
                if (ViewUtil.isPrinterReady()) view.getBtnVerVoucher().setEnabled(true);
                view.getBtnVerVoucher().setEnabled(true);
                view.getBtnNewItem().setEnabled(true);
                view.getBtnCerrar().setEnabled(false);
                view.getBtnEliminarRend().setEnabled(true);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(true);
                break;

            case VIEW:
                view.getBtnGuardar().setEnabled(false);
                view.getBtnAnular().setEnabled(false);
                view.getBtnCerrar().setEnabled(true);
                view.getBtnVerVoucher().setEnabled(true);
                view.getBtnEliminarRend().setEnabled(true);
                view.setEnableCabezeraFields(false);
                view.setEnableDetalleFields(false);
                break;
        }
        view.getBtnVerVoucher().setVisible(ViewUtil.isPrinterReady());
    }

    void closeWindow() {
        if (view.getSubWindow()!=null)
            view.getSubWindow().close();
    }
}
