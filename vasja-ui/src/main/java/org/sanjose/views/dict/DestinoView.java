package org.sanjose.views.dict;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.ScpCargocuartaRep;
import org.sanjose.repo.ScpDestinoRep;
import org.sanjose.repo.ScpTipodocumentoRep;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
// @UIScope
public class DestinoView extends DestinoUI implements View {

    public static final String VIEW_NAME = "Destino";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(DestinoView.class);
    public final DestinoLogic viewLogic = new DestinoLogic(this);
    public final ScpDestinoRep destinoRepo;
    public ScpDestino item;
    private BeanItem<ScpDestino> beanItem;
    private FieldGroup fieldGroup;

    private boolean isLoading = true;

    private boolean isEdit = false;

    private boolean isNuevo = false;

    @Autowired
    public DestinoView(ScpDestinoRep destinoRepo,
                       ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo) {
        this.destinoRepo = destinoRepo;
        setSizeFull();

        btnGuardar.setEnabled(false);
        btnAnular.setEnabled(false);


        // Nombre
//        DataFilterUtil.bindComboBox(selNombreCompleta, "txtNombredestino", destinoRepo.findByIndTipodestinoNot('3'), null);
//        selNombreCompleta.setTextInputAllowed(true);
//        selNombreCompleta.setInvalidAllowed(true);
//        selNombreCompleta.setNewItemsAllowed(true);

        txtNombreCompleta.addBlurListener(e -> {
            if (txtNombreCompleta.getValue() != null) {
                //log.info("Got from nombre " +selNombreCompleta.getValue());
                String oldNombre = txtNombreCompleta.getValue().toString();
                txtNombreCompleta.setValue(GenUtil.capitalizeEachWord(oldNombre.trim()));
                //selNombreCompleta.select(GenUtil.capitalizeEachWord(oldNombre.trim()));
            }
        });

        // Codigo
        DataFilterUtil.bindComboBox(selCodigo, "codDestino", destinoRepo.findByIndTipodestinoNot('3'), null);
        selCodigo.setTextInputAllowed(true);
        selCodigo.setInvalidAllowed(true);
        selCodigo.setNewItemsAllowed(true);

        selCodigo.addBlurListener(e -> {
            if (selCodigo.getValue() != null) {
                String tipo = ConsultaRucDni.RUC;
                try {
                    tipo = (tipoDePersona.getValue()!=null && tipoDePersona.getValue().toString().equals("N")) || selCodigo.getValue().toString().trim().length()==8 ? ConsultaRucDni.DNI : ConsultaRucDni.RUC;
                    Map<String, String> consulta = ConsultaRucDni.getInstance().get(tipo, selCodigo.getValue().toString().trim());
                    if (consulta.containsKey("nombre")) {
                        txtNombreCompleta.setValue(GenUtil.capitalizeEachWord(consulta.get("nombre").toLowerCase()));
                    }
                    if (consulta.containsKey("nombres")) {
                        ScpDestino dest = new ScpDestino();
                        txtNombreCompleta.setValue(GenUtil.capitalizeEachWord(consulta.get("nombres").toLowerCase()));
                    }
                    direccion.setValue(assembleDireccion(new String[] {"direccion", "distrito", "departamento", "provincia"}, consulta));
                    if (consulta.containsKey("tipoDocumento")) {
                        tipoDocumento.select("0" + consulta.get("tipoDocumento"));
                    }
                    if (consulta.containsKey("apellidoPaterno")) {
                        apellidoPaterno.setValue(GenUtil.capitalizeEachWord(consulta.get("apellidoPaterno").toLowerCase()));
                    }
                    if (consulta.containsKey("apellidoMaterno")) {
                        apellidoMaterno.setValue(GenUtil.capitalizeEachWord(consulta.get("apellidoMaterno").toLowerCase()));
                    }
                    if (consulta.containsKey("numeroDocumento")) {
                        ruc.setValue(consulta.get("numeroDocumento"));
                    }
                    if (tipo.equals(ConsultaRucDni.RUC) && consulta.containsKey("estado") && !consulta.get("estado").equals("ACTIVO")) {
                        MessageBox
                                .createQuestion()
                                .withCaption("Atencion!")
                                .withMessage("!El RUC: " + selCodigo.getValue() + " en la base de SUNAT esta deactivado!")
                                .withOkButton()
                                .open();
                    }
                } catch (ConsultaRucDniException he) {
                    if (tipo.equals(ConsultaRucDni.RUC)) {
                        String msg = "!No se podia encontra el RUC: " + selCodigo.getValue() + " en la base de SUNAT!";
                        if (he.getCause()!=null) {
                            msg = he.getMessage();
                        }
                        MessageBox
                                .createQuestion()
                                .withCaption("Atencion!")
                                .withMessage(msg)
                                .withOkButton()
                                .open();
                    }
                    log.debug("Could not find..." + tipo + " " + selCodigo.getValue());
                }
            }
        });

        // Clasificacion
        DataFilterUtil.bindTipoDestinoComboBox(clasificacion, "indTipodestino", "Sel Clasificacion");

        // Tipo doc
        DataFilterUtil.bindComboBox(tipoDocumento, "codTipodocumento", tipodocumentoRepo.findAll(),
                "Sel Tipo documento", "txtDescripcion");

        // Cargo 4ta
        DataFilterUtil.bindComboBox(cargo, "codCargo", cargocuartaRepo.findAll(), "Sel Cargo 4ta",
                "txtDescripcion");

        // Genero
        DataFilterUtil.bindGeneroComboBox(genero, "indSexo", "Sel Sexo");

        // Tipo persona
        DataFilterUtil.bindTipoPersonaComboBox(tipoDePersona, "indTipopersona", "Sel Tipo de persona");

        // Validators
        selCodigo.addValidator(new LocalizedBeanValidator(ScpDestino.class, "codDestino"));
        clasificacion.addValidator(new LocalizedBeanValidator(ScpDestino.class, "indTipodestino"));

        //selNombreCompleta.setDescription("Nombre");

        selNombreCompleta.addValidator(new LocalizedBeanValidator(ScpDestino.class, "txtNombredestino"));
        tipoDePersona.addValidator(new LocalizedBeanValidator(ScpDestino.class, "indTipopersona"));
        viewLogic.init();
    }


