package org.sanjose.views.rendicion;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.authentication.Role;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.model.ScpRendiciondetallePK;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.Viewing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.sanjose.util.GenUtil.verifyNumMoneda;
import static org.sanjose.views.sys.Viewing.Mode.*;

/**
 * VASJA class
 * User: prubach
 * Date: 20.09.16
 */
public class RendicionSimpleLogic extends RendicionSimpleItemLogic {

    private static final Logger log = LoggerFactory.getLogger(RendicionSimpleLogic.class);

    private FieldGroup fieldGroupCabezera;

    private BeanItem<ScpRendicioncabecera> beanItem;

    private String exportFileName = null;

    private FileDownloader xlsDownloader;

    private BigDecimal gastoTotal = new BigDecimal(0);

    @Override
    public void init(RendicionSimpleOperView view) {
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
        view.getBtnRegAnticipo().addClickListener(event -> registrarAnticipos());
        view.getBtnCerrar().addClickListener(event -> anular());
        view.getBtnVerVoucher().addClickListener(event -> ReportHelper.generateComprobante(beanItem.getBean()));
        view.getBtnEliminarRend().addClickListener(clickEvent -> eliminarRendicion(beanItem.getBean()));

        if (!Role.isPrivileged()) {
            view.getBtnEnviarAcontab().setVisible(false);
        } else {
            view.getBtnEnviarAcontab().addClickListener(event -> {
                Collection<Object> rendiciones = new ArrayList<>();
                rendiciones.add(beanItem.getBean());
                MainUI.get().getProcUtil().enviarContabilidadRendicion(rendiciones, manView.getService(), manView.getViewLogic());
                view.getBtnEnviarAcontab().setEnabled(false);
            });
        }
        view.getBtnImportar().addClickListener(clickEvent -> importDetalles());
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
        view.getBtnEnviarAcontab().setEnabled(!rendicioncabecera.isEnviado());
        setupExport();
        if (rendicioncabecera.getCodRendicioncabecera()!=null)
            switchMode(EDIT);
        addCommitHandlerToGrid();
    }

