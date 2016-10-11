package org.sanjose.views.banco;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.BeanValidator;
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
import org.sanjose.validator.TwoCombosValidator;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.INavigatorView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.sanjose.util.GenUtil.*;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class BancoItemLogic implements Serializable {


	private static final Logger log = LoggerFactory.getLogger(BancoItemLogic.class);
    protected VsjBancodetalle item;
    protected boolean isLoading = true;
    protected boolean isEdit = false;
    protected INavigatorView navigatorView;
    protected Character moneda;
    protected VsjBancocabecera bancocabecera;
    protected FieldGroup fieldGroup;
    BancoOperView view;
    private VsjBancodetalle savedBancodetalle;
    private BeanItem<VsjBancodetalle> beanItem;
    private ProcUtil procUtil;

    public void init(BancoOperView view) {
        this.view = view;
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirTotalBtn().addClickListener(event -> {
          //  if (savedBancodetalle!=null) ViewUtil.printComprobante(savedBancodetalle);
        });
        view.getModificarBtn().addClickListener(event -> editarComprobante());
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
            setCuentaLogic();
            setSaldos();
        });

        //--------- CABEZA

        // Cuenta
        view.getNumVoucher().setEnabled(false);

        DataFilterUtil.bindComboBox(view.getSelCuenta(), "id.codCtacontable",
                DataUtil.getBancoCuentas(view.getDataFechaComprobante().getValue(), view.getService().getPlanRepo()),
                "txtDescctacontable");
        view.getSelCuenta().addValueChangeListener(ev -> setCuentaLogic());
        // Fecha Doc
        prop = new ObjectProperty<>(ts);
        view.getFechaDoc().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxCabeza(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        view.getSelCodAuxCabeza().addValueChangeListener(valueChangeEvent -> {
            if (valueChangeEvent.getProperty().getValue()!=null)
                view.getSelResponsable().setValue(valueChangeEvent.getProperty().getValue());
        });

        view.getGlosaCabeza().addTextChangeListener(event -> {
            log.info("Text in cabeza: " + event.getText());
            if (event.getText()!=null && GenUtil.strNullOrEmpty(view.getGlosaDetalle().getValue())) {
                view.getGlosaDetalle().setValue(event.getText());
            }
        });

        // ------------ DETALLE

        // Proyecto
        DataFilterUtil.bindComboBox(view.getSelProyecto(), "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThan(new Date()),
                "txtDescproyecto");
        view.getSelProyecto().addValueChangeListener(this::setProyectoLogic);

        // Tercero
        DataFilterUtil.bindComboBox(view.getSelTercero(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestino('3'),
                "txtNombredestino");
        view.getSelTercero().addValueChangeListener(this::setTerceroLogic);

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

        view.getSelResponsable().addValueChangeListener(valueChangeEvent ->  {
            if (valueChangeEvent.getProperty().getValue()!=null)
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
                        '0','N', GenUtil.getYear(view.getDataFechaComprobante().getValue()), "101%", "102%", "104%", "106%"),
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

        DataFilterUtil.bindComboBox(view.getSelTipoMov(), "codTipocuenta", view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBanco(true, true),
                "txtTipocuenta");
        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findByCodTipocuenta(Integer.parseInt(tipoMov));
                view.getSelCtaContable().setValue(config.getCodCtacontablegasto());
                view.getSelRubroInst().setValue(config.getCodCtaespecial());
            }
        });
        view.getGlosaCabeza().setMaxLength(70);
        view.getGlosaDetalle().setMaxLength(70);

        addValidators();
        // Editing Destino
        view.getBtnDestino().addClickListener(event->editDestino(view.getSelCodAuxiliar()));
        view.getBtnResponsable().addClickListener(event->editDestino(view.getSelResponsable()));

        view.setEnableDetalleFields(false);
        view.setEnableCabezeraFields(false);
    }

    public void addValidators() {
        // Validators
        view.getDataFechaComprobante().addValidator(new BeanValidator(VsjBancodetalle.class, "fecFecha"));
        view.getFechaDoc().addValidator(new BeanValidator(VsjBancodetalle.class, "fecComprobantepago"));
        view.getSelProyecto().addValidator(new TwoCombosValidator(view.getSelTercero(), true, null));
        view.getSelTercero().addValidator(new TwoCombosValidator(view.getSelProyecto(), true, null));
        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable().addValidator(new BeanValidator(VsjBancodetalle.class, "codDestino"));
        view.getSelLugarGasto().addValidator(new BeanValidator(VsjBancodetalle.class, "codContraparte"));
        view.getSelCodAuxiliar().addValidator(new BeanValidator(VsjBancodetalle.class, "codDestinoitem"));
        view.getGlosaDetalle().addValidator(new BeanValidator(VsjBancodetalle.class, "txtGlosaitem"));
        view.getSerieDoc().addValidator(new BeanValidator(VsjBancodetalle.class, "txtSeriecomprobantepago"));
        view.getNumDoc().addValidator(new BeanValidator(VsjBancodetalle.class, "txtComprobantepago"));
        view.getSelCtaContable().addValidator(new BeanValidator(VsjBancodetalle.class, "codContracta"));
        view.getSelTipoMov().addValidator(new BeanValidator(VsjBancodetalle.class, "codTipomov"));
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
                //log.info("eliminar: " + item);
                String codDestino = item.getCodDestino();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtNombredestino())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            log.debug("To delete: " + item);

                            List<VsjBancodetalle> comprobantes = view.getService().getBancodetalleRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (VsjBancodetalle vcb : comprobantes) {
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
            } catch (CommitException ce) {
                Notification.show("Error al eliminar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.info("Got Commit Exception: " + ce.getMessage());
            }
            //destinoWindow.close();
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

    protected void setCuentaLogic() {
        if (view.getDataFechaComprobante().getValue()!=null && view.getSelCuenta().getValue()!=null) {
            ScpPlancontable cuenta = view.getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                    GenUtil.getYear(view.getDataFechaComprobante().getValue()), view.getSelCuenta().getValue().toString());
            BigDecimal saldo = procUtil.getSaldoCaja(view.getDataFechaComprobante().getValue(),
                    view.getSelCuenta().getValue().toString(), GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            String s = GenUtil.getSymMoneda(cuenta.getIndTipomoneda());
            view.getSaldoCuenta().setCaption(GenUtil.getSymMoneda(cuenta.getIndTipomoneda()));
            log.info("In setCuentaLogic: " + saldo + " cap: " + s + " " + cuenta.getIndTipomoneda());
            view.getSaldoCuenta().setNullRepresentation("0.00");
            view.getSaldoCuenta().setValue(df.format(saldo));
            log.info("In setCuentaLogic: " + df.format(saldo));
            moneda = GenUtil.getNumMoneda(cuenta.getIndTipomoneda());
            // If still no item created
            if (item==null) {
                nuevoComprobante(moneda);
            } else {
                item.setCodTipomoneda(moneda);
            }
            setMonedaLogic(moneda);
        }
    }


    private void setMonedaLogic(Character moneda) {
        log.info("in moneda logic: " +isLoading + " " + moneda);
        if (!isLoading) {
            try {
                fieldGroup.unbind(view.getNumEgreso());
                fieldGroup.unbind(view.getNumIngreso());
            } catch (FieldGroup.BindException be) {
            }

            //view.getSelCaja().removeAllValidators();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                log.info("in moneda logic set PEN");
                fieldGroup.bind(view.getNumEgreso(), "numHabersol");
                fieldGroup.bind(view.getNumIngreso(), "numDebesol");
            } else if (moneda.equals(USD)) {
                // Dolares
                // Cta Caja
                log.info("in moneda logic set USD");
                fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
                fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
            } else {
                // Euros
                // Cta Caja
                log.info("in moneda logic set EUR");
                fieldGroup.bind(view.getNumEgreso(), "numHabermo");
                fieldGroup.bind(view.getNumIngreso(), "numDebemo");

            }
            //view.getSelCaja().addValidator(new BeanValidator(VsjBancodetalle.class, "codCtacontable"));
            //view.getSelCaja().setEnabled(true);
            view.getNumEgreso().setEnabled(true);
            view.getNumIngreso().setEnabled(true);
            ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
            ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
            //view.setSaldoDeCajas();
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
            view.setEnableDetalleFields(true);
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(), "codTipocuenta",
                    view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaTercero(true, true, true),
                    "txtTipocuenta");
            view.getSelFuente().setValue("");
            view.getSelFuente().setEnabled(false);
            // Reset those fields
            if (!isEdit) {
                view.getSelCtaContable().setValue("");
                view.getSelRubroInst().setValue("");
                view.getSelRubroProy().setValue("");
            }
            //nombreTercero.setValue(getDestinoRepo().findByCodDestino(codTercero).getTxtNombredestino());
            setSaldos();
            //setCajaLogic();
        }
    }

    private void setEditorLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableDetalleFields(true);
            DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                    view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getYear(view.getDataFechaComprobante().getValue()), codProyecto),
                    "Sel Rubro proy", "txtDescctaproyecto");
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

                // Sel Tipo Movimiento
                DataFilterUtil.refreshComboBox(view.getSelTipoMov(), "codTipocuenta",
                        view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true),
                        "txtTipocuenta");
                // Reset those fields
                if (!isEdit) {
                    view.getSelCtaContable().setValue("");
                    view.getSelRubroInst().setValue("");
                }
                view.getSelTipoMov().setEnabled(true);
                view.getSelRubroInst().setEnabled(true);
                view.getSelCtaContable().setEnabled(true);
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.bindComboBox(view.getSelFuente(), "codFinanciera", financieraEfectList,
                    "Sel Fuente", "txtDescfinanciera");
            if (financieraEfectList.size()==1)
                view.getSelFuente().select(financieraEfectList.get(0).getCodFinanciera());

            //nombreTercero.setValue(getProyectoRepo().findByCodProyecto(codProyecto).getTxtDescproyecto());
            setSaldos();
            //setCajaLogic();
        } else {
            //log.info("disabling fin y planproy");
            view.getSelFuente().setEnabled(false);
            view.getSelFuente().setValue("");
            view.getSelRubroProy().setEnabled(false);
            view.getSelRubroProy().setValue("");
        }
    }

    private void bindForm(VsjBancodetalle item) {
        isLoading = true;

        isEdit = !GenUtil.objNullOrEmpty(item.getId());
        clearSaldos();
        beanItem = new BeanItem<>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(view.getSelProyecto(), "codProyecto");
        fieldGroup.bind(view.getSelTercero(), "codTercero");
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
            //throw new MonedaException("Moneda sellecionada no existe, esto nunca deberia pasar");
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

        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox)f).setPageLength(20);
        }
        view.setEnableDetalleFields(false);
        view.getSelProyecto().setEnabled(true);
        view.getSelTercero().setEnabled(true);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            log.info("is Edit in bindForm");
            if (item.getVsjBancocabecera()!=null && !GenUtil.strNullOrEmpty(item.getVsjBancocabecera().getTxtCorrelativo())) {
                log.info("setting numVoucher: " + item.getVsjBancocabecera().getTxtCorrelativo() + "-" + item.getId().getNumItem());
                view.getNumVoucher().setValue(item.getVsjBancocabecera().getTxtCorrelativo()+ "-" + item.getId().getNumItem());
            }
            view.getNumVoucher().setEnabled(false);
            view.setEnableDetalleFields(true);
            setCuentaLogic();
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorLogic(item.getCodProyecto());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
            }
        } else if (item.getVsjBancocabecera()!=null) {
            if (item.getId()==null) {
                log.info("is NOT Edit in bindForm but ID is null");
                view.getNumVoucher().setValue(item.getVsjBancocabecera().getTxtCorrelativo() + "-" + String.valueOf(view.getContainer().size() + 1));
            }
        }

        setSaldos();
        isEdit = false;
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView==null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void nuevoComprobante(Character moneda) {
        savedBancodetalle = null;
        VsjBancodetalle vcb = new VsjBancodetalle();
        if (bancocabecera!=null)
            vcb.setVsjBancocabecera(bancocabecera);
        vcb.setCodTipomoneda(moneda);
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        item = vcb;
        view.setEnableCabezeraFields(true);
        view.setEnableDetalleFields(true);
        view.getGuardarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getImprimirTotalBtn().setEnabled(false);
    }

    public void setNavigatorView(INavigatorView navigatorView) {
        this.navigatorView = navigatorView;
    }

    public void nuevoComprobante() {
        nuevoComprobante(PEN);
    }

    void editarComprobante() {
        editarComprobante(savedBancodetalle);
    }

    public void editarComprobante(VsjBancodetalle vcb) {
        savedBancodetalle = vcb;
        bindForm(vcb);
        view.getNewItemBtn().setEnabled(false);
        view.getGuardarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getImprimirTotalBtn().setEnabled(true);
    }

    // Helpers

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(view.getSelProyecto().getValue());
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.getSelTercero().getValue());
    }

    protected void clearSaldos() {
        //noinspection unchecked
        Arrays.stream(new Field[]{view.getSaldoCuenta(), view.getSaldoProyPEN(), view.getSaldoProyUSD(), view.getSaldoProyEUR()})
                .forEach(f -> f.setValue(""));
    }

    VsjBancodetalle getVsjBancodetalle() throws CommitException {
        fieldGroup.commit();
        VsjBancodetalle item = beanItem.getBean();
        log.info("got from getDetalle "+ item);
        view.setEnableDetalleFields(false);
        return item;
    }

}
