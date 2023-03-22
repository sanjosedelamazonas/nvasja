package org.sanjose.views.rendicion;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import org.sanjose.converter.DateToTimestampConverter;
import org.sanjose.model.*;
import org.sanjose.render.DateNotNullRenderer;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.PersistanceService;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;
import java.util.*;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class AnticipoManejoView extends AnticipoManejoUI {

    public static final String VIEW_NAME = "Anticipos";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(AnticipoManejoView.class);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id", "fecAnticipo", "txtGlosa",
            "numAnticipo", "indTipomoneda", "codComprobante"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"ID", "Fecha", "Glosa", "Monto", "Moneda", "Codigo"
    };

    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"id"};

    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "indTipomoneda", "codComprobante"
    };
    private final int[] FILTER_WIDTH = new int[]{
            2, 5, 18,
            4, 6
    };
    private PersistanceService service;

    private String codComprobante;

    private Character indTipomoneda;

    private ScpRendicioncabecera rendicioncabecera;

    private FieldGroup.CommitHandler gridCommitHandler;

    private FieldGroup fieldGroup;

    private BigDecimal total = new BigDecimal(0.00);

    public AnticipoManejoView(PersistanceService service) {
        this.service = service;
        setSizeFull();
    }

    public void init(ScpRendicioncabecera rendicioncabecera) {
        this.codComprobante = rendicioncabecera.getCodComprobante();
        this.indTipomoneda = rendicioncabecera.getCodTipomoneda();
        this.rendicioncabecera = rendicioncabecera;
        this.getTxtCodComprobante().setValue(codComprobante);
        @SuppressWarnings("unchecked") BeanItemContainer<VsjRendicionanticipo> container = new BeanItemContainer(VsjRendicionanticipo.class, getService().getVsjRendicionanticipoRep().findByCodComprobante(codComprobante));
        grid.setContainerDataSource(container);
        grid.setColumnOrder(VISIBLE_COLUMN_IDS);
        
        grid.getColumn("id").setEditable(false);
        grid.setSelectionMode(SelectionMode.MULTI);

        fieldGroup = new BeanFieldGroup<>(VsjRendicionanticipo.class);
        grid.setEditorFieldGroup(fieldGroup);

        grid.setEditorEnabled(true);

        TextField tf = (TextField) grid.getColumn("codComprobante").getEditorField();
        tf.setValue(codComprobante);

        PopupDateField fechaAnticipo = new PopupDateField();
        fechaAnticipo.setConverter(DateToTimestampConverter.INSTANCE);
        fechaAnticipo.setResolution(Resolution.DAY);
        fechaAnticipo.setValue(new Date());
        grid.getColumn("fecAnticipo").setEditorField(fechaAnticipo);
        grid.getColumn("fecAnticipo").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        NumberField numAnticipo = new NumberField();
        ViewUtil.setDefaultsForNumberField(numAnticipo);
        grid.getColumn("numAnticipo").setEditorField(numAnticipo);

        // Tipo Moneda
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "indTipomoneda", "Moneda");
        grid.getColumn("indTipomoneda").setEditorField(selTipomoneda);

        ViewUtil.alignMontosInGrid(grid);

        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS)
                .forEach( e -> grid.getColumn(e).setHidden(true));

        getBtnNuevo().addClickListener(clickEvent -> nuevoAnticipo());
        getBtnEliminar().addClickListener(clickEvent -> eliminarAnticipo());


        //grid.getEditorFieldGroup().removeCommitHandler(gridCommitHandler);

        gridCommitHandler = new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Object item = grid.getContainerDataSource().getItem(grid.getEditedItemId());
                // Attach logic to num fields
                try {
                    VsjRendicionanticipo vcb = ((VsjRendicionanticipo)((BeanItem) item).getBean()).prepareToSave();
                    if (vcb != null) {
                        vcb.setIndTipomoneda(indTipomoneda);
                        //fieldGroup.commit();
                        //commitEvent.getFieldBinder();
                        getService().getVsjRendicionanticipoRep().save(vcb);
                        grid.refreshRows(item);
                        calcTotal();
                    }
                } catch (FieldGroup.CommitException ce) {
                    Notification.show("No se puede guarder el item: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                    log.warn("Got Commit Exception: " + ce);
                }
            }
        };
        grid.getEditorFieldGroup().addCommitHandler(gridCommitHandler);
        calcTotal();
    }


    private void nuevoAnticipo() {
        clearSelection();
        VsjRendicionanticipo Anticipo = new VsjRendicionanticipo();
        Anticipo.setIndTipomoneda(indTipomoneda);
        Anticipo.setCodComprobante(codComprobante);
        Anticipo.setTxtGlosa("");
        Anticipo.setNumAnticipo(new BigDecimal(0.00));
        grid.getContainerDataSource().addItem(Anticipo);
    }

    private void eliminarAnticipo() {
        List<VsjRendicionanticipo> rows = new ArrayList<>();

        for (Object vsj : getSelectedRow()) {
            if (vsj instanceof VsjRendicionanticipo)
                rows.add((VsjRendicionanticipo) vsj);
        }
        clearSelection();
        for (VsjRendicionanticipo vsj : rows) {
            removeRow(vsj);
        }
        calcTotal();
    }

    public void refreshData(String codComprobante) {
        grid.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(getService().getVsjRendicionanticipoRep().findByCodComprobante(codComprobante));
        calcTotal();
    }

    public void calcTotal() {
        Collection<VsjRendicionanticipo> Anticipos = (Collection<VsjRendicionanticipo>)grid.getContainerDataSource().getItemIds();
        total = new BigDecimal(0.00);
        for (VsjRendicionanticipo ant : Anticipos) {
            total = total.add(ant.getNumAnticipo());
        }
        getTxtTotal().setValue(GenUtil.numFormat(total));
        rendicioncabecera.setNumTotalanticipo(total);
        getService().getRendicioncabeceraRep().save(rendicioncabecera);
    }

    public void enter(ViewChangeEvent event) {
        refreshData(codComprobante);
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return grid.getSelectedRows();
    }

    public void removeRow(VsjRendicionanticipo vsj) {
        getService().getVsjRendicionanticipoRep().delete(vsj);
        grid.getContainerDataSource().removeItem(vsj);
    }

    public PersistanceService getService() {
        return service;
    }


    public TextField getTxtCodComprobante() {
        return txtCodComprobante;
    }

    public Button getBtnCerrar() {
        return btnCerrar;
    }

    public Grid getGrid() {
        return grid;
    }

    public Button getBtnNuevo() {
        return btnNuevo;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public NumberField getTxtTotal() {
        return txtTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
