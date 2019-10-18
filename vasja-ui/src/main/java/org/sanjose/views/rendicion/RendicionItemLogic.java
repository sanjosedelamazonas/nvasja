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
import com.vaadin.ui.renderers.DateRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.converter.BigDecimalConverter;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.render.DateNotNullRenderer;
import org.sanjose.repo.ScpFinancieraRep;
import org.sanjose.repo.ScpPlanproyectoRep;
import org.sanjose.repo.Scp_ProyectoPorFinancieraRep;
import org.sanjose.util.*;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.views.sys.ComprobanteWarnGuardar;
import org.sanjose.views.sys.DestinoView;
import org.sanjose.views.sys.NavigatorViewing;
import tm.kod.widgets.numberfield.NumberField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
class RendicionItemLogic implements Serializable, ComprobanteWarnGuardar {

    private static final Logger log = LoggerFactory.getLogger(RendicionItemLogic.class);
    protected ScpRendiciondetalle item;
    protected boolean isLoading = true;
    protected boolean isEdit = false;
    protected NavigatorViewing navigatorView;
    protected Character moneda;
    protected ScpRendicioncabecera rendicioncabecera;
    protected FieldGroup fieldGroup;
    protected RendicionOperView view;
    private BeanItem<ScpRendiciondetalle> beanItem;

    private ComboBox selProyecto = new ComboBox();
    private ComboBox selCtacontable = new ComboBox();
    private ComboBox selCtaespecial = new ComboBox();

    private ItemClickEvent.ItemClickListener gridItemClickListener;
    private FieldGroup.CommitHandler gridCommitHandler;
    private Property.ValueChangeListener tipoCambioListener;

