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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.converter.CargoCuartaConverter;
import org.sanjose.converter.TipoDestinoConverter;
import org.sanjose.converter.TipoDocumentoConverter;
import org.sanjose.model.*;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.DestinoView;
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
public class DestinoListView extends DestinoListUI implements Viewing {

    public static final String VIEW_NAME = "Destinos";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(DestinoListView.class);
    private final DestinoListLogic viewLogic = new DestinoListLogic(this);
    private final String[] VISIBLE_COLUMN_IDS = new String[]{
            "codDestino", "txtNombre", "txtNombredestino", "indTipopersona", "indTipodestino", "indSexo",
            "indTipodctoidentidad", "txtRuc", "txtNumerodctoidentidad", "codCargo", "txtDireccion",
            "txtApellidomaterno", "txtApellidopaterno", "txtTelefono1", "txtTelefono2",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] HIDDEN_COLUMN_IDS = new String[] {
            "txtApellidomaterno", "txtApellidopaterno", "txtTelefono1", "txtTelefono2",
            "fecFregistro", "codUregistro", "fecFactualiza", "codUactualiza"
    };
    private final String[] VISIBLE_COLUMN_NAMES = new String[]{
            "Codigo", "Nombre", "Descripcion", "Tipo persona", "Clasificacion", "Genero",
            "Tipo doc", "RUC o DNI", "Num de doc", "Cargo", "Direccion",
            "Appelido materno", "Appelido paterno", "Telefono 1", "Telefono 2",
            "Fecha reg.", "Usuario reg.", "Fecha actual.", "Usuario actual."
    };
    private final int[] FILTER_WIDTH = new int[]{
            6, 10, 15, 3, 3, 3,
            3, 7, 7, 4, 8
    };

    private PersistanceService service;

    public DestinoListView(PersistanceService comprobanteService) {
        this.service = comprobanteService;
        setSizeFull();
    }

    @Override
    public void init() {
        @SuppressWarnings("unchecked") BeanItemContainer<ScpDestino> container = new BeanItemContainer(ScpDestino.class, service.getConfiguractacajabancoRepo().findAll());
        grid.setContainerDataSource(container);
        ViewUtil.setColumnNames(grid, VISIBLE_COLUMN_NAMES, VISIBLE_COLUMN_IDS, new String[] {});

        Arrays.asList(HIDDEN_COLUMN_IDS).forEach(colName ->  grid.getColumn(colName).setHidden(true));

        grid.setSelectionMode(SelectionMode.MULTI);

        grid.setEditorEnabled(false);

        ComboBox clasificacion = new ComboBox();
        DataFilterUtil.bindTipoDestinoComboBox(clasificacion, "indTipodestino", "Sel Clasificacion");
        grid.getColumn("indTipodestino").setEditorField(clasificacion);
        grid.getColumn("indTipodestino").setConverter(new TipoDestinoConverter());

        // Tipo doc
        ComboBox tipoDocumento = new ComboBox();
        DataFilterUtil.bindComboBox(tipoDocumento, "codTipodocumento", service.getTipodocumentoRepo().findAll(),
                "Sel Tipo documento", "txtDescripcion");
        grid.getColumn("indTipodctoidentidad").setEditorField(tipoDocumento);
        grid.getColumn("indTipodctoidentidad").setConverter(new TipoDocumentoConverter(service.getTipodocumentoRepo()));

        //.setRenderer(new HtmlRenderer());


        grid.getColumn("indTipodctoidentidad").setEditorField(tipoDocumento);


        // Cargo 4ta
        ComboBox cargo = new ComboBox();
        DataFilterUtil.bindComboBox(cargo, "codCargo", service.getCargocuartaRepo().findAll(), "Sel Cargo 4ta",
                "txtDescripcion");
        grid.getColumn("codCargo").setEditorField(cargo);
        grid.getColumn("codCargo").setConverter(new CargoCuartaConverter(service.getCargocuartaRepo()));


        // Genero
        ComboBox genero = new ComboBox();
        DataFilterUtil.bindGeneroComboBox(genero, "indSexo", "Sel Sexo");
        grid.getColumn("indSexo").setEditorField(genero);

        // Tipo persona
        ComboBox tipoDePersona = new ComboBox();
        DataFilterUtil.bindTipoPersonaComboBox(tipoDePersona, "indTipopersona", "Sel Tipo de persona");
        grid.getColumn("indTipopersona").setEditorField(tipoDePersona);

        //                new BeanFieldGroup<>(ScpDestino.class));
        ViewUtil.setupColumnFilters(grid, VISIBLE_COLUMN_IDS, FILTER_WIDTH, null, service);

        grid.addItemClickListener(this::setItemLogic);

        viewLogic.init();
    }


    private void setItemLogic(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            Object id = event.getItem().getItemProperty("codDestino").getValue();
            //ScpRendicioncabecera vcb = getService().getCajabancoRep().findByCodCajabanco((Integer) id);
            //viewLogic.modificarRendicion(vcb);
            //Object id = event.getItem().getItemProperty("codDestino").getValue();
            editDestino(getService().getDestinoRepo().findByCodDestino(id.toString()));
        }
    }


    public void editDestino(ScpDestino destino) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(700, Sizeable.Unit.PIXELS);
        destinoWindow.setHeight(550, Sizeable.Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(getService().getDestinoRepo(), getService().getCargocuartaRepo(), getService().getTipodocumentoRepo());
        if (destino==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.getBtnGuardar().addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            if (editedItem!=null) {
                destinoWindow.close();
                refreshData();
            }
        });
        destinoView.getBtnAnular().addClickListener(event -> {
            destinoView.viewLogic.anularDestino();
            destinoWindow.close();
        });

        destinoView.getBtnEliminar().addClickListener(clickEvent -> {
            try {
                ScpDestino item = destinoView.getScpDestino();
                String codDestino = item.getCodDestino();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtNombredestino())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            String msg = MainUI.get().getProcUtil().checkIfcanBeDeleted(codDestino, getService());
                            if (msg.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                refreshData();
                                destinoWindow.close();
                            } else {
                                MessageBox
                                        .createWarning()
                                        .withCaption("No se puede eliminar destino: " + item.getTxtNombredestino())
                                        .withMessage("Los sigientes comprobantes usan este destino como Responsable o como Codigo Auxiliar: " + msg)
                                        .open();
                            }
                        })
                        .withNoButton()
                        .open();
            } catch (FieldGroup.CommitException ce) {
                Notification.show("Error al eliminar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.info("Got Commit Exception: " + ce.getMessage());
            }
        });
        UI.getCurrent().addWindow(destinoWindow);
    }


    public void refreshData() {
        grid.getContainerDataSource().removeAllItems();
        ((BeanItemContainer) grid.getContainerDataSource()).addAll(service.getDestinoRepo().findAll());
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
