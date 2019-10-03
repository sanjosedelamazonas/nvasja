package org.sanjose.views.rendicion;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.views.sys.ComprobanteWarnGuardar;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class RendicionItemLogic implements Serializable, ComprobanteWarnGuardar {

    private static final Logger log = LoggerFactory.getLogger(RendicionItemLogic.class);
    protected ScpRendiciondetalle item;
    protected boolean isLoading = true;
    protected boolean isEdit = false;
    protected NavigatorViewing navigatorView;
    protected Character moneda;
    protected ScpRendicioncabecera rendicioncabecera;
    protected FieldGroup fieldGroup;
    protected RendicionOperView view;
    private BeanItem<ScpRendiciondetalle> beanItem;


    public void init(RendicionOperView view) {
        this.view = view;
    }

    public void setupEditComprobanteView() {

        //--------- CABEZA

        // Fecha Comprobante
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        view.getDataFechaComprobante().setPropertyDataSource(prop);
        view.getDataFechaComprobante().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaComprobante().setResolution(Resolution.DAY);
        view.getDataFechaComprobante().addValueChangeListener(event -> {
            if (view.getDataFechaComprobante().getValue()!=null)
//                DataFilterUtil.refreshComboBox(view.getSelCuenta(), "id.codCtacontable",
//                    DataUtil.getRendicionCuentas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo()),
//                    "txtDescctacontable");
            //setSaldos()
                //TODO - wg jakiej daty szukac projektow?
            refreshProyectoYcuentaPorFecha((Date)event.getProperty().getValue());
        });

        // Fecha registro

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<>(ts);
        view.getDataFechaRegistro().setPropertyDataSource(prop);
        view.getDataFechaRegistro().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaRegistro().setResolution(Resolution.DAY);
        view.getDataFechaRegistro().setValue(new Date());

        //view.getNumVoucher().setEnabled(false);

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable1(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.getSelMoneda(), "codTipomoneda");
        //view.getSelMoneda().addValueChangeListener(event -> setMonedaLogic(event.getProperty().getValue().toString().charAt(0)));


        // ------------ DETALLE

        view.getNumItem().setEnabled(false);

        // Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "txtDescripcion");

        DataFilterUtil.bindComboBox(view.getSelTipoMov(), view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true), "Tipo Movimiento",
                "codTipocuenta", "txtTipocuenta", "id");

        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findById(Integer.parseInt(tipoMov));
                if (config != null && view.getContainer().getItem(item)!=null) {
                    ScpRendiciondetalle sr = view.getContainer().getItem(item).getBean();
                    sr.setCodCtacontable(config.getCodCtacontablegasto());
                    sr.setCodCtaespecial(config.getCodCtaespecial());
                    view.grid.refreshRows(sr);
                }
            }
        });
        view.getTxtGlosaDetalle().setMaxLength(70);
        view.getTxtSerieDoc().setMaxLength(5);
        view.getTxtNumDoc().setMaxLength(20);

        // DETALLE - GRID

        // Fecha Pago
        PopupDateField pdf = new PopupDateField();
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        view.grid.getColumn("fecPagocomprobantepago").setEditorField(pdf);
        view.grid.getColumn("fecPagocomprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Fecha Doc
        pdf = new PopupDateField();
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.MINUTE);
        view.grid.getColumn("fecComprobantepago").setEditorField(pdf);
        view.grid.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Proyecto
        ComboBox selProyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");
        view.grid.getColumn("codProyecto").setEditorField(selProyecto);

        // Cta Contable
        ComboBox selCtacontable = new ComboBox();
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable",view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith('0', 'N', GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        view.grid.getColumn("codCtacontable").setEditorField(selCtacontable);

        // Rubro inst
        ComboBox selCtaespecial = new ComboBox();
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial",
               view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");
        view.grid.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        ComboBox selLugarGasto = new ComboBox();
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte",view.getService().getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txtDescContraparte");
        view.grid.getColumn("codContraparte").setEditorField(selLugarGasto);

        // Rubro Proy
        ComboBox selPlanproyecto = new ComboBox();
        DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
               view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        view.grid.getColumn("codCtaproyecto").setEditorField(selPlanproyecto);

        // Fuente
        ComboBox selFinanciera = new ComboBox();
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera",view.getService().getFinancieraRepo().findAll(),
                "Sel Fuente", "txtDescfinanciera");
        view.grid.getColumn("codFinanciera").setEditorField(selFinanciera);


        addValidators();
        // Editing Destino
        view.getBtnResponsable().addClickListener(event -> editDestino(view.getSelResponsable1()));
        view.getBtnAuxiliar().addClickListener(event -> editDestino(view.getSelCodAuxiliar()));

    }

    public void addValidators() {
        // Validators
        view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecComprobante"));
        view.getFechaDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecComprobantepago"));
        view.getDataFechaRegistro().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecFregistro"));
//        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
//        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable1().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "codDestino"));
        view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codDestino"));
        view.getTxtGlosaCabeza().setDescription("Glosa Cabeza");
        view.getTxtGlosaCabeza().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "txtGlosa"));
        view.getTxtGlosaDetalle().setDescription("Glosa Detalle");
        view.getTxtGlosaDetalle().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtGlosaitem"));
        view.getTxtSerieDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtSeriecomprobantepago"));
        view.getTxtNumDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtComprobantepago"));
        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codTipomov"));
