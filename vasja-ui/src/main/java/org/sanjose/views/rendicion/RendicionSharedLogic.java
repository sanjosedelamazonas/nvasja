package org.sanjose.views.rendicion;

import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.ScpRendicioncabecera;

public class RendicionSharedLogic {

    protected RendicionManejoView manView;

    void eliminarRendicion(ScpRendicioncabecera rendicioncabecera) {
        if (rendicioncabecera == null || rendicioncabecera.getCodRendicioncabecera()==null)
            return;
        if (rendicioncabecera.isEnviado()) {
            MessageBox
                    .createInfo()
                    .withCaption("Ya enviado a contabilidad")
                    .withMessage("No se puede eliminar porque ya esta enviado a la contabilidad.")
                    .withOkButton()
                    .open();
            return;
        }
        MessageBox
                .createQuestion()
                .withCaption("Eliminar")
                .withMessage("?Esta seguro que quiere eliminar esta rendicion?")
                .withYesButton(() ->  doEliminarRendicion(rendicioncabecera))
                .withNoButton()
                .open();
    }

    void doEliminarRendicion(ScpRendicioncabecera rendicioncabecera) {
        try {
            manView.getService().deleteRendicion(rendicioncabecera);
            manView.refreshData();
            MessageBox
                    .createInfo()
                    .withCaption("Elminado correctamente")
                    .withMessage("La rendicion ha sido eliminado.")
                    .withOkButton()
                    .open();
        } catch (Exception ce) {
            //log.info("Got Exception al eliminar comprobante: " + ce.getMessage());
            MessageBox
                    .createError()
                    .withCaption("Error al eliminar la rendicion:")
                    .withMessage(ce.getLocalizedMessage())
                    .withOkButton()
                    .open();
        }
    }
}
