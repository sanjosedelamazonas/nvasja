package org.sanjose.views.banco;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.SelectionEvent;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.converter.ZeroOneToBooleanConverter;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjBancodetalle;
import org.sanjose.model.VsjBancodetallePK;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.VsjView;
import org.vaadin.addons.CssCheckBox;
import tm.kod.widgets.numberfield.NumberField;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.sanjose.util.GenUtil.PEN;
import static org.sanjose.util.GenUtil.USD;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class BancoOperView extends BancoOperUI implements VsjView {

    public static final String VIEW_NAME = "Cheques";
    static final String[] VISIBLE_COLUMN_IDS_PEN = new String[]{"Numero", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebesol", "numHabersol"
    };
    static final String[] VISIBLE_COLUMN_NAMES_PEN = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing S/.", "Egr S/."
    };
    static final String[] VISIBLE_COLUMN_IDS_USD = new String[]{"Numero", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebedolar", "numHaberdolar"
    };
    static final String[] VISIBLE_COLUMN_NAMES_USD = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing $", "Egr $"
    };
    static final String[] VISIBLE_COLUMN_IDS_EUR = new String[]{"Numero", "codProyecto", "codTercero",
            "codContracta", "txtGlosaitem", "numDebemo", "numHabermo"
    };
    static final String[] VISIBLE_COLUMN_NAMES_EUR = new String[]{"Numero", "Proyecto", "Tercero",
            "Cuenta", "Glosa", "Ing €", "Egr €"
    };
    static final String[] NONEDITABLE_COLUMN_IDS = new String[]{};

    private static final Logger log = LoggerFactory.getLogger(BancoOperView.class);

    private final Field[] allFields = new Field[] { fechaDoc, selProyecto, selTercero,
            numIngreso, numEgreso, selResponsable, selLugarGasto, selCodAuxiliar, selTipoDoc, selCtaContable,
            selRubroInst, selRubroProy, selFuente, selTipoMov, glosaDetalle, serieDoc, numDoc,
            };
    private final Field[] cabezeraFields = new Field[]{chkCobrado, dataFechaComprobante, selCuenta, selCodAuxCabeza,
            glosaCabeza, cheque };
    private BancoLogic viewLogic = null;
    private BeanItemContainer<VsjBancodetalle> container;
    private GeneratedPropertyContainer gpContainer;
    private BancoService bancoService;

    public BancoOperView() {
    }

    public BancoOperView(BancoService bancoService) {
        this.bancoService = bancoService;
        setSizeFull();
    }

    public void init(BancoService bancoService) {
        this.bancoService = bancoService;
        init();
    }

    @Override
    public void init() {
        viewLogic = new BancoLogic();
        viewLogic.init(this);
        addStyleName("crud-view");
        ViewUtil.setDefaultsForNumberField(numIngreso);
        ViewUtil.setDefaultsForNumberField(numEgreso);

        viewLogic.setupEditComprobanteView();

        chkCobrado.setAnimated(true);
        chkCobrado.setSimpleMode(false);
        chkEnviado.setSimpleMode(false);
        chkEnviado.setConverter(new ZeroOneToBooleanConverter());
        // Grid
        //noinspection unchecked
        container = new BeanItemContainer(VsjBancodetalle.class, new ArrayList());
        gpContainer = new GeneratedPropertyContainer(container);
        gpContainer.addGeneratedProperty("Numero",
                new PropertyValueGenerator<String>() {
                    @Override
                    public String getValue(Item item, Object itemId,
                                            Object propertyId) {
                        //
                        return ((VsjBancocabecera)item.getItemProperty("vsjBancocabecera").getValue()).getTxtCorrelativo() +
                                "-" + ((VsjBancodetallePK)item.getItemProperty("id").getValue()).getNumItem();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        gridBanco.setContainerDataSource(gpContainer);
        gridBanco.setEditorEnabled(false);
        gridBanco.sort("fecFregistro", SortDirection.DESCENDING);

        gridBanco.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (selectionEvent.getSelected().isEmpty()) return;
                viewLogic.viewComprobante();
            }
        });

        ViewUtil.setColumnNames(gridBanco, VISIBLE_COLUMN_NAMES_PEN, VISIBLE_COLUMN_IDS_PEN, NONEDITABLE_COLUMN_IDS);

        ViewUtil.alignMontosInGrid(gridBanco);

        ViewUtil.colorizeRows(gridBanco, VsjBancodetalle.class);

        gridBanco.setSelectionMode(Grid.SelectionMode.SINGLE);
        setTotal(null);

    }

    public VsjBancodetalle getSelectedRow() {
        return (VsjBancodetalle) gridBanco.getSelectedRow();
    }

    public void setEnableDetalleFields(boolean enabled) {
        log.info("enabling detalle fields");
        for (Field f : allFields) f.setEnabled(enabled);
        btnResponsable.setEnabled(enabled);
        btnDestino.setEnabled(enabled);
    }


    public void setEnableCabezeraFields(boolean enabled) {
        for (Field f : cabezeraFields) f.setEnabled(enabled);
        btnAuxiliar.setEnabled(enabled);
    }

    public BeanItemContainer<VsjBancodetalle> getContainer() {
        return container;
    }

    private BigDecimal calcTotal(Character locMoneda) {
        BigDecimal total = new BigDecimal(0.00);
        for (VsjBancodetalle cajabanco : container.getItemIds()) {
            log.info("calcTotal: " + cajabanco);
            if (locMoneda.equals(PEN)) {
                total = total.add(cajabanco.getNumDebesol()).subtract(cajabanco.getNumHabersol());
            } else if (locMoneda.equals(USD))
                total = total.add(cajabanco.getNumDebedolar()).subtract(cajabanco.getNumHaberdolar());
            else
                total = total.add(cajabanco.getNumDebemo()).subtract(cajabanco.getNumHabermo());
        }
        return total;
    }

    public void setTotal(Character locMoneda) {
        if (locMoneda == null) {
            //viewLogic.item.getCodTipomoneda()
            log.info("in setSaldo - moneda = NULL");
            saldoTotal.setValue("Total:" +
                    "<span class=\"order-sum\"> S./ 0.00</span>");
            return;
        }
        if (locMoneda.equals(PEN)) {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
            order_summary_layout.removeStyleName("order-summary-layout-eur");
            ViewUtil.setColumnNames(gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_PEN,
                    BancoOperView.VISIBLE_COLUMN_IDS_PEN, BancoOperView.NONEDITABLE_COLUMN_IDS);
        } else if (locMoneda.equals(USD)) {
            order_summary_layout.removeStyleName("order-summary-layout-eur");
            order_summary_layout.addStyleName("order-summary-layout-usd");
            ViewUtil.setColumnNames(gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_USD,
                    BancoOperView.VISIBLE_COLUMN_IDS_USD, BancoOperView.NONEDITABLE_COLUMN_IDS);
        } else {
            order_summary_layout.removeStyleName("order-summary-layout-usd");
            order_summary_layout.addStyleName("order-summary-layout-eur");
            ViewUtil.setColumnNames(gridBanco, BancoOperView.VISIBLE_COLUMN_NAMES_EUR,
                    BancoOperView.VISIBLE_COLUMN_IDS_EUR, BancoOperView.NONEDITABLE_COLUMN_IDS);
        }
        ViewUtil.alignMontosInGrid(gridBanco);
        getContainer().sort(new Object[]{"txtCorrelativo"}, new boolean[]{true});

        saldoTotal.setContentMode(ContentMode.HTML);
        saldoTotal.setValue("Total:" +
                "<span class=\"order-sum\"> " + GenUtil.getSymMoneda(GenUtil.getLitMoneda(locMoneda)) + calcTotal(locMoneda).toString() + "</span>");
    }

    public void refreshData() {
        MainUI.get().getBancoManejoView().refreshData();
    }

    public BancoService getService() {
        return bancoService;
    }

    public ComboBox getSelProyecto() {
        return selProyecto;
    }

    public TextField getNumVoucher() {
        return numVoucher;
    }

    public ComboBox getSelFuente() {
        return selFuente;
    }

    public ComboBox getSelTercero() {
        return selTercero;
    }

    public TextField getSaldoProyPEN() {
        return saldoProyPEN;
    }

    public TextField getSaldoProyUSD() {
        return saldoProyUSD;
    }

    public TextField getSaldoProyEUR() {
        return saldoProyEUR;
    }

    public NumberField getNumIngreso() {
        return numIngreso;
    }

    public NumberField getNumEgreso() {
        return numEgreso;
    }

    public TextField getGlosaCabeza() {
        return glosaCabeza;
    }

    public TextField getGlosaDetalle() {
        return glosaDetalle;
    }

    public ComboBox getSelResponsable() {
        return selResponsable;
    }

    public Button getBtnResponsable() {
        return btnResponsable;
    }

    public ComboBox getSelLugarGasto() {
        return selLugarGasto;
    }

    public ComboBox getSelTipoMov() {
        return selTipoMov;
    }

    public ComboBox getSelCtaContable() {
        return selCtaContable;
    }

    public ComboBox getSelRubroInst() {
        return selRubroInst;
    }

    public ComboBox getSelRubroProy() {
        return selRubroProy;
    }

    public ComboBox getSelCodAuxiliar() {
        return selCodAuxiliar;
    }

    public Button getBtnDestino() {
        return btnDestino;
    }

    public PopupDateField getFechaDoc() {
        return fechaDoc;
    }

    public ComboBox getSelTipoDoc() {
        return selTipoDoc;
    }

    public TextField getSerieDoc() {
        return serieDoc;
    }

    public TextField getNumDoc() {
        return numDoc;
    }

    public Label getSaldoTotal() {
        return saldoTotal;
    }

    public Button getCerrarBtn() {
        return cerrarBtn;
    }

    public Button getGuardarBtn() {
        return guardarBtn;
    }

    public Button getModificarBtn() {
        return modificarBtn;
    }

    public Button getEliminarBtn() {
        return eliminarBtn;
    }

    public Button getNewItemBtn() {
        return newItemBtn;
    }

    public Button getImprimirTotalBtn() {
        return imprimirTotalBtn;
    }

    public PopupDateField getDataFechaComprobante() {
        return dataFechaComprobante;
    }

    public ComboBox getSelCuenta() {
        return selCuenta;
    }

    public TextField getSaldoCuenta() {
        return saldoCuenta;
    }

    public ComboBox getSelCodAuxCabeza() {
        return selCodAuxCabeza;
    }

    public TextField getCheque() {
        return cheque;
    }

    public Button getAnularBtn() {
        return anularBtn;
    }

    public Button getNewChequeBtn() {
        return newChequeBtn;
    }

    public Button getBtnAuxiliar() {
        return btnAuxiliar;
    }

    public TextField getTxtOrigen() {
        return txtOrigen;
    }

    public TextField getTxtNumCombrobante() {
        return txtNumCombrobante;
    }

    public CssCheckBox getChkCobrado() {
        return chkCobrado;
    }

    public CssCheckBox getChkEnviado() {
        return chkEnviado;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public BancoLogic getViewLogic() {
        return viewLogic;
    }
}
