package org.sanjose.views.banco;

import org.sanjose.views.sys.INavigatorView;

import java.util.Collection;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public interface BancoView extends INavigatorView {

    void clearSelection();

    Collection<Object> getSelectedRows();
}
