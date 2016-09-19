package org.sanjose.views;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.helper.*;
import org.sanjose.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class ComprobanteView extends ComprobanteUI implements View {

	private static final Logger log = LoggerFactory.getLogger(ComprobanteView.class);
	
    public static final String VIEW_NAME = "Caja";

    public static final String PEN="0";

    public static final String USD="1";

    public ComprobanteLogic viewLogic = new ComprobanteLogic(this);

    public VsjCajabanco item;

    public BeanItem<VsjCajabanco> beanItem;
    
    public VsjCajabancoRep repo;

    public ScpPlanproyectoRep planproyectoRepo;

    public ScpFinancieraRep financieraRepo;

    public Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo;

    public VsjConfiguractacajabancoRep configuractacajabancoRepo;

    public VsjConfiguracioncajaRep configuracioncajaRepo;

    public ScpProyectoRep proyectoRepo;

    public ScpDestinoRep destinoRepo;

    private ScpCargocuartaRep cargocuartaRepo;

    private ScpTipodocumentoRep tipodocumentoRepo;

    private ScpPlancontableRep planRepo;

    private EntityManager em;

    public FieldGroup fieldGroup;

    public Field[] allFields = new Field[] { fechaDoc, dataFechaComprobante, selProyecto, selTercero, selCaja, selMoneda,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosa, serieDoc, numDoc };

    boolean isLoading = true;

    boolean isEdit = false;

    public CajaManejoView cajaManejoView;

    @Autowired
    public ComprobanteView(VsjCajabancoRep repo, VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
                           ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                           ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                           ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                           Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo,
                           ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo, EntityManager em) {
    	this.repo = repo;
        this.planproyectoRepo = planproyectoRepo;
        this.financieraRepo = financieraRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        this.configuractacajabancoRepo = configuractacajabancoRepo;
        this.configuracioncajaRepo = configuracioncajaRepo;
        this.proyectoRepo = proyectoRepo;
        this.destinoRepo = destinoRepo;
        this.cargocuartaRepo = cargocuartaRepo;
        this.tipodocumentoRepo = tipodocumentoRepo;
        this.planRepo = planRepo;
        this.em = em;
        setSizeFull();
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        guardarBtn.setEnabled(false);
        modificarBtn.setEnabled(false);
        eliminarBtn.setEnabled(false);
        imprimirBtn.setEnabled(false);

        // Fecha
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        dataFechaComprobante.setPropertyDataSource(prop);
        dataFechaComprobante.setConverter(DateToTimestampConverter.INSTANCE);
        dataFechaComprobante.setResolution(Resolution.DAY);
        dataFechaComprobante.addValueChangeListener(event -> {
            setSaldoCaja();
            setSaldos();
            setSaldoDeCajas();
        });

        // Fecha Doc
        prop = new ObjectProperty<Timestamp>(ts);
        fechaDoc.setPropertyDataSource(prop);
        fechaDoc.setConverter(DateToTimestampConverter.INSTANCE);
        fechaDoc.setResolution(Resolution.DAY);

        // Proyecto
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()),
                "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(event -> setProyectoLogic(event));

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", destinoRepo.findByIndTipodestino("3"), "Sel Tercero",
                "txtNombredestino");
        selTercero.addValueChangeListener(event -> setTerceroLogic(event));

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(selMoneda, "codTipomoneda");
        selMoneda.addValueChangeListener(event -> setMonedaLogic(event.getProperty().getValue().toString()));

        numIngreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (!GenUtil.isZero(event.getProperty().getValue())) {
                        numEgreso.setValue("");
                    }
                }
            }
        );
        numEgreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (!GenUtil.isZero(event.getProperty().getValue())) {
                        numIngreso.setValue("");
                    }
                }
            }
        );

        // Responsable
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");

        // Lugar de gasto
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte", contraparteRepo.findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");

        // Cod. Auxiliar
        DataFilterUtil.bindComboBox(selCodAuxiliar, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(selTipoDoc, "codTipocomprobantepago", comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");

        // Cta Contable
        DataFilterUtil.bindComboBox(selCtaContable, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.bindComboBox(selRubroInst, "id.codCtaespecial",
                planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        // Rubro Proy
        DataFilterUtil.bindComboBox(selRubroProy, "id.codCtaproyecto",
                planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        // Fuente
        DataFilterUtil.bindComboBox(selFuente, "codFinanciera", financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");

        DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta", configuractacajabancoRepo.findByActivoAndParaCaja(true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        //selTipoMov.setEnabled(false);
        selTipoMov.addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = configuractacajabancoRepo.findByCodTipocuenta(Integer.parseInt(tipoMov));
                selCtaContable.setValue(config.getCodCtacontablegasto());
                selRubroInst.setValue(config.getCodCtaespecial());
            }
        });
        glosa.setMaxLength(70);

        // Validators
        dataFechaComprobante.addValidator(new BeanValidator(VsjCajabanco.class, "fecFecha"));
        fechaDoc.addValidator(new BeanValidator(VsjCajabanco.class, "fecComprobantepago"));
        selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
        selTercero.addValidator(new TwoCombosValidator(selProyecto, true, null));
        selMoneda.addValidator(new BeanValidator(VsjCajabanco.class, "codTipomoneda"));
        numIngreso.addValidator(new TwoNumberfieldsValidator(numEgreso, false, "Ingreso o egreso debe ser rellenado"));
        numEgreso.addValidator(new TwoNumberfieldsValidator(numIngreso, false, "Ingreso o egreso debe ser rellenado"));
        selResponsable.addValidator(new BeanValidator(VsjCajabanco.class, "codDestino"));
        selLugarGasto.addValidator(new BeanValidator(VsjCajabanco.class, "codContraparte"));
        selCodAuxiliar.addValidator(new BeanValidator(VsjCajabanco.class, "codDestinoitem"));
        glosa.addValidator(new BeanValidator(VsjCajabanco.class, "txtGlosaitem"));
        serieDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtSeriecomprobantepago"));
        numDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtComprobantepago"));
        selCtaContable.addValidator(new BeanValidator(VsjCajabanco.class, "codContracta"));

        // Editing Destino
        btnDestino.addClickListener(event->editDestino(selCodAuxiliar));
        btnResponsable.addClickListener(event->editDestino(selResponsable));

        setEnableFields(false);
        viewLogic.init();
    }

    private void setMonedaLogic(String moneda) {
        if (!isLoading) {
            try {
                fieldGroup.unbind(numEgreso);
                fieldGroup.unbind(numIngreso);
            } catch (FieldGroup.BindException be) {
            }
            selCaja.removeAllValidators();
            if (moneda.equals(PEN)) {
                // Soles        0
                // Cta Caja
                DataFilterUtil.bindComboBox(selCaja, "id.codCtacontable", DataUtil.getCajas(planRepo, true), "Sel Caja", "txtDescctacontable");
                setCajaLogic(PEN);
                fieldGroup.bind(numEgreso, "numHabersol");
                fieldGroup.bind(numIngreso, "numDebesol");
            } else {
                // Dolares
                // Cta Caja
                DataFilterUtil.bindComboBox(selCaja, "id.codCtacontable", DataUtil.getCajas(planRepo, false), "Sel Caja", "txtDescctacontable");
                setCajaLogic(USD);
                fieldGroup.bind(numEgreso, "numHaberdolar");
                fieldGroup.bind(numIngreso, "numDebedolar");
            }
            setSaldoCaja();
            selCaja.addValidator(new BeanValidator(VsjCajabanco.class, "codCtacontable"));
            selCaja.setEnabled(true);
            numEgreso.setEnabled(true);
            numIngreso.setEnabled(true);
            ViewUtil.setDefaultsForNumberField(numIngreso);
            ViewUtil.setDefaultsForNumberField(numEgreso);
            setSaldoDeCajas();
        }
    }


    private void editDestino(ComboBox comboBox) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(500,Unit.PIXELS);
        destinoWindow.setHeight(500,Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(destinoRepo, cargocuartaRepo, tipodocumentoRepo);
        if (comboBox.getValue()==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = destinoRepo.findByCodDestino(comboBox.getValue().toString());
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

                            List<VsjCajabanco> comprobantes = repo.findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
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
        DataFilterUtil.refreshComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        DataFilterUtil.refreshComboBox(selCodAuxiliar, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
    }


    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            f.setEnabled(enabled);
        }
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }

    public void setSaldoCaja() {
        if (dataFechaComprobante.getValue()!=null && selCaja.getValue()!=null && selMoneda.getValue()!=null) {
            BigDecimal saldo = new ProcUtil(em).getSaldoCaja(dataFechaComprobante.getValue(),
                    selCaja.getValue().toString(), selMoneda.getValue().toString());
            if (PEN.equals(selMoneda.getValue().toString())) {
                saldoCajaPEN.setValue(saldo.toString());
                saldoCajaUSD.setValue("");
            } else {
                saldoCajaUSD.setValue(saldo.toString());
                saldoCajaPEN.setValue("");
            }
        }
    }

    public void setSaldoDeCajas() {
        if (isPEN()) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
        } else  {
            order_summary_layout.addStyleName("order-summary-layout-usd");
        }
        cajaSaldosLayout.removeAllComponents();
        if (dataFechaComprobante.getValue() != null && selMoneda.getValue() != null) {
            BigDecimal total = new BigDecimal(0.00);
            for (ScpPlancontable caja : DataUtil.getCajas(planRepo, PEN.equals(selMoneda.getValue().toString()))) {

                BigDecimal saldo = new ProcUtil(em).getSaldoCaja(dataFechaComprobante.getValue(), caja.getId().getCodCtacontable()
                        , selMoneda.getValue().toString());
                Label salLbl = new Label();
                salLbl.setContentMode(ContentMode.HTML);
                salLbl.setValue(
                    caja.getId().getCodCtacontable() + " " + caja.getTxtDescctacontable() + ": <span class=\"order-sum\">"+  saldo + "</span");
                salLbl.setStyleName("order-item");
                cajaSaldosLayout.addComponent(salLbl);
                total = total.add(saldo);
            }
            saldoTotal.setContentMode(ContentMode.HTML);
            saldoTotal.setValue("Total :" +
                    "<span class=\"order-sum\"> " + (isPEN() ? "S/. " : "$ ") + total.toString() + "</span>");
        }
    }

    private boolean isProyecto() {
        return !GenUtil.objNullOrEmpty(selProyecto.getValue());
    }

    private boolean isTercero() {
        return !GenUtil.objNullOrEmpty(selTercero.getValue());
    }


    public void setSaldos() {
        if (dataFechaComprobante.getValue()!=null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            ProcUtil.Saldos res = null;
            if (isProyecto()) {
                res = new ProcUtil(em).getSaldos(dataFechaComprobante.getValue(), selProyecto.getValue().toString(), null);
                saldoProyPEN.setValue(df.format(res.getSaldoPEN()));
                saldoProyUSD.setValue(df.format(res.getSaldoUSD()));
                saldoProyEUR.setValue(df.format(res.getSaldoEUR()));
            }
            if (isTercero()) {
                res = new ProcUtil(em).getSaldos(dataFechaComprobante.getValue(), null, selTercero.getValue().toString());
                saldoProyPEN.setValue(df.format(res.getSaldoPEN()));
                saldoProyUSD.setValue(df.format(res.getSaldoUSD()));
                saldoProyEUR.setValue("");

            }
        }
    }

    public void clearSaldos() {
        Arrays.stream(new Field[]{saldoCajaPEN, saldoCajaUSD, saldoProyPEN, saldoProyUSD, saldoProyEUR})
                .forEach(f -> f.setValue(""));
    }


    private void setCajaLogic() {
        setCajaLogic(selMoneda.getValue().toString());
    }

    public void setCajaLogic(String tipomoneda) {

        if (isProyecto()) {
            List<VsjConfiguracioncaja> configs = configuracioncajaRepo.findByCodProyectoAndIndTipomoneda(
                    selProyecto.getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                selCaja.setValue(config.getCodCtacontable());
            } else {
                String catProy = proyectoRepo.findByCodProyecto(selProyecto.getValue().toString())
                        .getCodCategoriaproyecto();
                configs = configuracioncajaRepo.findByCodCategoriaproyectoAndIndTipomoneda(
                        catProy, tipomoneda);
                if (!configs.isEmpty()) {
                    VsjConfiguracioncaja config = configs.get(0);
                    selCaja.setValue(config.getCodCtacontable());
                }
            }
        } else if (isTercero()) {
            List<VsjConfiguracioncaja> configs = configuracioncajaRepo.findByCodDestinoAndIndTipomoneda(
                    selTercero.getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                selCaja.setValue(config.getCodCtacontable());
            }
        }
    }


    public void setProyectoLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        selProyecto.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    public void setTerceroLogic(Property.ValueChangeEvent event) {
        if (isLoading) return;
        if (event.getProperty().getValue()!=null)
            setEditorTerceroLogic(event.getProperty().getValue().toString());
        selTercero.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }


    public void setEditorTerceroLogic(String codTercero)  {
        if (!GenUtil.strNullOrEmpty(codTercero)) {
            setEnableFields(true);
            if (selMoneda.getValue() == null) {
                numIngreso.setEnabled(false);
                numEgreso.setEnabled(false);
            }
            DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta",
                    configuractacajabancoRepo.findByActivoAndParaCajaAndParaTercero(true, true, true),
                    "Sel Tipo de Movimiento", "txtTipocuenta");
            selFuente.setValue(null);
            selFuente.setEnabled(false);
            // Reset those fields
            if (!isEdit) {
                selCtaContable.setValue(null);
                selRubroInst.setValue(null);
                selRubroProy.setValue(null);
            }
            //nombreTercero.setValue(destinoRepo.findByCodDestino(codTercero).getTxtNombredestino());
            setSaldos();
            setCajaLogic();
        }
    }

    public void setEditorLogic(String codProyecto) {
        if (!GenUtil.strNullOrEmpty(codProyecto)) {
            setEnableFields(true);
            if (selMoneda.getValue()==null) {
                numIngreso.setEnabled(false);
                numEgreso.setEnabled(false);
            }
            DataFilterUtil.bindComboBox(selRubroProy, "id.codCtaproyecto",
                    planproyectoRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodProyecto(
                            "N", GenUtil.getCurYear(), codProyecto),
                    "Sel Rubro proy", "txtDescctaproyecto");
            List<Scp_ProyectoPorFinanciera>
                    proyectoPorFinancieraList = proyectoPorFinancieraRepo.findById_CodProyecto(codProyecto);

            // Filter financiera if exists in Proyecto Por Financiera
            List<ScpFinanciera> financieraList = financieraRepo.findAll();
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
                DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta",
                        configuractacajabancoRepo.findByActivoAndParaCajaAndParaProyecto(true, true, true),
                        "Sel Tipo de Movimiento", "txtTipocuenta");
                // Reset those fields
                if (!isEdit) {
                    selCtaContable.setValue(null);
                    selRubroInst.setValue(null);
                }
                selTipoMov.setEnabled(true);
                selRubroInst.setEnabled(true);
                selCtaContable.setEnabled(true);
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.bindComboBox(selFuente, "codFinanciera", financieraEfectList,
                    "Sel Fuente", "txtDescfinanciera");
            if (financieraEfectList.size()==1)
                selFuente.select(financieraEfectList.get(0).getCodFinanciera());

            //nombreTercero.setValue(proyectoRepo.findByCodProyecto(codProyecto).getTxtDescproyecto());
            setSaldos();
            setCajaLogic();
        } else {
            //log.info("disabling fin y planproy");
            selFuente.setEnabled(false);
            selFuente.setValue("");
            selRubroProy.setEnabled(false);
            selRubroProy.setValue("");
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
        fieldGroup.bind(selProyecto, "codProyecto");
        fieldGroup.bind(selTercero, "codTercero");
        fieldGroup.bind(selMoneda, "codTipomoneda");
        fieldGroup.bind(selCaja, "codCtacontable");
        fieldGroup.bind(dataFechaComprobante, "fecFecha");

        if (isEdit && PEN.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(numEgreso, "numHabersol");
            fieldGroup.bind(numIngreso, "numDebesol");
        } else if (isEdit && USD.equals(item.getCodTipomoneda())) {
            fieldGroup.bind(numEgreso, "numHaberdolar");
            fieldGroup.bind(numIngreso, "numDebedolar");
        }
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);
        fieldGroup.bind(glosa, "txtGlosaitem");
        fieldGroup.bind(selResponsable, "codDestino");
        fieldGroup.bind(selLugarGasto, "codContraparte");
        fieldGroup.bind(selCodAuxiliar, "codDestinoitem");
        fieldGroup.bind(selTipoDoc, "codTipocomprobantepago");
        fieldGroup.bind(serieDoc, "txtSeriecomprobantepago");
        fieldGroup.bind(numDoc, "txtComprobantepago");
        fieldGroup.bind(fechaDoc, "fecComprobantepago");
        fieldGroup.bind(selCtaContable, "codContracta");
        fieldGroup.bind(selRubroInst, "codCtaespecial");
        fieldGroup.bind(selRubroProy, "codCtaproyecto");
        fieldGroup.bind(selFuente, "codFinanciera");
        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
            if (f instanceof ComboBox)
                ((ComboBox)f).setPageLength(20);
        }
        setEnableFields(false);
        selProyecto.setEnabled(true);
        selTercero.setEnabled(true);
        dataFechaComprobante.setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                numVoucher.setValue(item.getTxtCorrelativo());
            } else
                numVoucher.setValue(new Integer(item.getCodCajabanco()).toString());
            setEnableFields(true);
            setSaldos();
            setSaldoCaja();
            if (!GenUtil.objNullOrEmpty(item.getCodProyecto())) {
                setEditorLogic(item.getCodProyecto().toString());
            } else {
                setEditorTerceroLogic(item.getCodTercero());
            }
        } else {
            setMonedaLogic(item.getCodTipomoneda());
            numVoucher.setValue("");
            setSaldoDeCajas();
        }
        isEdit = false;

    }

    public void anularComprobante() {
        fieldGroup.discard();
    }

    public VsjCajabanco getVsjCajabanco() throws FieldGroup.CommitException {
        fieldGroup.commit();
        VsjCajabanco item = beanItem.getBean();
        setEnableFields(false);
        return item;
    }


    public boolean isPEN() {
        return PEN.equals(selMoneda.getValue().toString());
    }

    public void setCajaManejoView(CajaManejoView cajaManejoView) {
        this.cajaManejoView = cajaManejoView;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

}
