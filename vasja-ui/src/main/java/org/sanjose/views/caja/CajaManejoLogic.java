package org.sanjose.views.caja;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class CajaManejoLogic extends CajaLogic implements ItemsRefreshing<ScpCajabanco>,Serializable, SaldoDelDia {

	private static final Logger log = LoggerFactory.getLogger(CajaManejoLogic.class);
    private final String[] COL_VIS_SALDO = new String[]{"codigo", "descripcion", "soles", "dolares", "euros"};
    protected CajaManejoViewing view;
    private CajaSaldoView saldosView = new CajaSaldoView();
    private Grid.FooterRow saldosFooterInicial=null;
    private Grid.FooterRow saldosFooterFinal=null;

    public void init(CajaManejoViewing cajaManejoView) {
        view = cajaManejoView;
        cajaView = view;
        view.getNuevoComprobante().addClickListener(e -> newComprobante());
        view.getNuevaTransferencia().addClickListener(e -> newTransferencia());
        view.getBtnModificar().addClickListener(e -> editarComprobante(view.getSelectedRow()));
        view.getBtnVerImprimir().addClickListener(e -> generateComprobante());
        //
        //view.btnImprimir.setVisible(ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT"));
        //view.btnImprimir.addClickListener(e -> printComprobante());
        view.getBtnEliminar().addClickListener(e -> eliminarComprobante(view.getSelectedRow()));
        saldosView.getBtnReporte().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(view.getFechaDesde().getValue(), view.getFechaHasta().getValue(), null));
        view.getBtnReporteImprimirCaja().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(view.getFechaDesde().getValue(), view.getFechaHasta().getValue(), null));
        view.getBtnDetallesSaldos().addClickListener(e -> {
            setSaldos(saldosView.getGridSaldoInicial(), true);
            setSaldos(saldosView.getGridSaldoFinal(), false);
            setSaldoDelDia();
            ViewUtil.openCajaSaldosInNewWindow(saldosView, view.getFechaDesde().getValue(), view.getFechaHasta().getValue());
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridCaja());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Nuevo cargo/abono", k -> newTransferencia());
            } else {

                gridContextMenu.addItem(!GenUtil.strNullOrEmpty(((ScpCajabanco) itemId).getCodTranscorrelativo()) ? "Ver detalle" : "Editar",
                        k -> editarComprobante((ScpCajabanco) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Nuevo cargo/abono", k -> newTransferencia());
                gridContextMenu.addItem("Ver Voucher", k -> generateComprobante());
                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> printComprobante());

                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> { enviarContabilidad((ScpCajabanco)itemId); });
                }
            }
        });
        setSaldos(saldosView.getGridSaldoFinal(), false);
    }

    private void generateComprobante() {
        ReportHelper.generateComprobante(view.getSelectedRow());
    }

    private void printComprobante() {
        ViewUtil.printComprobante(view.getSelectedRow());
    }

    public void setSaldosFinal() {
        setSaldos(saldosView.getGridSaldoFinal(), false);
    }

    // Realize logic from View
    public void filter(Date fechaDesde, Date fechaHasta) {
        view.getContainer().removeAllItems();
        view.setFilterInitialDate(fechaDesde);
        view.getContainer().addAll(view.getService().getCajabancoRep().findByFecFechaBetween(fechaDesde, fechaHasta));
        view.getGridCaja().setSortOrder(Sort.by("fecFecha", SortDirection.DESCENDING).then("txtCorrelativo", SortDirection.DESCENDING).build());
        calcFooterSums();
    }

    // Realize logic from View
    public void refreshData() {
        SortOrder[] sortOrders = view.getGridCaja().getSortOrder().toArray(new SortOrder[1]);
        filter(view.getFechaDesde().getValue(), view.getFechaHasta().getValue());
        view.getGridCaja().setSortOrder(Arrays.asList(sortOrders));
        calcFooterSums();
        setSaldosFinal();
    }

    public void setSaldos(Grid grid, boolean isInicial) {
        grid.getContainerDataSource().removeAllItems();
        BeanItemContainer<Caja> c = new BeanItemContainer<>(Caja.class);
        grid.setContainerDataSource(c);
        grid.setColumnOrder(COL_VIS_SALDO);
        grid.setColumns(COL_VIS_SALDO);

        BigDecimal totalSoles = new BigDecimal(0.00);
        BigDecimal totalUsd = new BigDecimal(0.00);
        BigDecimal totalEur = new BigDecimal(0.00);
        for (Caja caja : DataUtil.getCajasList(view.getService().getPlanRepo(),
                (isInicial ? GenUtil.getBeginningOfDay(view.getFechaDesde().getValue())
                        : GenUtil.getEndOfDay(view.getFechaHasta().getValue())))) {
            c.addItem(caja);
            totalSoles = totalSoles.add(caja.getSoles());
            totalUsd = totalUsd.add(caja.getDolares());
            totalEur = totalEur.add(caja.getEuros());
        }
        grid.getColumn("soles").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("dolares").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.getColumn("euros").setRenderer(new EmptyZeroNumberRendrer(
                "%02.2f", ConfigurationUtil.getLocale()));
        grid.setCellStyleGenerator(( Grid.CellReference cellReference ) -> {
            if ( "soles".equals( cellReference.getPropertyId() ) ||
                    "dolares".equals( cellReference.getPropertyId()) ||
                    "euros".equals( cellReference.getPropertyId())) {
                return "v-align-right";
            } else {
                return "v-align-left";
            }
        });

        grid.setFooterVisible(true);
        if (isInicial) {
            if (saldosFooterInicial==null) saldosFooterInicial = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterInicial.getCell("codigo").setText("TOTAL:");
            saldosFooterInicial.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterInicial.getCell("soles").setStyleName("v-align-right");
            saldosFooterInicial.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterInicial.getCell("dolares").setStyleName("v-align-right");
            saldosFooterInicial.getCell("euros").setText(dpf.format(totalEur.doubleValue()));
            saldosFooterInicial.getCell("euros").setStyleName("v-align-right");
        } else {
            if (saldosFooterFinal==null) saldosFooterFinal = grid.addFooterRowAt(0);
            DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                    null, ConfigurationUtil.get("DECIMAL_FORMAT"));
            saldosFooterFinal.getCell("codigo").setText("TOTAL:");
            saldosFooterFinal.getCell("soles").setStyleName("v-align-right");
            saldosFooterFinal.getCell("soles").setText(dpf.format(totalSoles.doubleValue()));
            saldosFooterFinal.getCell("dolares").setStyleName("v-align-right");
            saldosFooterFinal.getCell("dolares").setText(dpf.format(totalUsd.doubleValue()));
            saldosFooterFinal.getCell("euros").setText(dpf.format(totalEur.doubleValue()));
            saldosFooterFinal.getCell("euros").setStyleName("v-align-right");
            // set Saldo in CajaManejo
            Map<Character, BigDecimal> totals = new HashMap<>();
            totals.put(GenUtil.PEN, totalSoles);
            totals.put(GenUtil.USD, totalUsd);
            totals.put(GenUtil.EUR, totalEur);
            view.getSaldoCaja().setValue(dpf.format(totals.get(view.getMoneda()).doubleValue()));
        }
    }

    @Override
    public void setSaldoDelDia() {
        // Total del Dia
        BigDecimal totalSolesDiaIng = new BigDecimal(0.00);
        BigDecimal totalSolesDiaEgr = new BigDecimal(0.00);
        BigDecimal totalUsdDiaIng = new BigDecimal(0.00);
        BigDecimal totalUsdDiaEgr = new BigDecimal(0.00);
        BigDecimal totalEurDiaIng = new BigDecimal(0.00);
        BigDecimal totalEurDiaEgr = new BigDecimal(0.00);

        for (Object item : view.getGridCaja().getContainerDataSource().getItemIds()) {
            ScpCajabanco cajabanco = (ScpCajabanco) item;
            // PEN
            totalSolesDiaEgr = totalSolesDiaEgr.add(cajabanco.getNumHabersol());
            totalSolesDiaIng = totalSolesDiaIng.add(cajabanco.getNumDebesol());
            // USD
            totalUsdDiaEgr = totalUsdDiaEgr.add(cajabanco.getNumHaberdolar());
            totalUsdDiaIng = totalUsdDiaIng.add(cajabanco.getNumDebedolar());
            // EUR
            totalEurDiaEgr = totalEurDiaEgr.add(cajabanco.getNumHabermo());
            totalEurDiaIng = totalEurDiaIng.add(cajabanco.getNumDebemo());
        }
        DoubleDecimalFormatter dpf = new DoubleDecimalFormatter(
                null, ConfigurationUtil.get("DECIMAL_FORMAT"));

        // PEN
        saldosView.getValSolEgr().setValue(dpf.format(totalSolesDiaEgr.doubleValue()));
        saldosView.getValSolIng().setValue(dpf.format(totalSolesDiaIng.doubleValue()));
        saldosView.getValSolSaldo().setValue(dpf.format(totalSolesDiaIng.subtract(totalSolesDiaEgr).doubleValue()));
        // USD
        saldosView.getValDolEgr().setValue(dpf.format(totalUsdDiaEgr.doubleValue()));
        saldosView.getValDolIng().setValue(dpf.format(totalUsdDiaIng.doubleValue()));
        saldosView.getValDolSaldo().setValue(dpf.format(totalUsdDiaIng.subtract(totalUsdDiaEgr).doubleValue()));
        // EUR
        saldosView.getValEurEgr().setValue(dpf.format(totalEurDiaEgr.doubleValue()));
        saldosView.getValEurIng().setValue(dpf.format(totalEurDiaIng.doubleValue()));
        saldosView.getValEurSaldo().setValue(dpf.format(totalEurDiaIng.subtract(totalEurDiaEgr).doubleValue()));

        saldosView.gridSaldoDelDia.setColumnExpandRatio(0, 0);
    }

    @Override
    public void calcFooterSums() {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        BigDecimal sumDebesol = new BigDecimal(0.00);
        BigDecimal sumHabersol = new BigDecimal(0.00);
        BigDecimal sumDebedolar = new BigDecimal(0.00);
        BigDecimal sumHaberdolar = new BigDecimal(0.00);
        BigDecimal sumDebemo = new BigDecimal(0.00);
        BigDecimal sumHabermo = new BigDecimal(0.00);
        for (ScpCajabanco scp : view.getContainer().getItemIds()) {
            sumDebesol = sumDebesol.add(scp.getNumDebesol());
            sumHabersol = sumHabersol.add(scp.getNumHabersol());
            sumDebedolar = sumDebedolar.add(scp.getNumDebedolar());
            sumHaberdolar = sumHaberdolar.add(scp.getNumHaberdolar());
            sumDebemo = sumDebemo.add(scp.getNumDebemo());
            sumHabermo = sumHabermo.add(scp.getNumHabermo());
        }
        view.getGridCajaFooter().getCell("numDebesol").setText(df.format(sumDebesol));
        view.getGridCajaFooter().getCell("numHabersol").setText(df.format(sumHabersol));
        view.getGridCajaFooter().getCell("numDebedolar").setText(df.format(sumDebedolar));
        view.getGridCajaFooter().getCell("numHaberdolar").setText(df.format(sumHaberdolar));
        view.getGridCajaFooter().getCell("numDebemo").setText(df.format(sumDebemo));
        view.getGridCajaFooter().getCell("numHabermo").setText(df.format(sumHabermo));

        Arrays.asList(new String[] { "numDebesol", "numDebesol", "numHabersol", "numDebedolar", "numDebemo", "numHabermo"})
                .forEach( e -> view.getGridCajaFooter().getCell(e).setStyleName("v-align-right strong"));
    }

}