    public void init(RendicionOperView view) {
        this.view = view;
        tipoCambioListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                setTipoCambios((Date)valueChangeEvent.getProperty().getValue());
            }
        };
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

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<>(ts);
        view.getDataFechaRegistro().setPropertyDataSource(prop);
        view.getDataFechaRegistro().setConverter(DateToTimestampConverter.INSTANCE);
        view.getDataFechaRegistro().setResolution(Resolution.DAY);
        view.getDataFechaRegistro().setValue(new Date());

        //view.getNumVoucher().setEnabled(false);

        // Responsable
        DataFilterUtil.bindComboBox(view.getSelResponsable1(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        // Tipo Moneda
        DataFilterUtil.bindTipoMonedaOptionGroup(view.getSelMoneda(), "codTipomoneda");
        view.getSelMoneda().addValueChangeListener(event -> { if (event.getProperty().getValue()!=null) setMonedaLogic(event.getProperty().getValue().toString().charAt(0));});

        view.getNumTotalAnticipio().addBlurListener(event -> view.setTotal((Character)view.getSelMoneda().getValue()));

        // ------------ DETALLE

        view.getNumItem().setEnabled(false);

        // Auxiliar
        DataFilterUtil.bindComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");

        // Tipo doc
        DataFilterUtil.bindComboBox(view.getSelTipoDoc(), "codTipocomprobantepago", view.getService().getComprobantepagoRepo().findAll(),
                "txtDescripcion");

        DataFilterUtil.bindComboBox(view.getSelTipoMov(), view.getService().getConfiguractacajabancoRepo().findByActivoAndParaBancoAndParaProyecto(true, true, true), "Tipo Movimiento",
                "codTipocuenta", "txtTipocuenta", "id");

        //getSelTipoMov().setEnabled(false);
        view.getSelTipoMov().addValueChangeListener(event -> {
            if (!GenUtil.objNullOrEmpty(event.getProperty().getValue())) {
                String tipoMov = event.getProperty().getValue().toString();
                VsjConfiguractacajabanco config = view.getService().getConfiguractacajabancoRepo().findById(Integer.parseInt(tipoMov));
                if (config != null && view.getContainer().getItem(item)!=null) {
                    ScpRendiciondetalle sr = view.getContainer().getItem(item).getBean();
                    sr.setCodCtacontable(config.getCodCtacontablegasto());
                    sr.setCodCtaespecial(config.getCodCtaespecial());
                    view.grid.refreshRows(sr);
                }
            }
        });
        view.getTxtGlosaDetalle().setMaxLength(70);
        view.getTxtSerieDoc().setMaxLength(5);
        view.getTxtNumDoc().setMaxLength(20);

        //view.getFechaPago().setPropertyDataSource(prop);
        view.getFechaPago().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaPago().setResolution(Resolution.DAY);

        //view.getFechaPago().setPropertyDataSource(prop);
        view.getFechaDoc().setConverter(DateToTimestampConverter.INSTANCE);
        view.getFechaDoc().setResolution(Resolution.DAY);

        view.getFechaDoc().removeValueChangeListener(tipoCambioListener);
        view.getFechaDoc().addValueChangeListener(tipoCambioListener);

        // DETALLE - GRID

        // Fecha Pago
        PopupDateField pdf = new PopupDateField();
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        view.grid.getColumn("fecPagocomprobantepago").setEditorField(pdf);
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigurationUtil.get("DEFAULT_DATE_FORMAT"));
        view.grid.getColumn("fecPagocomprobantepago").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        //pdf.addValueChangeListener(e -> view.getFechaPago().setValue((Date)e.getProperty().getValue()));

        // Fecha Doc
        pdf = new PopupDateField();
        pdf.setPropertyDataSource(prop);
        pdf.setConverter(DateToTimestampConverter.INSTANCE);
        pdf.setResolution(Resolution.DAY);
        view.grid.getColumn("fecComprobantepago").setEditorField(pdf);
        view.grid.getColumn("fecComprobantepago").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));


        // Proyecto
        selProyecto.addValueChangeListener(this::setProyectoLogic);
        DataFilterUtil.bindComboBox(selProyecto, "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");
        view.grid.getColumn("codProyecto").setEditorField(selProyecto);

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
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "codTipomoneda", "Moneda");
        view.grid.getColumn("codTipomoneda").setEditorField(selTipomoneda);
        selTipomoneda.addValueChangeListener(e -> moneda = (Character)e.getProperty().getValue());

        String[] numFields = { "numHabersol", "numDebesol", "numHaberdolar", "numDebedolar", "numHabermo", "numDebemo", "numTcvdolar", "numTcmo" };
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
        view.getBtnAuxiliar().addClickListener(event -> editDestino(view.getSelCodAuxiliar()));

        /// FILTROS APLICAR A TODOS
        // Proyecto
        //view.getSetAllProyecto().addValueChangeListener(e -> this.updateItemProperty("codProyecto", e.getProperty().getValue()));
        DataFilterUtil.bindComboBox(view.getSetAllProyecto(), "codProyecto", view.getService().getProyectoRepo().findByFecFinalGreaterThanOrFecFinalLessThan(new Date(), GenUtil.getBegin20thCent()), "Sel Proyecto", "txtDescproyecto");

        // Fuente
        DataFilterUtil.bindComboBox(view.getSetAllFuente(), "codFinanciera",view.getService().getFinancieraRepo().findAll(),
                "Sel Fuente", "txtDescfinanciera");
        // Rubro Proy
        DataFilterUtil.bindComboBox(view.getSetAllPartida(), "id.codCtaproyecto",
                view.getService().getPlanproyectoRepo().findByFlgMovimientoAndId_TxtAnoproceso("N", GenUtil.getYear(view.getDataFechaComprobante().getValue())),
                "Sel Partida P.", "txtDescctaproyecto");
        // Lugar de Gasto
        DataFilterUtil.bindComboBox(view.getSetAllLugarGasto(), "codContraparte",view.getService().getContraparteRepo().findAll(),
                "Sel Lugar de Gasto", "txtDescContraparte");
        // Cuenta Contable
        DataFilterUtil.bindComboBox(view.getSetAllContable(), "id.codCtacontable",view.getService().getPlanRepo().
                findByFlgEstadocuentaAndFlgMovimientoAndId_TxtAnoprocesoAndId_CodCtacontableStartingWith(
                        '0', 'N', GenUtil.getCurYear(), ""), "Sel cta contable", "txtDescctacontable");
        // Rubro inst
        DataFilterUtil.bindComboBox(view.getSetAllRubrInst(), "id.codCtaespecial",
                view.getService().getPlanEspRepo().findByFlgMovimientoAndId_TxtAnoproceso('N', GenUtil.getCurYear()),
                "Sel Rubro Inst.", "txtDescctaespecial");

        view.getBtnSetAll().addClickListener(clickEvent -> {
            updateProperty(view.getSetAllProyecto(), "codProyecto");
            updateProperty(view.getSetAllFuente(), "codFinanciera");
            updateProperty(view.getSetAllPartida(), "codCtaproyecto");
            updateProperty(view.getSetAllLugarGasto(), "codContraparte");
            updateProperty(view.getSetAllContable(), "codCtacontable");
            updateProperty(view.getSetAllRubrInst(), "codCtaespecial");
            updateProperty(view.getSetAllFechaDoc(), "fecComprobantepago");
            updateProperty(view.getSetAllFechaPago(), "fecPagocomprobantepago");
        });
    }

    // TIPO CAMBIO LOGIC AND RECALCULATION

    private void setTipoCambios(Date fecha) {
        log.debug("setting tipo for: " + fecha);
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
            } catch (TipoCambio.TipoCambioNoExiste te) {
                log.debug(te.getLocalizedMessage());
                return;
            }
        }
        tipocambio = tipocambios.get(0);
        if (moneda==GenUtil.PEN) {
            beanItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTccdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcceuro()))
                beanItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcceuro());
        } else if (moneda==GenUtil.USD) {
            beanItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTcvdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcceuro()))
                beanItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcceuro());
        } else if (moneda==GenUtil.EUR) {
            beanItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).setValue(tipocambio.getNumTccdolar());
            if (!GenUtil.isNullOrZero(tipocambio.getNumTcveuro()))
                beanItem.getItemProperty("numTc" +  GenUtil.getDescMoneda(GenUtil.EUR)).setValue(tipocambio.getNumTcveuro());
        }
        List<ScpRendiciondetalle> detsToRefresh = new ArrayList<>();
        detsToRefresh.add(beanItem.getBean());
        view.grid.refreshRows(detsToRefresh);
        String[] numFields = {"numHaber", "numDebe"};
        Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(beanItem.getBean().getCodTipomoneda())));
    }

    protected void calculateInOtherCurrencies(String propertyName) {
        log.debug("calculating for: " + propertyName);
        if (view.grid.getColumn(propertyName).getEditorField()==null)
            return;
        Object newVal = beanItem.getItemProperty(propertyName).getValue();
        if (newVal==null)
            return;
        BigDecimal newNum = (BigDecimal)newVal;
        Character moneda = GenUtil.getNumMonedaFromDescContaining(propertyName);
        // Ignore in othter currencies than the one chosen for input
        if (!moneda.equals(beanItem.getItemProperty("codTipomoneda").getValue()))
            return;
        String haberDebe = propertyName.contains("Haber") ? "Haber" : "Debe";
        BigDecimal tcDolar = (BigDecimal)beanItem.getItemProperty("numTcv" + GenUtil.getDescMoneda(GenUtil.USD)).getValue();
        BigDecimal tcEuro = (BigDecimal)beanItem.getItemProperty("numTc" + GenUtil.getDescMoneda(GenUtil.EUR)).getValue();
//        BeanItem beanItem = view.getContainer().getItem(view.grid.getEditedItemId());
//        if (beanItem==null)
//            return;
        if (moneda==GenUtil.PEN) {
            if (!GenUtil.isNullOrZero(tcDolar))
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.USD)).setValue(
                        newNum.setScale(2).divide(tcDolar, RoundingMode.HALF_EVEN));
            if (!GenUtil.isNullOrZero(tcEuro))
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.EUR)).setValue(
                        newNum.setScale(2).divide(tcEuro, RoundingMode.HALF_EVEN));
        } else if (moneda==GenUtil.USD) {
            if (!GenUtil.isNullOrZero(tcDolar))
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.PEN)).setValue(
                    newNum.setScale(2).multiply(tcDolar));
            if (!GenUtil.isNullOrZero(tcEuro) && !GenUtil.isNullOrZero(tcDolar))
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.EUR)).setValue(
                    newNum.setScale(2).multiply(tcDolar).divide(tcEuro, RoundingMode.HALF_EVEN));
        } else if (!GenUtil.isNullOrZero(tcEuro) && !GenUtil.isNullOrZero(tcDolar)) {
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.PEN)).setValue(
                        newNum.setScale(2).multiply(tcEuro));
                beanItem.getItemProperty("num" + haberDebe + GenUtil.getDescMoneda(GenUtil.USD)).setValue(
                        newNum.setScale(2).multiply(tcEuro).divide(tcDolar, RoundingMode.HALF_EVEN));
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
                String[] numFields = {"numHaber", "numDebe"};
                Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(rend.getCodTipomoneda())));
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
        // Update Tipo Moneda on every item only if not advanced view
        this.moneda = moneda;
        if (!view.isVistaFull) {
            updateItemProperty("codTipomoneda", moneda, view.getContainer().getItemIds());
            ViewUtil.filterColumnsByMoneda(view.getGrid(), moneda);
        }
        ViewUtil.filterColumnsDisableByMoneda(view.getGrid(), moneda);
        // Gasto Total i Saldo Pendiente...
        view.setTotal(moneda);
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
        for (ScpRendiciondetalle sr : items) {
            if (newVal!=null) {
                if (newVal instanceof Date)
                    newVal = new Timestamp(((Date) newVal).getTime());
                view.getContainer().getItem(item).getItemProperty(itemProperty).setValue(newVal);
                detsToRefresh.add(sr);
            }
        }
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
        view.getDataFechaComprobante().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecComprobante"));
        view.getFechaDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecComprobantepago"));
        view.getDataFechaRegistro().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "fecFregistro"));
        view.getSelResponsable1().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "codDestino"));
        view.getSelCodAuxiliar().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codDestino"));
        view.getTxtGlosaCabeza().setDescription("Glosa Cabeza");
        view.getTxtGlosaCabeza().addValidator(new LocalizedBeanValidator(ScpRendicioncabecera.class, "txtGlosa"));
        view.getTxtGlosaDetalle().setDescription("Glosa Detalle");
        view.getTxtGlosaDetalle().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtGlosaitem"));
        view.getTxtSerieDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtSeriecomprobantepago"));
        view.getTxtNumDoc().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "txtComprobantepago"));
        view.getSelTipoMov().addValidator(new LocalizedBeanValidator(ScpRendiciondetalle.class, "codTipomov"));
