package org.sanjose.views;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
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

    private ComprobanteLogic viewLogic = new ComprobanteLogic(this);

    public VsjCajabanco item;
    
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

        //BeanItemContainer<VsjCajabanco> container = new BeanItemContainer(VsjCajabanco.class, repo.findAll());

        //gridCaja.setContainerDataSource(container);

        // Fecha
        PopupDateField pdf = dataFechaComprobante;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        //gridCaja.getColumn("fecFecha").setEditorField(pdf);
        //gridCaja.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Fecha Doc
        pdf = fechaDoc;
        prop = new ObjectProperty<Timestamp>(ts);
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        //gridCaja.getColumn("fecComprobantepago").setEditorField(pdf);
        //gridCaja.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        // Proyecto
        //ComboBox selTercero = selTercero;
        //ComboBox selProyecto = selProyecto;
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", proyectoRepo.findByFecFinalGreaterThan(new Date()), "Sel Proyecto", "txtDescproyecto");
        selProyecto.addValueChangeListener(event -> setProyectoLogic(event));
        selProyecto.addValidator(new TwoCombosValidator(selTercero, true, null));
//        selProyecto.setId("my-custom-combobox");

                //"setTimeout(function() { document.getElementById('my-custom-combobox').firstChild.select(); }, 0);");
        //gridCaja.getColumn("codProyecto").setEditorField(selProyecto);

        // Tercero
        DataFilterUtil.bindComboBox(selTercero, "codDestino", destinoRepo.findByIndTipodestino("3"), "Sel Tercero", "txtNombredestino");
        selTercero.addValueChangeListener(event -> setTerceroLogic(event));
        selTercero.addValidator(new TwoCombosValidator(selProyecto, true, null));
        //gridCaja.getColumn("codTercero").setEditorField(selTercero);

        // Cta Caja
        selCaja.setEnabled(false);
        selLugarGasto.setEnabled(false);
        //gridCaja.getColumn("codContracta").setEditorField(selCtacontablecaja);

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(selMoneda, "codTipomoneda");
        selMoneda.addValueChangeListener(event -> {


            if (event.getProperty().getValue().toString().equals("0")) {
                // Soles
                DataFilterUtil.bindComboBox(selCaja, "id.codCtacontable", planRepo.
                        findByFlgMovimientoAndId_TxtAnoprocesoAndIndTipomonedaAndId_CodCtacontableStartingWith(
                                "N", GenUtil.getCurYear(), "N", "101"), "Sel Caja", "txtDescctacontable");
                selCaja.setEnabled(true);
                setCajaLogic("0");
                fieldGroup.unbind(numEgreso);
                fieldGroup.unbind(numIngreso);
                fieldGroup.bind(numIngreso, "numHabersol");
                fieldGroup.bind(numEgreso, "numDebersol");


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
                fieldGroup.bind(numEgreso, "numDeberdolar");
                numEgreso.setEnabled(true);
                numIngreso.setEnabled(true);
            }
        });
        //gridCaja.getColumn("codTipomoneda").setEditorField(selTipomoneda);

        numIngreso.setDecimalAllowed(true);
        numIngreso.setDecimalPrecision(2);                 // maximum 2 digits after the decimal separator
        numIngreso.setDecimalSeparator('.');               // e.g. 1,5
        numIngreso.setDecimalSeparatorAlwaysShown(true);   // e.g. 12345 -> 12345,
        numIngreso.setMinimumFractionDigits(2);            // e.g. 123,4 -> 123,40
        numIngreso.setGroupingUsed(true);                  // use grouping (e.g. 12345 -> 12.345)
        numIngreso.setGroupingSeparator(' ');              // use '.' as grouping separator
        numIngreso.setGroupingSize(3);                     // 3 digits between grouping separators: 12.345.678
        numIngreso.setMinValue(0);                         // valid values must be >= 0 ...
        numIngreso.setMaxValue(999999.99);                     // ... and <= 999.9
        numIngreso.setErrorText("Numero invalido!"); // feedback message on bad input
        numIngreso.setNegativeAllowed(false);              // prevent negative numbers (defaults to true)

        numEgreso.setEnabled(false);
        numIngreso.setEnabled(false);

        numEgreso.setImmediate(true);
        numEgreso.setDecimalAllowed(true);
        numEgreso.setDecimalPrecision(2);                 // maximum 2 digits after the decimal separator
        numEgreso.setDecimalSeparator('.');               // e.g. 1,5
        numEgreso.setDecimalSeparatorAlwaysShown(true);   // e.g. 12345 -> 12345,
        numEgreso.setMinimumFractionDigits(2);            // e.g. 123,4 -> 123,40
        numEgreso.setGroupingUsed(true);                  // use grouping (e.g. 12345 -> 12.345)
        numEgreso.setGroupingSeparator(' ');              // use '.' as grouping separator
        numEgreso.setGroupingSize(3);                     // 3 digits between grouping separators: 12.345.678
        numEgreso.setMinValue(0);                         // valid values must be >= 0 ...
        numEgreso.setMaxValue(999999.99);                     // ... and <= 999.9
        numEgreso.setErrorText("Numero invalido!"); // feedback message on bad input
        numEgreso.setNegativeAllowed(false);              // prevent negative numbers (defaults to true)


        numIngreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (new BigDecimal(event.getProperty().getValue().toString()).doubleValue()!=0) {
                        numEgreso.setValue("");
                    }
                }
            }
        );
        numEgreso.addValueChangeListener(event -> {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (new BigDecimal(event.getProperty().getValue().toString()).doubleValue()!=0) {
                        numIngreso.setValue("");
                    }
                }
            }
        );

        // Responsable
        //ComboBox selResponsable = new ComboBox();
        DataFilterUtil.bindComboBox(selResponsable, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Responsable", "txtNombredestino");
        //gridCaja.getColumn("codDestino").setEditorField(selResponsable);
        // Lugar de gasto
        DataFilterUtil.bindComboBox(selLugarGasto, "codContraparte", contraparteRepo.findAll(),
                "Sel Lugar de Gasto", "txt_DescContraparte");

        // Cod. Auxiliar
        ComboBox selAuxiliar = selCodAuxiliar;
        DataFilterUtil.bindComboBox(selAuxiliar, "codDestino", destinoRepo.findByIndTipodestinoNot("3"),
                "Auxiliar", "txtNombredestino");
        //gridCaja.getColumn("codDestinoitem").setEditorField(selAuxiliar);

        // Tipo doc
        ComboBox selComprobantepago = selTipoDoc;
        DataFilterUtil.bindComboBox(selComprobantepago, "codTipocomprobantepago", comprobantepagoRepo.findAll(),
                "Sel Tipo", "txtDescripcion");
        //gridCaja.getColumn("codTipocomprobantepago").setEditorField(selComprobantepago);


        // Cta Contable
        selCtaContable.setEnabled(false);
        DataFilterUtil.bindComboBox(selCtaContable, "id.codCtacontable", planRepo.findByFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith("N", GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        //gridCaja.getColumn("codCtacontable").setEditorField(selCtacontable);

        // Rubro inst
        selRubroInst.setEnabled(false);
        DataFilterUtil.bindComboBox(selRubroInst, "id.codCtaespecial",
                planEspRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");

        // Rubro Proy
        selRubroProy.setEnabled(false);
        DataFilterUtil.bindComboBox(selRubroProy, "id.codCtaproyecto",
                planproyectoRepo.findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getCurYear()),
                "Sel Rubro proy", "txtDescctaproyecto");
        //gridCaja.getColumn("codCtaproyecto").setEditorField(selPlanproyecto);

        // Fuente
        selFuente.setEnabled(false);
        ComboBox selFinanciera = selFuente;
        DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", financieraRepo.findAll(),
                "Sel Fuente", "txtDescfinanciera");
        //gridCaja.getColumn("codFinanciera").setEditorField(selFinanciera);

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
        viewLogic.init();
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

        selRubroInst.setEnabled(true);
        selCtaContable.setEnabled(true);
        selLugarGasto.setEnabled(true);
        // Sel Tipo Movimiento
        selTipoMov.setEnabled(true);
        DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta",
                configuractacajabancoRepo.findByActivoAndParaCajaAndParaTercero(true, true, true),
                "Sel Tipo de Movimiento", "txtTipocuenta");
        //ComboBox selTercero = (ComboBox)gridCaja.getColumn("codTercero").getEditorField();
        selTercero.getValidators().stream().forEach(validator -> validator.validate(event.getProperty().getValue()));
        selFuente.setValue(null);
        selFuente.setEnabled(false);
        // Reset those fields
        selCtaContable.setValue(null);
        selRubroInst.setValue(null);
        //DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto", new ArrayList<ScpPlanproyecto>(),
        //        "-------", null);
        selRubroProy.setValue(null);
        selRubroProy.setEnabled(false);

        if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
            nombreTercero.setValue(destinoRepo.findByCodDestino(event.getProperty().getValue().toString()).getTxtNombredestino());
            ProcUtil.Saldos res = new ProcUtil(em).getSaldos(dataFechaComprobante.getValue(),null, event.getProperty().getValue().toString());
            saldoProyPEN.setValue(res.getSaldoPEN().toString());
            saldoProyUSD.setValue(res.getSaldoUSD().toString());
            saldoProyEUR.setValue("");
        }
