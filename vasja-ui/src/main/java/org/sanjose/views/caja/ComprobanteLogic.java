package org.sanjose.views.caja;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.SaldoChecker;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.sanjose.views.sys.ComprobanteWarnGuardar;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.Viewing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static org.sanjose.util.GenUtil.*;
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
class ComprobanteLogic implements Serializable, ComprobanteWarnGuardar {

	private static final Logger log = LoggerFactory.getLogger(ComprobanteLogic.class);

    protected ComprobanteViewing view;
    protected NavigatorViewing navigatorView;
    protected FieldGroup fieldGroup;
    protected BeanItem<ScpCajabanco> beanItem;
    private ScpCajabanco savedCajabanco;
    private boolean isLoading = false;
    private boolean isEdit = false;
    private ProcUtil procUtil;
    private Viewing.Mode mode;
    private SaldoChecker saldoChecker;
    private Button.ClickListener guardarBtnListner;

    private Property.ValueChangeListener selProyectoTerceroChangeListener;

    public void init(ComprobanteViewing comprobanteView) {
        view = comprobanteView;
        addWarningToGuardarBtn(true);
        view.getNuevoComprobante().addClickListener(event -> nuevoComprobante());
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirBtn().addClickListener(event -> {
            if (savedCajabanco!=null) {
                if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT"))
                    ViewUtil.printComprobante(savedCajabanco);
                else
                    ReportHelper.generateComprobante(savedCajabanco);
            }
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

    public void addWarningToGuardarBtn(boolean isWarn) {
        view.getGuardarBtn().removeClickListener(guardarBtnListner);
        if (isWarn) {
            guardarBtnListner = new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                    MessageBox
                            .createQuestion()
                            .withCaption("Atencion!")
                            .withMessage("La caja o proyecto/tercero no tiene suficiente recursos.\nEsta seguro que lo quiere guardar?")
                            .withYesButton(() ->  saveComprobante())
                            .withNoButton()
                            .open();
                }
            };
        } else {
            guardarBtnListner = new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    saveComprobante();
                }
            };
        }
        view.getGuardarBtn().addClickListener(guardarBtnListner);
    }

    void closeWindow() {
        if (view.getSubWindow()!=null)
            view.getSubWindow().close();
    }

    void anular() {
        if (mode.equals(NEW)) {
            switchMode(Viewing.Mode.EMPTY);
        } else {
            switchMode(Viewing.Mode.VIEW);
        }
        closeWindow();
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView == null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void saveComprobante() {
        try {
            ScpCajabanco item = getVsjCajabanco().prepareToSave();

            savedCajabanco = view.getService().save(item);

            view.getNumVoucher().setValue(savedCajabanco.getTxtCorrelativo());
            view.refreshData(item.getCodTipomoneda());

            switchMode(Viewing.Mode.VIEW);
            navigatorView.selectMoneda(savedCajabanco.getCodTipomoneda());
            navigatorView.selectItem(savedCajabanco);
            // Open Comprobante only in Gilmer view
            if (navigatorView instanceof CajaManejoView) {
                ViewUtil.printComprobante(savedCajabanco);
                closeWindow();
            }
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
        ScpCajabanco vcb = new ScpCajabanco();
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

    public void editarComprobante(ScpCajabanco vcb) {
        savedCajabanco = vcb;
        bindForm(vcb);
        if (vcb.isReadOnly())
            switchMode(Viewing.Mode.VIEW);
        else
            switchMode(Viewing.Mode.EDIT);
    }

    public void viewComprobante(ScpCajabanco vcb) {
        savedCajabanco = vcb;
        bindForm(vcb);
    }

    void eliminarComprobante() {
        if (savedCajabanco == null)
            return;
        if (savedCajabanco.isEnviado()) {
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
                .withMessage("?Esta seguro que quiere eliminar este comprobante?")
                .withYesButton(this::doEliminarComprobante)
                .withNoButton()
                .open();
    }

    void doEliminarComprobante() {
        try {
            ScpCajabanco item = getVsjCajabanco().prepareToEliminar();
            view.getGlosa().setValue(item.getTxtGlosaitem());
            log.info("Ready to ANULAR: " + item);
            savedCajabanco = view.getService().getCajabancoRep().save(item);
            view.getNumVoucher().setValue(Integer.toString(savedCajabanco.getCodCajabanco()));
            savedCajabanco = null;
            switchMode(Viewing.Mode.VIEW);
            view.refreshData(item.getCodTipomoneda());
            MessageBox
                    .createInfo()
                    .withCaption("Elminado correctamente")
                    .withMessage("El comprobante ha sido eliminado.")
                    .withOkButton(this::closeWindow)
                    .open();
        } catch (CommitException ce) {
            log.info("Got Commit Exception al eliminar comprobante: " + ce.getMessage());
            MessageBox
                    .createError()
                    .withCaption("Error al anular el comprobante:")
                    .withMessage(ce.getLocalizedMessage())
                    .withOkButton(this::closeWindow)
                    .open();
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
            Date newFecha = (Date)event.getProperty().getValue();
            setSaldoCaja();
            setSaldos();
            //view.setSaldoDeCajas();
            // Reload cajas depending on the Year of comprobante
            refreshProyectoYcuentaPorFecha(newFecha);
        });
        view.getDataFechaComprobante().focus();

        // Fecha Doc
        prop = new ObjectProperty<>(ts);
        view.getFechaDoc().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);


        //if (GenUtil.strNullOrEmpty())
        // Proyecto
/*        DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()),
                "Sel Proyecto", "txtDescproyecto");
        view.getTipoProyectoTercero().addValueChangeListener(this::setTipoProyectoTerceroLogic);
        view.getSelProyectoTercero().addValueChangeListener(this::setProyectoLogic);*/

        // Tercero
       // DataFilterUtil.bindComboBox(view.getSelTercero(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestino('3'), "Sel Tercero",
       //         "txtNombredestino");
       // view.getSelTercero().addValueChangeListener(this::setTerceroLogic);
        //view.getTipoProyectoTercero().addValueChangeListener(this::setTipoProyectoTerceroLogic);

        view.getTipoProyectoTercero().addValueChangeListener(this::setTipoProyectoTerceroLogic);

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
        view.getSelCaja().setNullSelectionAllowed(false);

        List<ScpDestino> destinoList = DataUtil.loadDestinos(view.getService());
        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable(), "codDestino", destinoList,
                "Responsable", "txtNombre");

        view.getSelResponsable().addValueChangeListener(valueChangeEvent ->  {
            if (valueChangeEvent.getProperty().getValue()!=null)
                view.getSelCodAuxiliar().setValue(valueChangeEvent.getProperty().getValue());
        });

        // Lugar de gasto
        DataFilterUtil.bindComboBox(view.getSelLugarGasto(), "codContraparte", view.getService().getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txtDescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", destinoList,
                "Auxiliar", "txtNombre");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "Sel Tipo", "txtDescripcion");

        // Cta Contable
        DataFilterUtil.bindComboBox(view.getSelCtaContable(), "id.codCtacontable",
                view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        '0', 'N', GenUtil.getCurYear(), "101%", "102%", "104%", "106%"),
                //getPlanRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""),
                "Sel cta contable", "txtDescctacontable");
        view.getSelCtaContable().setNullSelectionAllowed(false);

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
        view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "fecFecha"));
        view.getFechaDoc().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "fecComprobantepago"));

        //view.getSelProyectoTercero().addValidator(new TwoCombosValidator(view.getSelTercero(), true, null));
        ///view.getSelTercero().addValidator(new TwoCombosValidator(view.getSelProyectoTercero(), true, null));
        view.getSelMoneda().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codTipomoneda"));
        view.getNumIngreso().setDescription("Ingreso");
        view.getNumEgreso().setDescription("Egreso");
        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codDestino"));
        view.getSelLugarGasto().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codContraparte"));
        view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codDestinoitem"));
        view.getGlosa().setDescription("Glosa");
        view.getGlosa().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "txtGlosaitem"));
        view.getSerieDoc().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "txtSeriecomprobantepago"));
        view.getNumDoc().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "txtComprobantepago"));
        view.getSelCtaContable().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codContracta"));
        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codTipomov"));

        // Check saldos and warn
        saldoChecker = new SaldoChecker(view.getNumEgreso(), view.getSaldoCaja(), view.getSaldoProyPEN(), this);
        view.getNumEgreso().addBlurListener(event -> saldoChecker.check());

        // Editing Destino
        view.getBtnDestino().addClickListener(event->editDestino(view.getSelCodAuxiliar()));
        view.getBtnResponsable().addClickListener(event->editDestino(view.getSelResponsable()));
        view.setEnableFields(false);
        //nuevoComprobante();
        view.getTipoProyectoTercero().select(GenUtil.T_PROY);

        // Skip Tab for certain components
        view.getBtnResponsable().setTabIndex(-1);
        view.getBtnDestino().setTabIndex(-1);
        view.getSelCaja().setTabIndex(-1);
        view.getSelCtaContable().setTabIndex(-1);

    }

    private void editDestino(ComboBox comboBox) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setDraggable(true);
        destinoWindow.setWidth(700, Sizeable.Unit.PIXELS);
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

                            List<ScpCajabanco> comprobantes = view.getService().getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (ScpCajabanco vcb : comprobantes) {
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
        DataFilterUtil.refreshComboBox(view.getSelResponsable(), "codDestino", DataUtil.loadDestinos(view.getService()),
                "txtNombre");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", DataUtil.loadDestinos(view.getService()),
                "txtNombre");
    }

    private void refreshProyectoYcuentaPorFecha(Date newFecha) {
        if (newFecha==null)
            newFecha = new Date();
        if (newFecha==null || view.getService().getPlanRepo()==null) return;
        if (view.getSelCaja()!=null)
            DataFilterUtil.refreshComboBox(
                    view.getSelCaja(), DataUtil.getCajas(newFecha, view.getService().getPlanRepo(), PEN),
                    "id.codCtacontable", "txtDescctacontable", null);
        if (view.getSelCtaContable()!=null)
            DataFilterUtil.refreshComboBox(view.getSelCtaContable(),view.getService().getPlanRepo().
                        findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                '0', 'N', GenUtil.getYear(newFecha), "101%", "102%", "104%", "106%"),
                "id.codCtacontable", "txtDescctacontable", null);
        //TODO Proy
        if (isProyecto())
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().
                        findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(newFecha, newFecha, GenUtil.getBegin20thCent()),
                "Sel Proyecto", "txtDescproyecto");
        //view.getSelProyectoTercero().addValueChangeListener(this::setProyectoLogic);
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
            view.getSaldoProyPEN().removeStyleName("yield");
            view.getSaldoProyUSD().removeStyleName("yield");
            view.getSaldoProyEUR().removeStyleName("yield");
            Date fechaCajas = view.getDataFechaComprobante().getValue()!=null ? view.getDataFechaComprobante().getValue() : new Date();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                beanItem.getBean().setNumHaberdolar(new BigDecimal(0));
                beanItem.getBean().setNumDebedolar(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                DataFilterUtil.refreshComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(fechaCajas, view.getService().getPlanRepo(), moneda), "txtDescctacontable");
                fieldGroup.bind(view.getNumEgreso(), "numHabersol");
                fieldGroup.bind(view.getNumIngreso(), "numDebesol");
                saldoChecker.setProyectoField(view.getSaldoProyPEN());
            } else if (moneda.equals(USD)) {
                // Dolares
                // Cta Caja
                beanItem.getBean().setNumHabersol(new BigDecimal(0));
                beanItem.getBean().setNumDebesol(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                DataFilterUtil.refreshComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(fechaCajas, view.getService().getPlanRepo(), moneda), "txtDescctacontable");
                fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
                fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
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
                saldoChecker.setProyectoField(view.getSaldoProyEUR());
            }
            view.getLblSaldo().setValue("Saldo de caja ("+ GenUtil.getSymMoneda(getLitMoneda(moneda))+")");

            // copy values to new field
            view.getNumEgreso().setValue(oldNumEgreso);
            view.getNumIngreso().setValue(oldNumIngreso);
            //
            if (savedCajabanco != null && !GenUtil.objNullOrEmpty(savedCajabanco.getCodCtacontable()) && moneda.equals(savedCajabanco.getCodTipomoneda())) {
                view.getSelCaja().select(savedCajabanco.getCodCtacontable());
            } else {
                setCajaLogic(moneda);
            }
            view.getSelCaja().addValidator(new LocalizedBeanValidator(ScpCajabanco.class, "codCtacontable"));
            ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
            ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
            view.setSaldoDeCajas();
            saldoChecker.check();
        }
    }


    private void setSaldos() {
        if (view.getDataFechaComprobante().getValue()!=null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            Date fechaCajas = view.getDataFechaComprobante().getValue()!=null ? view.getDataFechaComprobante().getValue() : new Date();
            ProcUtil.Saldos res = null;
            if (isProyecto() && !GenUtil.objNullOrEmpty(view.getSelProyectoTercero().getValue())) {
                res = procUtil.getSaldos(fechaCajas, view.getSelProyectoTercero().getValue().toString(), null);
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue(df.format(res.getSaldoEUR()));
            }
            if (isTercero() && !GenUtil.objNullOrEmpty(view.getSelProyectoTercero().getValue())) {
                res = procUtil.getSaldos(fechaCajas, null, view.getSelProyectoTercero().getValue().toString());
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue(df.format(res.getSaldoEUR()));

            }
        }
    }

    private void setSaldoCaja() {
        if (view.getDataFechaComprobante().getValue()!=null && view.getSelCaja().getValue()!=null && view.getSelMoneda().getValue()!=null) {
            BigDecimal saldo = procUtil.getSaldoCaja(view.getDataFechaComprobante().getValue(),
                    view.getSelCaja().getValue().toString(), view.getSelMoneda().getValue().toString().charAt(0));
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());

            view.getSaldoCaja().setValue(df.format(saldo));
            saldoChecker.setSaldoField(view.getSaldoCaja());

            /*if (PEN.equals(view.getSelMoneda().getValue().toString().charAt(0))) {
                view.getSaldoCaja().setValue(saldo.toString());
                view.getSaldoCajaUSD().setValue("");
                view.getSaldoCajaEUR().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCaja());
            } else if (USD.equals(view.getSelMoneda().getValue().toString().charAt(0))) {
                view.getSaldoCajaUSD().setValue(saldo.toString());
                view.getSaldoCaja().setValue("");
                view.getSaldoCajaEUR().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCajaUSD());
            } else {
                view.getSaldoCajaEUR().setValue(saldo.toString());
                view.getSaldoCaja().setValue("");
                view.getSaldoCajaUSD().setValue("");
                saldoChecker.setSaldoField(view.getSaldoCajaEUR());
            }*/
        } else {
            view.getSaldoCaja().setValue("");
        }
    }

    private void setCajaLogic(Character tipomoneda) {
        VsjConfiguracioncaja config = null;
        if (isProyecto()) {
            List<VsjConfiguracioncaja> configs = view.getService().getConfiguracioncajaRepo().findByCodProyectoAndIndTipomoneda(
                    view.getSelProyectoTercero().getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                config = configs.get(0);
            } else {
                String catProy = view.getService().getProyectoRepo().findByCodProyecto(view.getSelProyectoTercero().getValue().toString())
                        .getCodCategoriaproyecto();
                configs = view.getService().getConfiguracioncajaRepo().findByCodCategoriaproyectoAndIndTipomoneda(
                        catProy, tipomoneda);
                if (!configs.isEmpty()) {
                    config = configs.get(0);
                }
            }
        } else if (isTercero()) {
            List<VsjConfiguracioncaja> configs = view.getService().getConfiguracioncajaRepo().findByCodDestinoAndIndTipomoneda(
                    view.getSelProyectoTercero().getValue().toString(), tipomoneda);
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

    private void setTipoProyectoTerceroLogic(Property.ValueChangeEvent event) {
        if (fieldGroup==null) return;
        view.getSelProyectoTercero().removeValueChangeListener(selProyectoTerceroChangeListener);
        if (isProyecto()) {
            beanItem.getBean().setCodTercero("");
            //fieldGroup.unbind(view.getSelProyectoTercero());
            fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
            selProyectoTerceroChangeListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    setProyectoLogic(valueChangeEvent);
                }
            };
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().
                            findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(view.getDataFechaComprobante().getValue(), view.getDataFechaComprobante().getValue(), GenUtil.getBegin20thCent()),
                    "Sel Proyecto", "txtDescproyecto");
        } else if (isTercero()) {
            beanItem.getBean().setCodProyecto("");
            //fieldGroup.unbind(view.getSelProyectoTercero());
            fieldGroup.bind(view.getSelProyectoTercero(), "codTercero");
            selProyectoTerceroChangeListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    setTerceroLogic(valueChangeEvent);
                }
            };
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codDestino", DataUtil.loadDestinos(view.getService()), "Sel Tercero",
                     "txtNombre");
        }
        view.getSelProyectoTercero().addValueChangeListener(selProyectoTerceroChangeListener);
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorProyectoLogic(event.getProperty().getValue().toString());
            //view.getSelTercero().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
        }
    }

    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorTerceroLogic(event.getProperty().getValue().toString());
            //view.getSelTercero().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
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
            // For Tercero select first movimiento in the list automatically
            for (Object itId : view.getSelTipoMov().getItemIds()) {
                view.getSelTipoMov().select(itId);
                break;
            }
            setSaldos();
            setMonedaLogic(view.getSelMoneda().getValue().toString().charAt(0));
            //saldoChecker.setProyectoField(view.getSelMoneda().getValue().equals('0') ? view.getSaldoProyPEN() : view.getSaldoCajaUSD());
        }
    }

    private void setEditorProyectoLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableFields(true);
            view.getSelFuente().setEnabled(true);
            DataFilterUtil.refreshComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                    view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "txtDescctaproyecto");

            DataUtil.setupAndBindproyectoPorFinanciera(codProyecto, view.getSelFuente(),
                    view.getService().getProyectoPorFinancieraRepo(), view.getService().getFinancieraRepo());
            fieldGroup.bind(view.getSelFuente(), "codFinanciera");
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

    private void bindForm(ScpCajabanco item) {
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
        view.getNumEgreso().setValue(null);
        view.getNumIngreso().setValue(null);

        if (!GenUtil.strNullOrEmpty(item.getCodProyecto()) && !GenUtil.strNullOrEmpty(item.getCodTercero())) {
            log.error("Problema con esta operacion " + item.getCodCajabanco() + " - codigo proyecto y codigo tercero son rellenadas!");
            //throw new CommitException("Problema con esta operacion codigo proyecto y codigo tercero son rellenadas!");
        }
        if (!GenUtil.strNullOrEmpty(item.getCodTercero())) {
            view.getTipoProyectoTercero().select(GenUtil.T_TERC);
        } else {
            view.getTipoProyectoTercero().select(GenUtil.T_PROY);
        }
        setTipoProyectoTerceroLogic(null);
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
        if (!GenUtil.strNullOrEmpty(item.getCodTercero())) {
          //  view.getTipoProyectoTercero().select(GenUtil.T_TERC);
            fieldGroup.bind(view.getSelProyectoTercero(), "codTercero");
        } else {
            //view.getTipoProyectoTercero().select(GenUtil.T_PROY);
            fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
        }


        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox) f).setPageLength(25);
        }
        view.setEnableFields(false);
        view.getSelProyectoTercero().setEnabled(true);
        view.getTipoProyectoTercero().setEnabled(true);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                view.getNumVoucher().setValue(item.getTxtCorrelativo());
            }
            // Refresh CtaContable for actual year
            //refreshProyectoYcuentaPorFecha(item.getFecFecha());
            view.setEnableFields(true);
            //setSaldos();
            setSaldoCaja();
            setSaldoCaja();
            //setTipoProyectoTerceroLogic(null);
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorProyectoLogic(item.getCodProyecto());
                //view.getTipoProyectoTercero().select(item.getCodProyecto());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
                //view.getTipoProyectoTercero().select(item.getCodTercero());
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
        return !GenUtil.objNullOrEmpty(view.getTipoProyectoTercero().getValue())
                && view.getTipoProyectoTercero().getValue().equals(GenUtil.T_PROY);
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.getTipoProyectoTercero().getValue())
                && view.getTipoProyectoTercero().getValue().equals(GenUtil.T_TERC);
    }

    private void clearSaldos() {
        //noinspection unchecked
        Arrays.stream(new Field[]{view.getSaldoCaja(), view.getSaldoProyPEN(), view.getSaldoProyUSD(), view.getSaldoProyEUR()})
                .forEach(f -> f.setValue(""));
    }

    ScpCajabanco getVsjCajabanco() throws FieldGroup.CommitException {
        fieldGroup.commit();
        ScpCajabanco item = beanItem.getBean();
        view.setEnableFields(false);
        return item;
    }

    protected void switchMode(Viewing.Mode newMode) {
        mode = newMode;
        switch (newMode) {
            case EMPTY:
                view.getCerrarBtn().setEnabled(true);
                view.getGuardarBtn().setEnabled(false);
                view.getAnularBtn().setEnabled(true);
                view.getModificarBtn().setEnabled(false);
                view.getEliminarBtn().setEnabled(false);
                view.getNuevoComprobante().setEnabled(true);
                view.getImprimirBtn().setEnabled(false);

                view.getSelProyectoTercero().setEnabled(false);
                view.getTipoProyectoTercero().setEnabled(false);
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

                view.getSelProyectoTercero().setEnabled(true);
                view.getTipoProyectoTercero().setEnabled(true);
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
                view.getAnularBtn().setEnabled(true);
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
