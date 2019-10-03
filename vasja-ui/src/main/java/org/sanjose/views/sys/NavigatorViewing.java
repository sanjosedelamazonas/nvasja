package org.sanjose.views.sys;

import org.sanjose.model.VsjItem;

/**
 * Created by ab on 22/09/2016.
 */
public interface NavigatorViewing {

    String getNavigatorViewName();

    void refreshData();

    void selectMoneda(Character moneda);

    void selectItem(VsjItem item);

}
