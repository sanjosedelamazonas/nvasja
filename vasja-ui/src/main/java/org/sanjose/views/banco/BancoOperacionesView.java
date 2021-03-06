package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.views.caja.CajaSaldoView;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;
import org.vaadin.addons.CssCheckBox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoOperacionesView extends BancoOperacionesUI implements Viewing, BancoViewing, GridViewing {

    public static final String VIEW_NAME = "Operaciones de Cheques";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private BancoOperacionesLogic viewLogic;
    private final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{
            "checkMesCobrado", "fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol", "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"
    };
    private final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{
            "Cobr.", "Fecha", "Numero", "Cuenta",
            "Auxiliar",  "Cheque", "Glosa",
            "Ing S/.", "Egr S/.", "Ing $", "Egr $", "Ing €", "Egr €",
            "Orig", "Comprob.", "Env", "Anul."
    };

    private final int[] FILTER_WIDTH = new int[]{
            2, 4, 4, 4,
            10, 6, 14,
            3, 3,
            1, 4, 1, 1
    };
    private final String[] NONEDITABLE_COLUMN_IDS_PEN = new String[]{"fecFecha", "txtCorrelativo", "codCtacontable",
            "scpDestino.txtNombredestino", "txtCheque", "txtGlosa",
            "numDebesol", "numHabersol",
            "codOrigenenlace", "codComprobanteenlace", "flgEnviado", "flg_Anula"};

    private BeanItemContainer<ScpBancocabecera> container;

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private PersistanceService bancoService;

    private CajaSaldoView saldosView = new CajaSaldoView();

    public Grid.FooterRow gridFooter;

    private GeneratedPropertyContainer gpContainer;

    public BancoOperacionesView(PersistanceService bancoService) {
        this.bancoService = bancoService;
    }

    @Override
    public void init() {
        setSizeFull();
        setHeight(102, Unit.PERCENTAGE);

        //noinspection unchecked
        container = new BeanItemContainer(ScpBancocabecera.class, getService().findByFecFechaBetween(filterInitialDate, new Date()));
        container.addNestedContainerBean("scpDestino");

        gpContainer = new GeneratedPropertyContainer(container);
        gpContainer.addGeneratedProperty("checkMesCobrado",
                new PropertyValueGenerator<Boolean>() {
                    @Override
                    public Boolean getValue(Item item, Object itemId,
                                           Object propertyId) {

                        return DataUtil.isCobrado((ScpBancocabecera) ((BeanItem)item).getBean(), getService());
                    }
                    @Override
                    public Class<Boolean> getType() {
                        return Boolean.class;
                    }
                });


        gridBanco.setContainerDataSource(gpContainer);
        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS_PEN);

        ViewUtil.setupDateFiltersThisMonth(container, fechaDesde, fechaHasta, this);

        gridBanco.getColumn("txtGlosa").setMaximumWidth(200);
        gridBanco.getColumn("scpDestino.txtNombredestino").setMaximumWidth(130);

        gridBanco.getColumn("txtCorrelativo").setHidden(true);
        gridBanco.getColumn("codOrigenenlace").setHidden(true);
        gridBanco.getColumn("flgEnviado").setHidden(true);
        gridBanco.getColumn("flg_Anula").setHidden(true);

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);
        //gridBanco.getColumn("flgCobrado").setRenderer(new CheckboxRenderer());
        gridBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(ScpBancocabecera.class));
        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS_PEN, FILTER_WIDTH, null);

        gridFooter = gridBanco.appendFooterRow();
        bancoOperView.init(MainUI.get().getBancoManejoView().getService());
        // Make the top buttons panel invisible if in this grid view
        bancoOperView.getTopButtons().setVisible(false);
        bancoOperView.getViewLogic().nuevoCheque(null);
        BancoOperacionesView bancoOperacionesView = this;
        gridBanco.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                ScpBancocabecera cabeceraSelected = (ScpBancocabecera) selectionEvent.getSelected().toArray()[0];
                bancoOperView.getViewLogic().setNavigatorView(bancoOperacionesView);
                bancoOperView.getViewLogic().editarCheque(cabeceraSelected);
            }
        });
        bancoOperView.getCerrarBtn().setVisible(false);
        viewLogic = new BancoOperacionesLogic(this);
        viewLogic.initView();
        viewLogic.setSaldos(getSaldosView().getGridSaldoInicial(), true);
        viewLogic.setSaldos(getSaldosView().getGridSaldoFinal(), false);
        selRepMoneda.select('0');
    }

    public void refreshData() {
        SortOrder[] sortOrders = gridBanco.getSortOrder().toArray(new SortOrder[1]);
        filter(fechaDesde.getValue(), fechaHasta.getValue());
        gridBanco.setSortOrder(Arrays.asList(sortOrders));
    }

    @Override
    public void selectItem(VsjItem item) {
        if (container.containsId(item))
            gridBanco.select(item);
    }

    @Override
    public void selectMoneda(Character moneda) {
        selRepMoneda.select(moneda);
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
        container.removeAllItems();
        setFilterInitialDate(fechaDesde);
        container.addAll(getService().findByFecFechaBetween(fechaDesde, fechaHasta));
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        gridBanco.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRows() {
        return gridBanco.getSelectedRows();
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public PersistanceService getService() {
        return bancoService;
    }

    public Grid getGridBanco() {
        return gridBanco;
    }

    public BancoOperView getBancoOperView() {
        return bancoOperView;
    }

    public DateField getFecMesCobrado() {
        return fecMesCobrado;
    }

    @Override
    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }

    public Button getBtnMarcarCobrado() {
        return btnMarcarCobrado;
    }

    public Button getBtnMarcarNoCobrado() {
        return btnMarcarNoCobrado;
    }

    public TextField getNumSaldoInicialSegBancos() {
        return numSaldoInicialSegBancos;
    }

    public TextField getNumSaldoInicialLibro() {
        return numSaldoInicialLibro;
    }

    public TextField getNumSaldoFinalSegBancos() {
        return numSaldoFinalSegBancos;
    }

    public CajaSaldoView getSaldosView() {
        return saldosView;
    }

    public DateField getFechaDesde() {
        return fechaDesde;
    }

    public DateField getFechaHasta() {
        return fechaHasta;
    }

    public TextField getNumSaldoFinalLibro() {
        return numSaldoFinalLibro;
    }

    public Button getBtnDetallesSaldos() {
        return btnDetallesSaldos;
    }

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date filterInitialDate) {
        this.filterInitialDate = filterInitialDate;
    }

    public ComboBox getSelRepMoneda() {
        return selRepMoneda;
    }

    public ComboBox getSelFiltroCuenta() {
        return selFiltroCuenta;
    }

    @Override
    public BeanItemContainer<ScpBancocabecera> getContainer() {
        return container;
    }

}
