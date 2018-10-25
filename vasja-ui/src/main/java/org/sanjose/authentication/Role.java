package org.sanjose.authentication;

import org.sanjose.MainUI;

/**
 * VASJA class
 * User: prubach
 * Date: 17.09.16
 */
public class Role {
    public final static String ADMIN = "ROL004";

    public final static String CONTADOR = "ROL005";

    public final static String CAJA = "ROL002";

    public final static String BANCO = "ROL006";


    public static boolean isAdmin() {
        return MainUI.get().getAccessControl().isUserInRole(Role.ADMIN);
    }

    public static boolean isPrivileged() {
        return MainUI.get().getAccessControl().isUserInRole(Role.CONTADOR) || isAdmin();
    }

    public static boolean isBanco() {
        return isPrivileged() || MainUI.get().getAccessControl().isUserInRole(Role.BANCO);
    }

    public static boolean isCaja() {
        return isPrivileged() || MainUI.get().getAccessControl().isUserInRole(Role.CAJA);
    }

    public static boolean isOnlyCaja() {
        return MainUI.get().getAccessControl().isUserInRole(Role.CAJA);
    }

}
