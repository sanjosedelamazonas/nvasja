package org.sanjose.views.rendicion;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.Notification;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.authentication.Role;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.caja.CajaSaldoView;

import java.io.Serializable;
import java.util.*;
import java.sql.Timestamp;

/**
 * This class provides an interface for the logical operations between the CRUD
 * manView, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the manView makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class RendicionManejoLogic extends RendicionSharedLogic implements ItemsRefreshing<ScpRendicioncabecera>, Serializable {

    private CajaSaldoView saldosView = new CajaSaldoView();

    public void init(RendicionManejoViewing rendicionManejoView) {
        manView = rendicionManejoView;
        manView.getBtnNueva().addClickListener(e -> nuevaRendicion());
        manView.getBtnModificar().addClickListener(e -> editarRendicion(manView.getSelectedRow()));
        manView.getBtnEnviar().addClickListener(e -> {
            MainUI.get().getProcUtil().checkDescuadradoAndEnviaContab(manView.getSelectedRow(), true, manView.getService(), manView, null);
        });
        manView.getBtnNoEnviado().addClickListener(e -> {
            MainUI.get().getProcUtil().checkDescuadradoAndEnviaContab(manView.getSelectedRow(), false, manView.getService(), manView, null);
        });
        manView.getBtnVerImprimir().addClickListener(e -> ReportHelper.generateComprobante(manView.getSelectedRow()));
        manView.getBtnEliminar().addClickListener(e -> eliminarRendicion(manView.getSelectedRow()));
        saldosView.getBtnReporte().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(manView.getFechaDesde().getValue(), manView.getFechaHasta().getValue(), null));
    }


    protected void nuevaRendicion() {
        manView.clearSelection();
        if (manView instanceof RendicionSimpleManejoView) {
            MainUI.get().getRendicionSimpleOperView().getViewLogic().nuevaRendicion();
            MainUI.get().getRendicionSimpleOperView().getViewLogic().setNavigatorView(manView);
            ViewUtil.openViewInNewWindow(MainUI.get().getRendicionSimpleOperView());
        } else {
            MainUI.get().getRendicionOperView().getViewLogic().nuevaRendicion();
            MainUI.get().getRendicionOperView().getViewLogic().setNavigatorView(manView);
            ViewUtil.openViewInNewWindow(MainUI.get().getRendicionOperView());
        }
    }

    protected void editarRendicion(ScpRendicioncabecera vcb) {
        MainUI.get().getProcUtil().fixZeroCodRendicionCabeceraForCod(vcb.getCodRendicioncabecera());
        if (vcb==null) return;
        if (manView instanceof RendicionSimpleManejoView) {
            MainUI.get().getRendicionSimpleOperView().getViewLogic().editarRendicion(vcb);
            ViewUtil.openViewInNewWindow(MainUI.get().getRendicionSimpleOperView());
        } else {
            MainUI.get().getRendicionOperView().getViewLogic().editarRendicion(vcb);
            ViewUtil.openViewInNewWindow(MainUI.get().getRendicionOperView());
        }
    }

    // Realize logic from View
    public void filter(Date fechaDesde, Date fechaHasta) {
        manView.getContainer().removeAllItems();
        manView.setFilterInitialDate(fechaDesde);
        if (Role.isCaja() || Role.isBanco() || Role.isPrivileged()) {
            manView.getContainer().addAll(manView.getService().getRendicioncabeceraRep().findByFecComprobanteBetween(fechaDesde, fechaHasta));
            manView.getContainer().removeContainerFilters("codUregistro");
        } else {
            manView.getContainer().addAll(manView.getService().getRendicioncabeceraRep().findByCodUregistroAndFecComprobanteBetween(CurrentUser.get(), fechaDesde, fechaHasta));
            manView.getContainer().removeContainerFilters("codUregistro");
            manView.getContainer().addContainerFilter(new Compare.Equal("codUregistro", CurrentUser.get()));
        }
        manView.getContainer().addAll(manView.getService().getRendicioncabeceraRep().findByFecComprobanteBetween(fechaDesde, fechaHasta));

        manView.getGrid().setSortOrder(Sort.by("fecComprobante", SortDirection.DESCENDING).then("codComprobante", SortDirection.DESCENDING).build());
    }

    // Realize logic from View
    public void refreshData() {
        SortOrder[] sortOrders = manView.getGrid().getSortOrder().toArray(new SortOrder[1]);
        filter(manView.getFechaDesde().getValue(), manView.getFechaHasta().getValue());
        manView.getGrid().setSortOrder(Arrays.asList(sortOrders));
    }


    @Override
    public void refreshItems(Collection<ScpRendicioncabecera> rendicioncabeceras) {
        SortOrder[] sortOrders = manView.getGrid().getSortOrder().toArray(new SortOrder[1]);
        manView.getGrid().deselectAll();
        manView.clearSelection();
        rendicioncabeceras.forEach(scb -> {
            manView.getGrid().getContainerDataSource().removeItem(scb);
            //ScpRendicioncabecera newScb = manView.getService().getRendicioncabeceraRep().findByCodRendicioncabecera(scb.getCodRendicioncabecera());
            manView.getGrid().getContainerDataSource().addItem(scb);
        });
        manView.getGrid().setSortOrder(Arrays.asList(sortOrders));
        manView.refreshData();
    }
}
