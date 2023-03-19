package org.sanjose.views.rendicion;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.converter.BigDecimalConverter;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.render.DateNotNullRenderer;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.TwoNumberfieldsValidator;
import org.sanjose.views.sys.ComprobanteWarnGuardar;
import org.sanjose.views.dict.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;
import tm.kod.widgets.numberfield.NumberField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * manView, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the manView makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class RendicionSimpleItemLogic extends RendicionSharedLogic implements Serializable, ComprobanteWarnGuardar {

    private static final Logger log = LoggerFactory.getLogger(RendicionSimpleItemLogic.class);
    protected ScpRendiciondetalle item;
    protected boolean isLoading = true;
    protected boolean isEdit = false;
    protected NavigatorViewing navigatorView;
    protected Character moneda;
    protected ScpRendicioncabecera rendicioncabecera;
    protected FieldGroup fieldGroup;
    protected List<Field> setAllFields = new ArrayList<>();
    protected RendicionSimpleOperView view;
    private BeanItem<ScpRendiciondetalle> beanItem;

    private ComboBox selProyecto = new ComboBox();
    private ComboBox selCtacontable = new ComboBox();
    private ComboBox selCtaespecial = new ComboBox();
    private PopupDateField selFechaDoc = new PopupDateField();
    private TextField txtGlosaDetalle = new TextField();
    private TextField txtSerieDoc = new TextField();
    private TextField txtNumDoc = new TextField();
    
    private ItemClickEvent.ItemClickListener gridItemClickListener;
    private FieldGroup.CommitHandler gridCommitHandler;
    private Property.ValueChangeListener tipoCambioListener;

    private Property.ValueChangeListener ingresoFieldValueChangeListener;
    private Property.ValueChangeListener egresoFieldValueChangeListener;

    public void init(RendicionSimpleOperView view) {
        this.view = view;
        tipoCambioListener = (Property.ValueChangeListener) valueChangeEvent -> setTipoCambios((Date)valueChangeEvent.getProperty().getValue());
        setAllFields.add(view.getSetAllProyecto());
        setAllFields.add(view.getSetAllFuente());
        setAllFields.add(view.getSetAllPartida());
        setAllFields.add(view.getSetAllLugarGasto());
        setAllFields.add(view.getSetAllContable());
        setAllFields.add(view.getSetAllRubrInst());
        setAllFields.add(view.getSetAllTcambioText());
        setAllFields.add(view.getSetAllFechaDoc());
        setAllFields.add(view.getSetAllFechaPago());
        manView = MainUI.get().getRendicionSimpleManejoView();
        navigatorView = manView;
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
                //TODO - wg jakiej daty szukac projektow?
            refreshProyectoYcuentaPorFecha((Date)event.getProperty().getValue());
        });

        // Fecha registro
//
//        ts = new Timestamp(System.currentTimeMillis());
//        prop = new ObjectProperty<>(ts);
//        view.getDataFechaRegistro().setPropertyDataSource(prop);
//        view.getDataFechaRegistro().setConverter(DateToTimestampConverter.INSTANCE);
//        view.getDataFechaRegistro().setResolution(Resolution.DAY);
//        view.getDataFechaRegistro().setValue(new Date());

        //manView.getNumVoucher().setEnabled(false);

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable1(), "codDestino", DataUtil.loadDestinos(view.getService()),
                "txtNombre");

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.getSelMoneda(), "codTipomoneda");
        view.getSelMoneda().addValueChangeListener(event -> { if (event.getProperty().getValue()!=null) setMonedaLogic(event.getProperty().getValue().toString().charAt(0));});

        view.getNumTotalAnticipo().addBlurListener(event -> view.setTotal((Character)view.getSelMoneda().getValue()));

        // ------------ DETALLE
//        // Tipo doc
//        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
//                "txtDescripcion");

//        DataFilterUtil.bindComboBox(view.getSelTipoMov(), view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true), "Tipo Movimiento",
//                "codTipocuenta", "txtTipocuenta", "id");

        //getSelTipoMov().setEnabled(false);
//        view.getSelTipoMov().addValueChangeListener(event -> {
//            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
//                String tipoMov = event.getProperty().getValue().toString();
//                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findById(Integer.parseInt(tipoMov));
//                if (config != null && view.getContainer().getItem(item)!=null) {
//                    ScpRendiciondetalle sr = view.getContainer().getItem(item).getBean();
//                    sr.setCodCtacontable(config.getCodCtacontablegasto());
//                    sr.setCodCtaespecial(config.getCodCtaespecial());
//                    view.grid.refreshRows(sr);
//                }
//            }
//        });
//        //manView.getFechaPago().setPropertyDataSource(prop);
//        view.getFechaPago().setConverter(DateToTimestampConverter.INSTANCE);
//        view.getFechaPago().setResolution(Resolution.DAY);

        // DETALLE - GRID

        // Fecha Pago
