package org.sanjose.views.caja;

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
import org.sanjose.model.ScpDestino;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.util.ViewUtil;
import org.sanjose.views.sys.DestinoView;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

    public CajaGridLogic() {

    }

    public void init(CajaGridView cajaGridView) {
        view = cajaGridView;
        procUtil = new ProcUtil(view.getService().getEm());
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.responsablesBtn.addClickListener(e -> editDestino(view.getSelectedRow()));
        view.enviarBtn.addClickListener(e -> enviarContabilidad(view.getSelectedRows()));
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
               
    }
    private void enviarContabilidad(Collection<Object> vcbs) {
        VsjCajabanco vcb = null;
        try {
            List<VsjCajabanco> vsjCajabancoList = new ArrayList<>();
            for (Object objVcb : vcbs) {
                vcb = (VsjCajabanco) objVcb;
                if (vcb.isEnviado()) {
                    continue;
                }
                vsjCajabancoList.add(vcb);
                // Check TipoDeCambio
                log.info("Check tipoDeCambio: " + vcb);
                List<ScpTipocambio> tipocambios = view.getService().getTipocambioRep().findById_FecFechacambio(
                        GenUtil.getBeginningOfDay(vcb.getFecFecha()));
                if (tipocambios.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Notification.show("Falta tipo de cambio para el dia: " + sdf.format(vcb.getFecFecha()), Notification.Type.WARNING_MESSAGE);
                    return;
                }
            }
            for (VsjCajabanco vcbS : vsjCajabancoList) {
                vcb = vcbS;
                log.info("Enviando: " + vcb);
                String result = procUtil.enviarContabilidad(vcb);
                log.info("Resultado: " + result);
                Notification.show("Operacion: " + vcb.getCodCajabanco(), result, Notification.Type.TRAY_NOTIFICATION);
            }
            if (vcbs.size()!=vsjCajabancoList.size()) {
                Notification.show("!Attention!", "!Algunas operaciones eran omitidas por ya ser enviadas!", Notification.Type.TRAY_NOTIFICATION);
            }
            view.refreshData();
        } catch (PersistenceException pe){
            Notification.show("Problema al enviar a contabilidad operacion: " + (vcb != null ? vcb.getCodCajabanco() : 0)
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause()!=null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause()!=null && pe.getCause().getCause()!=null ? "\n" + pe.getCause().getCause().getMessage() : "")
                    , Notification.Type.ERROR_MESSAGE);
        }
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