//        // Check saldos and warn
//        saldoChecker = new SaldoChecker(view.getNumEgreso(), view.getSaldoCuenta(), view.getSaldoProyPEN(), this);
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
                //ce.getFieldGroup().
                //ce.getInvalidFields().keySet().
            }
        });
        UI.getCurrent().addWindow(destinoWindow);
    }

    private void refreshDestino() {
        DataFilterUtil.refreshComboBox(view.getSelResponsable1(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
        DataFilterUtil.refreshComboBox(view.getSelCodAuxiliar(), "codDestino", view.getService().getDestinoRepo().findByIndTipodestinoNot('3'),
                "txtNombredestino");
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
        view.getTxtGlosaDetalle().setValue(null);
        view.getNumItem().setValue("");
        fieldGroup.bind(view.getTxtGlosaDetalle(), "txtGlosaitem");
        fieldGroup.bind(view.getSelCodAuxiliar(), "codDestino");
        fieldGroup.bind(view.getSelTipoDoc(), "codTipocomprobantepago");
        fieldGroup.bind(view.getTxtSerieDoc(), "txtSeriecomprobantepago");
        fieldGroup.bind(view.getTxtNumDoc(), "txtComprobantepago");
        fieldGroup.bind(view.getFechaDoc(), "fecComprobantepago");
        fieldGroup.bind(view.getFechaPago(), "fecPagocomprobantepago");
        Date fechaPagoComprobpago = (Date)beanItem.getItemProperty("fecPagocomprobantepago").getValue();
        if (fechaPagoComprobpago!=null && fechaPagoComprobpago.getTime()==GenUtil.getBegin20thCent().getTime())
            view.getFechaPago().setValue(null);
        fieldGroup.bind(view.getSelTipoMov(), "codTipomov");

        ViewUtil.setFieldsNullRepresentation(fieldGroup);
        view.getDataFechaComprobante().setEnabled(true);

        isLoading = false;
        if (isEdit) {
            // EDITING
            setNumVoucher(item);
        }
        else if (item.getScpRendicioncabecera() != null) {
            if (item.getId() == null && item.getScpRendicioncabecera().getCodComprobante() != null) {
                log.debug("is NOT Edit in bindForm but ID is null");
                view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
                view.getNumItem().setValue(String.valueOf(view.getContainer().size() + 1));
            }
        }
        isEdit = false;
    }

    protected void setNumVoucher(ScpRendiciondetalle item) {
        if (item.getScpRendicioncabecera() != null && !GenUtil.strNullOrEmpty(item.getScpRendicioncabecera().getCodComprobante())) {
            view.getNumVoucher().setValue(item.getScpRendicioncabecera().getCodComprobante());
            view.getNumItem().setValue(String.valueOf(item.getId().getNumNroitem()));
        }
        view.getNumVoucher().setEnabled(false);
    }

    // Buttons

    void cerrarAlManejo() {
        if (navigatorView == null)
            navigatorView = MainUI.get().getCajaManejoView();
        MainUI.get().getNavigator().navigateTo(navigatorView.getNavigatorViewName());
    }

    void nuevoItem(ScpRendiciondetalle vcb) {
        if (rendicioncabecera != null)
            vcb.setScpRendicioncabecera(rendicioncabecera);
        //vcb.setCodTipomoneda(moneda);
        //vcb.setFecComprobante(new Timestamp(System.currentTimeMillis()));
        vcb.setFecComprobantepago(new Timestamp(System.currentTimeMillis()));
        bindForm(vcb);
        item = vcb;
        view.setEnableCabezeraFields(true);
        view.setEnableDetalleFields(true);
        if (item.getId().getNumNroitem()==null)
            item.getId().setNumNroitem(view.getContainer().size()+1);
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
                } catch (FieldGroup.CommitException ce) {
                    Notification.show("Por favor rellena los datos necessarios en la parte a la derecha primero!", Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        };

        view.grid.addItemClickListener(gridItemClickListener);
        view.grid.getEditorFieldGroup().removeCommitHandler(gridCommitHandler);

        gridCommitHandler = new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Object item = view.grid.getContainerDataSource().getItem(view.grid.getEditedItemId());
                // Attach logic to num fields
                try {
                    ScpRendiciondetalle vcb = prepToSave();
                    if (vcb != null) {
                        // Copy date field values from grid to detalle fields
                        view.getFechaDoc().setValue((Date) view.getGrid().getColumn("fecComprobantepago").getEditorField().getValue());
                        view.getFechaPago().setValue((Date) view.getGrid().getColumn("fecPagocomprobantepago").getEditorField().getValue());
                        String[] numFields = {"numHaber", "numDebe"};
                        Arrays.asList(numFields).forEach(f -> calculateInOtherCurrencies(f + GenUtil.getDescMoneda(vcb.getCodTipomoneda())));
                        // Save data
                        fieldGroup.commit();
                        commitEvent.getFieldBinder();
                        final ScpRendiciondetalle vcbToSave = setEmptyStrings(vcb);
                        if (vcb.getScpRendicioncabecera().isEnviado()) {
                            MessageBox
                                    .createQuestion()
                                    .withCaption("Esta operacion ya esta enviado")
                                    .withMessage("?Esta seguro que quiere guardar los cambios?")
                                    .withYesButton(() -> {
                                        view.getService().getRendiciondetalleRep().save(vcbToSave);
                                        view.getGrid().refreshRows(item);
                                        bindForm(vcbToSave);
                                    })
                                    .withNoButton()
                                    .open();
                        } else {
                            view.getService().getRendiciondetalleRep().save(vcbToSave);
                            view.getGrid().refreshRows(item);
                            bindForm(vcbToSave);
                        }
                        moneda = (Character) view.getSelMoneda().getValue();
                        view.setTotal(moneda);
                        view.calcFooterSums();
                    }
                } catch (FieldGroup.CommitException ce) {
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
}