//        pdf.setPropertyDataSource(prop);
//        pdf.setConverter(DateToTimestampConverter.INSTANCE);
//        pdf.setResolution(Resolution.DAY);
        //view.grid.getColumn("fecPagocomprobantepago").setEditorField(pdf);
        //view.grid.getColumn("fecPagocomprobantepago").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        //pdf.addValueChangeListener(e -> manView.getFechaPago().setValue((Date)e.getProperty().getValue()));

        // Fecha Doc
//        ObjectProperty<Timestamp> prop = new ObjectProperty<>(rendicioncabecera.getFecComprobante());
//        pdf = new PopupDateField();
        selFechaDoc.setPropertyDataSource(prop);
        selFechaDoc.setConverter(DateToTimestampConverter.INSTANCE);
        selFechaDoc.setResolution(Resolution.DAY);
        view.grid.getColumn("fecPagocomprobantepago").setEditorField(selFechaDoc);
        view.grid.getColumn("fecPagocomprobantepago").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        selFechaDoc.removeValueChangeListener(tipoCambioListener);
        selFechaDoc.addValueChangeListener(tipoCambioListener);


        txtGlosaDetalle.setMaxLength(70);
        view.grid.getColumn("txtGlosaitem").setEditorField(txtGlosaDetalle);

        txtSerieDoc.setMaxLength(5);
        view.grid.getColumn("txtSeriecomprobantepago").setEditorField(txtSerieDoc);
        txtNumDoc.setMaxLength(20);
        view.grid.getColumn("txtComprobantepago").setEditorField(txtNumDoc);

        // Proyecto
        selProyecto.addValueChangeListener(this::setProyectoLogic);
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");
        view.grid.getColumn("codProyecto").setEditorField(selProyecto);


        // Tipo doc
        ComboBox selTipoDoc = new ComboBox();
        DataFilterUtil.bindComboBox(selTipoDoc, "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "txtDescripcion");
        view.grid.getColumn("codTipocomprobantepago").setEditorField(selTipoDoc);


        // Auxiliar
        ComboBox selAuxiliar = new ComboBox();
        DataFilterUtil.bindComboBox(selAuxiliar, "codDestino", DataUtil.loadDestinos(view.getService()),
                "txtNombre");
        view.grid.getColumn("codDestino").setEditorField(selAuxiliar);


        // Cta Contable
        DataFilterUtil.bindComboBox(selCtacontable, "id.codCtacontable",view.getService().getPlanRepo().findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith('0', 'N', GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        view.grid.getColumn("codCtacontable").setEditorField(selCtacontable);

        // Rubro inst
        DataFilterUtil.bindComboBox(selCtaespecial, "id.codCtaespecial",
               view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Sel cta especial", "txtDescctaespecial");
        view.grid.getColumn("codCtaespecial").setEditorField(selCtaespecial);

        // Lugar Gasto
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

        // Tipo Moneda
//        ComboBox selTipomoneda = new ComboBox();
//        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "codTipomoneda", "Moneda");
//        view.grid.getColumn("codTipomoneda").setEditorField(selTipomoneda);
//        selTipomoneda.addValueChangeListener(e -> moneda = (Character)e.getProperty().getValue());

        String[] numFields = { "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar", "numHabermo", "numDebemo" };
        Arrays.asList(numFields).forEach(f -> {
            NumberField nf = new NumberField();
            ViewUtil.setDefaultsForNumberField(nf);
            if (f.contains("Tc")) {
                nf.setConverter(new BigDecimalConverter(4));
                nf.setDecimalLength(4);
            }
            view.grid.getColumn(f).setEditorField(nf);
        });
        addValidators();
        // Editing Destino
        view.getBtnResponsable().addClickListener(event -> editDestino(view.getSelResponsable1()));
        //view.getBtnAuxiliar().addClickListener(event -> editDestino(view.getSelCodAuxiliar()));

        ViewUtil.setFieldsNullRepresentation(view.grid.getEditorFieldGroup());
    }

    // TIPO CAMBIO LOGIC AND RECALCULATION

    private void setTipoCambios(Date fecha) {
        setTipoCambios(fecha, beanItem);
        List<ScpRendiciondetalle> detsToRefresh = new ArrayList<>();
        detsToRefresh.add(beanItem.getBean());
        view.grid.refreshRows(detsToRefresh);
        calculateInOtherCurrencies();
    }

    private void setTipoCambios(Date fecha, BeanItem<ScpRendiciondetalle> bItem) {
        Character mon = (Character)bItem.getItemProperty("codTipomoneda").getValue();
        if (mon==null)
            mon = moneda;
        if (fecha==null || isLoading)
            return;
        fecha = GenUtil.getBeginningOfDay(fecha);
        // Get Tipo Cambio - if not exists, try to download
        List<ScpTipocambio> tipocambios = view.getService().getTipocambioRep().findById_FecFechacambio(fecha);
        ScpTipocambio tipocambio = null;
        if (tipocambios.isEmpty()) {
            try {
                TipoCambio.checkTipoCambio(fecha, view.getService().getTipocambioRep());
                tipocambios = view.getService().getTipocambioRep().findById_FecFechacambio(fecha);
                if (tipocambios.isEmpty())
                    return;
            } catch (TipoCambio.TipoCambioNoExiste | TipoCambio.TipoCambioNoSePuedeBajar te) {
                log.debug(te.getLocalizedMessage());
                return;
            }
        }
        tipocambio = tipocambios.get(0);
        if (mon==GenUtil.PEN) {
            bItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTccdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcceuro()))
                bItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcceuro());
        } else if (mon==GenUtil.USD) {
            bItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTcvdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcceuro()))
                bItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcceuro());
        } else if (mon==GenUtil.EUR) {
            bItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTccdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcveuro()))
                bItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcveuro());
        }
    }

    protected void calculateInOtherCurrencies() {
        calculateInOtherCurrencies(beanItem);
    }

    protected void calculateInOtherCurrencies(BeanItem<ScpRendiciondetalle>  bItem) {
        String[] numFields = {"numHaber", "numDebe"};
        Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(bItem.getBean().getCodTipomoneda()), bItem));
    }

    protected void calculateInOtherCurrencies(String propertyName, BeanItem<ScpRendiciondetalle> bItem) {
        if (view.grid.getColumn(propertyName).getEditorField()==null)
            return;
        Object newVal = bItem.getItemProperty(propertyName).getValue();
        if (newVal==null)
            return;
        BigDecimal newNum = (BigDecimal)newVal;
        Character moneda = GenUtil.getNumMonedaFromDescContaining(propertyName);
        // Ignore in othter currencies than the one chosen for input
        if (!moneda.equals(bItem.getItemProperty("codTipomoneda").getValue()))
            return;
        String haberDebe = propertyName.contains("Haber") ? "Haber" : "Debe";
        BigDecimal tcDolar = (BigDecimal)bItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).getValue();
        BigDecimal tcEuro = (BigDecimal)bItem.getItemProperty("numTc" + GenUtil.getDescMoneda(GenUtil.EUR)).getValue();
        if (moneda==GenUtil.PEN) {
            if (!GenUtil.isNullOrZero(tcDolar))
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.USD)).setValue(
                        newNum.setScale(2, RoundingMode.HALF_EVEN).divide(tcDolar, RoundingMode.HALF_EVEN));
            if (!GenUtil.isNullOrZero(tcEuro))
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.EUR)).setValue(
                        newNum.setScale(2, RoundingMode.HALF_EVEN).divide(tcEuro, RoundingMode.HALF_EVEN));
        } else if (moneda==GenUtil.USD) {
            if (!GenUtil.isNullOrZero(tcDolar))
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.PEN)).setValue(
                    newNum.setScale(2, RoundingMode.HALF_EVEN).multiply(tcDolar));
            if (!GenUtil.isNullOrZero(tcEuro) && !GenUtil.isNullOrZero(tcDolar))
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.EUR)).setValue(
                    newNum.setScale(2, RoundingMode.HALF_EVEN).multiply(tcDolar).divide(tcEuro, RoundingMode.HALF_EVEN));
        } else if (!GenUtil.isNullOrZero(tcEuro) && !GenUtil.isNullOrZero(tcDolar)) {
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.PEN)).setValue(
                        newNum.setScale(2, RoundingMode.HALF_EVEN).multiply(tcEuro));
                bItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.USD)).setValue(
                        newNum.setScale(2, RoundingMode.HALF_EVEN).multiply(tcEuro).divide(tcDolar, RoundingMode.HALF_EVEN));
        }
    }

    protected void ajusteTipoCambio() {
        ScpRendiciondetalle rend = view.getSelectedRow();
        if (rend!=null && beanItem!=null) {
            int scale = 2;
            BigDecimal difSol = new BigDecimal(view.getNumDifsol().getValue());
            BigDecimal difDolar = new BigDecimal(view.getNumDifdolar().getValue());
            while (scale<8 && (!GenUtil.isZero(difSol) || !GenUtil.isZero(difDolar))) {
                if (!GenUtil.isZero(difSol) && (!GenUtil.isZero(difDolar))) {
                    Notification.show("Error al ajustar - soles o dolares deben ser balancados!", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (!GenUtil.isZero(difDolar) && rend.getCodTipomoneda() != GenUtil.USD) {
                    BigDecimal tcDolar = rend.getNumHabersol().subtract(rend.getNumDebesol()).setScale(scale)
                            .divide(
                                    rend.getNumHaberdolar().subtract(rend.getNumDebedolar()).subtract(difDolar)
                                    , RoundingMode.HALF_EVEN);
                    rend.setNumTcvdolar(tcDolar);
                } else if (!GenUtil.isZero(difSol) && rend.getCodTipomoneda() != GenUtil.PEN) {
                    BigDecimal tcDolar = rend.getNumHabersol().subtract(rend.getNumDebesol()).subtract(difSol)
                            .divide(
                                    rend.getNumHaberdolar().subtract(rend.getNumDebedolar())
                                    , RoundingMode.HALF_EVEN).setScale(scale, RoundingMode.HALF_EVEN);
                    rend.setNumTcvdolar(tcDolar);
                }
                //beanItem.getItemProperty("num")
                List<ScpRendiciondetalle> detsToRefresh = new ArrayList<>();
                detsToRefresh.add(rend);
                view.grid.refreshRows(detsToRefresh);
                calculateInOtherCurrencies();
                view.grid.refreshRows(detsToRefresh);
                moneda = (Character) view.getSelMoneda().getValue();
                view.setTotal(moneda);
                view.calcFooterSums();
                difSol = new BigDecimal(view.getNumDifsol().getValue());
                difDolar = new BigDecimal(view.getNumDifdolar().getValue());
                scale++;
            }
        }
    }
    // END OF TIPO CAMBIO LOGIC

    private void setMonedaLogic(Character moneda) {
        // Remove validators on ingreso and egreso fields

        NumberField egresoField = (NumberField)view.grid.getColumn("numDebe" + GenUtil.getDescMoneda(this.moneda)).getEditorField();
        NumberField ingresoField = (NumberField)view.grid.getColumn("numHaber" + GenUtil.getDescMoneda(this.moneda)).getEditorField();
        if (egresoField!=null && ingresoField!=null) {
            egresoField.removeAllValidators();
            ingresoField.removeAllValidators();
            egresoField.removeValueChangeListener(egresoFieldValueChangeListener);
            ingresoField.removeValueChangeListener(ingresoFieldValueChangeListener);
        }

        // Update Tipo Moneda on every item only if not advanced manView
        this.moneda = moneda;
        if (!view.isVistaFull) {
            updateItemProperty("codTipomoneda", moneda, view.getContainer().getItemIds());
            ViewUtil.filterColumnsByMoneda(view.getGrid(), moneda);
        }
        ViewUtil.filterColumnsDisableByMoneda(view.getGrid(), moneda);
        // Gasto Total i Saldo Pendiente...
        view.setTotal(moneda);
        if (moneda.equals(GenUtil.EUR)) view.getSetAllTcambioText().setInputPrompt("T. Cambio EUR");
        else view.getSetAllTcambioText().setInputPrompt("T. Cambio USD");

        // add new validators for ingreso and egreso
        addEgresoAndIngresoValidatorsForMoneda(moneda);
    }

    private void updateProperty(Field f, String itemProperty) {
        if (!GenUtil.objNullOrEmpty(f.getValue()))
            updateItemProperty(itemProperty, f.getValue());
    }

    private void updateItemProperty(String itemProperty, Object newVal) {
        List<ScpRendiciondetalle> dets = new ArrayList<>();
        view.grid.getSelectedRows().forEach(e -> dets.add((ScpRendiciondetalle)e));
        updateItemProperty(itemProperty, newVal, dets);
    }

    private void updateItemProperty(String itemProperty, Object newVal, List<ScpRendiciondetalle> items) {
        List<ScpRendiciondetalle> detsToRefresh = new ArrayList<>();
        BeanItem<ScpRendiciondetalle> oldBeanItem = beanItem;
        for (ScpRendiciondetalle sr : items) {
            if (newVal!=null) {
                if (newVal instanceof Date)
                    newVal = new Timestamp(((Date) newVal).getTime());
                if (itemProperty.startsWith("numTc")) {
                    newVal = new BigDecimal(newVal.toString());
                    itemProperty = moneda.equals(GenUtil.EUR) ? "numTcmo" : "numTcvdolar";
                }
                view.getContainer().getItem(sr).getItemProperty(itemProperty).setValue(newVal);
                if (itemProperty.startsWith("numTc")) {
                    beanItem = new BeanItem<>(sr);
                    calculateInOtherCurrencies();
                }
                detsToRefresh.add(sr);
            }
        }
        beanItem = oldBeanItem;
        detsToRefresh.forEach(e -> {
            try {
                e = view.getService().saveRendicionOperacion(e.getScpRendicioncabecera(), e);
                view.grid.refreshRows(e);
            } catch (CommitException ce) {
                Notification.show("Error al aplicar a marcados: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.warn("Got Commit Exception: " + ce.getMessage());
            }
        });
    }

    public void addValidators() {
        // Validators
        //view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecComprobante"));
        selFechaDoc.addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecPagocomprobantepago"));
        view.getDataFechaRegistro().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecFregistro"));
        view.getSelResponsable1().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "codDestino"));
        //view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codDestino"));
        view.getTxtGlosaCabeza().setDescription("Glosa Cabeza");
        view.getTxtGlosaCabeza().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "txtGlosa"));
        txtGlosaDetalle.setDescription("Glosa Detalle");
        txtGlosaDetalle.addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtGlosaitem"));
        txtSerieDoc.addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtSeriecomprobantepago"));
        txtNumDoc.addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtComprobantepago"));

