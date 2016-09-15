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
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
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

    public EntityManager em;

    public FieldGroup fieldGroup;

    public List<Field> allFields;

    boolean isEditing = true;

    public CajaManejoView cajaManejoView;

    @Autowired
    public ComprobanteView(VsjCajabancoRep repo, VsjConfiguractacajabancoRep configuractacajabancoRepo, ScpPlancontableRep planRepo,
                           ScpPlanespecialRep planEspRepo, ScpProyectoRep proyectoRepo, ScpDestinoRep destinoRepo,
                           ScpComprobantepagoRep comprobantepagoRepo, ScpFinancieraRep financieraRepo,
                           ScpPlanproyectoRep planproyectoRepo, Scp_ProyectoPorFinancieraRep proyectoPorFinancieraRepo,
                           Scp_ContraparteRep contraparteRepo, VsjConfiguracioncajaRep configuracioncajaRepo, EntityManager em) {
    	this.repo = repo;
        this.planproyectoRepo = planproyectoRepo;
        this.financieraRepo = financieraRepo;
        this.proyectoPorFinancieraRepo = proyectoPorFinancieraRepo;
        this.configuractacajabancoRepo = configuractacajabancoRepo;
        this.configuracioncajaRepo = configuracioncajaRepo;
        this.proyectoRepo = proyectoRepo;
        this.destinoRepo = destinoRepo;
        this.em = em;
        setSizeFull();
        addStyleName("crud-view");


        guardarBtn.setEnabled(false);
        anularBtn.setEnabled(false);
        allFields = new ArrayList<>();

        // Fecha
        PopupDateField pdf = dataFechaComprobante;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        pdf.addValidator(new BeanValidator(VsjCajabanco.class, "fecFecha"));
        pdf.addValueChangeListener(event -> {
            setSaldoCaja();
            setSaldos();
        });

        allFields.add(pdf);
        // Fecha Doc
        pdf = fechaDoc;
        prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        pdf.addValidator(new BeanValidator(VsjCajabanco.class, "fecComprobantepago"));

        allFields.add(pdf);
        // Proyecto
        //ComboBox selTercero = selTercero;
        //ComboBox selProyecto = selProyecto;
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()), "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(event -> setProyectoLogic(event));
        selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
