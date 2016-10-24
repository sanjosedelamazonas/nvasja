package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.util.FilterableSortableGridTreeContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjBancodetallePK;
import org.sanjose.util.*;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.VsjView;
import org.vaadin.addons.CssCheckBox;
import org.vaadin.gridtree.GridTree;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoConciliacionView extends BancoConciliacionUI implements VsjView, BancoView {

    public static final String VIEW_NAME = "Conciliacion de bancos";
    private final BancoConciliacionLogic viewLogic = new BancoConciliacionLogic();
/*
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "txtCorrelativo", "fecFecha", "txtCheque", */
/*"codProyecto",*//*
 "codCtacontable",
            "scpDestino.txtNombredestino", "txtGlosa", "flgCobrado",
            "numDebesol", "numHabersol"
            //, "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
    };
*/

    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id", "txtCorrelativo", "fecFecha", "txtCheque", "codProyecto", "codCtacontable",
            "txtNombredestino", "txtGlosaitem", "flgCobrado",
            "numDebesol", "numHabersol", "flgEnviado", "flg_Anula"
            //, "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
    };

    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Num", "Numero", "Fecha", "Cheque", "Proyecto", "Cta Cont.",
            "Auxiliar", "Glosa", "Cobr",
            "Ing S/.", "Egr S/.", "Env", "Anul"
    };
    //"Ing $", "Egr $", "Ing €", "Egr €"
    private final int[] FILTER_WIDTH = new int[]{
            4, 4, 2, 4, 3, 3,
            10, 12, //1,
            //3, 3
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"id", "txtCorrelativo", "fecFecha", "txtCheque",
            "codProyecto", "codCtacontable",
            "txtNombredestino", "txtGlosaitem",
            "numDebesol", "numHabersol", "flgEnviado", "flg_Anula"};
    Grid gridBanco;

    HierarchicalContainer indContainer;

    FilterableSortableGridTreeContainer container = null;

    private BancoService bancoService;

    public BancoConciliacionView(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

     /*   HierarchicalContainer hc= createContainer();

        //Create grid tree container
        GridTreeContainer treeContainer=new GridTreeContainer(hc);

        GridTree gridBanco = new GridTree(treeContainer, "txtCorrelativo");
     */
        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);
        //noinspection unchecked
/*        container = new HierarchicalBeanItemContainer<>(VsjBancodetalle.class, "txtCorrelativo");
        BeanItemContainer<VsjBancodetalle> bcontainer = new BeanItemContainer<>(VsjBancodetalle.class);
        //container = new ContainerHierarchicalWrapper(bcontainer);

        container.addNestedContainerBean("vsjBancocabecera");
        container.addNestedContainerBean("vsjBancocabecera.scpDestino");
        container.addNestedContainerProperty("vsjBancocabecera.scpDestino.txtNombredestino");
        container.addNestedContainerProperty("vsjBancocabecera.txtCheque");
        container.addNestedContainerProperty("vsjBancocabecera.flgCobrado");*/

        indContainer = new HierarchicalContainer();
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
        indContainer.addContainerProperty("flgCobrado", Boolean.class, "");
        indContainer.addContainerProperty("flgEnviado", Character.class, "");
        indContainer.addContainerProperty("flg_Anula", Character.class, "");
        indContainer.addContainerProperty("cabeceraObject", VsjBancocabecera.class, "");

        //addItems(getService().getBancocabeceraRep().findByFecFechaBetween(GenUtil.dateAddMonths(new Date(), -2), new Date()));
        setupDateFilters();
        filterComprobantes();
        container = new FilterableSortableGridTreeContainer(indContainer);
        container.expandAll();

        gridBanco = new GridTree(container, "id");
        gridBanco.setWidth(100, Unit.PERCENTAGE);
        gridBanco.setHeight(100, Unit.PERCENTAGE);
        verticalGrid.addComponent(gridBanco);

        gridBanco.sort("txtCorrelativo", SortDirection.DESCENDING);
        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        gridBanco.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flg_Anula").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        gridBanco.getColumn("flgEnviado").setHidden(true);
        gridBanco.getColumn("flg_Anula").setHidden(true);

        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);
        gridBanco.setEditorSaveCaption("Guardar");

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);
        gridBanco.getColumn("flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        //gridBanco.getColumn("flgCobrado").
        //gridBanco.getColumn("flgCobrado").setRenderer(new FlgCobradoCheckboxRenderer());
        gridBanco.getColumn("flgCobrado").setEditorField(cobradoChkBox);

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.MULTI);

        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);
/*
        gridBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjBancocabecera.class));
        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);
        gridBanco.setEditorSaveCaption("Guardar");
*/

