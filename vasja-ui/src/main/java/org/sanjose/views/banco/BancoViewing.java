package org.sanjose.views.banco;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.views.caja.CajaSaldoView;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.PersistanceService;

import java.util.Collection;

/**
 * VASJA class
 * User: prubach
 * Date: 18.10.16
 */
public interface BancoViewing extends NavigatorViewing, GridViewing {

    void clearSelection();

    Collection<Object> getSelectedRows();

    DateField getFechaDesde();

    DateField getFechaHasta();

    PersistanceService getService();

    BancoOperView getBancoOperView();

    Grid getGridBanco();

    DateField getFecMesCobrado();

    Grid.FooterRow getGridFooter();

    CajaSaldoView getSaldosView();

    TextField getNumSaldoInicialSegBancos();

    TextField getNumSaldoInicialLibro();

    TextField getNumSaldoFinalSegBancos();

    TextField getNumSaldoFinalLibro();

    ComboBox getSelRepMoneda();

    ComboBox getSelFiltroCuenta();

    BeanItemContainer<ScpBancocabecera> getContainer();
}
