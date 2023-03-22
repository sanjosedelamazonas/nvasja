package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.converter.ZeroOneTrafficLightConverter;
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
            "codUsuario", "txtUsuario", "txtNombre", "codRol", "txtCorreo", "txtAplicacion", "flgEstado",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Codigo", "Usuario", "Nombre", "Rol", "Correo", "Aplicacion", "Activo",
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

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setEditorEnabled(false);

        //grid.getColumn("id.fecFechacambio").setRenderer(new DateRenderer(ConfigurationUtil.get("DEFAULT_DATE_RENDERER_FORMAT")));

                //                new BeanFieldGroup<>(ScpDestino.class));
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null, service);

        grid.addItemClickListener(this::setItemLogic);
        btnNuevoUsuario.addClickListener(e -> editUsuario(null));

        //btnEliminar.addClickListener(e -> eliminarTipoCambio());
        grid.getColumn("flgEstado").setConverter(new ZeroOneTrafficLightConverter()).setRenderer(new HtmlRenderer());

        ViewUtil.colorizeRowsUsuarios(grid);

        btnEliminar.addClickListener(clickEvent -> {
            MsgUsuario item = (MsgUsuario)grid.getSelectedRow();
            //String codUsuario = item.getCodUsuario();
            MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
            MessageBox
                    .createQuestion()
                    .withCaption("Eliminar: " + item.getTxtUsuario())
                    .withMessage("?Esta seguro que lo quiere eliminar?")
                    .withYesButton(() -> {
                        eliminarUsuario((MsgUsuario)grid.getSelectedRow());
                    })
                    .withNoButton()
                    .open();
        });

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
            usuarioCrearView.saveUsuario(this);
            usuarioWindow.close();
            refreshData();
        });
        usuarioCrearView.getBtnAnular().addClickListener(event -> {
            usuarioWindow.close();
        });
        UI.getCurrent().addWindow(usuarioWindow);
    }


    public void eliminarUsuario(MsgUsuario usuario) {
        if (service.getDestinoRepo().findByTxtUsuario(usuario.getTxtUsuario())!=null) {
            usuario.setFlgEstado('0');
            service.getMsgUsuarioRep().save(usuario);
            refreshData();
        } else {
            service.getMsgUsuarioRep().delete(usuario);
        }
    }

    public void notifySendingInvitation(CompletableFuture<String> sendRes){
        if (sendRes!=null){
            sendRes.join();
            try {
                String error = sendRes.get();
                if (error!=null) {
                    ViewUtil.showNotification(error, Notification.Type.ERROR_MESSAGE);
                } else {
                    ViewUtil.showNotification("La invitacion ha sido enviada correctamente", Notification.Type.TRAY_NOTIFICATION);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
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
