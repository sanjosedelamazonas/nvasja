package org.sanjose.views.rendicion;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.caja.CajaSaldoView;

import java.io.Serializable;
import java.util.*;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class RendicionManejoLogic implements ItemsRefreshing<ScpRendicioncabecera>, Serializable {

    protected RendicionManejoView view;
    private CajaSaldoView saldosView = new CajaSaldoView();

    public void init(RendicionManejoView rendicionManejoView) {
        view = rendicionManejoView;
        view.getBtnNueva().addClickListener(e -> nuevaRendicion());
        view.getBtnModificar().addClickListener(e -> editarRendicion(view.getSelectedRow()));
        //view.getBtnVerImprimir().addClickListener(e -> generateComprobante());
        //
        //view.btnImprimir.setVisible(ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT"));
        //view.btnImprimir.addClickListener(e -> printComprobante());
        view.getBtnEliminar().addClickListener(e -> eliminarComprobante(view.getSelectedRow()));
        saldosView.getBtnReporte().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(view.getFechaDesde().getValue(), view.getFechaHasta().getValue(), null));
        view.getBtnReporteImprimirCaja().addClickListener(clickEvent ->  ReportHelper.generateDiarioCaja(view.getFechaDesde().getValue(), view.getFechaHasta().getValue(), null));

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridCaja());
//        gridContextMenu.addGridBodyContextMenuListener(e -> {
//            gridContextMenu.removeItems();
//            final Object itemId = e.getItemId();
//            if (itemId == null) {
//                gridContextMenu.addItem("Nuevo comprobante", k -> nuevaRendicion());
//                gridContextMenu.addItem("Nuevo cargo/abono", k -> newTransferencia());
//            } else {
//
//                gridContextMenu.addItem(!GenUtil.strNullOrEmpty(((ScpCajabanco) itemId).getCodTranscorrelativo()) ? "Ver detalle" : "Editar",
//                        k -> modificarRendicion((ScpCajabanco) itemId));
//                gridContextMenu.addItem("Nuevo comprobante", k -> nuevaRendicion());
//                gridContextMenu.addItem("Ver Voucher", k -> generateComprobante());
//                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> printComprobante());
//
//                if (Role.isPrivileged()) {
//                    gridContextMenu.addItem("Enviar a contabilidad", k -> { enviarContabilidad((ScpCajabanco)itemId); });
//                }
//            }
//        });
    }


    protected void nuevaRendicion() {
        view.clearSelection();
        MainUI.get().getRendicionOperView().getViewLogic().nuevaRendicion();
        MainUI.get().getRendicionOperView().getViewLogic().setNavigatorView(view);
        ViewUtil.openRendicionInNewWindow(MainUI.get().getRendicionOperView());
        //MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

//    private void generateComprobante() {
//        ReportHelper.generateComprobante(view.getSelectedRow());
//    }
//
//    private void printComprobante() {
//        ViewUtil.printComprobante(view.getSelectedRow());
//    }

    protected void editarRendicion(ScpRendicioncabecera vcb) {
        if (vcb==null) return;
        //MainUI.get().getRendicionOperView().setNavigatorView(view);
        MainUI.get().getRendicionOperView().getViewLogic().editarRendicion(vcb);
        ViewUtil.openRendicionInNewWindow(MainUI.get().getRendicionOperView());
    }

    // Realize logic from View
    public void filter(Date fechaDesde, Date fechaHasta) {
        view.getContainer().removeAllItems();
        view.setFilterInitialDate(fechaDesde);
        view.getContainer().addAll(view.getService().getRendicioncabeceraRep().findByFecComprobanteBetween(fechaDesde, fechaHasta));
        view.getGridCaja().setSortOrder(Sort.by("fecComprobante", SortDirection.DESCENDING).then("codComprobante", SortDirection.DESCENDING).build());
        //calcFooterSums();
    }

    // Realize logic from View
    public void refreshData() {
        SortOrder[] sortOrders = view.getGridCaja().getSortOrder().toArray(new SortOrder[1]);
        filter(view.getFechaDesde().getValue(), view.getFechaHasta().getValue());
        view.getGridCaja().setSortOrder(Arrays.asList(sortOrders));
        //calcFooterSums();
        //setSaldosFinal();
    }

    @Override
    public void refreshItems(Collection<ScpRendicioncabecera> rendicioncabeceras) {
        rendicioncabeceras.forEach(scb -> {
//            ScpCajabanco newScb = view.getService().getRendicioncabeceraRep().findByCodCajabanco(scb.getCodCajabanco());
//            view.getGridCaja().getContainerDataSource().removeItem(scb);
//            view.getGridCaja().getContainerDataSource().addItem(newScb);
        });
        view.refreshData();
    }

    void eliminarComprobante(ScpRendicioncabecera rendicioncabecera) {
        if (rendicioncabecera == null)
            return;
        if (rendicioncabecera.isEnviado()) {
            MessageBox
                    .createInfo()
                    .withCaption("Ya enviado a contabilidad")
                    .withMessage("No se puede eliminar porque ya esta enviado a la contabilidad.")
                    .withOkButton()
                    .open();
            return;
        }
        MessageBox
                .createQuestion()
                .withCaption("Eliminar")
                .withMessage("?Esta seguro que quiere eliminar esta rendicion?")
                .withYesButton(() ->  doEliminarComprobante(rendicioncabecera))
                .withNoButton()
                .open();
    }

    void doEliminarComprobante(ScpRendicioncabecera rendicioncabecera) {
        try {
            view.getService().deleteRendicion(rendicioncabecera);
            view.refreshData();
            MessageBox
                    .createInfo()
                    .withCaption("Elminado correctamente")
                    .withMessage("La rendicion ha sido eliminado.")
                    .withOkButton()
                    .open();
        } catch (Exception ce) {
            //log.info("Got Exception al eliminar comprobante: " + ce.getMessage());
            MessageBox
                    .createError()
                    .withCaption("Error al eliminar la rendicion:")
                    .withMessage(ce.getLocalizedMessage())
                    .withOkButton()
                    .open();
        }
    }
}
