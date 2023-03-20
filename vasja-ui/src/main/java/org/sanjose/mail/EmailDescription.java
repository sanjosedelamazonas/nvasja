package org.sanjose.mail;

import org.simplejavamail.api.email.Email;

public class EmailDescription {

    private String to;

    private String usuario;

    private Email email;

    public EmailDescription(String to, String usuario, Email email) {
        this.to = to;
        this.usuario = usuario;
        this.email = email;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}