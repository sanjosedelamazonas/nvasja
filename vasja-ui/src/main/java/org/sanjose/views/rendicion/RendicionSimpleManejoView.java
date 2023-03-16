package org.sanjose.views.rendicion;

import com.vaadin.data.Item;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.authentication.Role;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
import org.sanjose.model.*;
import org.sanjose.util.*;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class RendicionSimpleManejoView extends RendicionSimpleManejoUI implements RendicionManejoViewing {

    public static final String VIEW_NAME = "Manejo de Rendiciones";

    public String getWindowTitle() {
        return "Registro de rendiciones";
    }

    private final RendicionManejoLogic viewLogic = new RendicionManejoLogic();
    private final String[] VISIBLE_COLUMN_IDS = new String[]{ "codComprobante", "fecComprobante", "destinoNombre", "txtGlosa",
            "numGastototal", "tipoMoneda", "flgEnviado", "codComprobanteenlace"
            //, "codTipomoneda"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] { "codTipomoneda"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"Numero", "Fecha", "Responsable", "Glosa general", "Monto",
            "Moneda", "Enviado a contab.", "Comprobante en contab."
    };
    private final int[] FILTER_WIDTH = new int[]{
            4, 4, 15, 15, 4, 2, 1, 5
    };
    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{ /*"txtCorrelativo"*/ /*"flgEnviado", "codOrigenenlace",
            "codComprobanteenlace"*/};

    private Date filterInitialDate = GenUtil.getBeginningOfMonth(GenUtil.dateAddDays(new Date(), -32));

    private BeanItemContainer<ScpRendicioncabecera> container;

    private PersistanceService comprobanteService;

    public RendicionSimpleManejoView(PersistanceService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    public Grid.FooterRow gridFooter;

    private Character moneda = null;

    private Boolean onlyEnviados = null;

    private GeneratedPropertyContainer gpContainer;

    private static final Logger log = LoggerFactory.getLogger(RendicionManejoView.class);

    @Override
    public void init() {
        setSizeFull();
        addStyleName("crud-view");

        //noinspection unchecked
        if (Role.isCaja() || Role.isBanco() || Role.isPrivileged()) {
            container = new BeanItemContainer(ScpRendicioncabecera.class, getService().getRendicioncabeceraRep().findByFecComprobanteBetween(filterInitialDate, new Date()));
            container.removeContainerFilters("codUregistro");
        } else {
            container = new BeanItemContainer(ScpRendicioncabecera.class, getService().getRendicioncabeceraRep().findByCodUregistroAndFecComprobanteBetween(CurrentUser.get(), filterInitialDate, new Date()));
            log.info("Loading rendiciones for user: " + CurrentUser.get() + " " + container.getItemIds().size());
            container.removeContainerFilters("codUregistro");
            container.addContainerFilter(new Compare.Equal("codUregistro", CurrentUser.get()));
        }
        gpContainer = new GeneratedPropertyContainer(container);
        gpContainer.addGeneratedProperty("msgUsuario",
                new PropertyValueGenerator<String>() {
                    @Override
                    public String getValue(Item item, Object itemId,
                                            Object propertyId) {
                        if (((BeanItem)item).getBean()!=null && ((ScpRendicioncabecera)((BeanItem)item).getBean()).getCodUregistro()!=null) {
                            MsgUsuario ingresadoPor = getService().getMsgUsuarioRep().findByTxtUsuario(((ScpRendicioncabecera) ((BeanItem) item).getBean()).getCodUregistro().toLowerCase());
                            if (ingresadoPor!=null)
                                return ingresadoPor.getTxtNombre();
                            else
                                return ((ScpRendicioncabecera) ((BeanItem) item).getBean()).getCodUregistro();
                        } else
                            return "";
                    }
                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });
        gpContainer.addGeneratedProperty("destinoNombre",
                new PropertyValueGenerator<String>() {
                    @Override
                    public String getValue(Item item, Object itemId,
                                           Object propertyId) {
                        if (((BeanItem)item).getBean()!=null && ((ScpRendicioncabecera)((BeanItem)item).getBean()).getCodUregistro()!=null) {
                            ScpDestino destino = getService().getDestinoRepo().findByCodDestino((((ScpRendicioncabecera) ((BeanItem) item).getBean())).getCodDestino());
                            if (destino != null)
                                return destino.getTxtNombre();
                        }
                        return "";
                    }
                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        gpContainer.addGeneratedProperty("tipoMoneda",
                new PropertyValueGenerator<String>() {
                    @Override
                    public String getValue(Item item, Object itemId,
                                            Object propertyId) {

                        return GenUtil.getSymMoneda(GenUtil.getLitMoneda(((ScpRendicioncabecera) ((BeanItem) item).getBean()).getCodTipomoneda()));
                    }
                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });


        grid.setContainerDataSource(gpContainer);
        log.info("Items " + gpContainer.getItemIds().size());

        grid.setEditorEnabled(false);
        grid.setSortOrder(Sort.by("fecComprobante", SortDirection.DESCENDING).then("codComprobante", SortDirection.DESCENDING).build());

        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        //Arrays.asList(HIDDEN_COLUMN_IDS).forEach( colName ->  grid.getColumn(colName).setHidden(true));

        ViewUtil.alignMontosInGrid(grid);

        if (Role.isPrivileged()) {
            getBtnEnviar().setVisible(true);
            grid.setSelectionMode(SelectionMode.MULTI);
        } else {
            grid.setSelectionMode(SelectionMode.SINGLE);
            getBtnEnviar().setVisible(false);
        }

        // Fecha Desde Hasta
        ViewUtil.setupDateFiltersRendicionesPreviousMonth(container, fechaDesde, fechaHasta, this);

        //grid.getColumn("fecComprobantepago").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));
        grid.getColumn("fecComprobante").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        DataFilterUtil.bindTipoMonedaComboBox(selMoneda, "cod_tipomoneda", "Moneda", false);
        selMoneda.select(moneda);

        selMoneda.setNullSelectionAllowed(true);
        selMoneda.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                grid.deselectAll();
                moneda = (Character)e.getProperty().getValue();
                container.removeContainerFilters("codTipomoneda");
                container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));
                ViewUtil.filterColumnsByMoneda(grid, moneda);
            } else {
                container.removeContainerFilters("codTipomoneda");
                ViewUtil.filterColumnsByMoneda(grid, moneda);
            }
        });
        if (moneda!=null)
            container.addContainerFilter(new Compare.Equal("codTipomoneda", moneda));

        // Responsable
        DataFilterUtil.bindComboBox(filtroResponsable, "codDestino", DataUtil.loadDestinos(getService()),
                "txtNombre", false);

        filtroResponsable.addValueChangeListener(e -> {
            if (e.getProperty().getValue() != null) {
                grid.deselectAll();
                container.removeContainerFilters("codDestino");
                container.addContainerFilter(new Compare.Equal("codDestino", (String)e.getProperty().getValue()));
            } else {
                container.removeContainerFilters("codDestino");
            }
        });

        grid.addItemClickListener(this::setItemLogic);
        grid.getColumn("flgEnviado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());
        grid.getColumn("flgEnviado").setHidden(true);

        DataFilterUtil.bindBooleanComboBox(getFiltroEnviadasCombo(), "flgEnviado", "Filtro enviadas a contabilidad", new String[] { "Enviadas",  "No enviadas" });
        getFiltroEnviadasCombo().addValueChangeListener(e -> {
            grid.deselectAll();
            onlyEnviados = (Boolean)e.getProperty().getValue();
            container.removeContainerFilters("flgEnviado");
            if (onlyEnviados!=null) {
                container.addContainerFilter(new Compare.Equal("flgEnviado", onlyEnviados ? '1' : '0'));
            }
        });
        viewLogic.init(this);

        // Add filters
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        // Run date filter
        ViewUtil.filterComprobantes(container, "fecComprobante", fechaDesde, fechaHasta, this);

        ViewUtil.colorizeRowsRendiciones(grid);

        //gridFooter = grid.appendFooterRow();
    }

    public void refreshData() {
        viewLogic.refreshData();
    }

    @Override
    public void selectItem(VsjItem item) {
        grid.select(item);        for (Object vcb : container.getItemIds()) {
            if (((ScpRendicioncabecera)vcb).getCodRendicioncabecera().equals(((ScpRendicioncabecera)item).getCodRendicioncabecera())) {
                grid.select(vcb);
                return;
            }
        }
    }

    @Override
    public void selectMoneda(Character moneda) {
        grid.deselectAll();
        selMoneda.select(moneda);
    }

    @Override
    public void filter(Date fechaDesde, Date fechaHasta) {
       viewLogic.filter(fechaDesde, fechaHasta);
    }

    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            //Object id = event.getItem().getItemProperty("codCajabanco").getValue();
            //ScpRendicioncabecera vcb = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
            //viewLogic.modificarRendicion(vcb);
            Object id = event.getItem().getItemProperty("codRendicioncabecera").getValue();
            ScpRendicioncabecera vcb = getService().getRendicioncabeceraRep().findByCodRendicioncabecera((Integer) id);
            viewLogic.editarRendicion(vcb);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }
    
    public Collection<Object> getSelectedRows() {
        return grid.getSelectedRows();
    }

    public ScpRendicioncabecera getSelectedRow() {
        if (grid.getSelectedRows().toArray().length>0)
            return (ScpRendicioncabecera) grid.getSelectedRows().toArray()[0];
        else
            return null;
    }

    @Override
    public String getNavigatorViewName() {
        return VIEW_NAME;
    }

    public PersistanceService getService() {
        return comprobanteService;
    }

    @Override
    public Date getFilterInitialDate() {
        return filterInitialDate;
    }

    @Override
    public void setFilterInitialDate(Date filterInitialDate) {
        this.filterInitialDate = filterInitialDate;
    }

    public DateField getFechaDesde() {
        return fechaDesde;
    }

    public DateField getFechaHasta() {
        return fechaHasta;
    }

    public ComboBox getSelMoneda() {
        return selMoneda;
    }

    public Grid getGrid() {
        return grid;
    }

    public Button getBtnVerImprimir() {
        return btnVerImprimir;
    }

    public Button getBtnModificar() {
        return btnModificar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Character getMoneda() {
        return moneda;
    }

    public BeanItemContainer<ScpRendicioncabecera> getContainer() {
        return container;
    }

    public Grid.FooterRow getGridFooter() {
        return gridFooter;
    }

    public Button getBtnNueva() {
        return btnNueva;
    }

    public Button getBtnEnviar() {
        return btnEnviar;
    }

    public ComboBox getFiltroEnviadasCombo() {
        return filtroEnviadasCombo;
    }

    public RendicionManejoLogic getViewLogic() {
        return viewLogic;
    }
}
