package org.sanjose.mail;


import java.util.concurrent.CompletableFuture;

public class EmailStatus {

    private String to;

    private String usuario;

    private CompletableFuture<Void> status;

    public EmailStatus(String to, String usuario, CompletableFuture<Void> status) {
        this.to = to;
        this.usuario = usuario;
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public CompletableFuture<Void> getStatus() {
        return status;
    }

    public void setStatus(CompletableFuture<Void> status) {
        this.status = status;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