//        // Check saldos and warn
//        saldoChecker = new SaldoChecker(view.getNumEgreso(), view.getSaldoCuenta(), view.getSaldoProyPEN(), this);
    }

    private void editDestino(ComboBox comboBox) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(500, Sizeable.Unit.PIXELS);
        destinoWindow.setHeight(550, Sizeable.Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(view.getService().getDestinoRepo(), view.getService().getCargocuartaRepo(), view.getService().getTipodocumentoRepo());
        if (comboBox.getValue() == null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.getService().getDestinoRepo().findByCodDestino(comboBox.getValue().toString());
            if (destino != null)
                destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.getBtnGuardar().addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            if (editedItem!=null) {
                destinoWindow.close();
                refreshDestino();
                comboBox.setValue(editedItem.getCodDestino());
            }

        });
        destinoView.getBtnAnular().addClickListener(event -> {
            destinoView.viewLogic.anularDestino();
            destinoWindow.close();
        });

        destinoView.getBtnEliminar().addClickListener(clickEvent -> {
            try {
                ScpDestino item = destinoView.getScpDestino();
                String codDestino = item.getCodDestino();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtNombredestino())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            log.debug("To delete: " + item);

                            List<ScpRendiciondetalle> comprobantes = view.getService().getRendiciondetalleRep().findByCodDestino(codDestino);
                            List<ScpRendicioncabecera> cabeceras = view.getService().getRendicioncabeceraRep().findByCodDestino(codDestino);
                            if (comprobantes.isEmpty() && cabeceras.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (ScpRendiciondetalle vcb : comprobantes) {
                                    sb.append("\n").append(vcb.getId().getNumNroitem()).append(" ").append(vcb.getFecComprobante())
                                            .append(" ").append(vcb.getTxtGlosaitem());
                                }
                                for (ScpRendicioncabecera vcb : cabeceras) {
                                    sb.append("\n").append(vcb.getCodRendicioncabecera()).append(" ").
                                            append(vcb.getCodRendicioncabecera()).append(" ").append(vcb.getFecComprobante()).append(" ").append(vcb.getTxtGlosa());
                                }
                                MessageBox
                                        .createWarning()
                                        .withCaption("No se puede eliminar destino: " + item.getTxtNombredestino())
                                        .withMessage("Los sigientes comprobantes usan este destino como Responsable o como Codigo Auxiliar: " + sb.toString())
                                        .open();
                            }
                        })
                        .withNoButton()
                        .open();
            } catch (CommitException ce) {
                Notification.show("Error al eliminar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.warn("Got Commit Exception: " + ce.getMessage());
            }
        });
        UI.getCurrent().addWindow(destinoWindow);
    }

    private void refreshDestino() {
        DataFilterUtil.refreshComboBox(view.getSelResponsable1(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
    }

    private void refreshProyectoYcuentaPorFecha(Date newFecha) {
        if (newFecha==null || view.getService().getPlanRepo()==null) return;
        //if (view.grid.getColumn("codCtacontable"))
//            DataFilterUtil.refreshComboBox(view.getSelCtaContable(),view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
//                    '0', 'N', GenUtil.getYear(newFecha), "101%", "102%", "104%", "106%"),
//                    "id.codCtacontable", "txtDescctacontable", null);
//        DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().
//                            findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(newFecha, newFecha, GenUtil.getBegin20thCent()),
//                    "Sel Proyecto", "txtDescproyecto");
        //view.getSelProyectoTercero().addValueChangeListener(this::setProyectoLogic);
    }


    public void bindForm(ScpRendiciondetalle item) {
        isLoading = true;
        isEdit = !GenUtil.objNullOrEmpty(item.getId());
        beanItem = new BeanItem<>(item);
        this.item = item;
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
   /*     if (!GenUtil.strNullOrEmpty(item.getCodProyecto()) && !GenUtil.strNullOrEmpty(item.getCodTercero())) {
            log.error("Problema con esta operacion " + item.getScpRendicioncabecera().getCodRendicioncabecera() + " - codigo proyecto y codigo tercero son rellenadas!");
        }
        if (!GenUtil.strNullOrEmpty(item.getCodTercero())) {
            view.getTipoProyectoTercero().select(GenUtil.T_TERC);
        } else {
            view.getTipoProyectoTercero().select(GenUtil.T_PROY);
        }
        *//*fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
        fieldGroup.bind(view.getSelTercero(), "codTercero");*//*
        if (PEN.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHabersol");
            fieldGroup.bind(view.getNumIngreso(), "numDebesol");
        } else if (USD.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
            fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
        } else if (EUR.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHabermo");
            fieldGroup.bind(view.getNumIngreso(), "numDebemo");
        } else {
            Notification.show("Moneda sellecionada no existe, esto nunca deberia pasar", Notification.Type.ERROR_MESSAGE);
        }
        ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
        ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
*/
        fieldGroup.bind(view.getTxtGlosaDetalle(), "txtGlosaitem");
        fieldGroup.bind(view.getSelCodAuxiliar(), "codDestino");
        fieldGroup.bind(view.getSelTipoDoc(), "codTipocomprobantepago");
        fieldGroup.bind(view.getTxtSerieDoc(), "txtSeriecomprobantepago");
        fieldGroup.bind(view.getTxtNumDoc(), "txtComprobantepago");
        fieldGroup.bind(view.getFechaDoc(), "fecComprobantepago");
        //fieldGroup.bind(view.getSelFuente(), "codFinanciera");
        fieldGroup.bind(view.getSelTipoMov(), "codTipomov");

        ViewUtil.setFieldsNullRepresentation(fieldGroup);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            log.debug("is Edit in bindForm");
            setNumVoucher(item);
        }
        else if (item.getScpRendicioncabecera() != null) {
            if (item.getId() == null && item.getScpRendicioncabecera().getCodComprobante() != null) {
                log.debug("is NOT Edit in bindForm but ID is null");
                view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
                view.getNumItem().setValue(String.valueOf(view.getContainer().size() + 1));
            }
        }
        isEdit = false;
    }

    protected void setNumVoucher(ScpRendiciondetalle item) {
        if (item.getScpRendicioncabecera() != null && !GenUtil.strNullOrEmpty(item.getScpRendicioncabecera().getCodComprobante())) {
            view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
            view.getNumItem().setValue(String.valueOf(item.getId().getNumNroitem()));
        }
        view.getNumVoucher().setEnabled(false);
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView == null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void nuevoItem(ScpRendiciondetalle vcb) {
        if (rendicioncabecera != null)
            vcb.setScpRendicioncabecera(rendicioncabecera);
        //vcb.setCodTipomoneda(moneda);
        //vcb.setFecComprobante(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        item = vcb;
        view.setEnableCabezeraFields(true);
        view.setEnableDetalleFields(true);
        if (item.getId().getNumNroitem()==null)
            item.getId().setNumNroitem(view.getContainer().size()+1);
        List<ScpRendiciondetalle> items = new ArrayList<>();
        items.add(item);
        view.getContainer().addAll(items);
        view.getContainer().sort(new Object[]{"numNritem"}, new boolean[]{true});
        view.grid.select(item);
        view.getGrid().setEditorEnabled(true);
    }

    public void setNavigatorView(NavigatorViewing navigatorView) {
        this.navigatorView = navigatorView;
    }

    // Helpers

    ScpRendiciondetalle getScpRendiciondetalle() throws CommitException {
        if (fieldGroup==null || beanItem==null || beanItem.getBean()==null)
            throw new CommitException("El cheque debe tener operaciones rellenadas");
        fieldGroup.commit();
        ScpRendiciondetalle item = beanItem.getBean();
        log.debug("got from getDetalle " + item);
        return item;
    }

    @Override
    public void addWarningToGuardarBtn(boolean isWarn) {
        //TODO Implement Warning when Saving!!!
    }

    protected void addCommitHandlerToGrid(){
        log.debug("Add commit");
        view.grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                try {
                    fieldGroup.commit();
                    log.debug("Item click");
                } catch (FieldGroup.CommitException ce) {
                    Notification.show("Por favor rellena los datos necessarios en la parte a la derecha primero!", Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        });

        view.grid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {


            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Object item = view.grid.getContainerDataSource().getItem(view.grid.getEditedItemId());
                try {
                    if (item != null) {
                        ScpRendiciondetalle vcb = (ScpRendiciondetalle) ((BeanItem) item).getBean();
                        final ScpRendiciondetalle vcbToSave = (ScpRendiciondetalle) vcb.prepareToSave();
                        fieldGroup.commit();
                        commitEvent.getFieldBinder();
//                    if (vcb.isEnviado()) {
//                        MessageBox
//                                .createQuestion()
//                                .withCaption("Esta operacion ya esta enviado")
//                                .withMessage("?Esta seguro que quiere guardar los cambios?")
//                                .withYesButton(() -> view.getService().getCajabancoRep().save(vcbToSave))
//                                .withNoButton()
//                                .open();
//                    } else
                        view.getService().getRendiciondetalleRep().save(vcbToSave);
                        bindForm(vcb);
                    }
                } catch (FieldGroup.CommitException ce) {
                    Notification.show("No se puede guarder el item: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        });
    }
}
