package org.sanjose.authentication;

import org.sanjose.util.GenUtil;
import org.sanjose.util.Rot10;
import org.sanjose.model.MsgUsuario;
import org.sanjose.repo.MsgUsuarioRep;

import java.util.HashMap;
import java.util.Map;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
public class MsgAccessControl implements AccessControl {

    private final MsgUsuarioRep msgUsuarioRep;

    private boolean devMode = false;

    private String devUser = null;

    private final Map<String, String> roles = new HashMap<>();

    public MsgAccessControl(MsgUsuarioRep repo, boolean devMode, String devUser) {
        this.msgUsuarioRep = repo;
        this.devMode = devMode;
        this.devUser = devUser;
        if (!GenUtil.strNullOrEmpty(devUser))
            signIn(devUser, "");
    }

    @Override
    public boolean signIn(String username, String password) {
        MsgUsuario usuario = null;
        if (devMode)
            usuario = msgUsuarioRep.findByTxtUsuario(username);
        else
            usuario = msgUsuarioRep.findByTxtUsuarioAndTxtPassword(username, Rot10.rot10(password));
        if (usuario==null) return false;
        roles.put(username, usuario.getCodRol());
        CurrentUser.set(username);
        return true;
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }

    @Override
    public boolean isUserInRole(String role) {

        return role.equals(roles.get(getPrincipalName()));
    }

    @Override
    public String getPrincipalName() {
        return CurrentUser.get();
    }

}
