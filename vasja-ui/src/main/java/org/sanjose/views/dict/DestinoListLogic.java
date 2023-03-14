package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.model.*;
import org.sanjose.util.ConfigurationUtil;

import java.io.Serializable;
import java.util.*;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class DestinoListLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(DestinoListLogic.class);
	
    private final DestinoListView view;

    public DestinoListLogic(DestinoListView destinoListView) {
        view = destinoListView;
    }

    public void init() {
    	view.btnNuevo.addClickListener(e -> nuevoDestino());
        view.btnEliminar.addClickListener(e -> eliminarDestinos());
    }
    
    private void nuevoDestino() {
        view.clearSelection();
        view.editDestino(null);
        //view.grid.getContainerDataSource().addItemAt(0, new ScpDestino());
    }
    
    private void eliminarDestinos() {
        List<ScpDestino> rows = new ArrayList<>();
    	Map<String, String> destinoChecks = new HashMap<>();
    	StringBuilder confirma = new StringBuilder();
        for (Object vsj : view.getSelectedRow()) {
            log.debug("Got selected: " + vsj);
            if (vsj instanceof ScpDestino) {
                ScpDestino destino = (ScpDestino) vsj;
                rows.add(destino);
                String msg = MainUI.get().getProcUtil().checkIfcanBeDeleted(destino.getCodDestino(), view.getService());
                if (!msg.isEmpty()) {
                    destinoChecks.put(destino.getCodDestino() + " " + destino.getTxtNombredestino(), msg);
                }
                confirma.append("\n" + destino.getCodDestino() + " " + destino.getTxtNombredestino());
            }


        }
        MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
        MessageBox
                .createQuestion()
                .withCaption("Atencion!")
                .withMessage("?Esta seguro que quiere eliminar los siguientes destinos:" + confirma + "?")
                .withYesButton(() -> {
                    if (destinoChecks.isEmpty()) {
                        view.clearSelection();
                        for (ScpDestino vsj : rows) {
                            log.debug("Removing: " + vsj.getCodDestino());
                            view.removeRow(vsj);
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("No se puede eliminar los porque los siguientes comprobantes usan los destinos como Responsable o como Codigo Auxiliar:");
                        sb.append("\n");
                        for (String key: destinoChecks.keySet()) {
                            sb.append("\n\n" + key + ": " + destinoChecks.get(key));
                        }
                        if (view.getSelectedRow().size()==1)
                            sb.append("\n\nQuiere remplacar este destino por otro?");
                        MessageBox mb = MessageBox
                                .createWarning()
                                .withCaption("!Atencion!: ")
                                .withMessage(sb.toString());
                        if (view.getSelectedRow().size()==1) {
                            List<Object> selrows = new ArrayList<Object>();
                            selrows.addAll(view.getSelectedRow());
                            mb.withYesButton(() -> {
                                replaceDestino((ScpDestino)selrows.get(0), view);
                            }).withNoButton().open();
                        } else {
                            mb.open();
                        }
                    }
                })
                .withNoButton()
                .open();
    }

    public DestinoReplaceView replaceDestino(ScpDestino destinoToReplace, DestinoListView destinoListView) {
            Window destinoReplaceWindow = new Window();

            destinoReplaceWindow.setWindowMode(WindowMode.NORMAL);
            destinoReplaceWindow.setWidth(850, Sizeable.Unit.PIXELS);
            destinoReplaceWindow.setHeight(90, Sizeable.Unit.PIXELS);
            destinoReplaceWindow.setModal(true);
            destinoReplaceWindow.setResizable(false);
            destinoReplaceWindow.setClosable(false);

            DestinoReplaceView destinoReplaceView = new DestinoReplaceView(destinoListView);
            destinoReplaceView.init(destinoToReplace);
            destinoReplaceView.setDestinoReplaceWindow(destinoReplaceWindow);
            destinoReplaceWindow.setContent(destinoReplaceView);
            UI.getCurrent().addWindow(destinoReplaceWindow);
            return destinoReplaceView;
    }
}
