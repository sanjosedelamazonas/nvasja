package org.sanjose.views.banco;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.Grid;
import org.sanjose.MainUI;
import org.sanjose.authentication.Role;
import org.sanjose.converter.MesCobradoToBooleanConverter;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpBancocabecera;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.VsjItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.SaldoDelDia;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 * <p>
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class BancoConciliacionLogic implements Serializable, SaldoDelDia {

    private static final Logger log = LoggerFactory.getLogger(BancoConciliacionLogic.class);
    private BancoConciliacionView view;
    private BancoGridLogic gridLogic;
    private boolean expandedAll = true;

    public void init(BancoConciliacionView bancoConciliacionView) {
        view = bancoConciliacionView;
        gridLogic = new BancoGridLogic(view);
        view.getExpandirContraerBtn().addClickListener(event -> {
            if (expandedAll) {
                view.container.collapseAll();
                view.getExpandirContraerBtn().setCaption("Expandir todo");
            } else {
                view.container.expandAll();
                view.getExpandirContraerBtn().setCaption("Contraer todo");
            }
            expandedAll = !expandedAll;
        });

        view.gridBanco.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                log.debug("in pre commit in banco concilliacion");
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                ScpBancocabecera vcb = getCabeceraFromItemId(view.gridBanco.getEditedItemId());
                if (vcb != null) {
                    vcb.setFlgCobrado((Boolean) view.gridBanco.getContainerDataSource().getItem(view.gridBanco.getEditedItemId()).getItemProperty("flgCobrado").getValue());
                    vcb.setCodMescobrado(new MesCobradoToBooleanConverter(vcb)
                            .convertToModel(vcb.getFlgCobrado(), String.class, ConfigurationUtil.LOCALE));
                    view.getService().updateCobradoInCabecera(vcb);
                }
            }
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.getGridBanco());
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
            } else {
                gridContextMenu.addItem("Editar", k -> gridLogic.editarCheque(getCabeceraFromItemId(itemId)));
                gridContextMenu.addItem("Nuevo cheque", k -> gridLogic.nuevoCheque());
                if (!(getCabeceraFromItemId(itemId)).isEnviado() || Role.isPrivileged()) {
                    gridContextMenu.addItem("Anular cheque", k -> gridLogic.anularCheque(getCabeceraFromItemId(itemId)));
                }
                if (Role.isPrivileged()) {
                    gridContextMenu.addItem("Enviar a contabilidad", k -> {
                        Collection<Object> cabecerasParaEnviar = new HashSet<Object>();
                        for (Object item : view.getSelectedRows()) {
                            cabecerasParaEnviar.add(getCabeceraFromItemId(item));
                        }
                        if (cabecerasParaEnviar.isEmpty() && itemId!=null)
                            cabecerasParaEnviar.add(getCabeceraFromItemId(itemId));
                        MainUI.get().getProcUtil().enviarContabilidadBanco(cabecerasParaEnviar, view.getService(), gridLogic);
                        view.gridBanco.deselectAll();
                    });
                }
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante(getCabeceraFromItemId(itemId)));
                if (ViewUtil.isPrinterReady())
                    gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante(getCabeceraFromItemId(itemId)));
            }
        });

    }

    private ScpBancocabecera getCabeceraFromItemId(Object itemId) {
        Object itemProperty = view.gridBanco.getContainerDataSource().getItem(itemId).getItemProperty("cabeceraObject");
        if (itemProperty != null) {
            try {
                Method mth = itemProperty.getClass().getMethod("getValue", new Class[]{});
                mth.setAccessible(true);
                return (ScpBancocabecera) mth.invoke(itemProperty);
            } catch (NoSuchMethodException nsm) {
                nsm.printStackTrace();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setSaldoDelDia() {

    }
}
