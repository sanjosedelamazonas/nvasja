package org.sanjose.views.caja;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.util.*;
import org.sanjose.views.sys.GridViewing;
import org.sanjose.views.sys.NavigatorViewing;
import org.sanjose.views.sys.Viewing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CajaSaldoView extends CajaSaldoUI {

    public static final String VIEW_NAME = "Saldos de caja";
    private Window subWindow;

    public CajaSaldoView() {
        init();
    }

    public void init() {
        setSizeFull();
        addStyleName("crud-view");
        getBtnAnular().addClickListener(clickEvent -> closeWindow());
    }

    private void closeWindow() {
        if (getSubWindow()!=null)
            getSubWindow().close();
    }

    public Label getValSolIng() {
        return valSolIng;
    }

    public Label getValSolEgr() {
        return valSolEgr;
    }

    public Label getValSolSaldo() {
        return valSolSaldo;
    }

    public Label getValDolIng() {
        return valDolIng;
    }

    public Label getValDolEgr() {
        return valDolEgr;
    }

    public Label getValDolSaldo() {
        return valDolSaldo;
    }

    public Label getValEurIng() {
        return valEurIng;
    }

    public Label getValEurEgr() {
        return valEurEgr;
    }

    public Label getValEurSaldo() {
        return valEurSaldo;
    }

    public Button getBtnReporte() {
        return btnReporte;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public Grid getGridSaldoInicial() {
        return gridSaldoInicial;
    }

    public Grid getGridSaldoFinal() {
        return gridSaldoFinal;
    }

    public GridLayout getGridSaldoDelDia() {
        return gridSaldoDelDia;
    }

    public Window getSubWindow() {
        return subWindow;
    }

    public void setSubWindow(Window subWindow) {
        this.subWindow = subWindow;
    }
}
