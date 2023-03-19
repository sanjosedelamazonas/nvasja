package org.sanjose.views.banco;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.*;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.SaldoDelDia;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public class BancoGridLogic implements ItemsRefreshing<ScpBancocabecera>, SaldoDelDia {

    private BancoViewing view;
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    private Grid.FooterRow saldosFooterInicial;
    private Grid.FooterRow saldosFooterFinal;

    protected ScpPlancontable bancoCuenta;

    private Character moneda = GenUtil.PEN;
    
    public BancoGridLogic(BancoViewing view) {
        this.view = view;
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
    }

    public void initView() {

        view.getGridBanco().setEditorEnabled(false);
        view.getGridBanco().sort("fecFecha", SortDirection.DESCENDING);

        view.getGridBanco().setSelectionMode(Grid.SelectionMode.MULTI);

        ViewUtil.alignMontosInGrid(view.getGridBanco());
        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersThisMonth(view.getContainer(), view.getFechaDesde(), view.getFechaHasta(), view);

        view.getFechaDesde().addValueChangeListener(e -> {
            calcFooterSums();
            DataFilterUtil.refreshComboBox(view.getSelFiltroCuenta(), "id.codCtacontable",
                    DataUtil.getBancoCuentas(view.getFechaDesde().getValue(), view.getService().getPlanRepo(), moneda),
                    "txtDescctacontable");
            setSaldoCuenta(bancoCuenta);
        });

        view.getFechaHasta().addValueChangeListener(e -> {
            calcFooterSums();
            setSaldoCuenta(bancoCuenta);
        });

        view.getGridBanco().getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        view.getGridBanco().getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        view.getGridBanco().getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        view.getGridBanco().getColumn("txtGlosa").setMaximumWidth(400);
        view.getGridBanco().getColumn("scpDestino.txtNombredestino").setMaximumWidth(200);
        view.getGridBanco().getColumn("checkMesCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //view.getGridBanco().getColumn("codBancocabecera").setHidden(true);

        // Single click selects, double click opens
        view.getGridBanco().addItemClickListener(e -> setItemLogic(e));

        // Run date filter
        ViewUtil.filterComprobantes(view.getContainer(), "fecFecha", view.getFechaDesde(), view.getFechaHasta(), view);

        ViewUtil.colorizeRows(view.getGridBanco(), ScpBancocabecera.class);

        // CABECA
        view.getFecMesCobrado().setResolution(Resolution.MONTH);
        view.getFecMesCobrado().setValue(new Date());

        DataFilterUtil.bindTipoMonedaComboBox(view.getSelRepMoneda(), "moneda", "", false);
        view.getSelRepMoneda().select(moneda);
        view.getSelRepMoneda().setNullSelectionAllowed(false);

        DataFilterUtil.bindTipoMonedaComboBox(view.getSelRepMoneda(), "cod_tipomoneda", "Moneda", false);
        ViewUtil.filterColumnsByMoneda(view.getGridBanco(), moneda);

        view.getSelRepMoneda().setNullSelectionAllowed(false);
        view.getSelRepMoneda().addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                moneda = (Character)e.getProperty().getValue();
                view.getContainer().removeContainerFilters("codTipomoneda");
                view.getContainer().addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(view.getGridBanco(), moneda);
                calcFooterSums();
                DataFilterUtil.refreshComboBox(view.getSelFiltroCuenta(), "id.codCtacontable",
                        DataUtil.getBancoCuentas(view.getFechaDesde().getValue(), view.getService().getPlanRepo(), moneda),
                        "txtDescctacontable");
            }
            bancoCuenta = null;
            view.getContainer().removeContainerFilters("codCtacontable");
            ViewUtil.filterColumnsByMoneda(view.getGridBanco(), moneda);
            setSaldoCuenta(bancoCuenta);
            setSaldoDelDia();
        });

        DataFilterUtil.bindComboBox(view.getSelFiltroCuenta(), "id.codCtacontable",
                DataUtil.getBancoCuentas(view.getFechaDesde().getValue(), view.getService().getPlanRepo(), moneda),
                "txtDescctacontable");
        view.getSelFiltroCuenta().setEnabled(true);
        view.getSelFiltroCuenta().addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                view.getContainer().removeContainerFilters("codCtacontable");
                view.getContainer().addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
                bancoCuenta = view.getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                        GenUtil.getYear(view.getFechaDesde().getValue()), view.getSelFiltroCuenta().getValue().toString());
                //view.getSelRepMoneda().select(GenUtil.getNumMoneda(cuenta.getIndTipomoneda()));
                view.getGridBanco().getColumn("txtGlosa").setMaximumWidth(500);
                calcFooterSums();

            } else {
                bancoCuenta = null;
                view.getContainer().removeContainerFilters("codCtacontable");
                ViewUtil.filterColumnsByMoneda(view.getGridBanco(), moneda);
                view.getGridBanco().getColumn("txtGlosa").setMaximumWidth(400);
            }
            setSaldoCuenta(bancoCuenta);
            setSaldoDelDia();
        });
        view.getSelFiltroCuenta().setPageLength(20);
    }

    public void nuevoCheque(ScpPlancontable bancoCuenta) {
        view.clearSelection();
        view.getBancoOperView().getViewLogic().nuevoCheque(bancoCuenta);
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
        ViewUtil.openViewInNewWindowBanco(view.getBancoOperView());
        //MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void nuevoCheque() {
        nuevoCheque(null);
    }

    public void editarCheque(ScpBancocabecera vcb) {
        view.getBancoOperView().getViewLogic().editarCheque(vcb);
        view.getBancoOperView().getViewLogic().setNavigatorView(view);
        ViewUtil.openViewInNewWindowBanco(view.getBancoOperView());
        //MainUI.get().getNavigator().navigateTo(BancoOperView.VIEW_NAME);
    }

    public void anularCheque(ScpBancocabecera cabeceraToAnular) {
        if (cabeceraToAnular.isEnviado() && !Role.isPrivileged()) {
            Notification.show("!No se puede eliminar este cheque porque ya esta enviado a contabilidad!", Notification.Type.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("?Esta seguro que quiere eliminar cheque numero: \n"
                + cabeceraToAnular.getTxtCheque() + " cod operacion: " + cabeceraToAnular.getCodBancocabecera());
        MessageBox
                .createQuestion()
                .withCaption("Eliminar cheque")
                .withMessage(sb.toString())
                .withYesButton(() -> {
                    try {
                        view.getService().anularCheque(cabeceraToAnular);
                        view.refreshData();
                    } catch (FieldGroup.CommitException ce) {
                        Notification.show("Error al anular: " + ce.getMessage());
                    }
                })
                .withNoButton()
                .open();
    }

    public void enviarContabilidad(ScpBancocabecera scpBancocabecera, boolean isEnviar) {
        Set<Object> cabecerasParaEnviar = new HashSet<>();
        view.getSelectedRows().forEach(e -> cabecerasParaEnviar.add(e));
        Collection<ScpBancocabecera> cabecerasParaRefresh = new ArrayList<>();
        if (cabecerasParaEnviar.isEmpty() && scpBancocabecera!=null) {
            cabecerasParaEnviar.add(scpBancocabecera);
            cabecerasParaRefresh.add(scpBancocabecera);
        }

        cabecerasParaEnviar.forEach(e -> cabecerasParaRefresh.add((ScpBancocabecera)e));
        if (isEnviar) {
            Set<ScpBancocabecera> cabecerasEnviados = new HashSet<>();
            List<String> cabecerasIdsEnviados = new ArrayList<>();
            // Check if already sent and ask if only marcar...
            for (Object objVcb : cabecerasParaEnviar) {
                ScpBancocabecera cajabanco = (ScpBancocabecera) objVcb;
                if (!cajabanco.isEnviado() && view.getService().checkIfAlreadyEnviado(cajabanco)) {
                    cabecerasEnviados.add(cajabanco);
                    cabecerasIdsEnviados.add(cajabanco.getCodBancocabecera().toString());
                }
            }
            for (ScpBancocabecera cajabanco : cabecerasEnviados) {
                cabecerasParaEnviar.remove(cajabanco);
            }
            if (cabecerasEnviados.isEmpty()) {
                MainUI.get().getProcUtil().enviarContabilidadBanco(cabecerasParaEnviar, view.getService(),this);
                view.getGridBanco().deselectAll();
                view.clearSelection();
            } else {
                MessageBox
                        .createQuestion()
                        .withCaption("!Atencion!")
                        .withMessage("?Estas operaciones ya fueron enviadas ("+ Arrays.toString(cabecerasIdsEnviados.toArray()) +"), quiere solo marcar los como enviadas?")
                        .withYesButton(() -> doMarcarEnviados(cabecerasParaEnviar, cabecerasEnviados))
                        .withNoButton()
                        .open();
            }
        } else {
            for (Object objVcb : cabecerasParaEnviar) {
                ScpBancocabecera curBancoCabecera = (ScpBancocabecera) objVcb;
                if (!curBancoCabecera.isEnviado()) {
                    Notification.show("!Atencion!", "!Omitiendo operacion " + curBancoCabecera.getTxtCorrelativo() + " - no esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                view.getGridBanco().deselect(curBancoCabecera);
                curBancoCabecera.setFlgEnviado('0');
                curBancoCabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
                curBancoCabecera.setCodUactualiza(CurrentUser.get());
                view.getService().getBancocabeceraRep().save(curBancoCabecera);
            }
            view.getGridBanco().deselectAll();
            view.clearSelection();
            view.refreshData();
            //....
        }
    }

    public void doMarcarEnviados(Collection<Object> cabecerasParaEnviar , Set<ScpBancocabecera> cabecerasEnviados) {
        for (ScpBancocabecera cabecera : cabecerasEnviados) {
            view.getGridBanco().deselect(cabecera);
            cabecera.setFlgEnviado('1');
            cabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
            cabecera.setCodUactualiza(CurrentUser.get());
            view.getService().getBancocabeceraRep().save(cabecera);
        }
        //this.refreshItems(cabecerasEnviados);
        if (!cabecerasParaEnviar.isEmpty())
            MainUI.get().getProcUtil().enviarContabilidadBanco(cabecerasParaEnviar, view.getService(), this);
        view.getGridBanco().deselectAll();
        view.clearSelection();
        view.refreshData();
    }

    public void generateComprobante() {
        for (Object obj : view.getSelectedRows()) {
            ScpBancocabecera vcb = (ScpBancocabecera) obj;
            ReportHelper.generateComprobante(vcb);
        }
    }

    public void printComprobante() {
        for (Object obj : view.getSelectedRows()) {
            ScpBancocabecera vcb = (ScpBancocabecera) obj;
            ViewUtil.printComprobante(vcb);
        }
    }

    @Override
    public void refreshItems(Collection<ScpBancocabecera> items) {
        items.forEach(scb -> {
            ScpBancocabecera newScb = view.getService().getBancocabeceraRep().findByCodBancocabecera(scb.getCodBancocabecera());
            view.getGridBanco().getContainerDataSource().removeItem(scb);
            view.getGridBanco().getContainerDataSource().addItem(newScb);
        });
        view.refreshData();
    }

    protected void setMesCobrado(boolean isCobrado) {
        for (Object item : view.getGridBanco().getSelectedRows()) {
            if (item != null) {
                ScpBancocabecera cab = (ScpBancocabecera) item;
                //vcb.setFlgCobrado(isCobrado);
                String mescob = DataUtil.checkMesCobrado(cab, view.getService());
                if (DataUtil.isCobrado(mescob)==isCobrado)
                    // Don't change anything if already is as it should be - so not to override mes cobrado
                    return;
                cab.setFlgCobrado(isCobrado);
                if (isCobrado) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM");
                    cab.setCodMescobrado(sdf.format(view.getFecMesCobrado().getValue()));
                } else {
                    cab.setCodMescobrado("");
                }
                view.getService().updateCobradoInCabecera(cab);
            }
        }
        view.refreshData();
        setSaldoCuenta(bancoCuenta);
    }

    // Single click - select row in grids
    void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codBancocabecera").getValue();
            ScpBancocabecera vcb = view.getService().getBancocabeceraRep().findByCodBancocabecera((Integer) id);
            editarCheque(vcb);
        }
        view.getGridBanco().deselectAll();
        view.getGridBanco().select(event.getItemId());
    }
    
    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (Object item: view.getGridBanco().getContainerDataSource().getItemIds()) {
            ScpBancocabecera scp = (ScpBancocabecera)item; 
            sumDebesol = sumDebesol.add(scp.getNumDebesol());
            sumHabersol = sumHabersol.add(scp.getNumHabersol());
            sumDebedolar = sumDebedolar.add(scp.getNumDebedolar());
            sumHaberdolar = sumHaberdolar.add(scp.getNumHaberdolar());
            sumDebemo = sumDebemo.add(scp.getNumDebemo());
            sumHabermo = sumHabermo.add(scp.getNumHabermo());
        }
        view.getGridFooter().getCell("numDebesol").setText(df.format(sumDebesol));
        view.getGridFooter().getCell("numHabersol").setText(df.format(sumHabersol));
        view.getGridFooter().getCell("numDebedolar").setText(df.format(sumDebedolar));
        view.getGridFooter().getCell("numHaberdolar").setText(df.format(sumHaberdolar));
        view.getGridFooter().getCell("numDebemo").setText(df.format(sumDebemo));
        view.getGridFooter().getCell("numHabermo").setText(df.format(sumHabermo));

        Arrays.asList(new String[] { "numDebesol", "numHabersol", "numHaberdolar", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> view.getGridFooter().getCell(e).setStyleName("v-align-right strong"));
    }

    public void setSaldos(Grid grid, boolean isInicial) {
        if (view.getFechaDesde().getValue()==null || view.getFechaHasta().getValue()==null)
            return;
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder(COL_VIS_SALDO);
        grid.setColumns(COL_VIS_SALDO);
        grid.getColumn("descripcion").setWidth(200);
        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        BigDecimal totalEuros = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getBancoCuentasList(view.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(view.getFechaDesde().getValue())
                        : GenUtil.getEndOfDay(view.getFechaHasta().getValue())), isInicial)) {
            c.addItem(caja);
            totalSoles = totalSoles.add(caja.getSoles());
            totalUsd = totalUsd.add(caja.getDolares());
            totalEuros = totalEuros.add(caja.getEuros());
        }
        grid.getColumn("soles").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("dolares").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("euros").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.setCellStyleGenerator((Grid.CellReference cellReference) -> {
            if ("soles".equals(cellReference.getPropertyId()) ||
                    "dolares".equals(cellReference.getPropertyId()) ||
                    "euros".equals(cellReference.getPropertyId())) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });

        grid.setFooterVisible(true);
        if (isInicial) {
            if (saldosFooterInicial == null) saldosFooterInicial = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterInicial.getCell("codigo").setText("TOTAL:");
            saldosFooterInicial.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterInicial.getCell("soles").setStyleName("v-align-right");
            saldosFooterInicial.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterInicial.getCell("dolares").setStyleName("v-align-right");
            saldosFooterInicial.getCell("euros").setText(dpf.format(totalEuros.doubleValue()));
            saldosFooterInicial.getCell("euros").setStyleName("v-align-right");
        } else {
            if (saldosFooterFinal == null) saldosFooterFinal = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterFinal.getCell("codigo").setText("TOTAL:");
            saldosFooterFinal.getCell("soles").setStyleName("v-align-right");
            saldosFooterFinal.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterFinal.getCell("dolares").setStyleName("v-align-right");
            saldosFooterFinal.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterFinal.getCell("euros").setText(dpf.format(totalEuros.doubleValue()));
            saldosFooterFinal.getCell("euros").setStyleName("v-align-right");
        }
        setSaldoDelDia();
    }

    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);
        BigDecimal totalEurosDiaIng = new BigDecimal(0.00);
        BigDecimal totalEurosDiaEgr = new BigDecimal(0.00);

        for (Object item : view.getGridBanco().getContainerDataSource().getItemIds()) {
            ScpBancocabecera bancocabecera = (ScpBancocabecera) item;
            // PEN
            totalSolesDiaEgr = totalSolesDiaEgr.add(bancocabecera.getNumHabersol());
            totalSolesDiaIng = totalSolesDiaIng.add(bancocabecera.getNumDebesol());
            // USD
            totalUsdDiaEgr = totalUsdDiaEgr.add(bancocabecera.getNumHaberdolar());
            totalUsdDiaIng = totalUsdDiaIng.add(bancocabecera.getNumDebedolar());
            // EUR
            totalEurosDiaEgr = totalEurosDiaEgr.add(bancocabecera.getNumHabermo());
            totalEurosDiaIng = totalEurosDiaIng.add(bancocabecera.getNumDebemo());
        }
        DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                null, ConfigurationUtil.get("DECIMAL_FORMAT"));
        // PEN
        view.getSaldosView().getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        view.getSaldosView().getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        view.getSaldosView().getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        view.getSaldosView().getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        view.getSaldosView().getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        view.getSaldosView().getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        view.getSaldosView().getValEurEgr().setValue(dpf.format(totalEurosDiaEgr.doubleValue()));
        view.getSaldosView().getValEurIng().setValue(dpf.format(totalEurosDiaIng.doubleValue()));
        view.getSaldosView().getValEurSaldo().setValue(dpf.format(totalEurosDiaIng.subtract(totalEurosDiaEgr).doubleValue()));

        view.getSaldosView().getGridSaldoDelDia().setColumnExpandRatio(0, 0);
    }

    public void setSaldoCuenta(ScpPlancontable cuenta) {
        if (cuenta==null) {
            view.getNumSaldoFinalLibro().setValue("");
            view.getNumSaldoFinalSegBancos().setValue("");
            view.getNumSaldoInicialLibro().setValue("");
            view.getNumSaldoInicialSegBancos().setValue("");
        } else {
            ProcUtil.SaldosBanco saldosIni = DataUtil.getBancoCuentaSaldos(cuenta, view.getFechaDesde().getValue(), true);
            ProcUtil.SaldosBanco saldosFin = DataUtil.getBancoCuentaSaldos(cuenta, view.getFechaHasta().getValue(), false);
            view.getNumSaldoInicialLibro().setValue(GenUtil.numFormat(saldosIni.getSegLibro()));
            view.getNumSaldoInicialSegBancos().setValue(GenUtil.numFormat(saldosIni.getSegBanco()));
            view.getNumSaldoFinalLibro().setValue(GenUtil.numFormat(saldosFin.getSegLibro()));
            view.getNumSaldoFinalSegBancos().setValue(GenUtil.numFormat(saldosFin.getSegBanco()));
        }
    }

}

