package org.sanjose.views;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.sanjose.model.*;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringComponent
@UIScope
public class DestinoView extends DestinoUI implements View {

	private static final Logger log = LoggerFactory.getLogger(DestinoView.class);
	
    public static final String VIEW_NAME = "Destino";

    public DestinoLogic viewLogic = new DestinoLogic(this);

    public ScpDestino item;

    public BeanItem<ScpDestino> beanItem;

    public ScpDestinoRep destinoRepo;

    public FieldGroup fieldGroup;

    boolean isLoading = true;

    boolean isEdit = false;

    public CajaManejoView cajaManejoView;

    @Autowired
    public DestinoView(ScpDestinoRep destinoRepo,
                       ScpCargocuartaRep cargocuartaRepo, ScpTipodocumentoRep tipodocumentoRepo) {
        this.destinoRepo = destinoRepo;
        setSizeFull();

        btnGuardar.setEnabled(false);
        btnAnular.setEnabled(false);

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
        codigo.addValidator(new BeanValidator(ScpDestino.class, "codDestino"));
        clasificacion.addValidator(new BeanValidator(ScpDestino.class, "indTipodestino"));
        nombreCompleta.addValidator(new BeanValidator(ScpDestino.class, "txtNombredestino"));
        tipoDePersona.addValidator(new BeanValidator(ScpDestino.class, "indTipopersona"));
        viewLogic.init();
    }


    public void bindForm(ScpDestino item) {
        isLoading = true;

        isEdit = false;
        if (!GenUtil.strNullOrEmpty(item.getCodDestino())) isEdit = true;
        beanItem = new BeanItem<ScpDestino>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(codigo, "codDestino");
        fieldGroup.bind(clasificacion, "indTipodestino");
        fieldGroup.bind(nombreCompleta, "txtNombredestino");
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
        for (Field f: fieldGroup.getFields()) {
            if (f instanceof TextField)
                ((TextField)f).setNullRepresentation("");
        }
        isLoading = false;
        if (isEdit) {
            // EDITING
            codigo.setEnabled(false);
        } else
            codigo.setEnabled(true);
        isEdit = false;
    }

    public void anularDestino() {
        if (fieldGroup!=null) fieldGroup.discard();
    }

    public ScpDestino getScpDestino() throws FieldGroup.CommitException {
        fieldGroup.commit();
        return beanItem.getBean();
    }

    public void setCajaManejoView(CajaManejoView cajaManejoView) {
        this.cajaManejoView = cajaManejoView;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

}
