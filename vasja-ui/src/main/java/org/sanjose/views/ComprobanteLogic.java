package org.sanjose.views;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.helper.PrintHelper;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.validator.TwoCombosValidator;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.print.PrintException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

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

    protected IComprobanteView view;

    protected VsjCajabanco savedCajabanco;

    protected VsjCajabanco item;

    protected BeanItem<VsjCajabanco> beanItem;

    protected FieldGroup fieldGroup;

    protected boolean isLoading = true;

    protected boolean isEdit = false;

    public static final String PEN="0";

    public static final String USD="1";

    protected ProcUtil procUtil;

    @Autowired
    public ComprobanteLogic(IComprobanteView  comprobanteView) {
        view = comprobanteView;
    }

    public void init() {
        view.getGuardarBtn().addClickListener(event -> saveComprobante());
        view.getNuevoComprobante().addClickListener(event -> nuevoComprobante());
        view.getCerrarBtn().addClickListener(event -> cerrarAlManejo());
        view.getImprimirBtn().addClickListener(event -> {
            if (savedCajabanco!=null) ViewUtil.printComprobante(savedCajabanco);
        });
        view.getModificarBtn().addClickListener(event -> editarComprobante());
        view.getEliminarBtn().addClickListener(event -> eliminarComprobante());
        procUtil = new ProcUtil(view.getEm());
    }


    public void setupEditComprobanteView() {
        // Fecha
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        view.getDataFechaComprobante().setPropertyDataSource(prop);
        view.getDataFechaComprobante().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaComprobante().setResolution(Resolution.DAY);
        view.getDataFechaComprobante().addValueChangeListener(event -> {
            setSaldoCaja();
            setSaldos();
            view.setSaldoDeCajas();
        });

        // Fecha Doc
        prop = new ObjectProperty<Timestamp>(ts);
        view.getFechaDoc().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);

        // Proyecto
        DataFilterUtil.bindComboBox(view.getSelProyecto(), "codProyecto", view.getProyectoRepo().findByFecFinalGreaterThan(new Date()),
                "Sel Proyecto", "txtDescproyecto");
        view.getSelProyecto().addValueChangeListener(event -> setProyectoLogic(event));

        // Tercero
        DataFilterUtil.bindComboBox(view.getSelTercero(), "codDestino", view.getDestinoRepo().findByIndTipodestino("3"), "Sel Tercero",
                "txtNombredestino");
        view.getSelTercero().addValueChangeListener(event -> setTerceroLogic(event));

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.getSelMoneda(), "codTipomoneda");
        view.getSelMoneda().addValueChangeListener(event -> setMonedaLogic(event.getProperty().getValue().toString()));

        view.getNumIngreso().addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (!GenUtil.isZero(event.getProperty().getValue())) {
                            view.getNumEgreso().setValue("");
                        }
                    }
                }
        );
        view.getNumEgreso().addValueChangeListener(event -> {
                    if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                        if (!GenUtil.isZero(event.getProperty().getValue())) {
                            view.getNumIngreso().setValue("");
                        }
                    }
                }
        );

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable(), "codDestino", view.getDestinoRepo().findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");

        view.getSelResponsable().addValueChangeListener(valueChangeEvent ->  {
            if (valueChangeEvent.getProperty().getValue()!=null)
                view.getSelCodAuxiliar().setValue(valueChangeEvent.getProperty().getValue());
        });

        // Lugar de gasto
        DataFilterUtil.bindComboBox(view.getSelLugarGasto(), "codContraparte", view.getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", view.getDestinoRepo().findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getComprobantepagoRepo().findAll(),
                "Sel Tipo", "txtDescripcion");


        // Cta Contable
        DataFilterUtil.bindComboBox(view.getSelCtaContable(), "id.codCtacontable",
                view.getPlanRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLikeAndId_CodCtacontableNotLike(
                        "N", GenUtil.getCurYear(), "101%", "102%", "104%", "106%"),
                //getPlanRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""),
                "Sel cta contable", "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.bindComboBox(view.getSelRubroInst(), "id.codCtaespecial",
                view.getPlanespecialRep().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        // Rubro Proy
        DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                view.getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        // Fuente
        DataFilterUtil.bindComboBox(view.getSelFuente(), "codFinanciera", view.getFinancieraRepo().findAll(),
                "Sel Fuente", "txtDescfinanciera");

        DataFilterUtil.bindComboBox(view.getSelTipoMov(), "codTipocuenta", view.getConfiguractacajabancoRepo().findByActivoAndParaCaja(true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getConfiguractacajabancoRepo().findByCodTipocuenta(Integer.parseInt(tipoMov));
                view.getSelCtaContable().setValue(config.getCodCtacontablegasto());
                view.getSelRubroInst().setValue(config.getCodCtaespecial());
            }
        });
        view.getGlosa().setMaxLength(70);

        // Validators
        view.getDataFechaComprobante().addValidator(new BeanValidator(VsjCajabanco.class, "fecFecha"));
        view.getFechaDoc().addValidator(new BeanValidator(VsjCajabanco.class, "fecComprobantepago"));
        view.getSelProyecto().addValidator(new TwoCombosValidator(view.getSelTercero(), true, null));
        view.getSelTercero().addValidator(new TwoCombosValidator(view.getSelProyecto(), true, null));
        view.getSelMoneda().addValidator(new BeanValidator(VsjCajabanco.class, "codTipomoneda"));
        view.getNumIngreso().addValidator(new TwoNumberfieldsValidator(view.getNumEgreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getNumEgreso().addValidator(new TwoNumberfieldsValidator(view.getNumIngreso(), false, "Ingreso o egreso debe ser rellenado"));
        view.getSelResponsable().addValidator(new BeanValidator(VsjCajabanco.class, "codDestino"));
        view.getSelLugarGasto().addValidator(new BeanValidator(VsjCajabanco.class, "codContraparte"));
        view.getSelCodAuxiliar().addValidator(new BeanValidator(VsjCajabanco.class, "codDestinoitem"));
        view.getGlosa().addValidator(new BeanValidator(VsjCajabanco.class, "txtGlosaitem"));
        view.getSerieDoc().addValidator(new BeanValidator(VsjCajabanco.class, "txtSeriecomprobantepago"));
        view.getNumDoc().addValidator(new BeanValidator(VsjCajabanco.class, "txtComprobantepago"));
        view.getSelCtaContable().addValidator(new BeanValidator(VsjCajabanco.class, "codContracta"));
        view.getSelTipoMov().addValidator(new BeanValidator(VsjCajabanco.class, "codTipomov"));
        // Editing Destino
        view.getBtnDestino().addClickListener(event->editDestino(view.getSelCodAuxiliar()));
        view.getBtnResponsable().addClickListener(event->editDestino(view.getSelResponsable()));

        view.setEnableFields(false);
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

        DestinoView destinoView = new DestinoView(view.getDestinoRepo(), view.getCargocuartaRepo(), view.getTipodocumentoRepo());
        if (comboBox.getValue()==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.getDestinoRepo().findByCodDestino(comboBox.getValue().toString());
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

                            List<VsjCajabanco> comprobantes = view.getRepo().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
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
        DataFilterUtil.refreshComboBox(view.getSelResponsable(), "codDestino", view.getDestinoRepo().findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getDestinoRepo().findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
    }


    private void setMonedaLogic(String moneda) {
        if (!isLoading) {
            try {
                fieldGroup.unbind(view.getNumEgreso());
                fieldGroup.unbind(view.getNumIngreso());
            } catch (FieldGroup.BindException be) {
            }
            view.getSelCaja().removeAllValidators();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                DataFilterUtil.bindComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(view.getPlanRepo(), true), "Sel Caja", "txtDescctacontable");
                setCajaLogic(PEN);
                fieldGroup.bind(view.getNumEgreso(), "numHabersol");
                fieldGroup.bind(view.getNumIngreso(), "numDebesol");
            } else {
                // Dolares
                // Cta Caja
                DataFilterUtil.bindComboBox(view.getSelCaja(), "id.codCtacontable", DataUtil.getCajas(view.getPlanRepo(), false), "Sel Caja", "txtDescctacontable");
                setCajaLogic(USD);
                fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
                fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
            }
            setSaldoCaja();
            view.getSelCaja().addValidator(new BeanValidator(VsjCajabanco.class, "codCtacontable"));
            view.getSelCaja().setEnabled(true);
            view.getNumEgreso().setEnabled(true);
            view.getNumIngreso().setEnabled(true);
            ViewUtil.setDefaultsForNumberField(view.getNumIngreso());
            ViewUtil.setDefaultsForNumberField(view.getNumEgreso());
            view.setSaldoDeCajas();
        }
    }


    public void setSaldos() {
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

    public void setSaldoCaja() {
        if (view.getDataFechaComprobante().getValue()!=null && view.getSelCaja().getValue()!=null && view.getSelMoneda().getValue()!=null) {
            BigDecimal saldo = procUtil.getSaldoCaja(view.getDataFechaComprobante().getValue(),
                    view.getSelCaja().getValue().toString(), view.getSelMoneda().getValue().toString());
            if (PEN.equals(view.getSelMoneda().getValue().toString())) {
                view.getSaldoCajaPEN().setValue(saldo.toString());
                view.getSaldoCajaUSD().setValue("");
            } else {
                view.getSaldoCajaUSD().setValue(saldo.toString());
                view.getSaldoCajaPEN().setValue("");
            }
        }
    }


    private void setCajaLogic() {
        setCajaLogic(view.getSelMoneda().getValue().toString());
    }

    public void setCajaLogic(String tipomoneda) {

        if (isProyecto()) {
            List<VsjConfiguracioncaja> configs = view.getConfiguracioncajaRepo().findByCodProyectoAndIndTipomoneda(
                    view.getSelProyecto().getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                view.getSelCaja().setValue(config.getCodCtacontable());
            } else {
                String catProy = view.getProyectoRepo().findByCodProyecto(view.getSelProyecto().getValue().toString())
                        .getCodCategoriaproyecto();
                configs = view.getConfiguracioncajaRepo().findByCodCategoriaproyectoAndIndTipomoneda(
                        catProy, tipomoneda);
                if (!configs.isEmpty()) {
                    VsjConfiguracioncaja config = configs.get(0);
                    view.getSelCaja().setValue(config.getCodCtacontable());
                }
            }
        } else if (isTercero()) {
            List<VsjConfiguracioncaja> configs = view.getConfiguracioncajaRepo().findByCodDestinoAndIndTipomoneda(
                    view.getSelTercero().getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                view.getSelCaja().setValue(config.getCodCtacontable());
            }
        }
        setSaldoCaja();
    }

    public void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        view.getSelProyecto().getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    public void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue() != null) {
            setEditorTerceroLogic(event.getProperty().getValue().toString());
            view.getSelTercero().getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
        }
    }

    public void setEditorTerceroLogic(String codTercero)  {
        if (!GenUtil.strNullOrEmpty(codTercero)) {
            view.setEnableFields(true);
            if (view.getSelMoneda().getValue() == null) {
                view.getNumIngreso().setEnabled(false);
                view.getNumEgreso().setEnabled(false);
            }
            DataFilterUtil.refreshComboBox(view.getSelTipoMov(), "codTipocuenta",
                    view.getConfiguractacajabancoRepo().findByActivoAndParaCajaAndParaTercero(true, true, true),
                    "Sel Tipo de Movimiento", "txtTipocuenta");
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
            setCajaLogic();
        }
    }

    public void setEditorLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            view.setEnableFields(true);
            if (view.getSelMoneda().getValue()==null) {
                view.getNumIngreso().setEnabled(false);
                view.getNumEgreso().setEnabled(false);
            }
            DataFilterUtil.bindComboBox(view.getSelRubroProy(), "id.codCtaproyecto",
                    view.getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "Sel Rubro proy", "txtDescctaproyecto");
            List<Scp_ProyectoPorFinanciera>
                    proyectoPorFinancieraList = view.getProyectoPorFinancieraRepo().findById_CodProyecto(codProyecto);

            // Filter financiera if exists in Proyecto Por Financiera
            List<ScpFinanciera> financieraList = view.getFinancieraRepo().findAll();
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
                DataFilterUtil.refreshComboBox(view.getSelTipoMov(), "codTipocuenta",
                        view.getConfiguractacajabancoRepo().findByActivoAndParaCajaAndParaProyecto(true, true, true),
                        "Sel Tipo de Movimiento", "txtTipocuenta");
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
            setCajaLogic();
        } else {
            //log.info("disabling fin y planproy");
            view.getSelFuente().setEnabled(false);
            view.getSelFuente().setValue("");
            view.getSelRubroProy().setEnabled(false);
            view.getSelRubroProy().setValue("");
        }
    }

    public void bindForm(VsjCajabanco item) {
        isLoading = true;

        isEdit = !GenUtil.strNullOrEmpty(item.getCodUregistro());
        clearSaldos();
        //getSelMoneda().setValue(null);
        beanItem = new BeanItem<VsjCajabanco>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(view.getSelProyecto(), "codProyecto");
        fieldGroup.bind(view.getSelTercero(), "codTercero");
        fieldGroup.bind(view.getSelMoneda(), "codTipomoneda");
        fieldGroup.bind(view.getSelCaja(), "codCtacontable");
        fieldGroup.bind(view.getDataFechaComprobante(), "fecFecha");

        if (isEdit && PEN.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHabersol");
            fieldGroup.bind(view.getNumIngreso(), "numDebesol");
        } else if (isEdit && USD.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(view.getNumEgreso(), "numHaberdolar");
            fieldGroup.bind(view.getNumIngreso(), "numDebedolar");
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
                ((ComboBox)f).setPageLength(20);
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
            } else
                view.getNumVoucher().setValue(new Integer(item.getCodCajabanco()).toString());
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
            view.getNumVoucher().setValue("");
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
            VsjCajabanco item = prepareToSave();
            savedCajabanco = view.getRepo().save(item);

            if (GenUtil.strNullOrEmpty(savedCajabanco.getTxtCorrelativo())) {
                savedCajabanco.setTxtCorrelativo(GenUtil.getTxtCorrelativo(savedCajabanco.getCodCajabanco()));
                savedCajabanco = view.getRepo().save(savedCajabanco);
            }
            view.getNumVoucher().setValue(savedCajabanco.getTxtCorrelativo());
            view.getGuardarBtn().setEnabled(false);
            view.getModificarBtn().setEnabled(true);
            view.getNuevoComprobante().setEnabled(true);
            view.refreshData();
            view.getImprimirBtn().setEnabled(true);
            if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
                ViewUtil.printComprobante(savedCajabanco);
            }
        } catch (CommitException ce) {
            StringBuilder sb = new StringBuilder();
            Map<Field<?>, Validator.InvalidValueException> fieldMap = ce.getInvalidFields();
            for (Field f : fieldMap.keySet()) {
                sb.append(f.getConnectorId() + " " + fieldMap.get(f).getHtmlMessage() + "\n");
            }
            Notification.show("Error al guardar el comprobante: " + ce.getLocalizedMessage() + "\n" + sb.toString(), Notification.Type.ERROR_MESSAGE);
            log.warn("Got Commit Exception: " + ce.getMessage() + "\n" + sb.toString());
        }
    }

    public VsjCajabanco prepareToSave() throws CommitException {
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
        if (item.getCodUregistro() == null) item.setCodUregistro(CurrentUser.get());
        if (item.getFecFregistro() == null) item.setFecFregistro(new Timestamp(System.currentTimeMillis()));
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
        return item;
    }

    public void nuevoComprobante(String moneda) {
        savedCajabanco = null;
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setFlgEnviado("0");
        vcb.setFlg_Anula("0");
        vcb.setIndTipocuenta("0");
        vcb.setCodTipomoneda(moneda);
        vcb.setFecFecha(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        view.getGuardarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getEliminarBtn().setEnabled(false);
        view.getImprimirBtn().setEnabled(false);
    }

    public void nuevoComprobante() {
        nuevoComprobante(PEN);
    }

    public void editarComprobante() {
        editarComprobante(savedCajabanco);
    }

    public void editarComprobante(VsjCajabanco vcb) {
        savedCajabanco = vcb;
        bindForm(vcb);
        view.getNuevoComprobante().setEnabled(false);
        view.getGuardarBtn().setEnabled(true);
        view.getEliminarBtn().setEnabled(true);
        view.getModificarBtn().setEnabled(false);
        view.getImprimirBtn().setEnabled(true);
    }

    public void eliminarComprobante() {
        try {
            if (savedCajabanco==null) {
                log.info("no se puede eliminar si no esta ya guardado");
                return;
            }
            if (savedCajabanco.getFlgEnviado().equals("1")) {
                Notification.show("Problema al eliminar", "No se puede eliminar porque ya esta enviado a la contabilidad",
                        Notification.Type.WARNING_MESSAGE);
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

            view.getGlosa().setValue(item.getTxtGlosaitem());
            log.info("Ready to ANULAR: " + item);
            savedCajabanco = view.getRepo().save(item);
            view.getNumVoucher().setValue(new Integer(savedCajabanco.getCodCajabanco()).toString());
            savedCajabanco = null;
            view.getGuardarBtn().setEnabled(false);
            view.getModificarBtn().setEnabled(false);
            view.getNuevoComprobante().setEnabled(true);
            view.refreshData();
            view.getImprimirBtn().setEnabled(false);
            view.getEliminarBtn().setEnabled(    false);
        } catch (CommitException ce) {
            Notification.show("Error al anular el comprobante: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
            log.info("Got Commit Exception: " + ce.getMessage());
        }
    }

    // Helpers

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(view.getSelProyecto().getValue());
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(view.getSelTercero().getValue());
    }

    public void clearSaldos() {
        Arrays.stream(new Field[]{view.getSaldoCajaPEN(), view.getSaldoCajaUSD(), view.getSaldoProyPEN(), view.getSaldoProyUSD(), view.getSaldoProyEUR()})
                .forEach(f -> f.setValue(""));
    }

    public VsjCajabanco getVsjCajabanco() throws FieldGroup.CommitException {
        fieldGroup.commit();
        VsjCajabanco item = beanItem.getBean();
        view.setEnableFields(false);
        return item;
    }

}
