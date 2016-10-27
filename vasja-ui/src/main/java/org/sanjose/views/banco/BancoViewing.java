package org.sanjose.views.banco;

import org.sanjose.views.sys.NavigatorViewing;

import java.util.Collection;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public interface BancoViewing extends NavigatorViewing {

    void clearSelection();

    Collection<Object> getSelectedRows();

    BancoService getService();

    BancoOperView getBancoOperView();
}