    public void bindForm(ScpDestino item) {
        isLoading = true;

        isEdit = !GenUtil.strNullOrEmpty(item.getCodDestino());
        beanItem = new BeanItem<>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(selCodigo, "codDestino");
        fieldGroup.bind(clasificacion, "indTipodestino");
        fieldGroup.bind(selNombreCompleta, "txtNombredestino");
        fieldGroup.bind(apellidoPaterno, "txtApellidopaterno");
        fieldGroup.bind(apellidoMaterno, "txtApellidomaterno");
        fieldGroup.bind(genero, "indSexo");

        fieldGroup.bind(direccion, "txtDireccion");
        fieldGroup.bind(tipoDocumento, "indTipodctoidentidad");
        fieldGroup.bind(numDocumento, "txtNumerodctoidentidad");
        fieldGroup.bind(telefono1, "txtTelefono1");
        fieldGroup.bind(telefono2, "txtTelefono2");
        fieldGroup.bind(ruc, "txtRuc");
        fieldGroup.bind(tipoDePersona, "indTipopersona");
        fieldGroup.bind(cargo, "codCargo");
        fieldGroup.getFields().stream().filter(f -> f instanceof TextField).forEach(f -> ((TextField) f).setNullRepresentation(""));
        isLoading = false;
        if (isEdit) {
            // EDITING
            selCodigo.setEnabled(false);
        } else
            selCodigo.setEnabled(true);
        isEdit = false;
    }

    public String assembleDireccion(String[] fields, Map<String, String> consulta) {
        List<String> parts = new ArrayList<>();
        for (String f : fields) {
            if (consulta.containsKey(f)) {
                String res = GenUtil.capitalizeEachWord(consulta.get(f).toLowerCase());
                if (!res.equals("") && !res.equals(" ") && !res.equals("-")) {
                    parts.add(res);
                }
            }
        }
        return String.join(", ", parts);
    }

    public void anularDestino() {
        if (fieldGroup!=null) fieldGroup.discard();
    }

    public ScpDestino getScpDestino() throws FieldGroup.CommitException {
        fieldGroup.commit();
        return beanItem.getBean();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnAnular() {
        return btnAnular;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Button getBtnNuevo() {
        return btnNuevo;
    }

    public boolean isNuevo() {
        return isNuevo;
    }

    public void setNuevo(boolean nuevo) {
        isNuevo = nuevo;
    }

    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public ComboBox getSelCodigo() {
        return selCodigo;
    }

    public ComboBox getSelNombreCompleta() {
        return selNombreCompleta;
    }
}
