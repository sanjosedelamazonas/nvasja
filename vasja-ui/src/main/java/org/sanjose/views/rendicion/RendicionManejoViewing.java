package org.sanjose.views.rendicion;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;
import org.sanjose.views.sys.Viewing;

import java.util.Collection;
import java.util.Date;

public interface RendicionManejoViewing extends NavigatorViewing, Viewing, GridViewing {

    PersistanceService getService();

    void refreshData();

    void setFilterInitialDate(Date filterInitialDate);

    DateField getFechaDesde();

    DateField getFechaHasta();

    ComboBox getSelMoneda();

    Grid getGrid();

    Button getBtnVerImprimir();

    Button getBtnModificar();

    Button getBtnEliminar();

    Character getMoneda();

    BeanItemContainer<ScpRendicioncabecera> getContainer();

    Grid.FooterRow getGridFooter();

    Button getBtnNueva();

    Button getBtnEnviar();

    Button getBtnNoEnviado();

    ComboBox getFiltroEnviadasCombo();

    void clearSelection();

    Collection<Object> getSelectedRows();

    ScpRendicioncabecera getSelectedRow();

    RendicionManejoLogic getViewLogic();

}
