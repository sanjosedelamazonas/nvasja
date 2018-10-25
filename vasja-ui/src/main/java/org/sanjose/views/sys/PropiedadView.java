package org.sanjose.views.sys;

import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.v7.ui.Grid.SelectionMode;
import org.sanjose.model.VsjPropiedad;
import org.sanjose.repo.VsjPropiedadRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;

import java.util.Collection;


/**          A
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class PropiedadView extends PropiedadUI implements Viewing {

    public static final String VIEW_NAME = "Config del Sistema";
    private static final Logger log = LoggerFactory.getLogger(PropiedadView.class);
    public final VsjPropiedadRep repo;
    private final PropiedadLogic viewLogic;
    private final String[] VISIBLE_COLUMN_IDS = new String[]{"nombre", "valor"};
    private final int[] FILTER_WIDTH = new int[]{16, 16};
    private PropiedadService propiedadService;

     public PropiedadView(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
        this.repo = propiedadService.getPropiedadRep();
        this.viewLogic = new PropiedadLogic(this);
        ConfigurationUtil.setPropiedadRepo(repo);
        if (ConfigurationUtil.getProperty("LOCALE") == null) {
            ConfigurationUtil.storeDefaultProperties();
            log.warn("Initializing Configuracion del Sistema");
//            Notification.show("Initializing Configuracion del Sistema", Notification.Type.ERROR_MESSAGE);
        }
        setSizeFull();
        addStyleName("crud-view");
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<VsjPropiedad> container = new BeanItemContainer(VsjPropiedad.class, repo.findAll());

        gridPropiedad.setSelectionMode(SelectionMode.MULTI);
        gridPropiedad
        	.setContainerDataSource(container);
        gridPropiedad.setColumns(VISIBLE_COLUMN_IDS);
        gridPropiedad.setColumnOrder(VISIBLE_COLUMN_IDS);
        gridPropiedad.sort("nombre", SortDirection.ASCENDING);

        gridPropiedad.setSelectionMode(SelectionMode.MULTI);

        gridPropiedad.setEditorFieldGroup(
                new BeanFieldGroup<>(VsjPropiedad.class));

        ViewUtil.setupColumnFilters(gridPropiedad, VISIBLE_COLUMN_IDS, FILTER_WIDTH);

        ContextMenu gridContextMenu = new ContextMenu(gridPropiedad, true);
        gridContextMenu.addContextMenuOpenListener(e -> {
            gridContextMenu.removeItems();
            // TODO 8
            final Object itemId = null;
            //final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nueva propiedad", k -> {
                    viewLogic.newPropiedad();
                });
            } else {
                gridContextMenu.addItem("Editar propiedad", k -> {
                    gridPropiedad.editItem(itemId);
                });
                gridContextMenu.addItem("Nueva propiedad", k -> {
                    viewLogic.newPropiedad();
                });
                gridContextMenu.addItem("Elminar esta propiedad", k -> {
                    gridPropiedad.getContainerDataSource().removeItem(itemId);
                });
            }
        });

        viewLogic.init();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
    }

    public void clearSelection() {
        gridPropiedad.getSelectionModel().reset();
    }

    public Collection<Object> getSelectedRow() {
        return gridPropiedad.getSelectedRows();
    }

    public void removeRow(VsjPropiedad vsj) {
    	repo.delete(vsj);    	
    	gridPropiedad.getContainerDataSource().removeItem(vsj);
    }

    public PropiedadService getPropiedadService() {
        return propiedadService;
    }
}
