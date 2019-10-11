package org.sanjose.views.banco;

import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import org.sanjose.views.caja.CajaSaldoView;
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

    DateField getFechaDesde();

    DateField getFechaHasta();

    BancoService getService();

    BancoOperView getBancoOperView();

    Grid getGridBanco();

    DateField getFecMesCobrado();

    Grid.FooterRow getGridFooter();

    CajaSaldoView getSaldosView();

    TextField getNumSaldoInicialSegBancos();

    TextField getNumSaldoInicialLibro();

    TextField getNumSaldoFinalSegBancos();

    TextField getNumSaldoFinalLibro();

}
