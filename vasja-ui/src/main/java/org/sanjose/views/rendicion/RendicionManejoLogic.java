package org.sanjose.views.rendicion;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.shared.data.sort.SortDirection;
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

    //protected RendicionManejoView manView;
    private CajaSaldoView saldosView = new CajaSaldoView();

    public void init(RendicionManejoViewing rendicionManejoView) {
        manView = rendicionManejoView;
        manView.getBtnNueva().addClickListener(e -> nuevaRendicion());
        manView.getBtnModificar().addClickListener(e -> editarRendicion(manView.getSelectedRow()));
        manView.getBtnEnviar().addClickListener(e -> enviarContabilidad(manView.getSelectedRow(), true));
        manView.getBtnNoEnviado().addClickListener(e -> enviarContabilidad(manView.getSelectedRow(), false));
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
        //MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    protected void editarRendicion(ScpRendicioncabecera vcb) {
        if (vcb==null) return;
        //MainUI.get().getRendicionOperView().setNavigatorView(manView);
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
        //calcFooterSums();
    }

    // Realize logic from View
    public void refreshData() {
        SortOrder[] sortOrders = manView.getGrid().getSortOrder().toArray(new SortOrder[1]);
        filter(manView.getFechaDesde().getValue(), manView.getFechaHasta().getValue());
        manView.getGrid().setSortOrder(Arrays.asList(sortOrders));
        //calcFooterSums();
        //setSaldosFinal();
    }

    public void enviarContabilidad(ScpRendicioncabecera rendicioncabecera, boolean isEnviar) {
        Collection<Object> cabecerasParaEnviar = manView.getSelectedRows();
        Collection<ScpRendicioncabecera> cabecerasParaRefresh = new ArrayList<>();
        if (cabecerasParaEnviar.isEmpty() && rendicioncabecera!=null) {
            cabecerasParaEnviar.add(rendicioncabecera);
            cabecerasParaRefresh.add(rendicioncabecera);
        }
        cabecerasParaEnviar.forEach(e -> cabecerasParaRefresh.add((ScpRendicioncabecera) e));
        if (isEnviar) {
            Set<ScpRendicioncabecera> cabecerasEnviados = new HashSet<>();
            List<String> cabecerasIdsEnviados = new ArrayList<>();
            // Check if already sent and ask if only marcar...
            for (Object objVcb : cabecerasParaEnviar) {
                ScpRendicioncabecera rendcab = (ScpRendicioncabecera) objVcb;
                if (!rendcab.isEnviado() && manView.getService().checkIfAlreadyEnviado(rendcab)) {
                    cabecerasEnviados.add(rendcab);
                    cabecerasIdsEnviados.add(rendcab.getCodRendicioncabecera().toString());
                }
            }
            for (ScpRendicioncabecera rendcab : cabecerasEnviados) {
                cabecerasParaEnviar.remove(rendcab);
            }
            if (cabecerasEnviados.isEmpty()) {
                manView.getGrid().deselectAll();
                manView.clearSelection();
                MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, manView.getService(), this);
            } else {
                MessageBox
                        .createQuestion()
                        .withCaption("!Atencion!")
                        .withMessage("?Estas operaciones ya fueron enviadas ("+ Arrays.toString(cabecerasIdsEnviados.toArray()) +"), quiere solo marcar los como enviadas?")
                        .withYesButton(() -> doMarcarEnviados(cabecerasParaEnviar, cabecerasEnviados))
                        .withNoButton()
                        .open();
            }
            //MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, manView.getService(), this);
        } else {
            for (Object objVcb : cabecerasParaEnviar) {
                ScpRendicioncabecera scpRendicioncabecera = (ScpRendicioncabecera) objVcb;
                if (!scpRendicioncabecera.isEnviado()) {
                    Notification.show("!Atencion!", "!Omitiendo operacion " + scpRendicioncabecera.getCodRendicioncabecera() + " - no esta enviada!", Notification.Type.TRAY_NOTIFICATION);
                    continue;
                }
                manView.getGrid().deselect(scpRendicioncabecera);
                scpRendicioncabecera.setFlgEnviado('0');
                scpRendicioncabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
                scpRendicioncabecera.setCodUactualiza(CurrentUser.get());
                manView.getService().getRendicioncabeceraRep().save(scpRendicioncabecera);
            }
            manView.getGrid().deselectAll();
            manView.clearSelection();
            refreshItems(cabecerasParaRefresh);
        }
    }

    public void doMarcarEnviados(Collection<Object> cabecerasParaEnviar , Set<ScpRendicioncabecera> cabecerasEnviados) {
        for (ScpRendicioncabecera cabecera : cabecerasEnviados) {
            manView.getGrid().deselect(cabecera);
            cabecera.setFlgEnviado('1');
            cabecera.setFecFactualiza(new Timestamp(System.currentTimeMillis()));
            cabecera.setCodUactualiza(CurrentUser.get());
            manView.getService().getRendicioncabeceraRep().save(cabecera);
        }
        manView.getGrid().deselectAll();
        //this.refreshItems(cabecerasEnviados);
        if (!cabecerasParaEnviar.isEmpty())
            MainUI.get().getProcUtil().enviarContabilidadRendicion(cabecerasParaEnviar, manView.getService(), this);
        refreshItems(cabecerasEnviados);
    }

    @Override
    public void refreshItems(Collection<ScpRendicioncabecera> rendicioncabeceras) {
        rendicioncabeceras.forEach(scb -> {
            ScpRendicioncabecera newScb = manView.getService().getRendicioncabeceraRep().findByCodRendicioncabecera(scb.getCodRendicioncabecera());
            manView.getGrid().getContainerDataSource().removeItem(scb);
            manView.getGrid().getContainerDataSource().addItem(newScb);
        });
        SortOrder[] sortOrders = manView.getGrid().getSortOrder().toArray(new SortOrder[1]);
        manView.getGrid().setSortOrder(Arrays.asList(sortOrders));
        //manView.refreshData();
    }
}
