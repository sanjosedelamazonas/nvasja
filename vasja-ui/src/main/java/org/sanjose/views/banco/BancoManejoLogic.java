package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.authentication.Role;
import org.sanjose.bean.Caja;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.DoubleDecimalFormatter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpPlancontable;
import org.sanjose.model.VsjCajaBancoItem;
import org.sanjose.render.EmptyZeroNumberRendrer;
import org.sanjose.util.*;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class BancoManejoLogic extends BancoGridLogic implements Serializable, SaldoDelDia {

    private BancoManejoView mView;

    public BancoManejoLogic(BancoManejoView view) {
        super(view);
        mView = view;
        init();
    }

    public void init() {
        mView.btnNuevoCheque.addClickListener(e -> nuevoCheque(bancoCuenta));
        mView.btnEditar.addClickListener(e -> {
            for (Object obj : mView.getSelectedRows()) {
                editarCheque((ScpBancocabecera) obj);
                break;
            }
        });
        mView.btnVerVoucher.addClickListener(e -> generateComprobante());
        mView.btnImprimir.addClickListener(e -> printComprobante());
        mView.btnReporte.addClickListener(e -> {
            ReportHelper.generateDiarioBanco(mView.getSelRepMoneda().getValue().toString().charAt(0),
                    mView.fechaDesde.getValue(), mView.fechaHasta.getValue(), null);
        });

        mView.getBtnDetallesSaldos().addClickListener(e -> {
            setSaldos(mView.getSaldosView().getGridSaldoInicial(), true);
            setSaldos(mView.getSaldosView().getGridSaldoFinal(), false);
            setSaldoDelDia();
            ViewUtil.openCajaSaldosInNewWindow(mView.getSaldosView(), mView.getFechaDesde().getValue(), mView.getFechaHasta().getValue());
        });
        mView.getSaldosView().getBtnReporte().addClickListener(e -> {
            ReportHelper.generateDiarioBanco(mView.getSelRepMoneda().getValue().toString().charAt(0),
                    mView.fechaDesde.getValue(), mView.fechaHasta.getValue(), null);
        });
        mView.getBtnEliminar().addClickListener(clickEvent -> {
            for (Object row : mView.getSelectedRows()) {
                anularCheque((ScpBancocabecera)row);
                return;
            }
        });
        mView.getBtnMarcarCobrado().addClickListener(clickEvent -> { setMesCobrado(true); });
        mView.getBtnMarcarNoCobrado().addClickListener(clickEvent -> { setMesCobrado(false); });

        GridContextMenu gridContextMenu = new GridContextMenu(mView.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> nuevoCheque(bancoCuenta));
            } else {
                gridContextMenu.addItem("Ver detalle", k -> editarCheque((ScpBancocabecera) itemId));
                gridContextMenu.addItem("Nuevo cheque", k -> nuevoCheque(bancoCuenta));
                if (!((ScpBancocabecera) itemId).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> anularCheque((ScpBancocabecera) itemId));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        enviarContabilidad((ScpBancocabecera)itemId);
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjCajaBancoItem) itemId));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajaBancoItem) itemId));
            }
        });
    }
}