//        selProyecto.setId("my-custom-combobox");
        allFields.add(selProyecto);
                //"setTimeout(function() { document.getElementById('my-custom-combobox').firstChild.select(); }, 0);");
        //gridCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", destinoRepo.findByIndTipodestino("3"), "Sel Tercero", "txtNombredestino");
        selTercero.addValueChangeListener(event -> setTerceroLogic(event));
        selTercero.addValidator(new TwoCombosValidator(selProyecto, true, null));
        //gridCaja.getColumn("codTercero").setEditorField(selTercero);
        allFields.add(selTercero);
        // Cta Caja
        selCaja.setEnabled(false);
        selLugarGasto.setEnabled(false);
        //gridCaja.getColumn("codContracta").setEditorField(selCtacontablecaja);

        selCaja.addValidator(new BeanValidator(VsjCajabanco.class, "codCtacontable"));
        allFields.add(selCaja);
        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(selMoneda, "codTipomoneda");
        selMoneda.addValueChangeListener(event -> {
            if (isEditing) {
                if (event.getProperty().getValue().toString().equals("0")) {
                    // Soles        0
                    DataFilterUtil.bindComboBox(selCaja, "id.codCtacontable", planRepo.
                            findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                                    "N", GenUtil.getCurYear(), "N", "101"), "Sel Caja", "txtDescctacontable");
                    selCaja.setEnabled(true);
                    setCajaLogic("0");
                    fieldGroup.unbind(numEgreso);
                    fieldGroup.unbind(numIngreso);
                    fieldGroup.bind(numIngreso, "numHabersol");
                    fieldGroup.bind(numEgreso, "numDebesol");
                    GenUtil.setDefaultsForNumberField(numIngreso);
                    GenUtil.setDefaultsForNumberField(numEgreso);
                    numEgreso.setEnabled(true);
                    numIngreso.setEnabled(true);
                } else {
                    // Dolares
                    DataFilterUtil.bindComboBox(selCaja, "id.codCtacontable", planRepo.
                            findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                                    "N", GenUtil.getCurYear(), "D", "101"), "Sel Caja", "txtDescctacontable");
                    selCaja.setEnabled(true);
                    setCajaLogic("1");
                    fieldGroup.unbind(numEgreso);
                    fieldGroup.unbind(numIngreso);
                    fieldGroup.bind(numIngreso, "numHaberdolar");
                    fieldGroup.bind(numEgreso, "numDebedolar");
                    numEgreso.setEnabled(true);
                    numIngreso.setEnabled(true);
                    GenUtil.setDefaultsForNumberField(numIngreso);
                    GenUtil.setDefaultsForNumberField(numEgreso);
                }
                setSaldoCaja();
            }
        });
        selMoneda.addValidator(new BeanValidator(VsjCajabanco.class, "codTipomoneda"));
        allFields.add(selMoneda);
        //gridCaja.getColumn("codTipomoneda").setEditorField(selTipomoneda);

        allFields.add(numEgreso);
        allFields.add(numIngreso);

        numEgreso.setEnabled(false);
        numIngreso.setEnabled(false);
        numIngreso.addValidator(new TwoNumberfieldsValidator(numEgreso, false, "Ingreso o egreso debe ser rellenado"));
        numEgreso.addValidator(new TwoNumberfieldsValidator(numIngreso, false, "Ingreso o egreso debe ser rellenado"));

        numIngreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (!"0,00".equals(event.getProperty().getValue())) {
                        numEgreso.setValue("");
                    }
                }
            }
        );
        numEgreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (!"0,00".equals(event.getProperty().getValue())) {
                        numIngreso.setValue("");
                    }
                }
            }
        );

        // Responsable
        //ComboBox selResponsable = new ComboBox();
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        selResponsable.addValidator(new BeanValidator(VsjCajabanco.class, "codDestino"));

        allFields.add(selResponsable);
        //gridCaja.getColumn("codDestino").setEditorField(selResponsable);
        // Lugar de gasto
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte", contraparteRepo.findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");
        selLugarGasto.addValidator(new BeanValidator(VsjCajabanco.class, "codContraparte"));

        allFields.add(selLugarGasto);
        // Cod. Auxiliar
        ComboBox selAuxiliar = selCodAuxiliar;
        DataFilterUtil.bindComboBox(selAuxiliar, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
        selCodAuxiliar.addValidator(new BeanValidator(VsjCajabanco.class, "codDestinoitem"));

        allFields.add(selCodAuxiliar);
        // Tipo doc
        ComboBox selComprobantepago = selTipoDoc;
        DataFilterUtil.bindComboBox(selComprobantepago, "codTipocomprobantepago", comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");
        allFields.add(selTipoDoc);

        // Cta Contable
        selCtaContable.setEnabled(false);
        DataFilterUtil.bindComboBox(selCtaContable, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");

        allFields.add(selCtaContable);
        // Rubro inst
        selRubroInst.setEnabled(false);
        DataFilterUtil.bindComboBox(selRubroInst, "id.codCtaespecial",
                planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        allFields.add(selRubroInst);
        // Rubro Proy
        selRubroProy.setEnabled(false);
        DataFilterUtil.bindComboBox(selRubroProy, "id.codCtaproyecto",
                planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        allFields.add(selRubroProy);
        // Fuente
        selFuente.setEnabled(false);
        ComboBox selFinanciera = selFuente;
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");
        allFields.add(selFuente);

        DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta", configuractacajabancoRepo.findByActivoAndParaCaja(true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        selTipoMov.setEnabled(false);
        selTipoMov.addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = configuractacajabancoRepo.findByCodTipocuenta(Integer.parseInt(tipoMov));
                //log.info("selected config: " + config);
                selCtaContable.setValue(config.getCodCtacontablegasto());
                selRubroInst.setValue(config.getCodCtaespecial());
            }

        });
        allFields.add(selTipoMov);
        allFields.add(glosa);
        glosa.addValidator(new BeanValidator(VsjCajabanco.class, "txtGlosaitem"));
        allFields.add(serieDoc);
        serieDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtSeriecomprobantepago"));
        allFields.add(numDoc);
        numDoc.addValidator(new BeanValidator(VsjCajabanco.class, "txtComprobantepago"));

        setEnableFields(false);
        viewLogic.init();
        //viewLogic.nuevoComprobante();
    }

    public void setEnableFields(boolean enabled) {
        for (Field f : allFields) {
            f.setEnabled(enabled);
        }
    }

    public void setSaldoCaja() {
        if (dataFechaComprobante.getValue()!=null && selCaja.getValue()!=null && selMoneda.getValue()!=null) {
            BigDecimal saldo = new ProcUtil(em).getSaldoCaja(dataFechaComprobante.getValue(),
                    selCaja.getValue().toString(), selMoneda.getValue().toString());
            if ("0".equals(selMoneda.getValue().toString())) {
                saldoCajaPEN.setValue(saldo.toString());
                saldoCajaUSD.setValue("");
            } else {
                saldoCajaUSD.setValue(saldo.toString());
                saldoCajaPEN.setValue("");
            }
        }
    }

    public void setSaldos() {
        if (dataFechaComprobante.getValue()!=null) {
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
            ProcUtil.Saldos res = null;
            if (!GenUtil.objNullOrEmpty(selProyecto.getValue())) {
                res = new ProcUtil(em).getSaldos(dataFechaComprobante.getValue(), selProyecto.getValue().toString(), null);
                saldoProyPEN.setValue(df.format(res.getSaldoPEN()));
                saldoProyUSD.setValue(df.format(res.getSaldoUSD()));
                saldoProyEUR.setValue(df.format(res.getSaldoEUR()));
            }
            if (!GenUtil.objNullOrEmpty(selTercero.getValue())) {
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

    public void setCajaLogic(String tipomoneda) {

        if (!GenUtil.objNullOrEmpty(selProyecto.getValue())) {
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
        } else if (!GenUtil.objNullOrEmpty(selTercero.getValue())) {
            List<VsjConfiguracioncaja> configs = configuracioncajaRepo.findByCodDestinoAndIndTipomoneda(
                    selTercero.getValue().toString(), tipomoneda);
            if (!configs.isEmpty()) {
                VsjConfiguracioncaja config = configs.get(0);
                selCaja.setValue(config.getCodCtacontable());
            }
        }
    }


    public void setProyectoLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null)
            setEditorLogic(event.getProperty().getValue().toString());
        //ComboBox selProyecto = (ComboBox)gridCaja.getColumn("codProyecto").getEditorField();
        selProyecto.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
    }

    public void setTerceroLogic(Property.ValueChangeEvent event) {
        setEnableFields(true);
        if (selMoneda.getValue()==null) {
            numIngreso.setEnabled(false);
            numEgreso.setEnabled(false);
        }
        // Sel Tipo Movimiento
        //selTipoMov.setEnabled(true);
        DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta",
                configuractacajabancoRepo.findByActivoAndParaCajaAndParaTercero(true, true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        selTercero.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
        selFuente.setValue(null);
        selFuente.setEnabled(false);
        // Reset those fields
        selCtaContable.setValue(null);
        selRubroInst.setValue(null);
        selRubroProy.setValue(null);
        selRubroProy.setEnabled(false);

        if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
            nombreTercero.setValue(destinoRepo.findByCodDestino(event.getProperty().getValue().toString()).getTxtNombredestino());
            setSaldos();
        }
    }


    public void setEditorLogic(String codProyecto) {
        ComboBox selFinanciera = selFuente;
        ComboBox selPlanproyecto = selRubroProy;

        if (codProyecto!=null && !codProyecto.isEmpty()) {
            setEnableFields(true);
            if (selMoneda.getValue()==null) {
                numIngreso.setEnabled(false);
                numEgreso.setEnabled(false);
            }
            DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto",
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
                selCtaContable.setValue(null);
                selRubroInst.setValue(null);
                selTipoMov.setEnabled(true);
                selRubroInst.setEnabled(true);
                selCtaContable.setEnabled(true);
            } else {
                financieraEfectList = financieraList;
            }
            DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraEfectList,
                    "Sel Fuente", "txtDescfinanciera");
            if (financieraEfectList.size()==1)
                selFinanciera.select(financieraEfectList.get(0).getCodFinanciera());

            nombreTercero.setValue(proyectoRepo.findByCodProyecto(codProyecto).getTxtDescproyecto());
            setSaldos();
        } else {
            log.info("disabling fin y planproy");
            selFinanciera.setEnabled(false);
            selFinanciera.setValue("");
            selPlanproyecto.setEnabled(false);
            selPlanproyecto.setValue("");
        }
    }

    public void bindForm(VsjCajabanco item) {
        isEditing = false;
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
        fieldGroup.bind(numIngreso, "numHabersol");
        fieldGroup.bind(numEgreso, "numDebesol");
        GenUtil.setDefaultsForNumberField(numIngreso);
        GenUtil.setDefaultsForNumberField(numEgreso);
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
        }
        setEnableFields(false);
        selProyecto.setEnabled(true);
        selTercero.setEnabled(true);
        dataFechaComprobante.setEnabled(true);

        if (item.getCodCajabanco()>0) {
            // EDITING
            if (!GenUtil.strNullOrEmpty(item.getTxtCorrelativo())) {
                numVoucher.setValue(item.getTxtCorrelativo());
            } else
                numVoucher.setValue(new Integer(item.getCodCajabanco()).toString());
            setEnableFields(true);
            setSaldos();
            setSaldoCaja();
        }
        isEditing = true;
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

    public void setCajaManejoView(CajaManejoView cajaManejoView) {
        this.cajaManejoView = cajaManejoView;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

}
