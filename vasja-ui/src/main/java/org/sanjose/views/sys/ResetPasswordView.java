package org.sanjose.views.sys;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import org.sanjose.model.VsjPasswordresettoken;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.VsjPasswordresettokenRep;
import org.sanjose.util.Rot10;

public class ResetPasswordView extends ResetPasswordUI {

    public ResetPasswordView(VsjPasswordresettoken token, MsgUsuarioRep msgUsuarioRep,
                             VsjPasswordresettokenRep vsjPasswordresettokenRep) {
        getBtnAceptar().setEnabled(true);
        getBtnAceptar().addClickListener(clickEvent -> {
            if (getTxtPass().getValue().length()>7 && getTxtPass().getValue().equals(getTxtPass2().getValue())) {
                token.getUser().setTxtPassword(Rot10.rot10(getTxtPass().getValue()));
                msgUsuarioRep.save(token.getUser());
                vsjPasswordresettokenRep.delete(token);
                showNotification(new Notification("La clave ha sido cambiado correctamente", Notification.Type.HUMANIZED_MESSAGE));
                String scheme = Page.getCurrent().getLocation().getScheme();
                String schemeSpecificPart = Page.getCurrent().getLocation().getAuthority();
                getUI().getPage().setLocation(scheme + "://" + schemeSpecificPart);
            } else {
                showNotification(new Notification("Las dos claves no son iguales o la clave no esta suficiente complicada - tiene que tener minimo 8 simbolos"));
            }
        });
    }

    private void showNotification(Notification notification) {
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

    public PasswordField getTxtPass() {
        return txtPass;
    }

    public PasswordField getTxtPass2() {
        return txtPass2;
    }

    public Button getBtnAceptar() {
        return btnAceptar;
    }
}