//        log.info("got saldos: "  + res);
    }


    public void setEditorLogic(String codProyecto) {
        ComboBox selFinanciera = selFuente;
        ComboBox selPlanproyecto = selRubroProy;

        if (codProyecto!=null && !codProyecto.isEmpty()) {
            selFinanciera.setEnabled(true);
            selPlanproyecto.setEnabled(true);
            selLugarGasto.setEnabled(true);
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
            ProcUtil.Saldos res = new ProcUtil(em).getSaldos(dataFechaComprobante.getValue(),codProyecto,null);
            saldoProyPEN.setValue(res.getSaldoPEN().toString());
            saldoProyUSD.setValue(res.getSaldoUSD().toString());
            saldoProyEUR.setValue(res.getSaldoEUR().toString());
            log.info("got saldos: "  + res);
        } else {
            //DataFilterUtil.bindComboBox(selTipoMov, "codTipocuenta",
            //        configuractacajabancoRepo.findByParaCajaAndParaTercero(true, true),
            //        "Sel Tipo de Movimiento", "txtTipocuenta");
            //DataFilterUtil.bindComboBox(selFinanciera, "codFinanciera", new ArrayList<ScpFinanciera>(),
            //        "-------", null);
            log.info("disabling fin y planproy");
            selFinanciera.setEnabled(false);
            selFinanciera.setValue("");
            //DataFilterUtil.bindComboBox(selPlanproyecto, "id.codCtaproyecto", new ArrayList<ScpPlanproyecto>(),
            //        "-------", null);
            selPlanproyecto.setEnabled(false);
            selPlanproyecto.setValue("");
        }
    }

    public void bindForm(VsjCajabanco item) {

        fieldGroup.setItemDataSource(new BeanItem<VsjCajabanco>(item));
        fieldGroup = new BeanFieldGroup<VsjCajabanco>(VsjCajabanco.class);
        fieldGroup.bind(selProyecto, "codProyecto");
        fieldGroup.bind(selTercero, "codTercero");
        fieldGroup.bind(selMoneda, "codTipomoneda");
        fieldGroup.bind(selCaja, "codContracta");
        fieldGroup.bind(dataFechaComprobante, "fecFecha");
        fieldGroup.bind(numIngreso, "numHabersol");
        fieldGroup.bind(numEgreso, "numDebersol");
        fieldGroup.bind(glosa, "txtGlosaitem");
        fieldGroup.bind(selResponsable, "codDestino");
        fieldGroup.bind(selLugarGasto, "codContraparte");
        fieldGroup.bind(selCodAuxiliar, "codDestinoitem");
        fieldGroup.bind(selTipoDoc, "codTipocomprobantepago");
        fieldGroup.bind(serieDoc, "txtSeriecomprobantepago");
        fieldGroup.bind(numDoc, "txtComprobantepago");
        fieldGroup.bind(fechaDoc, "fecComprobantepago");
        fieldGroup.bind(selCtaContable, "codCtacontable");
        fieldGroup.bind(selRubroInst, "codCtaespecial");
        fieldGroup.bind(selRubroProy, "codCtaproyecto");
        fieldGroup.bind(selFuente, "codFinanciera");
    }

    /*public VsjCajabanco getVsjCajabanco() {
        //fieldGroup.getItemDataSource().


    }
*/



    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

}
