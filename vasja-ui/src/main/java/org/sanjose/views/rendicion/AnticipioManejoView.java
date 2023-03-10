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
public class AnticipioManejoView extends AnticipioManejoUI {

    public static final String VIEW_NAME = "Anticipios";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(AnticipioManejoView.class);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "id", "fecAnticipio", "txtGlosa",
            "numAnticipio", "indTipomoneda", "codComprobante"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{"ID", "Fecha", "Glosa", "Monto", "Moneda", "Codigo"
    };

    private final String[] NONEDITABLE_COLUMN_IDS = new String[]{"id"};

    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "indTipomoneda", "codComprobante"
    };
    private final int[] FILTER_WIDTH = new int[]{
            3, 5, 12,
            4, 6
    };
    private PersistanceService service;

    private String codComprobante;

    private Character indTipomoneda;

    private ScpRendicioncabecera rendicioncabecera;

    private FieldGroup.CommitHandler gridCommitHandler;

    private FieldGroup fieldGroup;

    private BigDecimal total = new BigDecimal(0.00);

    public AnticipioManejoView(PersistanceService service) {
        this.service = service;
        setSizeFull();
    }

    public void init(ScpRendicioncabecera rendicioncabecera) {
        this.codComprobante = rendicioncabecera.getCodComprobante();
        this.indTipomoneda = rendicioncabecera.getCodTipomoneda();
        this.rendicioncabecera = rendicioncabecera;
        this.getTxtCodComprobante().setValue(codComprobante);
        @SuppressWarnings("unchecked") BeanItemContainer<VsjRendicionanticipio> container = new BeanItemContainer(VsjRendicionanticipio.class, getService().getVsjRendicionanticipioRep().findByCodComprobante(codComprobante));
        grid.setContainerDataSource(container);
        grid.setColumnOrder(VISIBLE_COLUMN_IDS);
        
//        grid.getDefaultHeaderRow().getCell("codConfiguracion").setText("Codigo");
//        grid.getColumn("txtConfiguracion").setWidth(200);
        grid.getColumn("id").setEditable(false);
        grid.setSelectionMode(SelectionMode.MULTI);

        fieldGroup = new BeanFieldGroup<>(VsjRendicionanticipio.class);
        grid.setEditorFieldGroup(fieldGroup);

        grid.setEditorEnabled(true);

        TextField tf = (TextField) grid.getColumn("codComprobante").getEditorField();
        tf.setValue(codComprobante);

        PopupDateField fechaAnticipio = new PopupDateField();
        fechaAnticipio.setConverter(DateToTimestampConverter.INSTANCE);
        fechaAnticipio.setResolution(Resolution.DAY);
        fechaAnticipio.setValue(new Date());
        grid.getColumn("fecAnticipio").setEditorField(fechaAnticipio);
        grid.getColumn("fecAnticipio").setRenderer(new DateNotNullRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

        NumberField numAnticipio = new NumberField();
        ViewUtil.setDefaultsForNumberField(numAnticipio);
        grid.getColumn("numAnticipio").setEditorField(numAnticipio);

        // Tipo Moneda
        ComboBox selTipomoneda = new ComboBox();
        DataFilterUtil.bindTipoMonedaComboBox(selTipomoneda, "indTipomoneda", "Moneda");
        grid.getColumn("indTipomoneda").setEditorField(selTipomoneda);

        ViewUtil.alignMontosInGrid(grid);

        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, NONEDITABLE_COLUMN_IDS);

        Arrays.asList(HIDDEN_COLUMN_IDS)
                .forEach( e -> grid.getColumn(e).setHidden(true));

        getBtnNuevo().addClickListener(clickEvent -> nuevoAnticipio());
        getBtnEliminar().addClickListener(clickEvent -> eliminarAnticipio());


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
                    VsjRendicionanticipio vcb = ((VsjRendicionanticipio)((BeanItem) item).getBean()).prepareToSave();
                    if (vcb != null) {
                        vcb.setIndTipomoneda(indTipomoneda);
                        //fieldGroup.commit();
                        //commitEvent.getFieldBinder();
                        getService().getVsjRendicionanticipioRep().save(vcb);
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


    private void nuevoAnticipio() {
        clearSelection();
        VsjRendicionanticipio anticipio = new VsjRendicionanticipio();
        anticipio.setIndTipomoneda(indTipomoneda);
        anticipio.setCodComprobante(codComprobante);
        anticipio.setTxtGlosa("");
        anticipio.setNumAnticipio(new BigDecimal(0.00));
        grid.getContainerDataSource().addItem(anticipio);
    }

    private void eliminarAnticipio() {
        List<VsjRendicionanticipio> rows = new ArrayList<>();

        for (Object vsj : getSelectedRow()) {
            if (vsj instanceof VsjRendicionanticipio)
                rows.add((VsjRendicionanticipio) vsj);
        }
        clearSelection();
        for (VsjRendicionanticipio vsj : rows) {
            removeRow(vsj);
        }
        calcTotal();
    }

    public void refreshData(String codComprobante) {
        grid.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(getService().getVsjRendicionanticipioRep().findByCodComprobante(codComprobante));
        calcTotal();
    }

    public void calcTotal() {
        Collection<VsjRendicionanticipio> anticipios = (Collection<VsjRendicionanticipio>)grid.getContainerDataSource().getItemIds();
        total = new BigDecimal(0.00);
        for (VsjRendicionanticipio ant : anticipios) {
            total = total.add(ant.getNumAnticipio());
        }
        getTxtTotal().setValue(GenUtil.numFormat(total));
        rendicioncabecera.setNumTotalanticipo(total);
        getService().getRendicioncabeceraRep().save(rendicioncabecera);
        //Collection<VsjRendicionanticipio> items = grid.getContainerDataSource().getItemIds();
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

    public void removeRow(VsjRendicionanticipio vsj) {
        getService().getVsjRendicionanticipioRep().delete(vsj);
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
