package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjBancodetallePK;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.VsjView;
import org.vaadin.addons.CssCheckBox;
import org.vaadin.gridtree.GridTree;
import org.vaadin.gridtree.GridTreeContainer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

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
            "id", "txtCorrelativo", "fecFecha"/*, "vsjBancocabecera.txtCheque"*/, "codProyecto", "codCtacontable",
            /*"vsjBancocabecera.scpDestino.txtNombredestino",*/ "txtGlosaitem", /*"vsjBancocabecera.flgCobrado",*/
            "numDebesol", "numHabersol"
            //, "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
    };

    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Num", "Numero", "Fecha"/*, "Cheque"*/, "Proyecto", "Cta Cont.",
            /*"Auxiliar", */"Glosa", /*"Cobr",*/
            "Ing S/.", "Egr S/."
    };
    //"Ing $", "Egr $", "Ing €", "Egr €"
    private final int[] FILTER_WIDTH = new int[]{
            4, 4, 2, 4, 3, 3,
            10, 12, //1,
            //3, 3
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{/*"txtCorrelativo", "fecFecha", "txtCheque",
            *//*"codProyecto", *//*"codCtacontable",
            "scpDestino.txtNombredestino", "txtGlosa",
            "numDebesol", "numHabersol"*/};
    Grid gridBanco;
    //private HierarchicalBeanItemContainer<VsjBancodetalle> container = null;
    ContainerHierarchicalWrapper container = null;
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
        //container = new HierarchicalBeanItemContainer<>(VsjBancodetalle.class, "txtCorrelativo");
        //BeanItemContainer<VsjBancodetalle> bcontainer = new BeanItemContainer<>(VsjBancodetalle.class);
        //container = new ContainerHierarchicalWrapper(bcontainer);

        //container.addNestedContainerBean("vsjBancocabecera");
        //container.addNestedContainerBean("vsjBancocabecera.scpDestino");
        //container.addNestedContainerProperty("vsjBancocabecera.scpDestino.txtNombredestino");
        //container.addNestedContainerProperty("vsjBancocabecera.txtCheque");
        //container.addNestedContainerProperty("vsjBancocabecera.flgCobrado");
        //IndexedContainer = new IndexedContainer();
        final HierarchicalContainer indCon = new HierarchicalContainer();
        indCon.addContainerProperty("id", Integer.class, "");
        indCon.addContainerProperty("txtCorrelativo", String.class, "");
        indCon.addContainerProperty("fecFecha", Date.class, "");
        indCon.addContainerProperty("txtCheque", String.class, "");
        indCon.addContainerProperty("codProyecto", String.class, "");
        indCon.addContainerProperty("codCtacontable", String.class, "");
        indCon.addContainerProperty("txtGlosaitem", String.class, "");
        indCon.addContainerProperty("numDebesol", BigDecimal.class, "");
        indCon.addContainerProperty("numHabersol", BigDecimal.class, "");
        for (VsjBancocabecera cabecera : getService().getBancocabeceraRep().findByFecFechaBetween(GenUtil.dateAddMonths(new Date(), -1), new Date())) {
            VsjBancodetalle newDet = new VsjBancodetalle();
            VsjBancodetallePK newId = new VsjBancodetallePK();
            newId.setCodBancocabecera(cabecera.getCodBancocabecera());
            newId.setNumItem(0);
            newDet.setId(newId);
            newDet.setTxtCorrelativo(cabecera.getTxtCorrelativo());
            newDet.setTxtGlosaitem(cabecera.getTxtGlosa());
            newDet.setVsjBancocabecera(cabecera);
            newDet.setNumDebesol(cabecera.getNumDebesol());
            newDet.setNumHabersol(cabecera.getNumHabersol());
            addItem(indCon, newDet);

            for (VsjBancodetalle det : getService().getBancodetalleRep().findById_CodBancocabecera(cabecera.getCodBancocabecera())) {
                //det.setTxtCorrelativo(cabecera.getTxtCorrelativo() + "-" + det.getId().getNumItem());
                addItem(indCon, det);
                //indCon.addItem(det);
                addParent(indCon, det.getId().getCodBancocabecera() + (det.getId().getNumItem() != 0 ? "-" + det.getId().getNumItem() : ""), newDet.getId().getCodBancocabecera() + "");
                //container.setParent(det, newDet);
            }
        }
        GridTreeContainer treeContainer = new GridTreeContainer(indCon);

        for (Object itemId : treeContainer.getItemIds()) {
            treeContainer.toogleCollapse(itemId);
        }
        gridBanco = new GridTree(treeContainer, "id");
        //gridBanco = new Grid();
        gridBanco.setWidth(100, Unit.PERCENTAGE);
        gridBanco.setHeight(100, Unit.PERCENTAGE);
        verticalGrid.addComponent(gridBanco);

//        gridBanco.setContainerDataSource(container);
        gridBanco.setEditorEnabled(false);
        //      gridBanco.sort("fecFecha", SortDirection.DESCENDING);
        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        //ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.MULTI);

/*
        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);
        ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta);
*/

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);

        gridBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjBancocabecera.class));
        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);

        gridBanco.setEditorSaveCaption("Guardar");

/*

        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setEditorField(cobradoChkBox);
        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setEditable(true);
        gridBanco.getColumn("vsjBancocabecera.flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

*/

        // Run date filter
        //    ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        // ViewUtil.colorizeRows(gridBanco, VsjBancodetalle.class);

/*
        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");
*/

/*
        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codCtacontable");
            }
            viewLogic.setSaldoDelDia();
        });
*/

        // Set Saldos Inicial
//        fechaDesde.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoInicial, true));
//        fechaHasta.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoFInal, false));

        viewLogic.init(this);
//        viewLogic.setSaldos(gridSaldoInicial, true);
//        viewLogic.setSaldos(gridSaldoFInal, false);

    }

    private void addItem(HierarchicalContainer indCon, VsjBancodetalle vbd) {
        final Item item = indCon.addItem(vbd.getId().getCodBancocabecera() + (vbd.getId().getNumItem() != 0 ? "-" + vbd.getId().getNumItem() : ""));
        item.getItemProperty("id").setValue(vbd.getId().getCodBancocabecera());
        item.getItemProperty("txtCorrelativo").setValue(vbd.getTxtCorrelativo());
        item.getItemProperty("fecFecha").setValue(vbd.getFecFecha());
        item.getItemProperty("txtCheque").setValue(vbd.getVsjBancocabecera().getTxtCheque());
        item.getItemProperty("codProyecto").setValue(vbd.getCodProyecto());
        item.getItemProperty("codCtacontable").setValue(vbd.getCodCtacontable());
        item.getItemProperty("txtGlosaitem").setValue(vbd.getTxtGlosaitem());
        item.getItemProperty("numDebesol").setValue(vbd.getNumDebesol());
        item.getItemProperty("numHabersol").setValue(vbd.getNumHabersol());
    }

    public void refreshData() {
        container.removeAllItems();
//        container.addAll(getService().getBancodetalleRep().findAll());
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
