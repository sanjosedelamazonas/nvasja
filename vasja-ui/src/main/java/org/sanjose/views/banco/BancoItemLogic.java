package org.sanjose.views.banco;

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
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.SaldoChecker;
import org.sanjose.validator.TwoCombosValidator;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.sanjose.views.sys.ComprobanteWarnGuardar;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.sanjose.util.GenUtil.*;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class BancoItemLogic implements Serializable, ComprobanteWarnGuardar {


    private static final Logger log = LoggerFactory.getLogger(BancoItemLogic.class);
    protected ScpBancodetalle item;
    protected boolean isLoading = true;
    protected boolean isEdit = false;
    protected NavigatorViewing navigatorView;
    protected Character moneda;
    protected ScpBancocabecera bancocabecera;
    protected FieldGroup fieldGroup;
    protected BancoOperView view;
    private BeanItem<ScpBancodetalle> beanItem;
    private ProcUtil procUtil;
    private SaldoChecker saldoChecker;

    private Property.ValueChangeListener selProyectoTerceroValueChangeListener;

    public void init(BancoOperView view) {
        this.view = view;
        procUtil = MainUI.get().getProcUtil();
    }


    public void setupEditComprobanteView() {
        // Fecha
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        view.getDataFechaComprobante().setPropertyDataSource(prop);
        view.getDataFechaComprobante().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaComprobante().setResolution(Resolution.DAY);
        view.getDataFechaComprobante().addValueChangeListener(event -> {
            if (view.getDataFechaComprobante().getValue()!=null)
                DataFilterUtil.refreshComboBox(view.getSelCuenta(), "id.codCtacontable",
                    DataUtil.getBancoCuentas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo()),
                    "txtDescctacontable");
            setCuentaLogic();
            setSaldos();
            refreshProyectoYcuentaPorFecha((Date)event.getProperty().getValue());
        });

        //--------- CABEZA

        // Cuenta
        view.getNumVoucher().setEnabled(false);

        DataFilterUtil.bindComboBox(view.getSelCuenta(), "id.codCtacontable",
                DataUtil.getBancoCuentas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo()),
                "txtDescctacontable");
        view.getSelCuenta().addValueChangeListener(ev -> {
                    setCuentaLogic();
                    selProyectoTerceroValueChangeListener = new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                            setProyectoLogic(valueChangeEvent);
                        }
                    };
                    view.getSelProyectoTercero().addValueChangeListener(selProyectoTerceroValueChangeListener);
        });
        // Fecha Doc
        prop = new ObjectProperty<>(ts);
        view.getFechaDoc().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxCabeza(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        view.getSelCodAuxCabeza().addValueChangeListener(valueChangeEvent -> {
            if (valueChangeEvent.getProperty().getValue() != null)
                view.getSelResponsable().setValue(valueChangeEvent.getProperty().getValue());
        });

        view.getGlosaCabeza().addBlurListener(event -> {
           if (view.getGlosaCabeza().getValue() != null && GenUtil.strNullOrEmpty(view.getGlosaDetalle().getValue())) {
                view.getGlosaDetalle().setValue(view.getGlosaCabeza().getValue());
            }
        });
        view.getSaldoCuenta().setNullRepresentation("0.00");
        view.getGlosaCabeza().setMaxLength(150);
        view.getCheque().setMaxLength(20);

        view.getChkCobrado().setEnabled(false);

        // ------------ DETALLE


        view.getNumItem().setEnabled(false);
        view.getMontoTotal().setValue("0.00");

        view.getTipoProyectoTercero().addValueChangeListener(this::setTipoProyectoTerceroLogic);


        // Proyecto
       /* DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()),
                "txtDescproyecto");
        view.getSelProyectoTercero().addValueChangeListener(this::setProyectoLogic);

        // Tercero
        DataFilterUtil.bindComboBox(view.getSelTercero(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestino('3'),
                "txtNombredestino");
        view.getSelTercero().addValueChangeListener(this::setTerceroLogic);*/

        // Fuente
        DataFilterUtil.bindComboBox(view.getSelFuente(), "codFinanciera", view.getService().getFinancieraRepo().findAll(),
                "txtDescfinanciera");

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

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        view.getSelResponsable().addValueChangeListener(valueChangeEvent -> {
            if (valueChangeEvent.getProperty().getValue() != null)
                view.getSelCodAuxiliar().setValue(valueChangeEvent.getProperty().getValue());
        });

        // Lugar de gasto
        DataFilterUtil.bindComboBox(view.getSelLugarGasto(), "codContraparte", view.getService().getContraparteRepo().findAll(),
                "txtDescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "txtDescripcion");


        // Cta Contable
        DataFilterUtil.bindComboBox(view.getSelCtaContable(), "id.codCtacontable",
                view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        '0', 'N', GenUtil.getYear(view.getDataFechaComprobante().getValue()), "101%", "102%", "104%", "106%"),
                //getPlanRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""),
                "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.bindComboBox(view.getSelRubroInst(), "id.codCtaespecial",
                view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getYear(view.getDataFechaComprobante().getValue())),
                "txtDescctaespecial");

        // Rubro Proy
        DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getYear(view.getDataFechaComprobante().getValue())),
                "txtDescctaproyecto");


        DataFilterUtil.bindComboBox(view.getSelTipoMov(), view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true), "Tipo Movimiento",
                "codTipocuenta", "txtTipocuenta", "id");

        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findById(Integer.parseInt(tipoMov));
                if (config != null) {
                    view.getSelCtaContable().setValue(config.getCodCtacontablegasto());
                    view.getSelRubroInst().setValue(config.getCodCtaespecial());
                }
            }
        });
        view.getGlosaDetalle().setMaxLength(70);
        view.getSerieDoc().setMaxLength(5);
        view.getNumDoc().setMaxLength(20);

        addValidators();
        // Editing Destino
        view.getBtnDestino().addClickListener(event -> editDestino(view.getSelCodAuxiliar()));
        view.getBtnResponsable().addClickListener(event -> editDestino(view.getSelResponsable()));
        view.getBtnAuxiliar().addClickListener(event -> editDestino(view.getSelCodAuxCabeza()));
        view.getTipoProyectoTercero().select(GenUtil.T_PROY);
    }

    public void addValidators() {
        // Validators
        view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "fecFecha"));
        view.getFechaDoc().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "fecComprobantepago"));
        //view.getSelProyectoTercero().addValidator(new TwoCombosValidator(view.getSelTercero(), true, null));
        //view.getSelTercero().addValidator(new TwoCombosValidator(view.getSelProyectoTercero(), true, null));
        view.getNumIngreso().setDescription("Ingreso");
        view.getNumEgreso().setDescription("Egreso");
        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "codDestino"));
        view.getSelLugarGasto().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "codContraparte"));
        view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "codDestinoitem"));
        view.getGlosaCabeza().setDescription("Glosa Cabeza");
        view.getGlosaCabeza().addValidator(new LocalizedBeanValidator(ScpBancocabecera.class, "txtGlosa"));
        view.getGlosaDetalle().setDescription("Glosa Detalle");
        view.getGlosaDetalle().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "txtGlosaitem"));
        view.getSerieDoc().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "txtSeriecomprobantepago"));
        view.getNumDoc().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "txtComprobantepago"));
        view.getSelCtaContable().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "codContracta"));
        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(ScpBancodetalle.class, "codTipomov"));
        view.getSelCodAuxCabeza().addValidator(new LocalizedBeanValidator(ScpBancocabecera.class, "codDestino"));
        view.getSelCuenta().addValidator(new LocalizedBeanValidator(ScpBancocabecera.class, "codCtacontable"));
        // Check saldos and warn
        saldoChecker = new SaldoChecker(view.getNumEgreso(), view.getSaldoCuenta(), view.getSaldoProyPEN(), this);
        view.getNumEgreso().addBlurListener(event -> saldoChecker.check());
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

                            List<ScpBancodetalle> comprobantes = view.getService().getBancodetalleRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            List<ScpBancocabecera> cabeceras = view.getService().getBancocabeceraRep().findByCodDestino(codDestino);
                            if (comprobantes.isEmpty() && cabeceras.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (ScpBancodetalle vcb : comprobantes) {
                                    sb.append("\n").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha())
                                            .append(" ").append(vcb.getTxtGlosaitem());
                                }
                                for (ScpBancocabecera vcb : cabeceras) {
                                    sb.append("\n").append(vcb.getTxtCorrelativo()).append(" ").
                                            append(vcb.getCodBancocabecera()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosa());
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
        DataFilterUtil.refreshComboBox(view.getSelResponsable(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxCabeza(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
    }

    private void refreshProyectoYcuentaPorFecha(Date newFecha) {
        if (newFecha==null || view.getService().getPlanRepo()==null) return;
        DataFilterUtil.refreshComboBox(view.getSelCuenta(),
                DataUtil.getBancoCuentas(newFecha, view.getService().getPlanRepo()),
                "id.codCtacontable","txtDescctacontable", null);
        if (view.getSelCtaContable()!=null)
            DataFilterUtil.refreshComboBox(view.getSelCtaContable(),view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                    '0', 'N', GenUtil.getYear(newFecha), "101%", "102%", "104%", "106%"),
                    "id.codCtacontable", "txtDescctacontable", null);
        if (isProyecto())
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().
                            findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(newFecha, newFecha, GenUtil.getBegin20thCent()),
                    "Sel Proyecto", "txtDescproyecto");
        //view.getSelProyectoTercero().addValueChangeListener(this::setProyectoLogic);
    }

    private void setTipoProyectoTerceroLogic(Property.ValueChangeEvent event) {
        if (fieldGroup==null) return;
        if (isProyecto()) {
            beanItem.getBean().setCodTercero("");
            fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
            view.getSelProyectoTercero().removeValueChangeListener(selProyectoTerceroValueChangeListener);
            selProyectoTerceroValueChangeListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    setProyectoLogic(valueChangeEvent);
                }
            };
            view.getSelProyectoTercero().addValueChangeListener(selProyectoTerceroValueChangeListener);
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codProyecto", view.getService().getProyectoRepo().
                            findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(view.getDataFechaComprobante().getValue(), view.getDataFechaComprobante().getValue(), GenUtil.getBegin20thCent()),
                    "Sel Proyecto", "txtDescproyecto");
        } else if (isTercero()) {
            beanItem.getBean().setCodProyecto("");
            fieldGroup.bind(view.getSelProyectoTercero(), "codTercero");
            view.getSelProyectoTercero().removeValueChangeListener(selProyectoTerceroValueChangeListener);
            selProyectoTerceroValueChangeListener = new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                    setTerceroLogic(valueChangeEvent);
                }
            };
            view.getSelProyectoTercero().addValueChangeListener(selProyectoTerceroValueChangeListener);
            DataFilterUtil.bindComboBox(view.getSelProyectoTercero(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestino('3'), "Sel Tercero",
                    "txtNombredestino");
        }
    }

    protected void setCuentaLogic() {
        if (view.getDataFechaComprobante().getValue() != null && view.getSelCuenta().getValue() != null) {
            ScpPlancontable cuenta = view.getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                    GenUtil.getYear(view.getDataFechaComprobante().getValue()), view.getSelCuenta().getValue().toString());
            BigDecimal saldo = procUtil.getSaldoBanco(GenUtil.getEndOfDay(GenUtil.dateAddDays(view.getDataFechaComprobante().getValue(),-1)),
                    view.getSelCuenta().getValue().toString(), GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            view.getSaldoCuenta().setCaption(GenUtil.getSymMoneda(cuenta.getIndTipomoneda()));
            log.debug("In setCuentaLogic: " + df.format(saldo));
            view.getSaldoCuenta().setValue(df.format(saldo));
            moneda = GenUtil.getNumMoneda(cuenta.getIndTipomoneda());
            // If still no item created
            if (item == null) {
                nuevoComprobante(moneda);
            } else {
                item.setCodTipomoneda(moneda);
            }
            setMonedaLogic(moneda);
        }
    }

    private void setMonedaLogic(Character moneda) {
        log.debug("in moneda logic: " + isLoading + " " + moneda);
        if (!isLoading) {
            String oldNumEgreso = view.getNumEgreso().getValue();
            String oldNumIngreso = view.getNumIngreso().getValue();
            try {
                fieldGroup.unbind(view.getNumEgreso());
                fieldGroup.unbind(view.getNumIngreso());
            } catch (FieldGroup.BindException be) {
            }
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                log.debug("in moneda logic set PEN");
                beanItem.getBean().setNumHaberdolar(new BigDecimal(0));
                beanItem.getBean().setNumDebedolar(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                fieldGroup.bind(view.getNumEgreso(), "numHabersol");
                fieldGroup.bind(view.getNumIngreso(), "numDebesol");
                saldoChecker.setProyectoField(view.getSaldoProyPEN());
            } else if (moneda.equals(USD)) {
                // Dolares
                // Cta Caja
                log.debug("in moneda logic set USD");
                beanItem.getBean().setNumHabersol(new BigDecimal(0));
                beanItem.getBean().setNumDebesol(new BigDecimal(0));
                beanItem.getBean().setNumHabermo(new BigDecimal(0));
                beanItem.getBean().setNumDebemo(new BigDecimal(0));
                fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
                fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
                saldoChecker.setProyectoField(view.getSaldoProyUSD());
            } else {
                // Euros
                // Cta Caja
                log.debug("in moneda logic set EUR");
                beanItem.getBean().setNumHaberdolar(new BigDecimal(0));
                beanItem.getBean().setNumDebedolar(new BigDecimal(0));
                beanItem.getBean().setNumHabersol(new BigDecimal(0));
                beanItem.getBean().setNumDebesol(new BigDecimal(0));
                fieldGroup.bind(view.getNumEgreso(), "numHabermo");
                fieldGroup.bind(view.getNumIngreso(), "numDebemo");
                saldoChecker.setProyectoField(view.getSaldoProyEUR());
            }
            view.getNumEgreso().setEnabled(true);
            view.getNumIngreso().setEnabled(true);
            // copy values to new field
            view.getNumEgreso().setValue(oldNumEgreso);
            view.getNumIngreso().setValue(oldNumIngreso);
            ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
            ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
        }
    }

    private void setSaldos() {
        if (view.getDataFechaComprobante().getValue() != null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            ProcUtil.Saldos res = null;
            if (isProyecto() && !GenUtil.objNullOrEmpty(view.getSelProyectoTercero().getValue())) {
                res = procUtil.getSaldos(view.getDataFechaComprobante().getValue(), view.getSelProyectoTercero().getValue().toString(), null);
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue(df.format(res.getSaldoEUR()));
            }
            if (isTercero() && !GenUtil.objNullOrEmpty(view.getSelProyectoTercero().getValue())) {
                res = procUtil.getSaldos(view.getDataFechaComprobante().getValue(), null, view.getSelProyectoTercero().getValue().toString());
                view.getSaldoProyPEN().setValue(df.format(res.getSaldoPEN()));
                view.getSaldoProyUSD().setValue(df.format(res.getSaldoUSD()));
                view.getSaldoProyEUR().setValue(df.format(res.getSaldoEUR()));

            }
            saldoChecker.check();
        }
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null)
            setEditorProyectoLogic(event.getProperty().getValue().toString());
        //view.getSelProyectoTercero().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    private void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorTerceroLogic(event.getProperty().getValue().toString());
          //  view.getSelTercero().getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
        }
    }

    private void setEditorTerceroLogic(String codTercero) {
        if (!GenUtil.strNullOrEmpty(codTercero)) {
            view.setEnableDetalleFields(true);
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(),
                    view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaTercero(true, true, true),
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
        }
    }

    private void setEditorProyectoLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableDetalleFields(true);
            DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                    view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getYear(view.getDataFechaComprobante().getValue()), codProyecto),
                    "Rubro proyecto", "txtDescctaproyecto");
            DataUtil.setupAndBindproyectoPorFinanciera(codProyecto, view.getSelFuente(),
                    view.getService().getProyectoPorFinancieraRepo(), view.getService().getFinancieraRepo());

                // Sel Tipo Movimiento
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(),
                    view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true),
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
        } else {
            //log.info("disabling fin y planproy");
            view.getSelFuente().setEnabled(false);
            view.getSelFuente().setValue("");
            view.getSelRubroProy().setEnabled(false);
            view.getSelRubroProy().setValue("");
        }
    }

    public void bindForm(ScpBancodetalle item) {
        isLoading = true;
        isEdit = !GenUtil.objNullOrEmpty(item.getId());
        beanItem = new BeanItem<>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        if (!GenUtil.strNullOrEmpty(item.getCodProyecto()) && !GenUtil.strNullOrEmpty(item.getCodTercero())) {
            log.error("Problema con esta operacion " + item.getScpBancocabecera().getCodBancocabecera() + " - codigo proyecto y codigo tercero son rellenadas!");
        }
        if (!GenUtil.strNullOrEmpty(item.getCodTercero())) {
            view.getTipoProyectoTercero().select(GenUtil.T_TERC);
        } else {
            view.getTipoProyectoTercero().select(GenUtil.T_PROY);
        }
        /*fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
        fieldGroup.bind(view.getSelTercero(), "codTercero");*/
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
        fieldGroup.bind(view.getGlosaDetalle(), "txtGlosaitem");
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
            fieldGroup.bind(view.getSelProyectoTercero(), "codTercero");
        } else {
            fieldGroup.bind(view.getSelProyectoTercero(), "codProyecto");
        }

        for (Field f : fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField) f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox) f).setPageLength(25);
        }
        view.getTipoProyectoTercero().setEnabled(true);
        view.getSelProyectoTercero().setEnabled(true);
        //view.getSelTercero().setEnabled(true);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            log.debug("is Edit in bindForm");
            setNumVoucher(item);
            setCuentaLogic();
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorProyectoLogic(item.getCodProyecto());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
            }
        } else if (item.getScpBancocabecera() != null) {
            if (item.getId() == null && item.getScpBancocabecera().getTxtCorrelativo() != null) {
                log.debug("is NOT Edit in bindForm but ID is null");
                view.getNumVoucher().setValue(item.getScpBancocabecera().getTxtCorrelativo());
                view.getNumItem().setValue(String.valueOf(view.getContainer().size() + 1));
            }
        }
        setSaldos();
        isEdit = false;
    }

    protected void setNumVoucher(ScpBancodetalle item) {
        if (item.getScpBancocabecera() != null && !GenUtil.strNullOrEmpty(item.getScpBancocabecera().getTxtCorrelativo())) {
            view.getNumVoucher().setValue(item.getScpBancocabecera().getTxtCorrelativo());
            view.getNumItem().setValue(String.valueOf(item.getId().getNumItem()));
        }
        view.getNumVoucher().setEnabled(false);
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView == null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void nuevoComprobante(Character moneda) {
        ScpBancodetalle vcb = new ScpBancodetalle();
        if (bancocabecera != null)
            vcb.setScpBancocabecera(bancocabecera);
        vcb.setCodTipomoneda(moneda);
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        item = vcb;
        view.setEnableCabezeraFields(true);
        view.setEnableDetalleFields(false);
        view.getTipoProyectoTercero().setEnabled(true);
        view.getSelProyectoTercero().setEnabled(true);
        //view.getSelTercero().setEnabled(true);
    }

    public void setNavigatorView(NavigatorViewing navigatorView) {
        this.navigatorView = navigatorView;
    }

    // Helpers

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(view.getTipoProyectoTercero().getValue())
                && view.getTipoProyectoTercero().getValue().equals(GenUtil.T_PROY);
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.getTipoProyectoTercero().getValue())
                && view.getTipoProyectoTercero().getValue().equals(GenUtil.T_TERC);
    }

    protected void clearSaldos() {
        //noinspection unchecked
        Arrays.stream(new Field[]{view.getSaldoCuenta(), view.getSaldoProyPEN(), view.getSaldoProyUSD(), view.getSaldoProyEUR()})
                .forEach(f -> f.setValue(""));
    }

    ScpBancodetalle getScpBancodetalle() throws CommitException {
        if (fieldGroup==null || beanItem==null || beanItem.getBean()==null)
            throw new CommitException("El cheque debe tener operaciones rellenadas");
        fieldGroup.commit();
        ScpBancodetalle item = beanItem.getBean();
        log.debug("got from getDetalle " + item);
        return item;
    }

    @Override
    public void addWarningToGuardarBtn(boolean isWarn) {
        //TODO Implement Warning when Saving!!!
    }
}
