package org.sanjose.views;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.helper.EnviarException;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.ScpTipocambio;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.model.VsjCajabanco;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import org.sanjose.util.ProcUtil;

import javax.persistence.PersistenceException;

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

    public CajaGridLogic(CajaGridView cajaGridView) {
        view = cajaGridView;
    }

    protected ProcUtil procUtil;

    public void init() {
        procUtil = new ProcUtil(view.getEm());
        view.nuevoComprobante.addClickListener(e -> newComprobante());
        view.responsablesBtn.addClickListener(e -> editDestino(view.getSelectedRow()));
        view.enviarBtn.addClickListener(e -> enviarContabilidad(view.getSelectedRow()));


        view.gridCaja.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {
            }
            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                Object item = view.gridCaja.getContainerDataSource().getItem(view.gridCaja.getEditedItemId());
                if (item!=null) {
                    VsjCajabanco vcb = (VsjCajabanco) ((BeanItem) item).getBean();
                    final VsjCajabanco vcbToSave = DataUtil.prepareToSave(vcb);
                    if ("1".equals(vcb.getFlgEnviado())) {
                        MessageBox
                                .createQuestion()
                                .withCaption("Esta operacion ya esta enviado")
                                .withMessage("?Esta seguro que quiere guardar los cambios?")
                                .withYesButton(() -> {
                                    view.repo.save(vcbToSave);
                                })
                                .withNoButton()
                                .open();
                    } else
                        view.repo.save(vcbToSave);

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
                if ("1".equals(vcb.getFlgEnviado())) {
                    continue;
                }
                vsjCajabancoList.add(vcb);
                // Check TipoDeCambio
                log.info("Check tipoDeCambio: " + vcb);
                List<ScpTipocambio> tipocambios = view.getTipocambioRepo().findById_FecFechacambio(
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
            Notification.show("Problema al enviar a contabilidad operacion: " + vcb.getCodCajabanco()
                    + "\n\n" + pe.getMessage() +
                    (pe.getCause()!=null ? "\n" + pe.getCause().getMessage() : "")
                    + (pe.getCause()!=null && pe.getCause().getCause()!=null ? "\n" + pe.getCause().getCause().getMessage() : "")
                    , Notification.Type.ERROR_MESSAGE);
        } catch (EnviarException e) {
            Notification.show("Problema al enviar a contabilidad operacion: " + e.getCajabanco().getCodCajabanco()
                    + "\n" + e.getMessage() +"\n", Notification.Type.ERROR_MESSAGE);
        }
    }

    private void editDestino(Collection<Object> vcbs) {
        Optional<Object> objVcb = vcbs.stream().findFirst();
        VsjCajabanco vcb = null;
        try {
            vcb = (VsjCajabanco) objVcb.get();
        } catch (NoSuchElementException ne) {
            vcb = null;
        }
        log.info("Got vcb: " + vcb);

        Window destinoWindow = new Window();

        destinoWindow.setWindowMode(WindowMode.NORMAL);
        destinoWindow.setWidth(500, Sizeable.Unit.PIXELS);
        destinoWindow.setHeight(550, Sizeable.Unit.PIXELS);
        destinoWindow.setPositionX(200);
        destinoWindow.setPositionY(50);
        destinoWindow.setModal(true);
        destinoWindow.setClosable(false);

        DestinoView destinoView = new DestinoView(view.getDestinoRepo(), view.getCargocuartaRepo(), view.getTipodocumentoRepo());
        if (vcb==null)
            destinoView.viewLogic.nuevoDestino();
        else {
            ScpDestino destino = view.getDestinoRepo().findByCodDestino(vcb.getCodDestino());
            if (destino!=null)
                destinoView.viewLogic.editarDestino(destino);
        }
        destinoWindow.setContent(destinoView);

        destinoView.btnGuardar.addClickListener(event -> {
            ScpDestino editedItem = destinoView.viewLogic.saveDestino();
            //vcb.setCodDestino(editedItem.getCodDestino());
            destinoWindow.close();
            //refreshDestino();


        });
        destinoView.btnAnular.addClickListener(event -> {
            destinoView.viewLogic.anularDestino();
            destinoWindow.close();
        });

        destinoView.btnEliminar.addClickListener(clickEvent -> {
            try {
                ScpDestino item = destinoView.getScpDestino();
                //log.info("eliminar: " + item);
                String codDestino = item.getCodDestino();
                MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
                MessageBox
                        .createQuestion()
                        .withCaption("Eliminar: " + item.getTxtNombredestino())
                        .withMessage("Esta seguro que lo quiere eliminar?")
                        .withYesButton(() -> {
                            log.debug("To delete: " + item);

                            List<VsjCajabanco> comprobantes = view.getRepo().findByCodDestinoOrCodDestinoitem(codDestino, codDestino);
                            if (comprobantes.isEmpty()) {
                                destinoView.destinoRepo.delete(item);
                                //refreshDestino();
                                destinoWindow.close();
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (VsjCajabanco vacb : comprobantes) {
                                    sb.append("\n" + vacb.getTxtCorrelativo() + " " + vacb.getFecFecha() + " " + vacb.getTxtGlosaitem());
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


    public void cancelProduct() {
        setFragmentParameter("");
        view.clearSelection();
//        view.editProduct(null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String productId) {
        String fragmentParameter;
        if (productId == null || productId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = productId;
        }

        Page page = MainUI.get().getPage();
  /*      page.setUriFragment("!" + SampleCrudView.VIEW_NAME + "/"
                + fragmentParameter, false);
  */  }

    public void enter(String productId) {
        if (productId != null && !productId.isEmpty()) {
        	log.info("Configuracion Logic getting: " + productId);
            if (productId.equals("new")) {
     //       	newConfiguracion();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(productId);
  //                  Product product = findProduct(pid);
    //                view.selectRow(product);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public void newComprobante() {
        view.clearSelection();
        setFragmentParameter("new");
        VsjCajabanco vcb = new VsjCajabanco();
        vcb.setCodMes("03");

        vcb.setTxtAnoproceso("2016");
        vcb.setFlgEnviado("0");
        vcb.setCodDestino("000");
        vcb.setCodTipomoneda("0");
        vcb.setIndTipocuenta("0");

        view.gridCaja.getContainerDataSource().addItem(vcb);
    }


    public void deleteConfiguracion() {
        List<VsjCajabanco> rows = new ArrayList<VsjCajabanco>();
    	
        for (Object vsj : view.getSelectedRow()) {
        	log.info("Got selected: " + vsj);
        	if (vsj instanceof VsjCajabanco)
        		rows.add((VsjCajabanco)vsj);
        }
        view.clearSelection();
        //setFragmentParameter("new");
        for (VsjCajabanco vsj : rows) {
        	log.info("Removing: " + vsj.getCodCajabanco());        	
        	view.removeRow(vsj);
        }
        //view.gridCaja.getContainerDataSource().removeItem(itemId)
    }    
}
