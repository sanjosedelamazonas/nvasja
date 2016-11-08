package org.sanjose.views.caja;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.DestinoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public class CajaGridLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(CajaGridLogic.class);

    private CajaGridView view;
    private ProcUtil procUtil;

    public void init(CajaGridView cajaGridView) {
        view = cajaGridView;
        procUtil = MainUI.get().getProcUtil();
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.responsablesBtn.addClickListener(e -> editDestino(view.getSelectedRow()));
        view.enviarBtn.addClickListener(e -> procUtil.enviarContabilidad(view.getSelectedRows(), view.getService()));
        view.editarBtn.addClickListener(e -> editarComprobante(view.getSelectedRow()));
        view.imprimirBtn.addClickListener(event -> {
                    if (view.getSelectedRow()!=null) ViewUtil.printComprobante(view.getSelectedRow());
                });

        view.gridCaja.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                Object item = view.gridCaja.getContainerDataSource().getItem(view.gridCaja.getEditedItemId());
                if (item!=null) {
                    VsjCajabanco vcb = (VsjCajabanco) ((BeanItem) item).getBean();
                    final VsjCajabanco vcbToSave = vcb.prepareToSave();
                    if (vcb.isEnviado()) {
                        MessageBox
                                .createQuestion()
                                .withCaption("Esta operacion ya esta enviado")
                                .withMessage("?Esta seguro que quiere guardar los cambios?")
                                .withYesButton(() -> view.getService().getCajabancoRep().save(vcbToSave))
                                .withNoButton()
                                .open();
                    } else
                        view.getService().getCajabancoRep().save(vcbToSave);

                }
            }
        });

        GridContextMenu gridContextMenu = new GridContextMenu(view.gridCaja);
        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            final Object itemId = e.getItemId();
            if (itemId == null) {
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
            } else {
                gridContextMenu.addItem("Editar", k -> editarComprobante((VsjCajabanco) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Enviar a contabilidad", k -> {
                    if (!view.getSelectedRows().isEmpty()) {
                        procUtil.enviarContabilidad(view.getSelectedRows(), view.getService());
                    } else {
                        List<Object> cajabancos = new ArrayList<>();
                        cajabancos.add(itemId);
                        procUtil.enviarContabilidad(cajabancos, view.getService());
                    }
                    view.refreshData();
                });
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjItem) itemId));
                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((VsjCajabanco) itemId));
            }
        });
    }


    private void editDestino(VsjCajabanco vcb) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(500, Sizeable.Unit.PIXELS);
        destinoWindow.setHeight(550, Sizeable.Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(view.getService().getDestinoRepo(), view.getService().getCargocuartaRepo(),
                view.getService().getTipodocumentoRepo());
        if (vcb==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.getService().getDestinoRepo().findByCodDestino(vcb.getCodDestino());
            if (destino!=null)
                destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.getBtnGuardar().addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            //vcb.setCodDestino(editedItem.getCodDestino());
            destinoWindow.close();
            //refreshDestino();


        });
        destinoView.getBtnAnular().addClickListener(event -> {
            destinoView.viewLogic.anularDestino();
            destinoWindow.close();
        });

        destinoView.getBtnEliminar().addClickListener(clickEvent -> {
            try {
                ScpDestino item = destinoView.getScpDestino();
                String codDestino = item.getCodDestino();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtNombredestino())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            log.debug("To delete: " + item);

                            List<VsjCajabanco> comprobantes = view.getService().getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                //refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (VsjCajabanco vacb : comprobantes) {
                                    sb.append("\n").append(vacb.getTxtCorrelativo()).append(" ").append(vacb.getFecFecha()).append(" ").append(vacb.getTxtGlosaitem());
                                }
                                MessageBox
                                        .createWarning()
                                        .withCaption("No se puede eliminar destino: " + item.getTxtNombredestino())
                                        .withMessage("Los sigientes comprobantes usan este destino como Responsable o como Codigo Auxiliar: " + sb.toString())
                                        .open();
                            }
                        })
                        .withNoButton()
                        .open();
            } catch (FieldGroup.CommitException ce) {
                Notification.show("Error al eliminar el destino: " + ce.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                log.info("Got Commit Exception: " + ce.getMessage());
            }
            //destinoWindow.close();
        });
        UI.getCurrent().addWindow(destinoWindow);
    }


    public void enter(String productId) {
    }

    private void newComprobante() {
        view.clearSelection();
        MainUI.get().getComprobanteView().viewLogic.setNavigatorView(this.view);
        MainUI.get().getComprobanteView().viewLogic.nuevoComprobante();
        MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
    }

    private void editarComprobante(VsjCajabanco vcb) {
        if (vcb==null) return;
        if (!vcb.isEnviado() && !vcb.isAnula()) {
            // Transferencia
            if (!GenUtil.strNullOrEmpty(vcb.getCodTranscorrelativo())) {
                try {
                    MainUI.get().getTransferenciaView().viewLogic.editarTransferencia(vcb);
                    MainUI.get().getTransferenciaView().viewLogic.setNavigatorView(view);
                    MainUI.get().getNavigator().navigateTo(TransferenciaView.VIEW_NAME);
                } catch (NonEditableException e) {
                    Notification.show("No es editable", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            } else {
                MainUI.get().getComprobanteView().viewLogic.setNavigatorView(this.view);
                MainUI.get().getComprobanteView().viewLogic.editarComprobante(vcb);
                MainUI.get().getNavigator().navigateTo(ComprobanteView.VIEW_NAME);
            }
        }
    }
}
