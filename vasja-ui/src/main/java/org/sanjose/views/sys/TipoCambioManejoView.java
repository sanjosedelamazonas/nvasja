package org.sanjose.views.sys;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.ScpTipocambioPK;
import org.sanjose.model.VsjConfiguractacajabanco;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.TipoCambio;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.dict.DestinoListLogic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.*;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link DestinoListLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class TipoCambioManejoView extends TipoCambioManejoUI implements Viewing {

    public static final String VIEW_NAME = "Tipo de Cambio";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(TipoCambioManejoView.class);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id.fecFechacambio", "numTccdolar", "numTcvdolar", "numTcceuro", "numTcveuro",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Fecha", "Compra USD", "Venta USD", "Compra EUR", "Venta EUR",
            "Fecha reg.", "Usuario reg.", "Fecha actual.", "Usuario actual."
    };
    private final int[] FILTER_WIDTH = new int[]{
            5, 5, 5, 5, 5,
            5, 7, 5, 7
    };

    private PersistanceService service;

    public TipoCambioManejoView(PersistanceService comprobanteService) {
        this.service = comprobanteService;
        setSizeFull();
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<ScpDestino> container = new BeanItemContainer(ScpTipocambio.class, new ArrayList());
        grid.setContainerDataSource(container);
        container.addNestedContainerBean("id");
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, new String[] {});

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach(colName ->  grid.getColumn(colName).setHidden(true));

        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setEditorEnabled(true);

        fechaAno.setValue(new Date());
        fechaAno.addValueChangeListener(val -> refreshData());

        grid.getColumn("id.fecFechacambio").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));


        ViewUtil.alignMontosInGrid(grid);

        //                new BeanFieldGroup<>(ScpDestino.class));
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null, service);

        //grid.addItemClickListener(this::setItemLogic);
        btnNuevoTipoCambio.addClickListener(e -> nuevoTipoCambio());

        btnEliminar.addClickListener(e -> eliminarTipoCambio());

        btnBajar.addClickListener(e -> intentarBajarTC());

        FieldGroup.CommitHandler gridCommitHandler = new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                BeanItem beanItem = (BeanItem)grid.getContainerDataSource().getItem(grid.getEditedItemId());
                ScpTipocambio tipocambio = (ScpTipocambio)beanItem.getBean();
                // Attach logic to num fields
                tipocambio.prepareToSave();
                tipocambio.setFecFactualiza(new Timestamp(new Date().getTime()));
                tipocambio.setCodUactualiza(CurrentUser.get());
                getService().getTipocambioRep().save(tipocambio);
            }
        };
        grid.getEditorFieldGroup().addCommitHandler(gridCommitHandler);
    }

    private void intentarBajarTC() {
        try {
            bajarTipoCambio();
        } catch (TipoCambio.TipoCambioNoSePuedeBajar te) {
            MessageBox
                    .createError()
                    .withCaption("!Atencion!")
                    .withMessage(te.getMessage())
                    .open();
        }
    }


    private void bajarTipoCambio() throws TipoCambio.TipoCambioNoSePuedeBajar {
        List<Date> dates = GenUtil.getDatesBetween(fecDesde.getValue(), fecHasta.getValue());
        List<ScpTipocambio> tcToRefresh = new ArrayList<>();
        for (Date d : dates) {
            int i=0;
            boolean noExiste = false;
            while (noExiste && i < 3) {
                i++;
                try {
                    ScpTipocambio tc = TipoCambio.checkTipoCambio(GenUtil.dateAddDays(d,1-i), getService().getTipocambioRep());
                    System.out.println("tc " + d.toString() + " " + tc);
                    if (tc != null)
                        tcToRefresh.add(tc);
                } catch (TipoCambio.TipoCambioNoExiste te) {
                    System.out.println("no existe " + d.toString());
                    noExiste = true;
                }
            }
        }
        for (ScpTipocambio tc : tcToRefresh) {
            grid.getContainerDataSource().removeItem(tc);
            grid.getContainerDataSource().addItem(tc);
            SortOrder[] sortOrders = grid.getSortOrder().toArray(new SortOrder[1]);
            grid.setSortOrder(Arrays.asList(sortOrders));
        }

    }

    private void nuevoTipoCambio() {
        clearSelection();
        ScpTipocambio tipocambio = new ScpTipocambio();
        tipocambio.prepareToSave();
        ScpTipocambioPK tipocambioId = new ScpTipocambioPK();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        tipocambioId.setTxtAnoproceso(sdf.format(getFechaAno().getValue()));
        tipocambioId.setFecFechacambio(GenUtil.getBeginningOfDay(new Date()));
        tipocambio.setId(tipocambioId);
        grid.getContainerDataSource().addItemAt(0, tipocambio);
    }

    private void eliminarTipoCambio() {
        Collection<Object> rows = grid.getSelectedRows();
        for (Object obj : rows) {
            ScpTipocambio tc = (ScpTipocambio)obj;
            grid.getContainerDataSource().removeItem(tc);
            getService().getTipocambioRep().delete(tc);
        }
    }


    public void refreshData() {
        grid.getContainerDataSource().removeAllItems();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(service.getTipocambioRep().findById_TxtAnoprocesoOrderById_FecFechacambioDesc(sdf.format(getFechaAno().getValue())));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return grid.getSelectedRows();
    }

    public void removeRow(ScpDestino vsj) {
        service.getDestinoRepo().delete(vsj);
        grid.getContainerDataSource().removeItem(vsj);
    }

    public PersistanceService getService() {
        return service;
    }

    public DateField getFechaAno() {
        return fechaAno;
    }

    public DateField getFecDesde() {
        return fecDesde;
    }

    public DateField getFecHasta() {
        return fecHasta;
    }

    public Button getBtnBajar() {
        return btnBajar;
    }

    public Button getBtnNuevoTipoCambio() {
        return btnNuevoTipoCambio;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Grid getGrid() {
        return grid;
    }
}