/*

        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setEditorField(cobradoChkBox);
        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setEditable(true);
        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

*/

        // Run date filter
        //filterComprobantes(container, fechaDesde, fechaHasta);

        ViewUtil.colorizeRows(gridBanco, FilterableSortableGridTreeContainer.class);


        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");

        selFiltroCuenta.addValueChangeListener(e -> {
            filterComprobantes();
            viewLogic.setSaldoDelDia();
        });

        // Set Saldos Inicial
//        fechaDesde.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoInicial, true));
//        fechaHasta.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoFInal, false));

        viewLogic.init(this);
//        viewLogic.setSaldos(gridSaldoInicial, true);
//        viewLogic.setSaldos(gridSaldoFInal, false);

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
        fechaDesde.addValueChangeListener(valueChangeEvent -> filterComprobantes());

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
                addItems(getService().getBancocabeceraRep().findByFecFechaBetweenAndCodCtacontable(from, to, codCtaContable));
            else
                addItems(getService().getBancocabeceraRep().findByFecFechaBetween(from, to));
        }
    }

    private void addItems(List<VsjBancocabecera> vsjBancocabeceras) {
        indContainer.removeAllItems();
        for (VsjBancocabecera cabecera : vsjBancocabeceras) {
            VsjBancodetalle newDet = new VsjBancodetalle();
            VsjBancodetallePK newId = new VsjBancodetallePK();
            newId.setCodBancocabecera(cabecera.getCodBancocabecera());
            newId.setNumItem(0);
            newDet.setId(newId);
            newDet.setFecFecha(cabecera.getFecFecha());
            newDet.setTxtCorrelativo(cabecera.getTxtCorrelativo());
            newDet.setTxtGlosaitem(cabecera.getTxtGlosa());
            newDet.setVsjBancocabecera(cabecera);
            newDet.setNumDebesol(cabecera.getNumDebesol());
            newDet.setNumHabersol(cabecera.getNumHabersol());
            List<VsjBancodetalle> detalles = getService().getBancodetalleRep().findById_CodBancocabecera(cabecera.getCodBancocabecera());
            if (detalles.size() > 1) addItem(indContainer, newDet, true);

            for (VsjBancodetalle det : detalles) {
                if (detalles.size() > 1) {
                    addItem(indContainer, det, false);
                    addParent(indContainer, det.getId().getCodBancocabecera() + (det.getId().getNumItem() != 0 ? "-" + det.getId().getNumItem() : ""), newDet.getId().getCodBancocabecera() + "");
                } else {
                    addItem(indContainer, det, true);
                }
            }
        }
    }


    private void addItem(HierarchicalContainer indCon, VsjBancodetalle vbd, boolean isParent) {
        final Item item = indCon.addItem(vbd.getId().getCodBancocabecera() + (vbd.getId().getNumItem() != 0 ? "-" + vbd.getId().getNumItem() : ""));
        item.getItemProperty("id").setValue(vbd.getId().getCodBancocabecera());
        item.getItemProperty("txtCorrelativo").setValue(vbd.getTxtCorrelativo());
        item.getItemProperty("fecFecha").setValue(vbd.getFecFecha());
        item.getItemProperty("txtCheque").setValue(vbd.getVsjBancocabecera().getTxtCheque());
        item.getItemProperty("codProyecto").setValue(vbd.getCodProyecto());
        item.getItemProperty("codCtacontable").setValue(vbd.getCodCtacontable());
        ScpDestino destino = getService().getDestinoRepo().findByCodDestino(vbd.getVsjBancocabecera().getCodDestino());
        item.getItemProperty("txtNombredestino").setValue(destino != null ? destino.getTxtNombredestino() : "");
        item.getItemProperty("txtGlosaitem").setValue(vbd.getTxtGlosaitem());
        item.getItemProperty("numDebesol").setValue(vbd.getNumDebesol());
        item.getItemProperty("numHabersol").setValue(vbd.getNumHabersol());
        item.getItemProperty("flg_Anula").setValue(vbd.getFlg_Anula());
        item.getItemProperty("flgEnviado").setValue(vbd.getVsjBancocabecera().getFlgEnviado());
        item.getItemProperty("flgCobrado").setValue(isParent && !vbd.getVsjBancocabecera().isAnula() ? vbd.getVsjBancocabecera().getFlgCobrado() : null);
        item.getItemProperty("cabeceraObject").setValue(vbd.getVsjBancocabecera());
    }

    public void refreshData() {
        container.removeAllItems();
        filterComprobantes();
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
    }

    @Override
    public void enter(ViewChangeEvent event) {
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

    public BancoService getService() {
        return bancoService;
    }

    public Grid getGridBanco() {
        return gridBanco;
    }

    private void addParent(HierarchicalContainer container,String item,String parent) {
        if(container.getItem(item)!=null) {
            if(container.getItem(parent)!=null) {
                container.setParent(item,parent);
            }
        }
    }
}
