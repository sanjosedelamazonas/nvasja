package org.sanjose.views.banco;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.VsjView;
import org.vaadin.addons.CssCheckBox;

import java.util.Collection;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoConciliacionView extends BancoConciliacionUI implements VsjView, BancoView {

    public static final String VIEW_NAME = "Conciliacion de bancos";
    private final BancoConciliacionLogic viewLogic = new BancoConciliacionLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "txtCorrelativo", "fecFecha", "txtCheque", /*"codProyecto",*/ "codCtacontable",
            "scpDestino.txtNombredestino", "txtGlosa", "flgCobrado",
            "numDebesol", "numHabersol"
            //, "numDebedolar", "numHaberdolar", "numDebemo", "numHabermo",
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Numero", "Fecha", "Cheque", /*"Proyecto", */"Cta Cont.", "Auxiliar", "Glosa", "Cobr",
            "Ing S/.", "Egr S/.",
    };
    //"Ing $", "Egr $", "Ing €", "Egr €"
    private final int[] FILTER_WIDTH = new int[]{
            4, 4, 4, 3, //3,
            10, 12, 1,
            3, 3
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"txtCorrelativo", "fecFecha", "txtCheque",
            /*"codProyecto", */"codCtacontable",
            "scpDestino.txtNombredestino", "txtGlosa",
            "numDebesol", "numHabersol"};
    Grid gridBanco;
    private BeanItemContainer<VsjBancocabecera> container;
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
        gridBanco = new Grid();
        gridBanco.setWidth(100, Unit.PERCENTAGE);
        gridBanco.setHeight(100, Unit.PERCENTAGE);
        verticalGrid.addComponent(gridBanco);

        //noinspection unchecked
        container = new BeanItemContainer(VsjBancocabecera.class, getService().findAll());
        container.addNestedContainerBean("scpDestino");
        gridBanco.setContainerDataSource(container);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFecha", SortDirection.DESCENDING);
        gridBanco.getColumn("fecFecha").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        // Add filters
        ViewUtil.setupColumnFilters(gridBanco, VISIBLE_COLUMN_IDS, FILTER_WIDTH, viewLogic);

        ViewUtil.alignMontosInGrid(gridBanco);

        gridBanco.setSelectionMode(SelectionMode.MULTI);

        // Fecha Desde Hasta
        //ViewUtil.setupDateFiltersThisDay(container, fechaDesde, fechaHasta);
        ViewUtil.setupDateFiltersPreviousMonth(container, fechaDesde, fechaHasta);

        CssCheckBox cobradoChkBox = new CssCheckBox();
        cobradoChkBox.setSimpleMode(false);
        cobradoChkBox.setAnimated(false);
        cobradoChkBox.setCaption("");
        cobradoChkBox.setBigPreset(false);
        //gridBanco.getColumn("flgCobrado").setRenderer(new CheckboxRenderer());
        gridBanco.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjBancocabecera.class));
        gridBanco.setEditorEnabled(true);
        gridBanco.setEditorBuffered(true);

        gridBanco.setEditorSaveCaption("Guardar");

        gridBanco.getColumn("flgCobrado").setEditorField(cobradoChkBox);
        gridBanco.getColumn("flgCobrado").setEditable(true);
        gridBanco.getColumn("flgCobrado").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecFecha", fechaDesde, fechaHasta);

        ViewUtil.colorizeRows(gridBanco, VsjBancocabecera.class);

        DataFilterUtil.bindComboBox(selFiltroCuenta, "id.codCtacontable",
                DataUtil.getBancoCuentas(fechaDesde.getValue(), getService().getPlanRepo()),
                "txtDescctacontable");

        selFiltroCuenta.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                container.removeContainerFilters("codCtacontable");
                container.addContainerFilter(new Compare.Equal("codCtacontable", e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codCtacontable");
            }
            viewLogic.setSaldoDelDia();
        });

        // Set Saldos Inicial
//        fechaDesde.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoInicial, true));
//        fechaHasta.addValueChangeListener(ev -> viewLogic.setSaldos(gridSaldoFInal, false));

        viewLogic.init(this);
//        viewLogic.setSaldos(gridSaldoInicial, true);
//        viewLogic.setSaldos(gridSaldoFInal, false);

    }

    public void refreshData() {
        container.removeAllItems();
        container.addAll(getService().findAll());
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


/*
    private HierarchicalContainer createContainer(int nItems) {
        if(nItems<30) {
            nItems=30;
        }
        final String[] names={"Billy", "Willy","Timmy","Bob","Mog","Rilley", "Ville","Bobby", "Moby", "Ben"};
        final String[] lastName={"Black","White","Anaya","Anders","Andersen","Anderson","Andrade","Andre","Andres","Andrew","Andrews"};
        final int  minIncome=1500;
        final int maxIncome=4000;

        final HierarchicalContainer container=new HierarchicalContainer();
        container.addContainerProperty("id", Integer.class, 0);
        container.addContainerProperty("name", String.class, "");
        container.addContainerProperty("lastName", String.class, "");
        container.addContainerProperty("income", Integer.class, 0);

        for(int i=0;i<nItems;i++) {
            final Object itemId=""+i;
            final Item item=container.addItem(itemId);
            item.getItemProperty("id").setValue(i);
            item.getItemProperty("name").setValue(getValueFromArray(names));
            item.getItemProperty("lastName").setValue(getValueFromArray(lastName));
            item.getItemProperty("income").setValue(generateIncome(minIncome,maxIncome));
            container.addItem(itemId);
        }
        createHierarcy(container,nItems);
        Notification.show(nItems+ " created" );
        return container;
    }
    private void addParent(HierarchicalContainer container,String item,String parent) {
        if(container.getItem(item)!=null) {
            if(container.getItem(parent)!=null) {
                container.setParent(item,parent);
            }
        }
    }
    private void createHierarcy(HierarchicalContainer container,int nItems) {
        final int nLevels=5;
        for(int i=0;i<nItems;i++) {
            final String itemId=""+i;
            if((i%nLevels)==0) {

            }
            else if ((i%nLevels)==2) {
                addParent(container,itemId,(i-2)+"");
            }
            else {
                addParent(container,itemId,(i-1)+"");
            }
        }
    }
*/

}
