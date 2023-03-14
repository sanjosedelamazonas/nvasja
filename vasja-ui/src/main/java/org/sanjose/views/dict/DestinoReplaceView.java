package org.sanjose.views.dict;

import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.views.sys.PersistanceService;

public class DestinoReplaceView extends DestinoReplaceUI {

    private PersistanceService service;

    private ScpDestino scpDestinoToReplace;

    private Window destinoReplaceWindow;

    private DestinoListView destinoListView;

    public DestinoReplaceView(DestinoListView destinoListView) {
        this.destinoListView = destinoListView;
        this.service = destinoListView.getService();
    }


    public void init(ScpDestino codDestinoToReplace) {
        this.scpDestinoToReplace = codDestinoToReplace;
        this.getTxtDestinoElimnado().setEnabled(false);
        this.getTxtDestinoElimnado().setValue(scpDestinoToReplace.getCodDestino() + " " + scpDestinoToReplace.getTxtNombredestino());
        DataFilterUtil.bindComboBox(getSelNuevoDestino(), "codDestino", service.getDestinoRepo().findByCodDestinoNotLike(codDestinoToReplace.getCodDestino()),
                "txtNombre");

        getBtnAnular().addClickListener(clickEvent -> destinoReplaceWindow.close());
        getBtnRemplacar().addClickListener(clickEvent -> {
            String codDestOld = scpDestinoToReplace.getCodDestino();
            String codDestNew = getSelNuevoDestino().getValue().toString();
            int cambios = MainUI.get().getProcUtil().replaceDestino(scpDestinoToReplace.getCodDestino(), getSelNuevoDestino().getValue().toString(), service);
            String msg = MainUI.get().getProcUtil().checkIfcanBeDeleted(scpDestinoToReplace.getCodDestino(), service);
            if (!msg.isEmpty()) {
                MessageBox
                        .createError()
                        .withCaption("!Atencion!")
                        .withMessage("Problema al remplacar y eliminar destino")
                        .open();
            } else {
                destinoListView.removeRow(scpDestinoToReplace);
                destinoListView.clearSelection();
                this.destinoReplaceWindow.close();
                //destinoListView.refreshData();
                MessageBox
                        .createInfo()
                        .withCaption("!Info!")
                        .withMessage(String.format("Destino: " + codDestOld + " ha sido remplacado por: " + codDestNew + " con " + cambios + " cambios"))
                        .open();
            }
        });
    }

    public TextField getTxtDestinoElimnado() {
        return txtDestinoElimnado;
    }

    public ComboBox getSelNuevoDestino() {
        return selNuevoDestino;
    }

    public Button getBtnRemplacar() {
        return btnRemplacar;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public void setDestinoReplaceWindow(Window destinoReplaceWindow) {
        this.destinoReplaceWindow = destinoReplaceWindow;
    }
}
