package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.FilterableSortableGridTreeContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.MainUI;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.helper.NotImplementedException;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.views.caja.CajaSaldoView;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;
import org.vaadin.gridtree.GridTree;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoConciliacionView extends BancoConciliacionUI implements Viewing, BancoViewing {

    public static final String VIEW_NAME = "Conciliacion de bancos";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private final BancoConciliacionLogic viewLogic = new BancoConciliacionLogic();

    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id", "txtCorrelativo", "fecFecha", "txtCheque", "codProyecto", "codCtacontable",
            "txtNombredestino", "txtGlosaitem", "flgCobrado",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "flgEnviado", "flg_Anula"
    };

    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Num", "Numero", "Fecha", "Cheque", "Proyecto", "Cta Cont.",
            "Auxiliar", "Glosa", "Cobr",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Env", "Anul"
    };

    private final int[] FILTER_WIDTH = new int[]{
            5, 5, 4, 4, 4, 4,
            10, 12, 2,
            3, 3, 3, 3, 3, 3,
            2, 2
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"id", "txtCorrelativo", "fecFecha", "txtCheque",
            "codProyecto", "codCtacontable",
            "txtNombredestino", "txtGlosaitem",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo", "flgEnviado", "flg_Anula", "flgCobrado"};
    Grid gridBanco;

    FilterableSortableGridTreeContainer container = null;

    private PersistanceService bancoService;

    public BancoConciliacionView(PersistanceService bancoService) {
        this.bancoService = bancoService;
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        setupDateFilters();
        filterComprobantes();
        gridBanco.setWidth(100, Unit.PERCENTAGE);
        gridBanco.setHeight(100, Unit.PERCENTAGE);
        verticalGrid.addComponent(gridBanco);

        gridBanco.sort("txtCorrelativo", SortDirection.DESCENDING);
        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridBanco.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flgEnviado").setHidden(true);
        gridBanco.getColumn("flg_Anula").setHidden(true);

        gridBanco.getColumn("txtGlosaitem").setMaximumWidth(400);

        gridBanco.setEditorEnabled(false);
        gridBanco.setEditorBuffered(true);
        gridBanco.setEditorSaveCaption("Guardar");

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);
        gridBanco.getColumn("flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //TODO - disable edit flg_cobrado
        //gridBanco.getColumn("flgCobrado").setEditorField(cobradoChkBox);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);
        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.SINGLE);

        ViewUtil.colorizeRows(gridBanco, FilterableSortableGridTreeContainer.class);

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");
        selFiltroCuenta.setPageLength(20);

        selFiltroCuenta.addValueChangeListener(e -> {
            filterComprobantes();
        });
        selFiltroCuenta.setEnabled(true);
        viewLogic.init(this);
    }

    private void setupDateFilters() {
        Date defDesde = GenUtil.getBeginningOfMonth(new Date());
        Date defHasta = GenUtil.getEndOfDay(new Date());
        // Fecha Desde
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ObjectProperty<Timestamp> prop = new ObjectProperty<>(ts);
        fechaDesde.setPropertyDataSource(prop);
        fechaDesde.setConverter(DateToTimestampConverter.INSTANCE);
        fechaDesde.setResolution(Resolution.DAY);
        fechaDesde.setValue(defDesde);
        fechaDesde.addValueChangeListener(valueChangeEvent -> {
            filterComprobantes();
            DataFilterUtil.refreshComboBox(selFiltroCuenta, "id.codCtacontable",
                    DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                    "txtDescctacontable");
        });

        ts = new Timestamp(System.currentTimeMillis());
        prop = new ObjectProperty<>(ts);
        fechaHasta.setPropertyDataSource(prop);
        fechaHasta.setConverter(DateToTimestampConverter.INSTANCE);
        fechaHasta.setResolution(Resolution.DAY);
        fechaHasta.setValue(defHasta);
        fechaHasta.addValueChangeListener(valueChangeEvent -> filterComprobantes());
    }

    public void filterComprobantes() {
        String codCtaContable = selFiltroCuenta.getValue() != null ? selFiltroCuenta.getValue().toString() : null;
        Date from, to = null;
        if (fechaDesde.getValue() != null || fechaHasta.getValue() != null) {
            from = (fechaDesde.getValue() != null ? fechaDesde.getValue() : new Date(0));
            to = (fechaHasta.getValue() != null ? fechaHasta.getValue() : new Date(Long.MAX_VALUE));
            if (codCtaContable != null)
                loadItems(getService().getBancocabeceraRep().findByFecFechaBetweenAndCodCtacontable(from, to, codCtaContable));
            else
                loadItems(getService().getBancocabeceraRep().findByFecFechaBetween(from, to));
        }
        if (selFiltroCuenta.getValue() != null) {
            ScpPlancontable cuenta = getService().getPlanRepo().findById_TxtAnoprocesoAndId_CodCtacontable(
                    GenUtil.getYear(fechaDesde.getValue()), selFiltroCuenta.getValue().toString());
            BigDecimal saldo = MainUI.get().getProcUtil().getSaldoBanco(GenUtil.getEndOfDay(GenUtil.dateAddDays(fechaDesde.getValue(),-1)),
                    selFiltroCuenta.getValue().toString(), GenUtil.getNumMoneda(cuenta.getIndTipomoneda())).getSegLibro();
            DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance(ConfigurationUtil.getLocale()));
            txtSaldoInicial.setValue(GenUtil.getSymMoneda(cuenta.getIndTipomoneda()) + " " + df.format(saldo));
            saldo = MainUI.get().getProcUtil().getSaldoBanco(fechaHasta.getValue(),
                    selFiltroCuenta.getValue().toString(), GenUtil.getNumMoneda(cuenta.getIndTipomoneda())).getSegLibro();
            txtSaldoFinal.setValue(GenUtil.getSymMoneda(cuenta.getIndTipomoneda()) + " " + df.format(saldo));
            ViewUtil.filterColumnsByMoneda(gridBanco, GenUtil.getNumMoneda(cuenta.getIndTipomoneda()).charValue());
        } else {
            ViewUtil.filterColumnsByMoneda(gridBanco, 'A');
            txtSaldoInicial.setValue("");
            txtSaldoFinal.setValue("");
        }
    }

    private void loadItems(List<ScpBancocabecera> vsjBancocabeceras) {
        HierarchicalContainer indContainer = new HierarchicalContainer();
        indContainer.addContainerProperty("id", Integer.class, "");
        indContainer.addContainerProperty("txtCorrelativo", String.class, "");
        indContainer.addContainerProperty("fecFecha", Date.class, "");
        indContainer.addContainerProperty("txtCheque", String.class, "");
        indContainer.addContainerProperty("codProyecto", String.class, "");
        indContainer.addContainerProperty("codCtacontable", String.class, "");
        indContainer.addContainerProperty("txtNombredestino", String.class, "");
        indContainer.addContainerProperty("txtGlosaitem", String.class, "");
        indContainer.addContainerProperty("numDebesol", BigDecimal.class, "");
        indContainer.addContainerProperty("numHabersol", BigDecimal.class, "");
        indContainer.addContainerProperty("numDebedolar", BigDecimal.class, "");
        indContainer.addContainerProperty("numHaberdolar", BigDecimal.class, "");
        indContainer.addContainerProperty("numDebemo", BigDecimal.class, "");
        indContainer.addContainerProperty("numHabermo", BigDecimal.class, "");
        indContainer.addContainerProperty("flgCobrado", Boolean.class, "");
        indContainer.addContainerProperty("flgEnviado", Character.class, "");
        indContainer.addContainerProperty("flg_Anula", Character.class, "");
        indContainer.addContainerProperty("cabeceraObject", ScpBancocabecera.class, "");

        for (ScpBancocabecera cabecera : vsjBancocabeceras) {
            ScpBancodetalle newDet = new ScpBancodetalle();
            ScpBancodetallePK newId = new ScpBancodetallePK();
            newId.setCodBancocabecera(cabecera.getCodBancocabecera());
            newId.setNumItem(0);
            newDet.setId(newId);
            newDet.setFecFecha(cabecera.getFecFecha());
            newDet.setTxtCorrelativo(cabecera.getTxtCorrelativo());
            newDet.setTxtGlosaitem(cabecera.getTxtGlosa());
            newDet.setScpBancocabecera(cabecera);
            newDet.setNumDebesol(cabecera.getNumDebesol());
            newDet.setNumHabersol(cabecera.getNumHabersol());
            newDet.setNumDebedolar(cabecera.getNumDebedolar());
            newDet.setNumHaberdolar(cabecera.getNumHaberdolar());
            newDet.setNumDebemo(cabecera.getNumDebemo());
            newDet.setNumHabermo(cabecera.getNumHabermo());
            List<ScpBancodetalle> detalles = getService().getBancodetalleRep().findById_CodBancocabecera(cabecera.getCodBancocabecera());
            if (detalles.size() > 1) addItem(indContainer, newDet, true);

            for (ScpBancodetalle det : detalles) {
                if (detalles.size() > 1) {
                    addItem(indContainer, det, false);
                    addParent(indContainer, det.getId().getCodBancocabecera() + (det.getId().getNumItem() != 0 ? "-" + det.getId().getNumItem() : ""), newDet.getId().getCodBancocabecera() + "");
                } else {
                    addItem(indContainer, det, true);
                }
            }
        }
        container = new FilterableSortableGridTreeContainer(indContainer);
        container.expandAll();
        if (gridBanco == null)
            gridBanco = new GridTree(container, "id");
        else
            gridBanco.setContainerDataSource(container);
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
    }

    private void addItem(HierarchicalContainer indCon, ScpBancodetalle vbd, boolean isParent) {
        final Item item = indCon.addItem(vbd.getId().getCodBancocabecera() + (vbd.getId().getNumItem() != 0 ? "-" + vbd.getId().getNumItem() : ""));
        item.getItemProperty("id").setValue(vbd.getId().getCodBancocabecera());
        item.getItemProperty("txtCorrelativo").setValue(vbd.getTxtCorrelativo());
        item.getItemProperty("fecFecha").setValue(vbd.getFecFecha());
        item.getItemProperty("txtCheque").setValue(vbd.getScpBancocabecera().getTxtCheque());
        item.getItemProperty("codProyecto").setValue(vbd.getCodProyecto());
        item.getItemProperty("codCtacontable").setValue(vbd.getCodContracta());
        ScpDestino destino = getService().getDestinoRepo().findByCodDestino(vbd.getScpBancocabecera().getCodDestino());
        item.getItemProperty("txtNombredestino").setValue(destino != null ? destino.getTxtNombredestino() : "");
        item.getItemProperty("txtGlosaitem").setValue(vbd.getTxtGlosaitem());
        item.getItemProperty("numDebesol").setValue(vbd.getNumDebesol());
        item.getItemProperty("numHabersol").setValue(vbd.getNumHabersol());
        item.getItemProperty("numDebedolar").setValue(vbd.getNumDebedolar());
        item.getItemProperty("numHaberdolar").setValue(vbd.getNumHaberdolar());
        item.getItemProperty("numDebemo").setValue(vbd.getNumDebemo());
        item.getItemProperty("numHabermo").setValue(vbd.getNumHabermo());
        item.getItemProperty("flg_Anula").setValue(vbd.getFlg_Anula());
        item.getItemProperty("flgEnviado").setValue(vbd.getScpBancocabecera().getFlgEnviado());
        item.getItemProperty("flgCobrado").setValue(isParent && !vbd.getScpBancocabecera().isAnula() ? vbd.getScpBancocabecera().getFlgCobrado() : null);
        item.getItemProperty("cabeceraObject").setValue(vbd.getScpBancocabecera());
    }

    private void addParent(HierarchicalContainer container, String item, String parent) {
        if (container.getItem(item) != null) {
            if (container.getItem(parent) != null) {
                container.setParent(item, parent);
            }
        }
    }

    public Button getExpandirContraerBtn() {
        return expandirContraerBtn;
    }

    @Override
    public void refreshData() {
        SortOrder[] sortOrders = gridBanco.getSortOrder().toArray(new SortOrder[1]);
        filterComprobantes();
        if (sortOrders.length==0)
            gridBanco.sort("fecFecha", SortDirection.DESCENDING);
        else
            gridBanco.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectItem(VsjItem item) {
        for (Object vcb : container.getItemIds()) {
            if (((ScpBancocabecera)vcb).getCodBancocabecera().equals(((ScpBancocabecera)item).getCodBancocabecera())) {
                gridBanco.select(vcb);
                return;
            }
        }
    }

    @Override
    public void selectMoneda(Character moneda) {
        throw new NotImplementedException("");
    }


    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    @Override
    public void clearSelection() {
        gridBanco.getSelectionModel().reset();
    }

    @Override
    public Collection<Object> getSelectedRows() {

        Collection<Object> selected = gridBanco.getSelectedRows();
        return selected;
    }

    @Override
    public DateField getFechaDesde() {
        throw new NotImplementedException("");
    }

    @Override
    public DateField getFechaHasta() {
        throw new NotImplementedException("");
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    @Override
    public PersistanceService getService() {
        return bancoService;
    }

    @Override
    public BancoOperView getBancoOperView() {
        return MainUI.get().getBancoOperView();
    }

    @Override
    public Grid getGridBanco() {
        return gridBanco;
    }

    @Override
    public DateField getFecMesCobrado() {
        throw new NotImplementedException("");
    }

    @Override
    public Grid.FooterRow getGridFooter() {
        throw new NotImplementedException("");
    }

    @Override
    public CajaSaldoView getSaldosView() {
        throw new NotImplementedException("");
    }

    @Override
    public TextField getNumSaldoInicialSegBancos() {
        throw new NotImplementedException("");
    }

    @Override
    public TextField getNumSaldoInicialLibro() {
        throw new NotImplementedException("");
    }

    @Override
    public TextField getNumSaldoFinalSegBancos() {
        throw new NotImplementedException("");
    }

    @Override
    public TextField getNumSaldoFinalLibro() {
        throw new NotImplementedException("");
    }

    @Override
    public ComboBox getSelRepMoneda() {
        throw new NotImplementedException("");    }

    @Override
    public ComboBox getSelFiltroCuenta() {
        throw new NotImplementedException("");    }

    @Override
    public BeanItemContainer<ScpBancocabecera> getContainer() {
        throw new NotImplementedException("");
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {

    }

    @Override
    public Date getFilterInitialDate() {
        throw new NotImplementedException("");    }

    @Override
    public void setFilterInitialDate(Date fecha) {

    }
}
