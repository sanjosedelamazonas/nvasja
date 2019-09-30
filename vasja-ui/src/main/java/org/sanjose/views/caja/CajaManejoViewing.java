package org.sanjose.views.caja;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import org.sanjose.model.ScpCajabanco;

import java.util.Date;

public interface CajaManejoViewing extends CajaViewing{

    void setFilterInitialDate(Date filterInitialDate);

    DateField getFechaDesde();

    DateField getFechaHasta();

    ComboBox getSelMoneda();

    TextField getSaldoCaja();

    Button getBtnDetallesSaldos();

    Button getBtnReporteImprimirCaja();

    ComboBox getSelFiltroCaja();

    Button getNuevaTransferencia();

    Button getNuevoComprobante();

    Grid getGridCaja();

    Button getBtnVerImprimir();

    Button getBtnModificar();

    Button getBtnEliminar();

    Character getMoneda();

    ScpCajabanco getSelectedRow();

    BeanItemContainer<ScpCajabanco> getContainer();

    Grid.FooterRow getGridCajaFooter();

    ComprobanteView getComprobView();
}
