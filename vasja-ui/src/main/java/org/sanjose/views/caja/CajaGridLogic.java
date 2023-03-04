package org.sanjose.views.caja;

import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.NonEditableException;
import org.sanjose.helper.ReportHelper;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.VsjItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.ItemsRefreshing;
import org.sanjose.views.sys.DestinoView;

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
public class CajaGridLogic extends CajaLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(CajaGridLogic.class);

    private CajaGridView view;


    public void init(CajaGridView cajaGridView) {
        view = cajaGridView;
        cajaView = view;
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.nuevaTransferencia.addClickListener(e -> newTransferencia());
        view.responsablesBtn.addClickListener(e -> editDestino(view.getSelectedRow()));
        view.enviarBtn.addClickListener(e -> {
            if (!view.getSelectedRows().isEmpty()) {
                enviarContabilidad(view.getSelectedRow(), true);
            }
            view.gridCaja.deselectAll();
        });
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
                    ScpCajabanco vcb = (ScpCajabanco) ((BeanItem) item).getBean();
                    final ScpCajabanco vcbToSave = vcb.prepareToSave();
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
                gridContextMenu.addItem(!GenUtil.strNullOrEmpty(((ScpCajabanco) itemId).getCodTranscorrelativo()) ? "Ver detalle" : "Editar",
                        k -> editarComprobante((ScpCajabanco) itemId));
                gridContextMenu.addItem("Nuevo comprobante", k -> newComprobante());
                gridContextMenu.addItem("Enviar a contabilidad", k -> { enviarContabilidad((ScpCajabanco)itemId, true); });
                gridContextMenu.addItem("Marcar no enviado a contabilidad", k -> { enviarContabilidad((ScpCajabanco)itemId, false); });
                gridContextMenu.addItem("Ver Voucher", k -> ReportHelper.generateComprobante((VsjItem) itemId));
                if (ViewUtil.isPrinterReady()) gridContextMenu.addItem("Imprimir Voucher", k -> ViewUtil.printComprobante((ScpCajabanco) itemId));
            }
        });
    }

    private void editDestino(ScpCajabanco vcb) {
        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(700, Sizeable.Unit.PIXELS);
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
            if (editedItem!=null) {
                destinoWindow.close();
            }
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

                            List<ScpCajabanco> comprobantes = view.getService().getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                //refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (ScpCajabanco vacb : comprobantes) {
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
        });
        UI.getCurrent().addWindow(destinoWindow);
    }

    public void enter(String productId) {
    }
}
