package org.sanjose.views.caja;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.validator.BeanValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneToBooleanConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.SaldoChecker;
import org.sanjose.validator.TwoCombosValidator;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.Viewing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

import static org.sanjose.util.GenUtil.EUR;
import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;
import static org.sanjose.views.sys.Viewing.Mode.NEW;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class ComprobanteLogic implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ComprobanteLogic.class);

    protected ComprobanteViewing view;
    protected NavigatorViewing navigatorView;
    protected FieldGroup fieldGroup;
    protected BeanItem<VsjCajabanco> beanItem;
    private VsjCajabanco savedCajabanco;
    private boolean isLoading = false;
    private boolean isEdit = false;
    private ProcUtil procUtil;
    private Viewing.Mode mode;
    private SaldoChecker saldoChecker;

    public void init(ComprobanteViewing comprobanteView) {
        view = comprobanteView;
        view.getGuardarBtn().addClickListener(event -> saveComprobante());
        view.getNuevoComprobante().addClickListener(event -> nuevoComprobante());
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirBtn().addClickListener(event -> {
            if (savedCajabanco!=null) ViewUtil.printComprobante(savedCajabanco);
        });
        view.getAnularBtn().addClickListener(event -> anular());
        view.getModificarBtn().addClickListener(event -> editarComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
        procUtil = MainUI.get().getProcUtil();
        // Don't show navigation buttons if opened in subwindow Nuevo Comprobante
        if (view.getSubWindow()!=null && view instanceof ComprobanteView) {
            view.getNuevoComprobante().setVisible(false);
            view.getModificarBtn().setVisible(false);
            view.getCerrarBtn().setVisible(false);
        } else if (view.getSubWindow()!=null && view instanceof TransferenciaView) {
            ((TransferenciaView) view).getNuevaTransBtn().setVisible(false);
        }
    }

    void anular() {
        fieldGroup.discard();
        if (mode.equals(NEW)) {
            nuevoComprobante();
            switchMode(Viewing.Mode.EMPTY);
        } else {
            switchMode(Viewing.Mode.VIEW);
        }
        if (view.getSubWindow()!=null)
            view.getSubWindow().close();
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView == null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void saveComprobante() {
        try {
            VsjCajabanco item = getVsjCajabanco().prepareToSave();

            savedCajabanco = view.getService().save(item);

            view.getNumVoucher().setValue(savedCajabanco.getTxtCorrelativo());
            view.refreshData();
            switchMode(Viewing.Mode.VIEW);
            if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
                ViewUtil.printComprobante(savedCajabanco);
            }
            if (view.getSubWindow()!=null)
                view.getSubWindow().close();
        } catch (CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            Notification.show("Error al guardar el comprobante: \n" + errMsg, Notification.Type.ERROR_MESSAGE);
            //log.warn("Got Commit Exception: " + ce.getMessage() + "\n" + errMsg);
            view.setEnableFields(true);
            switchMode(Viewing.Mode.EDIT);
        }
    }

    void nuevoComprobante(char moneda) {
        savedCajabanco = null;
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setFlgEnviado('0');
        vcb.setFlg_Anula('0');
        vcb.setIndTipocuenta('0');
        vcb.setCodTipomoneda(moneda);
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        switchMode(NEW);
    }

    public void nuevoComprobante() {
        nuevoComprobante(PEN);
    }

    void editarComprobante() {
        editarComprobante(savedCajabanco);
    }

    public void editarComprobante(VsjCajabanco vcb) {
        savedCajabanco = vcb;
        bindForm(vcb);
        if (vcb.isReadOnly())
            switchMode(Viewing.Mode.VIEW);
        else
            switchMode(Viewing.Mode.EDIT);
    }

    public void viewComprobante(VsjCajabanco vcb) {
        savedCajabanco = vcb;
        bindForm(vcb);
    }

    void eliminarComprobante() {
        try {
            if (savedCajabanco == null) {
                log.info("no se puede eliminar si no esta ya guardado");
                return;
            }
            if (savedCajabanco.isEnviado()) {
                Notification.show("Problema al eliminar", "No se puede eliminar porque ya esta enviado a la contabilidad",
                        Notification.Type.WARNING_MESSAGE);
                return;
            }
            VsjCajabanco item = getVsjCajabanco().prepareToEliminar();

            view.getGlosa().setValue(item.getTxtGlosaitem());
            log.info("Ready to ANULAR: " + item);
            savedCajabanco = view.getService().getCajabancoRep().save(item);
            view.getNumVoucher().setValue(Integer.toString(savedCajabanco.getCodCajabanco()));
            savedCajabanco = null;
            switchMode(Viewing.Mode.VIEW);
            view.refreshData();
        } catch (CommitException ce) {
            Notification.show("Error al anular el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    // Setup and bind fields

    public void setupEditComprobanteView() {
        // Fecha
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        view.getDataFechaComprobante().setPropertyDataSource(prop);
        view.getDataFechaComprobante().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaComprobante().setResolution(Resolution.DAY);
        view.getDataFechaComprobante().addValueChangeListener(event -> {
            setSaldoCaja();
            setSaldos();
            view.setSaldoDeCajas();
        });

        // Fecha Doc
        prop = new ObjectProperty<>(ts);
        view.getFechaDoc().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);

        // Proyecto
        DataFilterUtil.bindComboBox(view.getSelProyecto(), "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()),
                "Sel Proyecto", "txtDescproyecto");
        view.getSelProyecto().addValueChangeListener(this::setProyectoLogic);

        // Tercero
        DataFilterUtil.bindComboBox(view.getSelTercero(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestino('3'), "Sel Tercero",
                "txtNombredestino");
        view.getSelTercero().addValueChangeListener(this::setTerceroLogic);

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.getSelMoneda(), "codTipomoneda");
        view.getSelMoneda().addValueChangeListener(event -> setMonedaLogic(event.getProperty().getValue().toString().charAt(0)));

        view.getNumIngreso().addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (GenUtil.isInvertedZero(event.getProperty().getValue())) {
                            view.getNumEgreso().setValue("");
                        }
                    }
                }
        );
        view.getNumEgreso().addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (GenUtil.isInvertedZero(event.getProperty().getValue())) {
                            view.getNumIngreso().setValue("");
                        }
                    }
                }
        );

        DataFilterUtil.bindComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(new Date(), view.getService().getPlanRepo(), PEN), "Sel Caja", "txtDescctacontable");
        view.getSelCaja().addValueChangeListener(e -> setSaldoCaja());

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "Responsable", "txtNombredestino");

        view.getSelResponsable().addValueChangeListener(valueChangeEvent ->  {
            if (valueChangeEvent.getProperty().getValue()!=null)
                view.getSelCodAuxiliar().setValue(valueChangeEvent.getProperty().getValue());
        });

        // Lugar de gasto
        DataFilterUtil.bindComboBox(view.getSelLugarGasto(), "codContraparte", view.getService().getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txtDescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "Auxiliar", "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "Sel Tipo", "txtDescripcion");


        // Cta Contable
        DataFilterUtil.bindComboBox(view.getSelCtaContable(), "id.codCtacontable",
                view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        '0', 'N', GenUtil.getCurYear(), "101%", "102%", "104%", "106%"),
                //getPlanRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""),
                "Sel cta contable", "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.bindComboBox(view.getSelRubroInst(), "id.codCtaespecial",
                view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        // Rubro Proy
        DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Rubro proyecto", "txtDescctaproyecto");
        // Fuente
        DataFilterUtil.bindComboBox(view.getSelFuente(), "codFinanciera", view.getService().getFinancieraRepo().findAll(),
                "Fuente", "txtDescfinanciera");

        DataFilterUtil.bindComboBox(view.getSelTipoMov(), view.getService().getConfiguractacajabancoRepo().findByActivoAndParaCajaAndParaProyecto(true, true, true),
                "Tipo Movimiento", "codTipocuenta", "txtTipocuenta", "id");
        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findById(Integer.parseInt(tipoMov));
                if (config!=null) {
                    view.getSelCtaContable().setValue(config.getCodCtacontablegasto());
                    view.getSelRubroInst().setValue(config.getCodCtaespecial());
                }

            }
        });
        view.getGlosa().setMaxLength(70);
        view.getNumDoc().setMaxLength(20);
        view.getSerieDoc().setMaxLength(5);

        // Validators
        view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "fecFecha"));
        view.getFechaDoc().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "fecComprobantepago"));
        view.getSelProyecto().addValidator(new TwoCombosValidator(view.getSelTercero(), true, null));
        view.getSelTercero().addValidator(new TwoCombosValidator(view.getSelProyecto(), true, null));
        view.getSelMoneda().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codTipomoneda"));
        view.getNumIngreso().setDescription("Ingreso");
        view.getNumEgreso().setDescription("Egreso");
        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codDestino"));
        view.getSelLugarGasto().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codContraparte"));
        view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codDestinoitem"));
        view.getGlosa().setDescription("Glosa");
        view.getGlosa().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "txtGlosaitem"));
        view.getSerieDoc().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "txtSeriecomprobantepago"));
        view.getNumDoc().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "txtComprobantepago"));
        view.getSelCtaContable().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codContracta"));
        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codTipomov"));

        // Check saldos and warn
        saldoChecker = new SaldoChecker(view.getNumEgreso(), view.getSaldoCajaPEN(), view.getSaldoProyPEN());
        view.getNumEgreso().addBlurListener(event -> saldoChecker.check());

        // Editing Destino
        view.getBtnDestino().addClickListener(event->editDestino(view.getSelCodAuxiliar()));
        view.getBtnResponsable().addClickListener(event->editDestino(view.getSelResponsable()));
        view.setEnableFields(false);
        nuevoComprobante();
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
        if (comboBox.getValue()==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.getService().getDestinoRepo().findByCodDestino(comboBox.getValue().toString());
            if (destino!=null)
                destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.getBtnGuardar().addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            destinoWindow.close();
            refreshDestino();
            comboBox.setValue(editedItem.getCodDestino());

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

                            List<VsjCajabanco> comprobantes = view.getService().getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (VsjCajabanco vcb : comprobantes) {
                                    sb.append("\n").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosaitem());
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
            } catch (FieldGroup.CommitException ce) {
                Notification.show("Error al eliminar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.info("Got Commit Exception: " + ce.getMessage());
            }
        });
        UI.getCurrent().addWindow(destinoWindow);
    }


    private void refreshDestino() {
        DataFilterUtil.refreshComboBox(view.getSelResponsable(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
    }


    private void setMonedaLogic(Character moneda) {
        if (!isLoading) {
            String oldNumEgreso = view.getNumEgreso().getValue();
            String oldNumIngreso = view.getNumIngreso().getValue();
            try {
                fieldGroup.unbind(view.getNumEgreso());
                fieldGroup.unbind(view.getNumIngreso());
            } catch (FieldGroup.BindException be) {
            }
            view.getSelCaja().removeAllValidators();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                beanItem.getBean().setNumHaberdolar(new BigDecimal(0));
                beanItem.getBean().setNumDebedolar(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                DataFilterUtil.refreshComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo(), moneda), "txtDescctacontable");
                fieldGroup.bind(view.getNumEgreso(), "numHabersol");
                fieldGroup.bind(view.getNumIngreso(), "numDebesol");
                saldoChecker.setSaldoField(view.getSaldoCajaPEN());
                saldoChecker.setProyectoField(view.getSaldoProyPEN());
            } else if (moneda.equals(USD)) {
                // Dolares
                // Cta Caja
                beanItem.getBean().setNumHabersol(new BigDecimal(0));
                beanItem.getBean().setNumDebesol(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                DataFilterUtil.refreshComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo(), moneda), "txtDescctacontable");
                fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
                fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
                saldoChecker.setSaldoField(view.getSaldoCajaUSD());
                saldoChecker.setProyectoField(view.getSaldoProyUSD());
            } else {
                // Euro
                // Cta Caja
                beanItem.getBean().setNumHabersol(new BigDecimal(0));
                beanItem.getBean().setNumDebesol(new BigDecimal(0));
                beanItem.getBean().setNumHaberdolar(new BigDecimal(0));
                beanItem.getBean().setNumDebedolar(new BigDecimal(0));
                DataFilterUtil.refreshComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo(), moneda), "txtDescctacontable");
                fieldGroup.bind(view.getNumEgreso(), "numHabermo");
                fieldGroup.bind(view.getNumIngreso(), "numDebemo");
                saldoChecker.setSaldoField(view.getSaldoCajaEUR());
                saldoChecker.setProyectoField(view.getSaldoProyEUR());
            }
            // copy values to new field
            view.getNumEgreso().setValue(oldNumEgreso);
            view.getNumIngreso().setValue(oldNumIngreso);
            //
            if (savedCajabanco != null && !GenUtil.objNullOrEmpty(savedCajabanco.getCodCtacontable()) && moneda.equals(savedCajabanco.getCodTipomoneda())) {
                view.getSelCaja().select(savedCajabanco.getCodCtacontable());
            } else {
                setCajaLogic(moneda);
            }
            view.getSelCaja().addValidator(new LocalizedBeanValidator(VsjCajabanco.class, "codCtacontable"));
            ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
            ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
            view.setSaldoDeCajas();
        }
    }


    private void setSaldos() {
        if (view.getDataFechaComprobante().getValue()!=null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            ProcUtil.Saldos res = null;
            if (isProyecto()) {
                res = procUtil.getSaldos(view.getDataFechaComprobante().getValue(), view.getSelProyecto().getValue().toString(), null);
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue(df.format(res.getSaldoEUR()));
            }
            if (isTercero()) {
                res = procUtil.getSaldos(view.getDataFechaComprobante().getValue(), null, view.getSelTercero().getValue().toString());
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue("");

            }
        }
    }

    private void setSaldoCaja() {
        if (view.getDataFechaComprobante().getValue()!=null && view.getSelCaja().getValue()!=null && view.getSelMoneda().getValue()!=null) {
            BigDecimal saldo = procUtil.getSaldoCaja(GenUtil.getEndOfDay(GenUtil.dateAddDays(view.getDataFechaComprobante().getValue(),-1)),
                    view.getSelCaja().getValue().toString(), view.getSelMoneda().getValue().toString().charAt(0));
            if (PEN.equals(view.getSelMoneda().getValue().toString().charAt(0))) {
                view.getSaldoCajaPEN().setValue(saldo.toString());
                view.getSaldoCajaUSD().setValue("");
                view.getSaldoCajaEUR().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCajaPEN());
            } else if (USD.equals(view.getSelMoneda().getValue().toString().charAt(0))) {
                view.getSaldoCajaUSD().setValue(saldo.toString());
                view.getSaldoCajaPEN().setValue("");
                view.getSaldoCajaEUR().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCajaUSD());
            } else {
                view.getSaldoCajaEUR().setValue(saldo.toString());
                view.getSaldoCajaPEN().setValue("");
                view.getSaldoCajaUSD().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCajaEUR());
            }
        }
    }

    private void setCajaLogic(Character tipomoneda) {
        VsjConfiguracioncaja config = null;
        if (isProyecto()) {
            List<VsjConfiguracioncaja> configs = view.getService().getConfiguracioncajaRepo().findByCodProyectoAndIndTipomoneda(
                    view.getSelProyecto().getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                config = configs.get(0);
            } else {
                String catProy = view.getService().getProyectoRepo().findByCodProyecto(view.getSelProyecto().getValue().toString())
                        .getCodCategoriaproyecto();
                configs = view.getService().getConfiguracioncajaRepo().findByCodCategoriaproyectoAndIndTipomoneda(
                        catProy, tipomoneda);
                if (!configs.isEmpty()) {
                    config = configs.get(0);
                }
            }
        } else if (isTercero()) {
            List<VsjConfiguracioncaja> configs = view.getService().getConfiguracioncajaRepo().findByCodDestinoAndIndTipomoneda(
                    view.getSelTercero().getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                config = configs.get(0);
            }
        }
        if (config==null) {
            List<VsjConfiguracioncaja> configs = view.getService().getConfiguracioncajaRepo().
                    findByIndTipomonedaAndCodDestinoAndCodProyectoAndCodCategoriaproyecto(tipomoneda,null,null,null);
            if (!configs.isEmpty()) {
                config = configs.get(0);
            }
        }
        if (config!=null) view.getSelCaja().setValue(config.getCodCtacontable());
        setSaldoCaja();
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        view.getSelProyecto().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorTerceroLogic(event.getProperty().getValue().toString());
            view.getSelTercero().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
        }
    }

    private void setEditorTerceroLogic(String codTercero)  {
        if (!GenUtil.strNullOrEmpty(codTercero)) {
            view.setEnableFields(true);
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(),
                    view.getService().getConfiguractacajabancoRepo().findByActivoAndParaCajaAndParaTercero(true, true, true),
                    "codTipocuenta", "txtTipocuenta", "id");
            view.getSelFuente().setValue("");
            view.getSelFuente().setEnabled(false);
            // Reset those fields
            if (!isEdit) {
                view.getSelCtaContable().setValue("");
                view.getSelRubroInst().setValue("");
                view.getSelRubroProy().setValue("");
            }
            setSaldos();
            setMonedaLogic(view.getSelMoneda().getValue().toString().charAt(0));
            //saldoChecker.setProyectoField(view.getSelMoneda().getValue().equals('0') ? view.getSaldoProyPEN() : view.getSaldoCajaUSD());
        }
    }

    private void setEditorLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableFields(true);
            view.getSelFuente().setEnabled(true);
            DataFilterUtil.refreshComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                    view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "txtDescctaproyecto");

            List<Scp_ProyectoPorFinanciera>
                    proyectoPorFinancieraList = view.getService().getProyectoPorFinancieraRepo().findById_CodProyecto(codProyecto);

            // Filter financiera if exists in Proyecto Por Financiera
            List<ScpFinanciera> financieraList = view.getService().getFinancieraRepo().findAll();
            List<ScpFinanciera> financieraEfectList = new ArrayList<>();
            if (proyectoPorFinancieraList!=null && !proyectoPorFinancieraList.isEmpty()) {
                List<String> codFinancieraList = proyectoPorFinancieraList.stream().map(proyectoPorFinanciera -> proyectoPorFinanciera.getId().getCodFinanciera()).collect(Collectors.toList());

                for (ScpFinanciera financiera : financieraList) {
                    if (financiera.getCodFinanciera()!=null &&
                            codFinancieraList.contains(financiera.getCodFinanciera())) {
                        financieraEfectList.add(financiera);
                    }
                }
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.refreshComboBox(view.getSelFuente(), "codFinanciera", financieraEfectList,
                    "txtDescfinanciera");
            if (financieraEfectList.size()==1)
                view.getSelFuente().select(financieraEfectList.get(0).getCodFinanciera());

            // Sel Tipo Movimiento
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(),
                    view.getService().getConfiguractacajabancoRepo().findByActivoAndParaCajaAndParaProyecto(true, true, true),
                    "codTipocuenta", "txtTipocuenta", "id");
            // Reset those fields
            if (!isEdit) {
                view.getSelCtaContable().setValue("");
                view.getSelRubroInst().setValue("");
            }
            view.getSelTipoMov().setEnabled(true);
            view.getSelRubroInst().setEnabled(true);
            view.getSelCtaContable().setEnabled(true);
            setSaldos();
            setMonedaLogic(view.getSelMoneda().getValue().toString().charAt(0));
            //saldoChecker.setProyectoField(view.getSelMoneda().getValue().toString().charAt(0)==PEN ? view.getSaldoProyPEN() : view.getSaldoCajaUSD());
        } else {
            view.getSelFuente().setEnabled(false);
            view.getSelFuente().setValue("");
            view.getSelRubroProy().setEnabled(false);
            view.getSelRubroProy().setValue("");
        }
    }

    private void bindForm(VsjCajabanco item) {
        isLoading = true;
        isEdit = !GenUtil.strNullOrEmpty(item.getCodUregistro());
        clearSaldos();
        beanItem = new BeanItem<>(item);
        if (fieldGroup != null) {
            fieldGroup.discard();
            List<Field<?>> fieldList = new ArrayList<>(fieldGroup.getFields());
            for (Field f : fieldList) {
                fieldGroup.unbind(f);
            }
            fieldGroup.setItemDataSource(beanItem);
        } else {
            fieldGroup = new FieldGroup(beanItem);
        }
        fieldGroup.bind(view.getSelProyecto(), "codProyecto");
        fieldGroup.bind(view.getSelTercero(), "codTercero");
        fieldGroup.bind(view.getSelMoneda(), "codTipomoneda");
        fieldGroup.bind(view.getDataFechaComprobante(), "fecFecha");
        fieldGroup.bind(view.getSelCaja(), "codCtacontable");
        view.getChkEnviado().setConverter(new ZeroOneToBooleanConverter());
        fieldGroup.bind(view.getChkEnviado(), "flgEnviado");
        view.getChkEnviado().setEnabled(false);
        fieldGroup.bind(view.getTxtOrigen(), "codOrigenenlace");
        view.getTxtOrigen().setEnabled(false);
        fieldGroup.bind(view.getTxtNumCombrobante(), "codComprobanteenlace");
        view.getTxtNumCombrobante().setEnabled(false);

        if (isEdit && PEN.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHabersol");
            fieldGroup.bind(view.getNumIngreso(), "numDebesol");
        } else if (isEdit && USD.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
            fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
        } else if (isEdit && EUR.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHabermo");
            fieldGroup.bind(view.getNumIngreso(), "numDebemo");
        }
        ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
        ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
        fieldGroup.bind(view.getGlosa(), "txtGlosaitem");
        fieldGroup.bind(view.getSelResponsable(), "codDestino");
        fieldGroup.bind(view.getSelLugarGasto(), "codContraparte");
        fieldGroup.bind(view.getSelCodAuxiliar(), "codDestinoitem");
        fieldGroup.bind(view.getSelTipoDoc(), "codTipocomprobantepago");
        fieldGroup.bind(view.getSerieDoc(), "txtSeriecomprobantepago");
        fieldGroup.bind(view.getNumDoc(), "txtComprobantepago");
        fieldGroup.bind(view.getFechaDoc(), "fecComprobantepago");
        fieldGroup.bind(view.getSelCtaContable(), "codContracta");
        fieldGroup.bind(view.getSelRubroInst(), "codCtaespecial");
        fieldGroup.bind(view.getSelRubroProy(), "codCtaproyecto");
        fieldGroup.bind(view.getSelFuente(), "codFinanciera");
        fieldGroup.bind(view.getSelTipoMov(), "codTipomov");

        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox) f).setPageLength(25);
        }
        view.setEnableFields(false);
        view.getSelProyecto().setEnabled(true);
        view.getSelTercero().setEnabled(true);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            view.setEnableFields(true);
            setSaldos();
            setSaldoCaja();
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorLogic(item.getCodProyecto());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
            }
        } else {
            view.getSelMoneda().setValue(item.getCodTipomoneda());
            view.getNumVoucher().setValue("");
            view.setSaldoDeCajas();
        }
        isEdit = false;
    }

    // Helpers

    public void setNavigatorView(NavigatorViewing navigatorView) {
        this.navigatorView = navigatorView;
    }

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(view.getSelProyecto().getValue());
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.getSelTercero().getValue());
    }

    private void clearSaldos() {
        //noinspection unchecked
        Arrays.stream(new Field[]{view.getSaldoCajaPEN(), view.getSaldoCajaUSD(), view.getSaldoCajaEUR(), view.getSaldoProyPEN(), view.getSaldoProyUSD(), view.getSaldoProyEUR()})
                .forEach(f -> f.setValue(""));
    }

    VsjCajabanco getVsjCajabanco() throws FieldGroup.CommitException {
        fieldGroup.commit();
        VsjCajabanco item = beanItem.getBean();
        view.setEnableFields(false);
        return item;
    }

    protected void switchMode(Viewing.Mode newMode) {
        mode = newMode;
        switch (newMode) {
            case EMPTY:
                view.getCerrarBtn().setEnabled(true);
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                view.getModificarBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(false);
                view.getNuevoComprobante().setEnabled(true);
                view.getImprimirBtn().setEnabled(false);

                view.getSelProyecto().setEnabled(false);
                view.getSelTercero().setEnabled(false);
                view.getDataFechaComprobante().setEnabled(false);
                break;

            case NEW:
                view.getCerrarBtn().setEnabled(false);
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                view.getModificarBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(false);
                view.getNuevoComprobante().setEnabled(false);
                view.getImprimirBtn().setEnabled(false);

                view.getSelProyecto().setEnabled(true);
                view.getSelTercero().setEnabled(true);
                view.getDataFechaComprobante().setEnabled(true);
                break;

            case EDIT:
                view.getCerrarBtn().setEnabled(false);
                view.getGuardarBtn().setEnabled(true);
                view.getAnularBtn().setEnabled(true);
                view.getModificarBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(true);
                view.getNuevoComprobante().setEnabled(false);
                view.getImprimirBtn().setEnabled(true);
                break;

            case VIEW:
                view.getCerrarBtn().setEnabled(true);
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(false);
                view.getImprimirBtn().setEnabled(true);

                if (beanItem != null && (beanItem.getBean().isAnula() ||
                        (beanItem.getBean().isEnviado() && !Role.isPrivileged()))) {
                    view.getModificarBtn().setEnabled(false);
                    view.getEliminarBtn().setEnabled(false);
                    view.getNuevoComprobante().setEnabled(false);
                } else {
                    view.getModificarBtn().setEnabled(true);
                    view.getEliminarBtn().setEnabled(true);
                    view.getNuevoComprobante().setEnabled(true);
                }
                view.setEnableFields(false);
                break;
        }
    }
}
