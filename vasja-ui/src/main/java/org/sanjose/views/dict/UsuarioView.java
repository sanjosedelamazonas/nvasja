package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link DestinoListLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class UsuarioView extends UsuarioUI implements Viewing {

    public static final String VIEW_NAME = "Usuarios";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(UsuarioView.class);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "codUsuario", "txtUsuario", "txtNombre", "codRol", "txtCorreo", "txtAplicacion",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Codigo", "Usuario", "Nombre", "Rol", "Correo", "Aplicacion",
            "Fecha reg.", "Usuario reg.", "Fecha actual.", "Usuario actual."
    };
    private final int[] FILTER_WIDTH = new int[]{
            5, 7, 12, 4, 14, 6,
            5, 5, 5, 5
    };

    private PersistanceService service;

    public UsuarioView(PersistanceService service) {
        this.service = service;
        setSizeFull();
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<ScpDestino> container = new BeanItemContainer(MsgUsuario.class, new ArrayList());
        grid.setContainerDataSource(container);
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, new String[] {});

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach(colName ->  grid.getColumn(colName).setHidden(true));

        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setEditorEnabled(false);

        //grid.getColumn("id.fecFechacambio").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

                //                new BeanFieldGroup<>(ScpDestino.class));
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null, service);

        grid.addItemClickListener(this::setItemLogic);
        btnNuevoUsuario.addClickListener(e -> editUsuario(null));

        //btnEliminar.addClickListener(e -> eliminarTipoCambio());
    }


    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("txtUsuario").getValue();
            editUsuario(getService().getMsgUsuarioRep().findByTxtUsuario(id.toString()));
        }
    }


    public void editUsuario(MsgUsuario usuario) {
        Window usuarioWindow = new Window();

        usuarioWindow.setWindowMode(WindowMode.NORMAL);
        usuarioWindow.setDraggable(true);
        usuarioWindow.setWidth(700, Sizeable.Unit.PIXELS);
        usuarioWindow.setHeight(550, Sizeable.Unit.PIXELS);
        usuarioWindow.setPositionX(200);
        usuarioWindow.setPositionY(50);
        usuarioWindow.setModal(true);
        usuarioWindow.setClosable(false);

        UsuarioCrearView usuarioCrearView = new UsuarioCrearView(getService().getMsgUsuarioRep(),
                getService().getMsgRolRep(), getService().getDestinoRepo());
        if (usuario==null)
            usuarioCrearView.nuevoUsuario();
        else {
            usuarioCrearView.editarUsuario(usuario);
        }
        usuarioWindow.setContent(usuarioCrearView);

        usuarioCrearView.getBtnGuardar().addClickListener(event -> {
            CompletableFuture<String> sendRes = usuarioCrearView.saveUsuario();
            usuarioWindow.close();
            refreshData();
            if (sendRes!=null){
                sendRes.join();
                try {
                    String error = sendRes.get();
                    if (error!=null) {
                        Notification.show(error, Notification.Type.WARNING_MESSAGE);
                    } else {
                        Notification.show("La invitacion ha sido enviada", Notification.Type.HUMANIZED_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        usuarioCrearView.getBtnAnular().addClickListener(event -> {
            usuarioWindow.close();
        });

        usuarioCrearView.getBtnEliminar().addClickListener(clickEvent -> {
            try {
                MsgUsuario item = usuarioCrearView.getMsgUsuario();
                String codUsuario = item.getCodUsuario();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtUsuario())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            usuarioCrearView.eliminarUsuario(usuario);
                            refreshData();
                        })
                        .withNoButton()
                        .open();
            } catch (FieldGroup.CommitException ce) {
                Notification.show("Error al eliminar el usuario: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.info("Got Commit Exception: " + ce.getMessage());
            }
        });
        UI.getCurrent().addWindow(usuarioWindow);
    }

    public void refreshData() {
        grid.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(service.getMsgUsuarioRep().findAll());
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

}
