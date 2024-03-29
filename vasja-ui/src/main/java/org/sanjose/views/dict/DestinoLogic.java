package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.GenUtil;

import java.io.Serializable;
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
public class DestinoLogic implements Serializable {

	
	private static final Logger log = LoggerFactory.getLogger(DestinoLogic.class);
	
    private final DestinoView view;
    private String vacio="";
    private Character FlgImg='1';
    private Character Nada=' ';

    public DestinoLogic(DestinoView DestinoView) {
        view = DestinoView;
    }

    public void init() {
        view.btnNuevo.addClickListener(event -> nuevoDestino());
    }

    public ScpDestino saveDestino() {
        try {
            if (GenUtil.strNullOrEmpty(view.selCodigo.getValue().toString())) {
                try {
                    // Generate cod destino if wasn't given
                    List<ScpDestino> lastDestinos = view.destinoRepo.findByCodDestinoLikeOrderByCodDestinoDesc("%");
                    String lastCodigoDestino = null;
                    for (ScpDestino scpDestino : lastDestinos) {
                        if (scpDestino.getCodDestino().matches("\\d+")) {
                            lastCodigoDestino = scpDestino.getCodDestino();
                            break;
                        }
                    }
                    Long newId = Long.valueOf(lastCodigoDestino) + 1;
                    String cod = String.format("%08d", newId);
                    view.selCodigo.setValue(cod);
                } catch (NumberFormatException pe) {
                    MessageBox
                            .createWarning()
                            .withCaption("Problema al guardar el destino")
                            .withMessage("!No se puede generar nuevo cod destino - por favor entrega un codigo!")
                            .withOkButton(
                            )
                            .open();
                    return null;
                }

            }
            ScpDestino item = view.getScpDestino();
            if (view.isNuevo() && view.destinoRepo.findByCodDestino(item.getCodDestino()) != null) {
                MessageBox
                        .createWarning()
                        .withCaption("Problema al guardar el destino")
                        .withMessage("!Un destino con codigo " + item.getCodDestino() + " ya existe!\n" +
                                "Su nombre o descripcion es: " + item.getTxtNombredestino())
                        .withOkButton(
                        )
                        .open();
                return null;
            }
            item.prepToSave();

            log.info("Ready to save: " + item);
            ScpDestino saved = view.destinoRepo.save(item);
            view.btnGuardar.setEnabled(false);
            view.btnAnular.setEnabled(false);
            return saved;
        } catch (CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el destino")
                    .withMessage("!Error al guardar el destino: " + errMsg)
                    .withOkButton(
                    )
                    .open();
            return null;
        } catch (Exception ce) {
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el destino")
                    .withMessage("!Error al guardar el destino: " + ce.getLocalizedMessage())
                    .withOkButton(
                    )
                    .open();
            return null;
        }
    }

    public void anularDestino() {
        view.anularDestino();
    }


    public void enter(String productId) {
    }


    public void nuevoDestino() {
        view.setNuevo(true);
        view.anularDestino();
        ScpDestino vcb = new ScpDestino();
        view.bindForm(vcb);
        view.btnGuardar.setEnabled(true);
        view.btnAnular.setEnabled(true);
        view.btnEliminar.setEnabled(false);
    }


    public void editarDestino(ScpDestino vcb) {
        view.bindForm(vcb);
        view.setNuevo(false);
        view.btnGuardar.setEnabled(true);
        view.btnAnular.setEnabled(true);
        view.btnEliminar.setEnabled(true);
    }
}
