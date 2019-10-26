package org.sanjose.views.caja;

import com.vaadin.ui.Grid;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;

import java.util.Collection;

public interface CajaViewing extends NavigatorViewing {

    void clearSelection();

    Collection<Object> getSelectedRows();

    PersistanceService getService();

    Grid getGridCaja();
}
