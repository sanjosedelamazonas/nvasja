package org.sanjose.views;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.helper.*;
import org.sanjose.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class ComprobanteLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(ComprobanteLogic.class);

    protected ComprobanteView view;

    protected VsjCajabanco savedCajabanco;

    protected VsjCajabanco item;

    protected BeanItem<VsjCajabanco> beanItem;

    protected FieldGroup fieldGroup;

    protected boolean isLoading = true;

    protected boolean isEdit = false;

    public static final String PEN="0";

    public static final String USD="1";

    public ComprobanteLogic(ComprobanteView ComprobanteView) {
        view = ComprobanteView;
    }

    public void init() {
        view.guardarBtn.addClickListener(event -> saveComprobante());
        view.nuevoComprobante.addClickListener(event -> nuevoComprobante());
        view.cerrarBtn.addClickListener(event -> cerrarAlManejo());
        view.imprimirBtn.addClickListener(event -> {
            if (savedCajabanco!=null) ReportHelper.generateComprobante(savedCajabanco);
        });
        view.modificarBtn.addClickListener(event -> editarComprobante(savedCajabanco));
        view.eliminarBtn.addClickListener(event -> eliminarComprobante());
    }


    public void setupEditComprobanteView() {
        // Fecha
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        view.dataFechaComprobante.setPropertyDataSource(prop);
        view.dataFechaComprobante.setConverter(DateToTimestampConverter.INSTANCE);
        view.dataFechaComprobante.setResolution(Resolution.DAY);
        view.dataFechaComprobante.addValueChangeListener(event -> {
            setSaldoCaja();
            setSaldos();
            view.setSaldoDeCajas();
        });

        // Fecha Doc
        prop = new ObjectProperty<Timestamp>(ts);
        view.fechaDoc.setPropertyDataSource(prop);
        view.fechaDoc.setConverter(DateToTimestampConverter.INSTANCE);
        view.fechaDoc.setResolution(Resolution.DAY);

        // Proyecto
        DataFilterUtil.bindComboBox(view.selProyecto, "codProyecto", view.proyectoRepo.findByFecFinalGreaterThan(new Date()),
                "Sel Proyecto", "txtDescproyecto");
        view.selProyecto.addValueChangeListener(event -> setProyectoLogic(event));

        // Tercero
        DataFilterUtil.bindComboBox(view.selTercero, "codDestino", view.destinoRepo.findByIndTipodestino("3"), "Sel Tercero",
                "txtNombredestino");
        view.selTercero.addValueChangeListener(event -> setTerceroLogic(event));

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.selMoneda, "codTipomoneda");
        view.selMoneda.addValueChangeListener(event -> setMonedaLogic(event.getProperty().getValue().toString()));

        view.numIngreso.addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (!GenUtil.isZero(event.getProperty().getValue())) {
                            view.numEgreso.setValue("");
                        }
                    }
                }
        );
        view.numEgreso.addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (!GenUtil.isZero(event.getProperty().getValue())) {
                            view.numIngreso.setValue("");
                        }
                    }
                }
        );

        // Responsable
        DataFilterUtil.bindComboBox(view.selResponsable, "codDestino", view.destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");

        // Lugar de gasto
        DataFilterUtil.bindComboBox(view.selLugarGasto, "codContraparte", view.contraparteRepo.findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.selCodAuxiliar, "codDestino", view.destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.selTipoDoc, "codTipocomprobantepago", view.comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");


        // Cta Contable
        DataFilterUtil.bindComboBox(view.selCtaContable, "id.codCtacontable",
                view.planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        "N", GenUtil.getCurYear(), "101%", "102%", "104%", "106%"),
                //planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""),
                "Sel cta contable", "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.bindComboBox(view.selRubroInst, "id.codCtaespecial",
                view.planespecialRep.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        // Rubro Proy
        DataFilterUtil.bindComboBox(view.selRubroProy, "id.codCtaproyecto",
                view.planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        // Fuente
        DataFilterUtil.bindComboBox(view.selFuente, "codFinanciera", view.financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");

        DataFilterUtil.bindComboBox(view.selTipoMov, "codTipocuenta", view.configuractacajabancoRepo.findByActivoAndParaCaja(true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        //selTipoMov.setEnabled(false);
        view.selTipoMov.addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.configuractacajabancoRepo.findByCodTipocuenta(Integer.parseInt(tipoMov));
                view.selCtaContable.setValue(config.getCodCtacontablegasto());
                view.selRubroInst.setValue(config.getCodCtaespecial());
            }
        });
        view.glosa.setMaxLength(70);

        // Validators
        view.dataFechaComprobante.addValidator(new BeanValidator(VsjCajabanco.class, "fecFecha"));
        view.fechaDoc.addValidator(new BeanValidator(VsjCajabanco.class, "fecComprobantepago"));
        view.selProyecto.addValidator(new TwoCombosValidator(view.selTercero, true, null));
        view.selTercero.addValidator(new TwoCombosValidator(view.selProyecto, true, null));
        view.selMoneda.addValidator(new BeanValidator(VsjCajabanco.class, "codTipomoneda"));
        view.numIngreso.addValidator(new TwoNumberfieldsValidator(view.numEgreso, false, "Ingreso o egreso debe ser rellenado"));
        view.numEgreso.addValidator(new TwoNumberfieldsValidator(view.numIngreso, false, "Ingreso o egreso debe ser rellenado"));
        view.selResponsable.addValidator(new BeanValidator(VsjCajabanco.class, "codDestino"));
        view.selLugarGasto.addValidator(new BeanValidator(VsjCajabanco.class, "codContraparte"));
        view.selCodAuxiliar.addValidator(new BeanValidator(VsjCajabanco.class, "codDestinoitem"));
        view.glosa.addValidator(new BeanValidator(VsjCajabanco.class, "txtGlosaitem"));
        view.serieDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtSeriecomprobantepago"));
        view.numDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtComprobantepago"));
        view.selCtaContable.addValidator(new BeanValidator(VsjCajabanco.class, "codContracta"));

        // Editing Destino
        view.btnDestino.addClickListener(event->editDestino(view.selCodAuxiliar));
        view.btnResponsable.addClickListener(event->editDestino(view.selResponsable));

        view.setEnableFields(false);
    }

    private void editDestino(ComboBox comboBox) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(500, Sizeable.Unit.PIXELS);
        destinoWindow.setHeight(500, Sizeable.Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(view.destinoRepo, view.cargocuartaRepo, view.tipodocumentoRepo);
        if (comboBox.getValue()==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.destinoRepo.findByCodDestino(comboBox.getValue().toString());
            if (destino!=null)
                destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.btnGuardar.addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            destinoWindow.close();
            refreshDestino();
            comboBox.setValue(editedItem.getCodDestino());

        });
        destinoView.btnAnular.addClickListener(event -> {
            destinoView.viewLogic.anularDestino();
            destinoWindow.close();
        });

        destinoView.btnEliminar.addClickListener(clickEvent -> {
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

                            List<VsjCajabanco> comprobantes = view.repo.findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (VsjCajabanco vcb : comprobantes) {
                                    sb.append("\n" + vcb.getTxtCorrelativo() + " " + vcb.getFecFecha() + " " + vcb.getTxtGlosaitem());
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
            //destinoWindow.close();
        });
        UI.getCurrent().addWindow(destinoWindow);
    }


    private void refreshDestino() {
        DataFilterUtil.refreshComboBox(view.selResponsable, "codDestino", view.destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.selCodAuxiliar, "codDestino", view.destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
    }


    private void setMonedaLogic(String moneda) {
        if (!isLoading) {
            try {
                fieldGroup.unbind(view.numEgreso);
                fieldGroup.unbind(view.numIngreso);
            } catch (FieldGroup.BindException be) {
            }
            view.selCaja.removeAllValidators();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                DataFilterUtil.bindComboBox(view.selCaja, "id.codCtacontable", DataUtil.getCajas(view.planRepo, true), "Sel Caja", "txtDescctacontable");
                setCajaLogic(PEN);
                fieldGroup.bind(view.numEgreso, "numHabersol");
                fieldGroup.bind(view.numIngreso, "numDebesol");
            } else {
                // Dolares
                // Cta Caja
                DataFilterUtil.bindComboBox(view.selCaja, "id.codCtacontable", DataUtil.getCajas(view.planRepo, false), "Sel Caja", "txtDescctacontable");
                setCajaLogic(USD);
                fieldGroup.bind(view.numEgreso, "numHaberdolar");
                fieldGroup.bind(view.numIngreso, "numDebedolar");
            }
            setSaldoCaja();
            view.selCaja.addValidator(new BeanValidator(VsjCajabanco.class, "codCtacontable"));
            view.selCaja.setEnabled(true);
            view.numEgreso.setEnabled(true);
            view.numIngreso.setEnabled(true);
            ViewUtil.setDefaultsForNumberField(view.numIngreso);
            ViewUtil.setDefaultsForNumberField(view.numEgreso);
            view.setSaldoDeCajas();
        }
    }


    public void setSaldos() {
        if (view.dataFechaComprobante.getValue()!=null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            ProcUtil.Saldos res = null;
            if (isProyecto()) {
                res = new ProcUtil(view.getEm()).getSaldos(view.dataFechaComprobante.getValue(), view.selProyecto.getValue().toString(), null);
                view.saldoProyPEN.setValue(df.format(res.getSaldoPEN()));
                view.saldoProyUSD.setValue(df.format(res.getSaldoUSD()));
                view.saldoProyEUR.setValue(df.format(res.getSaldoEUR()));
            }
            if (isTercero()) {
                res = new ProcUtil(view.getEm()).getSaldos(view.dataFechaComprobante.getValue(), null, view.selTercero.getValue().toString());
                view.saldoProyPEN.setValue(df.format(res.getSaldoPEN()));
                view.saldoProyUSD.setValue(df.format(res.getSaldoUSD()));
                view.saldoProyEUR.setValue("");

            }
        }
    }

    public void setSaldoCaja() {
        if (view.dataFechaComprobante.getValue()!=null && view.selCaja.getValue()!=null && view.selMoneda.getValue()!=null) {
            BigDecimal saldo = new ProcUtil(view.getEm()).getSaldoCaja(view.dataFechaComprobante.getValue(),
                    view.selCaja.getValue().toString(), view.selMoneda.getValue().toString());
            if (PEN.equals(view.selMoneda.getValue().toString())) {
                view.saldoCajaPEN.setValue(saldo.toString());
                view.saldoCajaUSD.setValue("");
            } else {
                view.saldoCajaUSD.setValue(saldo.toString());
                view.saldoCajaPEN.setValue("");
            }
        }
    }


    private void setCajaLogic() {
        setCajaLogic(view.selMoneda.getValue().toString());
    }

    public void setCajaLogic(String tipomoneda) {

        if (isProyecto()) {
            List<VsjConfiguracioncaja> configs = view.configuracioncajaRepo.findByCodProyectoAndIndTipomoneda(
                    view.selProyecto.getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                view.selCaja.setValue(config.getCodCtacontable());
            } else {
                String catProy = view.proyectoRepo.findByCodProyecto(view.selProyecto.getValue().toString())
                        .getCodCategoriaproyecto();
                configs = view.configuracioncajaRepo.findByCodCategoriaproyectoAndIndTipomoneda(
                        catProy, tipomoneda);
                if (!configs.isEmpty()) {
                    VsjConfiguracioncaja config = configs.get(0);
                    view.selCaja.setValue(config.getCodCtacontable());
                }
            }
        } else if (isTercero()) {
            List<VsjConfiguracioncaja> configs = view.configuracioncajaRepo.findByCodDestinoAndIndTipomoneda(
                    view.selTercero.getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                view.selCaja.setValue(config.getCodCtacontable());
            }
        }
    }

    public void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        view.selProyecto.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    public void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorTerceroLogic(event.getProperty().getValue().toString());
            view.selTercero.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
        }
    }

    public void setEditorTerceroLogic(String codTercero)  {
        if (!GenUtil.strNullOrEmpty(codTercero)) {
            view.setEnableFields(true);
            if (view.selMoneda.getValue() == null) {
                view.numIngreso.setEnabled(false);
                view.numEgreso.setEnabled(false);
            }
            DataFilterUtil.bindComboBox(view.selTipoMov, "codTipocuenta",
                    view.configuractacajabancoRepo.findByActivoAndParaCajaAndParaTercero(true, true, true),
                    "Sel Tipo de Movimiento", "txtTipocuenta");
            view.selFuente.setValue(null);
            view.selFuente.setEnabled(false);
            // Reset those fields
            if (!isEdit) {
                view.selCtaContable.setValue(null);
                view.selRubroInst.setValue(null);
                view.selRubroProy.setValue(null);
            }
            //nombreTercero.setValue(destinoRepo.findByCodDestino(codTercero).getTxtNombredestino());
            setSaldos();
            setCajaLogic();
        }
    }

    public void setEditorLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableFields(true);
            if (view.selMoneda.getValue()==null) {
                view.numIngreso.setEnabled(false);
                view.numEgreso.setEnabled(false);
            }
            DataFilterUtil.bindComboBox(view.selRubroProy, "id.codCtaproyecto",
                    view.planproyectoRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "Sel Rubro proy", "txtDescctaproyecto");
            List<Scp_ProyectoPorFinanciera>
                    proyectoPorFinancieraList = view.proyectoPorFinancieraRepo.findById_CodProyecto(codProyecto);

            // Filter financiera if exists in Proyecto Por Financiera
            List<ScpFinanciera> financieraList = view.financieraRepo.findAll();
            List<ScpFinanciera> financieraEfectList = new ArrayList<>();
            if (proyectoPorFinancieraList!=null && !proyectoPorFinancieraList.isEmpty()) {
                List<String> codFinancieraList = new ArrayList<>();
                for (Scp_ProyectoPorFinanciera proyectoPorFinanciera : proyectoPorFinancieraList)
                    codFinancieraList.add(proyectoPorFinanciera.getId().getCodFinanciera());

                for (ScpFinanciera financiera : financieraList) {
                    if (financiera.getCodFinanciera()!=null &&
                            codFinancieraList.contains(financiera.getCodFinanciera())) {
                        financieraEfectList.add(financiera);
                    }
                }

                // Sel Tipo Movimiento
                DataFilterUtil.bindComboBox(view.selTipoMov, "codTipocuenta",
                        view.configuractacajabancoRepo.findByActivoAndParaCajaAndParaProyecto(true, true, true),
                        "Sel Tipo de Movimiento", "txtTipocuenta");
                // Reset those fields
                if (!isEdit) {
                    view.selCtaContable.setValue(null);
                    view.selRubroInst.setValue(null);
                }
                view.selTipoMov.setEnabled(true);
                view.selRubroInst.setEnabled(true);
                view.selCtaContable.setEnabled(true);
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.bindComboBox(view.selFuente, "codFinanciera", financieraEfectList,
                    "Sel Fuente", "txtDescfinanciera");
            if (financieraEfectList.size()==1)
                view.selFuente.select(financieraEfectList.get(0).getCodFinanciera());

            //nombreTercero.setValue(proyectoRepo.findByCodProyecto(codProyecto).getTxtDescproyecto());
            setSaldos();
            setCajaLogic();
        } else {
            //log.info("disabling fin y planproy");
            view.selFuente.setEnabled(false);
            view.selFuente.setValue("");
            view.selRubroProy.setEnabled(false);
            view.selRubroProy.setValue("");
        }
    }

    public void bindForm(VsjCajabanco item) {
        isLoading = true;

        isEdit = false;
        if (item.getCodCajabanco()>0) isEdit = true;
        clearSaldos();
        //selMoneda.setValue(null);
        beanItem = new BeanItem<VsjCajabanco>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(view.selProyecto, "codProyecto");
        fieldGroup.bind(view.selTercero, "codTercero");
        fieldGroup.bind(view.selMoneda, "codTipomoneda");
        fieldGroup.bind(view.selCaja, "codCtacontable");
        fieldGroup.bind(view.dataFechaComprobante, "fecFecha");

        if (isEdit && PEN.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.numEgreso, "numHabersol");
            fieldGroup.bind(view.numIngreso, "numDebesol");
        } else if (isEdit && USD.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.numEgreso, "numHaberdolar");
            fieldGroup.bind(view.numIngreso, "numDebedolar");
        }
        ViewUtil.setDefaultsForNumberField(view.numIngreso);
        ViewUtil.setDefaultsForNumberField(view.numEgreso);
        fieldGroup.bind(view.glosa, "txtGlosaitem");
        fieldGroup.bind(view.selResponsable, "codDestino");
        fieldGroup.bind(view.selLugarGasto, "codContraparte");
        fieldGroup.bind(view.selCodAuxiliar, "codDestinoitem");
        fieldGroup.bind(view.selTipoDoc, "codTipocomprobantepago");
        fieldGroup.bind(view.serieDoc, "txtSeriecomprobantepago");
        fieldGroup.bind(view.numDoc, "txtComprobantepago");
        fieldGroup.bind(view.fechaDoc, "fecComprobantepago");
        fieldGroup.bind(view.selCtaContable, "codContracta");
        fieldGroup.bind(view.selRubroInst, "codCtaespecial");
        fieldGroup.bind(view.selRubroProy, "codCtaproyecto");
        fieldGroup.bind(view.selFuente, "codFinanciera");
        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox)f).setPageLength(20);
        }
        view.setEnableFields(false);
        view.selProyecto.setEnabled(true);
        view.selTercero.setEnabled(true);
        view.dataFechaComprobante.setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                view.numVoucher.setValue(item.getTxtCorrelativo());
            } else
                view.numVoucher.setValue(new Integer(item.getCodCajabanco()).toString());
            view.setEnableFields(true);
            setSaldos();
            setSaldoCaja();
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorLogic(item.getCodProyecto().toString());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
            }
        } else {
            setMonedaLogic(item.getCodTipomoneda());
            view.numVoucher.setValue("");
            view.setSaldoDeCajas();
        }
        isEdit = false;

    }


    // Buttons

    public void cerrarAlManejo() {
        MainUI.get().getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
    }

    @Transactional
    public void saveComprobante() {
        try {
            VsjCajabanco item = getVsjCajabanco();
            if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
                throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            item.setCodMes(sdf.format(item.getFecFecha()));
            sdf = new SimpleDateFormat("yyyy");
            item.setTxtAnoproceso(sdf.format(item.getFecFecha()));
            if (!GenUtil.strNullOrEmpty(item.getCodProyecto())) {
                item.setIndTipocuenta("0");
            } else {
                item.setIndTipocuenta("1");
            }
            if (item.getCodUregistro()==null) item.setCodUregistro(CurrentUser.get());
            if (item.getFecFregistro()==null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
            item.setCodUactualiza(CurrentUser.get());
            item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

            // Verify moneda and fields
            if (ComprobanteView.PEN.equals(item.getCodTipomoneda())) {
                if (GenUtil.isNullOrZero(item.getNumHabersol()) && GenUtil.isNullOrZero(item.getNumDebesol()))
                    throw new CommitException("Selected SOL but values are zeros or nulls");
                if (!GenUtil.isNullOrZero(item.getNumHaberdolar()) || !GenUtil.isNullOrZero(item.getNumDebedolar()))
                    throw new CommitException("Selected SOL but values for Dolar are not zeros or nulls");
                item.setNumHaberdolar(new BigDecimal(0.00));
                item.setNumDebedolar(new BigDecimal(0.00));
            } else {
                if (GenUtil.isNullOrZero(item.getNumHaberdolar()) && GenUtil.isNullOrZero(item.getNumDebedolar()))
                    throw new CommitException("Selected USD but values are zeros or nulls");
                if (!GenUtil.isNullOrZero(item.getNumHabersol()) || !GenUtil.isNullOrZero(item.getNumDebesol()))
                    throw new CommitException("Selected USD but values for SOL are not zeros or nulls");
                item.setNumHabersol(new BigDecimal(0.00));
                item.setNumDebesol(new BigDecimal(0.00));
            }
            log.info("Ready to save: " + item);
            savedCajabanco = view.repo.save(item);

            if (GenUtil.strNullOrEmpty(savedCajabanco.getTxtCorrelativo())) {
                savedCajabanco.setTxtCorrelativo(GenUtil.getTxtCorrelativo(savedCajabanco.getCodCajabanco()));
                savedCajabanco = view.repo.save(savedCajabanco);
            }
            view.numVoucher.setValue(savedCajabanco.getTxtCorrelativo());
            view.guardarBtn.setEnabled(false);
            view.modificarBtn.setEnabled(true);
            view.nuevoComprobante.setEnabled(true);
            view.cajaManejoView.refreshData();
            view.imprimirBtn.setEnabled(true);
        } catch (CommitException ce) {
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    public void enter(String productId) {
    }


    public void nuevoComprobante() {
//        setFragmentParameter("new");
        savedCajabanco = null;
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setFlgEnviado("0");
        vcb.setFlg_Anula("0");
        vcb.setIndTipocuenta("0");
        vcb.setCodTipomoneda(ComprobanteView.PEN);
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        view.guardarBtn.setEnabled(true);
        view.modificarBtn.setEnabled(false);
        view.eliminarBtn.setEnabled(false);
        view.imprimirBtn.setEnabled(false);
    }

    public void editarComprobante(VsjCajabanco vcb) {
  //      setFragmentParameter("edit");
        savedCajabanco = vcb;
        bindForm(vcb);
        view.nuevoComprobante.setEnabled(false);
        view.guardarBtn.setEnabled(true);
        view.eliminarBtn.setEnabled(true);
        view.modificarBtn.setEnabled(false);
        view.imprimirBtn.setEnabled(true);
    }

    public void eliminarComprobante() {
        try {
            if (savedCajabanco==null) {
                log.info("no se puede eliminar si no esta ya guardado");
                return;
            }
            if (savedCajabanco.getFlgEnviado().equals("1")) {
                Notification.show("No se puede eliminar porque ya esta enviado a la contabilidad");
                return;
            }
            VsjCajabanco item = getVsjCajabanco();
            if (GenUtil.strNullOrEmpty(item.getCodProyecto()) && GenUtil.strNullOrEmpty(item.getCodTercero()))
                throw new Validator.InvalidValueException("Codigo Proyecto o Codigo Tercero debe ser rellenado");

            item.setCodUactualiza(CurrentUser.get());
            item.setFecFactualiza(new Timestamp(System.currentTimeMillis()));

            // Verify moneda and fields
            item.setNumHabersol(new BigDecimal(0.00));
            item.setNumDebesol(new BigDecimal(0.00));
            item.setNumHaberdolar(new BigDecimal(0.00));
            item.setNumDebedolar(new BigDecimal(0.00));

            item.setTxtGlosaitem("ANULADO - " + (item.getTxtGlosaitem().length()>60 ?
                    item.getTxtGlosaitem().substring(0,60) : item.getTxtGlosaitem()));
            item.setFlg_Anula("1");

            view.glosa.setValue(item.getTxtGlosaitem());
            log.info("Ready to ANULAR: " + item);
            savedCajabanco = view.repo.save(item);
            view.numVoucher.setValue(new Integer(savedCajabanco.getCodCajabanco()).toString());
            savedCajabanco = null;
            view.guardarBtn.setEnabled(false);
            view.modificarBtn.setEnabled(false);
            view.nuevoComprobante.setEnabled(true);
            view.cajaManejoView.refreshData();
            view.imprimirBtn.setEnabled(false);
            view.eliminarBtn.setEnabled(    false);
        } catch (CommitException ce) {
            Notification.show("Error al anular el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    // Helpers

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(view.selProyecto.getValue());
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.selTercero.getValue());
    }

    public void clearSaldos() {
        Arrays.stream(new Field[]{view.saldoCajaPEN, view.saldoCajaUSD, view.saldoProyPEN, view.saldoProyUSD, view.saldoProyEUR})
                .forEach(f -> f.setValue(""));
    }

    public VsjCajabanco getVsjCajabanco() throws FieldGroup.CommitException {
        fieldGroup.commit();
        VsjCajabanco item = beanItem.getBean();
        view.setEnableFields(false);
        return item;
    }

}