//        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codTipomov"));
//        // Check saldos and warn
//        saldoChecker = new SaldoChecker(manView.getNumEgreso(), manView.getSaldoCuenta(), manView.getSaldoProyPEN(), this);
    }



    public void addEgresoAndIngresoValidatorsForMoneda(Character moneda) {
        NumberField egresoField = (NumberField)view.grid.getColumn("numDebe" + GenUtil.getDescMoneda(moneda)).getEditorField();
        NumberField ingresoField = (NumberField)view.grid.getColumn("numHaber" + GenUtil.getDescMoneda(moneda)).getEditorField();

        egresoField.addValidator(
                new TwoNumberfieldsValidator(ingresoField,
                        false, "Ingreso y egreso debe tener valor"));
        ingresoField.addValidator(
                new TwoNumberfieldsValidator(egresoField,
                        false, "Ingreso y egreso debe tener valor"));

        ingresoFieldValueChangeListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (GenUtil.isInvertedZero(event.getProperty().getValue())) {
                        egresoField.setValue("");
                    }
                }
            }
        };
        egresoFieldValueChangeListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                    if (GenUtil.isInvertedZero(event.getProperty().getValue())) {
                        ingresoField.setValue("");
                    }
                }
            }
        };

        ingresoField.addValueChangeListener(ingresoFieldValueChangeListener);
        egresoField.addValueChangeListener(egresoFieldValueChangeListener);
    }

    private void setProyectoLogic(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue()!=null) {
            String codProyecto = event.getProperty().getValue().toString();
            ComboBox selFinanciera = (ComboBox)view.grid.getColumn("codFinanciera").getEditorField();
            ComboBox selPlanproyecto = (ComboBox) view.grid.getColumn("codCtaproyecto").getEditorField();
            DataFilterUtil.setEditorLogicPorProyecto(codProyecto, selFinanciera, selPlanproyecto, view.getService().getPlanproyectoRepo(), view.getService().getProyectoPorFinancieraRepo(), view.getService().getFinancieraRepo());
        }
        ComboBox selProyecto = (ComboBox)view.grid.getColumn("codProyecto").getEditorField();
        selProyecto.getValidators().forEach(validator -> validator.validate(event.getProperty().getValue()));
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
        DataFilterUtil.refreshComboBox(view.getSelResponsable1(), "codDestino", DataUtil.loadDestinos(view.getService()),
                "txtNombre");
        //DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
        //        "txtNombredestino");
    }

    private void refreshProyectoYcuentaPorFecha(Date newFecha) {
        if (newFecha==null || view.getService().getPlanRepo()==null) return;
        // Proyecto
        DataFilterUtil.refreshComboBox(selProyecto, "codProyecto", view.getService().getProyectoRepo().
                            findByFecFinalGreaterThanEqualAndFecInicioLessThanEqualOrFecFinalLessThanEqual(newFecha, newFecha, GenUtil.getBegin20thCent()),
                    "txtDescproyecto");
        // Cta Contable
        DataFilterUtil.refreshComboBox(selCtacontable, "id.codCtacontable", view.getService().getPlanRepo().
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith('0', 'N', GenUtil.getYear(newFecha), ""), "txtDescctacontable");

        // Rubro inst
        DataFilterUtil.refreshComboBox(selCtaespecial, "id.codCtaespecial",
                view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getYear(newFecha)),
                "txtDescctaespecial");

        // Rubro Proy
        ComboBox selPlanproyecto = new ComboBox();
        DataFilterUtil.refreshComboBox(selPlanproyecto, "id.codCtaproyecto",
                view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getYear(newFecha)),
                "txtDescctaproyecto");
    }


    public void bindForm(ScpRendiciondetalle item) {
        isLoading = true;
        isEdit = !GenUtil.objNullOrEmpty(item.getId());
        beanItem = new BeanItem<>(item);
        this.item = item;
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        //view.getTxtGlosaDetalle().setValue(null);
        //view.getNumItem().setValue("");
        //fieldGroup.bind(view.getTxtGlosaDetalle(), "txtGlosaitem");
        //fieldGroup.bind(view.getSelCodAuxiliar(), "codDestino");
        //fieldGroup.bind(view.getSelTipoDoc(), "codTipocomprobantepago");
        //fieldGroup.bind(view.getTxtSerieDoc(), "txtSeriecomprobantepago");
        //fieldGroup.bind(view.getTxtNumDoc(), "txtComprobantepago");
        //fieldGroup.bind(view.getFechaDoc(), "fecComprobantepago");
        //fieldGroup.bind(view.getFechaPago(), "fecPagocomprobantepago");
        //Date fechaPagoComprobpago = (Date)beanItem.getItemProperty("fecPagocomprobantepago").getValue();
        //if (fechaPagoComprobpago!=null && fechaPagoComprobpago.getTime()==GenUtil.getBegin20thCent().getTime())
        //    view.getFechaPago().setValue(null);
        //fieldGroup.bind(view.getSelTipoMov(), "codTipomov");

        ViewUtil.setFieldsNullRepresentation(fieldGroup);
        //view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            setNumVoucher(item);
        }
        else if (item.getScpRendicioncabecera() != null) {
            if (item.getId() == null && item.getScpRendicioncabecera().getCodComprobante() != null) {
                log.debug("is NOT Edit in bindForm but ID is null");
                view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
                //view.getNumItem().setValue(String.valueOf(view.getContainer().size() + 1));
            }
        }
        isEdit = false;
    }

    protected void setNumVoucher(ScpRendiciondetalle item) {
        if (item.getScpRendicioncabecera() != null && !GenUtil.strNullOrEmpty(item.getScpRendicioncabecera().getCodComprobante())) {
            view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
            //view.getNumItem().setValue(String.valueOf(item.getId().getNumNroitem()));
        }
        view.getNumVoucher().setEnabled(false);
    }

    // Buttons
    protected void nuevoItem() {

        item = new ScpRendiciondetalle();
        item.setCodTipomoneda(rendicioncabecera.getCodTipomoneda());
        ScpRendiciondetallePK id = new ScpRendiciondetallePK();
        id = id.prepareToSave(item);
        id.setCodRendicioncabecera(rendicioncabecera.getCodRendicioncabecera());
        item.setId(id);
        item.getId().setCodComprobante(rendicioncabecera.getCodComprobante());
        if (item.getId().getNumNroitem()==null)
            item.getId().setNumNroitem(view.getContainer().size()+1);
        item.setFecComprobante(rendicioncabecera.getFecComprobante());
        item.setFecComprobantepago(rendicioncabecera.getFecComprobante());
        item.setTxtComprobantepago("");
        item.setTxtSeriecomprobantepago("");
        item.setTxtGlosaitem(rendicioncabecera.getTxtGlosa());

        if (rendicioncabecera != null)
            item.setScpRendicioncabecera(rendicioncabecera);
        item.setFecComprobantepago(new Timestamp(view.getDataFechaComprobante().getValue().getTime()));
        // if selected other item then copy most fields
        if (!view.getGrid().getContainerDataSource().getItemIds().isEmpty()) {
            for (Object o : view.getGrid().getContainerDataSource().getItemIds()) {
                ScpRendiciondetalle prevItem = (ScpRendiciondetalle)o;
                if (prevItem.getId().getNumNroitem()==item.getId().getNumNroitem()-1) {
                    item.setCodDestino(prevItem.getCodDestino());
                    item.setCodTipocomprobantepago(prevItem.getCodTipocomprobantepago());
                    item.setTxtComprobantepago(prevItem.getTxtComprobantepago());
                    item.setTxtSeriecomprobantepago(prevItem.getTxtSeriecomprobantepago());
                    item.setFecPagocomprobantepago(prevItem.getFecPagocomprobantepago());
                    item.setTxtGlosaitem(prevItem.getTxtGlosaitem());
                    item.setCodProyecto(prevItem.getCodProyecto());
                    item.setCodFinanciera(prevItem.getCodFinanciera());
                    item.setCodCtaproyecto(prevItem.getCodCtaproyecto());
                    item.setCodContraparte(prevItem.getCodContraparte());
                    item.setCodCtacontable(prevItem.getCodCtacontable());
                    item.setCodCtaactividad(prevItem.getCodCtaactividad());
                    item.setCodCtaespecial(prevItem.getCodCtaespecial());
                    item.setNumTcvdolar(prevItem.getNumTcvdolar());
                    item.setNumTcmo(prevItem.getNumTcmo());
                    break;
                }
            }
        }
        view.setEnableCabezeraFields(true);
        //view.setEnableDetalleFields(true);
        List<ScpRendiciondetalle> items = new ArrayList<>();
        items.add(item);
        view.getContainer().addAll(items);
        view.getContainer().sort(new Object[]{"numNritem"}, new boolean[]{true});
        view.grid.deselectAll();
        view.grid.select(item);
        view.getGrid().setEditorEnabled(true);
        addCommitHandlerToGrid();
    }

    public void setNavigatorView(NavigatorViewing navigatorView) {
        this.navigatorView = navigatorView;
    }

    @Override
    public void addWarningToGuardarBtn(boolean isWarn) {
        //TODO Implement Warning when Saving!!!
    }

    // Save grid row
    protected void addCommitHandlerToGrid(){
        view.grid.removeItemClickListener(gridItemClickListener);
        gridItemClickListener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                try {
                    fieldGroup.commit();
                    view.grid.deselectAll();
                    view.grid.select(itemClickEvent.getItemId());
                    // Filter enabled columns according to chosen moneda for current row
                    Object mon = view.getGrid().getContainerDataSource().getItem(itemClickEvent.getItemId()).getItemProperty("codTipomoneda");
                    Character itemMoneda = (Character)((MethodProperty) mon).getValue();
                    ViewUtil.filterColumnsDisableByMoneda(view.getGrid(), itemMoneda);
                    if (!view.isVistaFull) {
                        ViewUtil.filterColumnsByMoneda(view.getGrid(), itemMoneda);
                    }
                } catch (CommitException ce) {
                    Notification.show("Por favor rellena los datos necessarios en la parte a la derecha primero!", Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        };

        view.grid.addItemClickListener(gridItemClickListener);
        view.grid.getEditorFieldGroup().removeCommitHandler(gridCommitHandler);

        gridCommitHandler = new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws CommitException {

            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws CommitException {
                Object item = view.grid.getContainerDataSource().getItem(view.grid.getEditedItemId());
                // Attach logic to num fields

                try {
                    ScpRendiciondetalle vcb = prepToSave();
                    if (vcb != null) {
                        // Copy date field values from grid to detalle fields
                        //view.getFechaDoc().setValue((Date) view.getGrid().getColumn("fecComprobantepago").getEditorField().getValue());
                        //view.getFechaPago().setValue((Date) view.getGrid().getColumn("fecPagocomprobantepago").getEditorField().getValue());

                        // Check if empty Tipo Cambio and set
                        if ((vcb.getCodTipomoneda().equals(GenUtil.USD) || vcb.getCodTipomoneda().equals(GenUtil.PEN))
                                && GenUtil.isNullOrZero((BigDecimal) beanItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).getValue())
                        || (vcb.getCodTipomoneda().equals(GenUtil.EUR) && GenUtil.isNullOrZero((BigDecimal) beanItem.getItemProperty("numTcmo").getValue()))
                        ) {
                            setTipoCambios(selFechaDoc.getValue());
                        }
                        calculateInOtherCurrencies();
                        // Save data
                        fieldGroup.commit();
                        commitEvent.getFieldBinder();
                        final ScpRendiciondetalle vcbToSave = setEmptyStrings(vcb);
                        vcbToSave.setFecComprobante(vcb.getScpRendicioncabecera().getFecComprobante());
                        ScpRendiciondetallePK id = vcbToSave.getId().prepareToSave(vcbToSave);
                        vcbToSave.setId(id);

                        if (vcb.getScpRendicioncabecera().isEnviado()) {
                            MessageBox
                                    .createQuestion()
                                    .withCaption("Esta operacion ya esta enviado")
                                    .withMessage("?Esta seguro que quiere guardar los cambios?")
                                    .withYesButton(() -> {
                                        view.getService().getRendiciondetalleRep().save(vcbToSave);
                                        view.getGrid().refreshRows(item);
                                        bindForm(vcbToSave);
                                        //.getRendiciondetalleRep().save(vcbToSave);
                                    })
                                    .withNoButton()
                                    .open();
                        } else {
                            //view.getService().getRendiciondetalleRep().save(vcbToSave);
                            view.getService().getRendiciondetalleRep().save(vcbToSave);
                            view.getGrid().refreshRows(item);
                            bindForm(vcbToSave);
                        }
                        moneda = (Character) view.getSelMoneda().getValue();
                        view.setTotal(moneda);
                        view.calcFooterSums();
                    }
                } catch (CommitException ce) {
                    Notification.show("No se puede guarder el item: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        };
        view.grid.getEditorFieldGroup().addCommitHandler(gridCommitHandler);
    }

    protected ScpRendiciondetalle prepToSave() throws CommitException {
        return prepToSave(view.grid.getContainerDataSource().getItem(view.grid.getEditedItemId()));
    }

    protected ScpRendiciondetalle prepToSave(Object item) throws CommitException {
        ScpRendiciondetalle vcb = (ScpRendiciondetalle) ((BeanItem) item).getBean();
        vcb = vcb.prepareToSave();
        if (item != null) {
            for (Object o : ((BeanItem) item).getItemPropertyIds()) {
                String propName = (String) o;
                Property prop = ((BeanItem) item).getItemProperty(propName);
                if (prop.getValue() == null && prop.getType() == String.class)
                    prop.setValue("");
                if (prop.getValue() == null && prop.getType() == Timestamp.class)
                    prop.setValue(new Timestamp(GenUtil.getBegin20thCent().getTime()));
                if (prop.getValue() == null && prop.getType() == BigDecimal.class)
                    prop.setValue(new BigDecimal(0));
            }
        }
        return vcb;
    }

    protected ScpRendiciondetalle setEmptyStrings(ScpRendiciondetalle rd) {
        if (rd.getCodTipocomprobantepago()==null) rd.setCodTipocomprobantepago("");
        if (rd.getTxtSeriecomprobantepago()==null) rd.setTxtSeriecomprobantepago("");
        if (rd.getTxtComprobantepago()==null) rd.setTxtComprobantepago("");
        if (rd.getFecPagocomprobantepago()==null) rd.setFecPagocomprobantepago(new Timestamp(GenUtil.getBegin20thCent().getTime()));
        if (rd.getCodDestino()==null) rd.setCodDestino("");
        if (rd.getCodTipomov()==null) rd.setCodTipomov(0);
        return rd;
    }

    protected void addImportedDetalles(List<ScpRendiciondetalle> importedDets) {
        int i = view.getContainer().size()+1;
        List<ScpRendiciondetalle> rendsToAdd = new ArrayList<>();
        for (ScpRendiciondetalle det : importedDets) {
            ScpRendiciondetallePK id = new ScpRendiciondetallePK();
            id = id.prepareToSave(det);
            id.setCodRendicioncabecera(this.rendicioncabecera.getCodRendicioncabecera());
            id.setNumNroitem(i);
            id.setCodFilial(this.rendicioncabecera.getCodFilial());
            id.setCodOrigen(this.rendicioncabecera.getCodOrigen());
            id.setCodComprobante(this.rendicioncabecera.getCodComprobante());
            det.setId(id);
            det.setScpRendicioncabecera(this.rendicioncabecera);
            i++;
            BeanItem<ScpRendiciondetalle> item = new BeanItem<>(det);
            setTipoCambios(det.getFecComprobantepago(), item);
            calculateInOtherCurrencies(item);
            try {
                det = setEmptyStrings(prepToSave(item));
                rendsToAdd.add(view.getService().getRendiciondetalleRep().save(det));
            } catch (CommitException ce) {
                Notification.show("Problema al guardar detalle: " + ce.getMessage() + "\n" + det);
            }
        }
        view.getContainer().addAll(rendsToAdd);
        view.getGrid().select(rendsToAdd.get(0));
        view.getContainer().sort(new Object[]{"numNritem"}, new boolean[]{true});
        view.getContainer().sort(new Object[]{"id.numNroitem"}, new boolean[]{true});
        view.setTotal(moneda);
        view.calcFooterSums();
    }
}
