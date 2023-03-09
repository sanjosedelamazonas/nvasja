package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.*;
import org.sanjose.util.ConfigurationUtil;

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
public class DestinoListLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(DestinoListLogic.class);
	
    private final DestinoListView view;

    public DestinoListLogic(DestinoListView destinoListView) {
        view = destinoListView;
    }

    public void init() {
    	view.btnNuevo.addClickListener(e -> nuevoDestino());
//        view.grid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
//            @Override
//            public void preCommit(CommitEvent commitEvent) throws CommitException {
//            }
//            @Override
//            public void postCommit(CommitEvent commitEvent) throws CommitException {
//                Object item = view.grid.getContainerDataSource().getItem(view.grid.getEditedItemId());
//                if (item!=null)
//                    view.getService().getDestinoRepo().save((ScpDestino) ((BeanItem) item).getBean());
//            }
//        });
        view.btnEliminar.addClickListener(e -> eliminarDestinos());
    }
    
    private void nuevoDestino() {
        view.clearSelection();
        view.editDestino(null);
        //view.grid.getContainerDataSource().addItemAt(0, new ScpDestino());
    }
    
    private void eliminarDestinos() {
        List<ScpDestino> rows = new ArrayList<>();
    	
        for (Object vsj : view.getSelectedRow()) {
            log.debug("Got selected: " + vsj);
            if (vsj instanceof ScpDestino)
        		rows.add((ScpDestino)vsj);
        }



//        MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
//        MessageBox
//                .createQuestion()
//                .withCaption("Eliminar: " + item.getTxtNombredestino())
//                .withMessage("Esta seguro que lo quiere eliminar?")
//                .withYesButton(() -> {
//                    log.debug("To delete: " + item);
//
//                    List<ScpCajabanco> comprobantes = getService().getCajabancoRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
//                    List<ScpBancocabecera> bancoscabeceras = getService().getBancocabeceraRep().findByCodDestino(codDestino);
//                    List<ScpBancodetalle> bancositems = getService().getBancodetalleRep().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
//                    List<ScpRendicioncabecera> rendicionescab = getService().getRendicioncabeceraRep().findByCodDestino(codDestino);
//                    List<ScpRendiciondetalle> rendicionitems = getService().getRendiciondetalleRep().findByCodDestino(codDestino);
//
//                    StringBuilder sb = new StringBuilder();
//                    for (ScpCajabanco vcb : comprobantes) {
//                        sb.append("\n").append("Caja: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosaitem());
//                    }
//                    for (ScpBancodetalle bancodet : bancositems) {
//                        ScpBancocabecera cab = bancodet.getScpBancocabecera();
//                        if (!bancoscabeceras.contains(cab))
//                            bancoscabeceras.add(cab);
//
//                    }
//                    for (ScpRendiciondetalle renddet : rendicionitems) {
//                        ScpRendicioncabecera cab = renddet.getScpRendicioncabecera();
//                        if (!rendicionescab.contains(cab))
//                            rendicionescab.add(cab);
//
//                    }
//
//                    for (ScpCajabanco vcb : comprobantes) {
//                        sb.append("\n").append("Caja: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosaitem());
//                    }
//                    for (ScpBancocabecera vcb : bancoscabeceras) {
//                        sb.append("\n").append("Banco: ").append(vcb.getTxtCorrelativo()).append(" ").append(vcb.getFecFecha()).append(" ").append(vcb.getTxtGlosa());
//                    }
//                    for (ScpRendicioncabecera vcb : rendicionescab) {
//                        sb.append("\n").append("Rendicion: ").append(vcb.getCodComprobante()).append(" ").append(vcb.getFecComprobante()).append(" ").append(vcb.getTxtGlosa());
//                    }
//                    if (sb.toString().isEmpty()) {
//                        destinoView.destinoRepo.delete(item);
//                        refreshData();
//                        destinoWindow.close();
//                    } else {
//                        MessageBox
//                                .createWarning()
//                                .withCaption("No se puede eliminar destino: " + item.getTxtNombredestino())
//                                .withMessage("Los sigientes comprobantes usan este destino como Responsable o como Codigo Auxiliar: " + sb.toString())
//                                .open();
//                    }
//                })
//                .withNoButton()
//                .open();
//
//
//

        view.clearSelection();
        for (ScpDestino vsj : rows) {
            log.debug("Removing: " + vsj.getCodDestino());
            view.removeRow(vsj);
        }
    }
}
