package org.sanjose;

import com.vaadin.annotations.Viewport;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.sanjose.model.VsjPasswordresettoken;
import org.sanjose.repo.MsgUsuarioRep;

import org.sanjose.repo.VsjPasswordresettokenRep;
import org.sanjose.views.sys.ResetPasswordExpired;
import org.sanjose.views.sys.ResetPasswordUI;
import org.sanjose.views.sys.ResetPasswordView;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path="/resetpass/*")
@Viewport("user-scalable=yes,initial-scale=1.0")
public class ResetPassUI extends UI {

    private MsgUsuarioRep usuarioRep = null;
    private ResetPasswordUI resetPasswordUI = null;
    private VsjPasswordresettokenRep vsjPasswordresettokenRep;

    private static final Logger log = LoggerFactory.getLogger(ResetPassUI.class);

    @Autowired
    private ResetPassUI(MsgUsuarioRep msgUsuarioRep, VsjPasswordresettokenRep vsjPasswordresettokenRep) {
        this.usuarioRep = msgUsuarioRep;
        this.vsjPasswordresettokenRep = vsjPasswordresettokenRep;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("Reset Clave");
        String tokenString = vaadinRequest.getParameter("token");
        if (tokenString!=null) {
            VsjPasswordresettoken token = vsjPasswordresettokenRep.findByToken(tokenString);
            if (token!=null && !token.isExpired()) {
                ResetPasswordView resetPasswordView = new ResetPasswordView(token, usuarioRep, vsjPasswordresettokenRep);
                setContent(resetPasswordView);
            }
        } else {
            setContent(new ResetPasswordExpired());
        }
    }
}