    private Optional loadDetallesToGrid(ScpRendicioncabecera rendCab) {
        if (rendCab.getCodRendicioncabecera()==null) return Optional.ofNullable(null);
        List<ScpRendiciondetalle> bancodetalleList = view.getService().getRendiciondetalleRep()
                .findById_CodRendicioncabecera(rendCab.getCodRendicioncabecera());
//        List<ScpRendiciondetalle> bancodetalleListOld = view.getService().getRendiciondetalleRep()
//                .findById_CodComprobanteAndId_CodOrigenAndId_CodMesAndId_TxtAnoprocesoAndId_CodFilial(
//                        rendCab.getCodComprobante(), rendCab.getCodOrigen(), rendCab.getCodMes(), rendCab.getTxtAnoproceso(), "01");


//        Set<ScpRendiciondetalle> rendset = new HashSet<>();
//        rendset.addAll(bancodetalleList);
//        rendset.addAll(bancodetalleListOld);
//

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

    protected void nuevoItem() {
        switchMode(EDIT);
        // If grid is not empty save current and then create a new one.
        if (item!=null) {
            saveCabecera();
            item = null;
        }
        saveCabecera();
        super.nuevoItem();
    }

    public void viewComprobante() {
        if (!view.grid.getSelectedRows().isEmpty()) {
            bindForm((ScpRendiciondetalle)view.grid.getSelectedRows().toArray()[0]);
            addCommitHandlerToGrid();
        }
        switchMode(EDIT);
    }

    public void setupExport() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        exportFileName = "RendicionExport_"
                + beanItem.getBean().getCodComprobante() + "_"
                + sdf.format(new Date(System.currentTimeMillis()))
                + ".xlsx";
        if (xlsDownloader!=null && xlsDownloader.isAttached())
            xlsDownloader.detach();
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                try {
                    return new ByteArrayInputStream(new RendicionExportXLS(beanItem.getBean(),
                            view.getService()).getExported().toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, exportFileName);
        xlsDownloader = new FileDownloader(resource);
        xlsDownloader.extend(view.getBtnGuardarExcel());
    }

    @Override
    public void cerrarAlManejo() {
        if (xlsDownloader!=null && xlsDownloader.isAttached())
            xlsDownloader.detach();
        view.getGrid().deselectAll();
        item = null;
        beanItem = null;
        if (navigatorView == null) navigatorView = MainUI.get().getRendicionSimpleManejoView();
        navigatorView.refreshData();
        if (rendicioncabecera!=null) navigatorView.selectMoneda(rendicioncabecera.getCodTipomoneda());
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
        closeWindow();
    }

    private void bindForm(ScpRendicioncabecera item) {
        isLoading = true;
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
        //fieldGroupCabezera.bind(view.getDataFechaRegistro(), "fecFregistro");
        //fieldGroupCabezera.bind(view.getNumTotalAnticipo(), "numTotalanticipo");

        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        view.getTxtGastoTotal().setValue(df.format(item.getNumGastototal()));
        gastoTotal = item.getNumGastototal();

        view.getTxtSaldoPendiente().setValue(df.format(item.getNumSaldopendiente()));
        if (item.getNumTotalanticipo()!=null) {
            view.getNumTotalAnticipo().setValue(df.format(item.getNumTotalanticipo()));
        }
        view.getNumTotalAnticipo().setEnabled(false);
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
            //if (view.getTxtGastoTotal()!=null)
            cabecera.setNumGastototal(gastoTotal);
            if (!verifyNumMoneda(cabecera.getCodTipomoneda()))
                throw new FieldGroup.CommitException("Moneda no esta de tipo numeral");
            log.debug("cabezera ready: " + cabecera);


            List<ScpRendiciondetalle> bancodetalleList = view.getService().getRendiciondetalleRep()
                    .findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera());
            for (ScpRendiciondetalle det : bancodetalleList) {
                det.setFecComprobante(cabecera.getFecComprobante());
                det.setFecComprobantepago(cabecera.getFecComprobante());
                ScpRendiciondetallePK detId = det.getId().prepareToSave(det);
                det.prepareToSave();
                view.getService().getRendiciondetalleRep().save(det);
            }
            // Committing detalle
            /*if (rendicionItem!=null) {
                BeanItem<ScpRendiciondetalle> beanItem = new BeanItem<>(item);
                final ScpRendiciondetalle rendiItem = prepToSave(beanItem);
                fieldGroup.commit();
                String[] numFields = {"numHaber", "numDebe"};
                Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(rendiItem.getCodTipomoneda()), beanItem));
                rendicionItem = setEmptyStrings(rendiItem);
            }*/

            //rendicionItem = view.getService().saveRendicionOperacion(cabecera, rendicionItem);
            rendicioncabecera  = view.getService().saveRendicionCabecera(cabecera);
            //rendicioncabecera = rendicionItem.getScpRendicioncabecera();
            //log.debug("cabecera after save: " + rendicionItem.getScpRendicioncabecera());
            //boolean isNew = rendicionItem.getFecFregistro() == null;
            //setNumVoucher(rendicionItem);
            moneda = rendicioncabecera.getCodTipomoneda();
            loadDetallesToGrid(cabecera);
            setupExport();
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
        Collection<Object> items = view.getGrid().getSelectedRows();
        if (items == null || items.isEmpty())
            return;
        List<ScpRendiciondetalle> detalles = new ArrayList<>();
        items.forEach(it -> detalles.add((ScpRendiciondetalle)it));
        if (detalles.get(0).getScpRendicioncabecera().isEnviado()) {
            MessageBox
                    .createInfo()
                    .withCaption("Ya enviado a contabilidad")
                    .withMessage("No se puede eliminar porque ya esta enviado a la contabilidad.")
                    .withOkButton()
                    .open();
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("?Esta seguro que quiere eliminar detalles:\n");
        detalles.forEach(det -> sb.append(det.getScpRendicioncabecera().getCodRendicioncabecera() + "-" + det.getId().getNumNroitem() + "\n"));
        sb.append(" ?\n");
        MessageBox
                .createQuestion()
                .withCaption("Eliminar")
                .withMessage(sb.toString())
                .withYesButton(this::doEliminarComprobante)
                .withNoButton()
                .open();
    }

    private void doEliminarComprobante() {
        Collection<Object> items = view.getGrid().getSelectedRows();
        List<ScpRendiciondetalle> detalles = new ArrayList<>();
        items.forEach(it -> detalles.add((ScpRendiciondetalle)it));
        ScpRendicioncabecera rendcab = null;
        for (ScpRendiciondetalle rendiciondetalle : detalles) {
            rendcab = rendiciondetalle.getScpRendicioncabecera();
            view.getService().deleteRendicionOperacion(rendiciondetalle.getScpRendicioncabecera(), rendiciondetalle);
        }
        loadDetallesToGrid(rendcab);
        ViewUtil.clearFields(fieldGroup);
        //view.getNumItem().setValue("");
        item = null;
        switchMode(VIEW);
    }

    protected void registrarAnticipos() {
        Window anticipoWindow = new Window();

        anticipoWindow.setWindowMode(WindowMode.NORMAL);
        anticipoWindow.setWidth(700, Sizeable.Unit.PIXELS);
        anticipoWindow.setHeight(550, Sizeable.Unit.PIXELS);
        anticipoWindow.setPositionX(200);
        anticipoWindow.setPositionY(50);
        anticipoWindow.setDraggable(true);
        anticipoWindow.setModal(false);
        anticipoWindow.setClosable(false);

        AnticipoManejoView anticipoView = new AnticipoManejoView(view.getService());
        anticipoView.init(beanItem.getBean());
        anticipoWindow.setContent(anticipoView);

//        anticipoView.getBtnGuardar().addClickListener(event -> {
//            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
//            if (editedItem != null) {
//                anticipoWindow.close();
//                refreshData();
//            }
//        });
        anticipoView.getBtnCerrar().addClickListener(event -> {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            view.getNumTotalAnticipo().setValue(df.format(anticipoView.getTotal()));
            view.getTxtSaldoPendiente().setValue(GenUtil.numFormat(anticipoView.getTotal().subtract(view.calcTotal(moneda))));
            anticipoWindow.close();
        });

        UI.getCurrent().addWindow(anticipoWindow);
    }


    protected void importDetalles() {
        ScpRendiciondetalle res = saveCabecera();
        if (res==null)
            return;
        item = null;
        //ImportView importView = new ImportView(this);
        //ViewUtil.openViewInNewWindow(importView, 1100, 600);
    }

    private void switchMode(Viewing.Mode newMode) {
        switch (newMode) {
            case EMPTY:
                view.getBtnGuardar().setEnabled(false);
                view.getBtnCerrar().setEnabled(true);
                view.getBtnAnular().setEnabled(false);
                view.getBtnEliminar().setEnabled(false);
                view.getBtnVerVoucher().setEnabled(false);
                view.getBtnNewItem().setEnabled(false);
                view.getBtnEliminarRend().setEnabled(false);
                view.getBtnRegAnticipo().setEnabled(false);
                view.getBtnGuardarExcel().setEnabled(false);
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
                view.getBtnCerrar().setEnabled(true);
                view.getBtnEliminarRend().setEnabled(false);
                view.getBtnRegAnticipo().setEnabled(false);
                view.getBtnGuardarExcel().setEnabled(false);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(false);
                break;

            case EDIT:
                view.getBtnGuardar().setEnabled(true);
                view.getBtnCerrar().setEnabled(true);
                //view.getBtnAnular().setEnabled(true);
                if (view.getContainer().size() > 1) view.getBtnEliminar().setEnabled(true);
                else view.getBtnEliminar().setEnabled(false);
                if (ViewUtil.isPrinterReady()) view.getBtnVerVoucher().setEnabled(true);
                view.getBtnVerVoucher().setEnabled(true);
                view.getBtnNewItem().setEnabled(true);
                view.getBtnEliminarRend().setEnabled(true);
                view.getBtnRegAnticipo().setEnabled(true);
                view.getBtnGuardarExcel().setEnabled(true);
                view.setEnableCabezeraFields(true);
                view.setEnableDetalleFields(true);
                break;

            case VIEW:
                view.getBtnGuardar().setEnabled(false);
                view.getBtnAnular().setEnabled(false);
                view.getBtnCerrar().setEnabled(true);
                view.getBtnVerVoucher().setEnabled(true);
                view.getBtnEliminarRend().setEnabled(true);
                view.getBtnRegAnticipo().setEnabled(true);
                view.getBtnGuardarExcel().setEnabled(true);
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

    public BigDecimal getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(BigDecimal gastoTotal) {
        this.gastoTotal = gastoTotal;
    }
}
