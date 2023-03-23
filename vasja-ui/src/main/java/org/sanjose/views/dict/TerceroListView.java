package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.converter.BooleanTrafficLightConverter;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.util.*;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link DestinoListLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class TerceroListView extends TerceroListUI implements Viewing {

    public static final String VIEW_NAME = "Terceros";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(TerceroListView.class);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "activo", "codDestino", "txtNombre", "txtNombredestino", "indSexo",
            "txtEmail", "enviarreporte", "txtUsuario",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "txtNombre", "indSexo", "txtEmail", "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Activo", "Codigo", "Nombre", "Descripcion", "Genero",
            "Correo", "Enviar reporte", "Usuario",
            "Fecha reg.", "Usuario reg.", "Fecha actual.", "Usuario actual."
    };
    private final int[] FILTER_WIDTH = new int[]{
            6, 10, 15, 3, 3, 3,
            3, 7, 7, 4, 8, 5
    };
    private ComboBox usuario = new ComboBox();

    private PersistanceService service;

    public TerceroListView(PersistanceService comprobanteService) {
        this.service = comprobanteService;
        setSizeFull();
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<ScpDestino> container = new BeanItemContainer(ScpDestino.class, new ArrayList());
        grid.setContainerDataSource(container);
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, new String[] {});

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach(colName ->  grid.getColumn(colName).setHidden(true));

        grid.setSelectionMode(SelectionMode.MULTI);

        grid.setEditorEnabled(true);

        // Usuario
        DataFilterUtil.bindComboBox(usuario, "txtUsuario", service.getMsgUsuarioRep().findAll(), "", null);
        grid.getColumn("txtUsuario").setEditorField(usuario);

        // Genero
        ComboBox genero = new ComboBox();
        DataFilterUtil.bindGeneroComboBox(genero, "indSexo", "Sel Sexo");
        grid.getColumn("indSexo").setEditorField(genero);


        grid.getColumn("activo").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());
        grid.getColumn("enviarreporte").setConverter(new BooleanTrafficLightConverter()).setRenderer(new HtmlRenderer());

        //                new BeanFieldGroup<>(ScpDestino.class));
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null, service);

        //grid.addItemClickListener(this::setItemLogic);
        btnNuevo.addClickListener(e -> nuevoDestino());

        FieldGroup.CommitHandler gridCommitHandler = new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

            }
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                BeanItem beanItem = (BeanItem)grid.getContainerDataSource().getItem(grid.getEditedItemId());
                ScpDestino destinoToSave  = (ScpDestino)beanItem.getBean();
                // Attach logic to num fields
                destinoToSave.prepToSave();
                getService().getDestinoRepo().save(destinoToSave);
            }
        };
        grid.getEditorFieldGroup().addCommitHandler(gridCommitHandler);
        ViewUtil.colorizeRowsTerceros(grid);
    }

    private void nuevoDestino() {
        clearSelection();
        ScpDestino newTercero = new ScpDestino();
        newTercero.setIndTipodestino('3');
        newTercero.setActivo(true);
        newTercero.setEnviarreporte(true);
        grid.getContainerDataSource().addItem(newTercero);
    }

    public void refreshData() {
        grid.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(service.getDestinoRepo().findByIndTipodestino('3'));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
        DataFilterUtil.refreshComboBox(usuario, service.getMsgUsuarioRep().findAll(), "txtUsuario", null,null);
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
}
