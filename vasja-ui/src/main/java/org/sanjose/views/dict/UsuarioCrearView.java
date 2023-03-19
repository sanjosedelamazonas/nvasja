package org.sanjose.views.dict;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpDestino;
import org.sanjose.repo.MsgRolRep;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.ScpDestinoRep;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.Rot10;
import org.sanjose.validator.LocalizedBeanValidator;
import org.sanjose.validator.TwoPasswordfieldsValidator;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;
import org.sanjose.views.sys.DestinoUI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link ConfiguracionCtaCajaBancoLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
//@SpringComponent
// @UIScope
public class UsuarioCrearView extends UsuarioCrearUI implements View {

    public static final String VIEW_NAME = "Crear/Editar Usuario";
    public String getWindowTitle() {
        return VIEW_NAME;
    }
    private static final Logger log = LoggerFactory.getLogger(UsuarioCrearView.class);
    public final MsgUsuarioRep usuarioRep;
    public MsgUsuario item;
    private BeanItem<MsgUsuario> beanItem;
    private FieldGroup fieldGroup;
    private ScpDestinoRep destinoRep;

    private boolean isLoading = true;

    private boolean isEdit = false;

    private boolean isNuevo = false;

    //@Autowired
    public UsuarioCrearView(MsgUsuarioRep usuarioRep, MsgRolRep msgRolRep, ScpDestinoRep destinoRep) {

        this.usuarioRep = usuarioRep;
        this.destinoRep = destinoRep;
        setSizeFull();

        btnGuardar.setEnabled(false);
        btnAnular.setEnabled(false);
        codUsuario.setEnabled(false);
        // Codigo
        DataFilterUtil.bindComboBox(codRol, "codRol", msgRolRep.findByCodRolLikeOrderByCodRolDesc("%"),
                "txt_descripcion");

        txtNombre.addBlurListener(e -> {
            if (txtNombre.getValue() != null) {
                String oldNombre = txtNombre.getValue();
                txtNombre.setValue(GenUtil.capitalizeEachWord(oldNombre.trim()));
            }
        });

        getChkEnviarInvitacion().addValueChangeListener(e -> {
            if (getChkEnviarInvitacion().getValue()) {
                getTxtPass1().setEnabled(false);
                getTxtPass2().setEnabled(false);
            } else {
                getTxtPass1().setEnabled(true);
                getTxtPass2().setEnabled(true);
            }
        });

        getTxtPass1().addValidator(new TwoPasswordfieldsValidator(getTxtPass2(), false, null));
        getTxtPass2().addValidator(new TwoPasswordfieldsValidator(getTxtPass1(), false, null));
        getTxtCorreo().addValidator(new EmailValidator("El correo no esta correcto"));

        getTxtCorreo().addValidator(new Validator() {
            @Override
            public void validate(Object o) throws InvalidValueException {
                if ((!GenUtil.objNullOrEmpty(o)) && (usuarioRep.findByTxtCorreoIgnoreCase((String)o))!=null){
                    throw new InvalidValueException("Usuario con este correo ya existe!");
                }
            }
        });
        getTxtUsuario().addValidator(new Validator() {
            @Override
            public void validate(Object o) throws InvalidValueException {
                if ((!GenUtil.objNullOrEmpty(o)) && (usuarioRep.findByTxtUsuarioIgnoreCase((String)o))!=null){
                    throw new InvalidValueException("Este usuario ya existe!");
                }
            }
        });
    }


    public MsgUsuario saveUsuario() {
        try {
            if (GenUtil.strNullOrEmpty(codUsuario.getValue())) {
                try {
                    // Generate cod usuario if wasn't given
                    List<MsgUsuario> msgUsuarios = usuarioRep.findByCodUsuarioLikeOrderByCodUsuarioDesc("%");
                    String lastCodUsuario = null;
                    for (MsgUsuario msgUsuario : msgUsuarios) {
                        if (msgUsuario.getCodUsuario().matches("\\d+")) {
                            lastCodUsuario = msgUsuario.getCodUsuario();
                            break;
                        }
                    }
                    Long newId = Long.valueOf(lastCodUsuario) + 1;
                    String cod = String.format("%03d", newId);
                    codUsuario.setValue(cod);
                } catch (NumberFormatException pe) {
                    codUsuario.setEnabled(true);
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
            MsgUsuario usuario = getMsgUsuario();
            usuario.prepToSave();
            if (!GenUtil.strNullOrEmpty(getTxtPass1().getValue())) {
                usuario.setTxtPassword(Rot10.rot10(getTxtPass1().getValue()));
            }
            btnGuardar.setEnabled(false);
            btnAnular.setEnabled(false);
            log.info("Ready to save: " + usuario);
            return usuarioRep.save(usuario);
        } catch (FieldGroup.CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el usuario")
                    .withMessage("!Error al guardar el usuario: " + errMsg)
                    .withOkButton(
                    )
                    .open();
            return null;
        } catch (Exception ce) {
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el usuario")
                    .withMessage("!Error al guardar el usuario: " + ce.getLocalizedMessage())
                    .withOkButton(
                    )
                    .open();
            return null;
        }
    }


    public void eliminarUsuario(MsgUsuario usuario) {
        if (destinoRep.findByTxtUsuario(usuario.getTxtUsuario())!=null) {
            usuario.setFlgEstado('0');
            usuarioRep.save(usuario);
        } else {
            usuarioRep.delete(usuario);
        }
    }

    public void enter(String productId) {
    }


    public void nuevoUsuario() {
        setNuevo(true);
        MsgUsuario vcb = new MsgUsuario();
        bindForm(vcb);
        btnGuardar.setEnabled(true);
        btnAnular.setEnabled(true);
        btnEliminar.setEnabled(false);
        isNuevo=true;
    }


    public void editarUsuario(MsgUsuario vcb) {
        bindForm(vcb);
        btnGuardar.setEnabled(true);
        btnAnular.setEnabled(true);
        btnEliminar.setEnabled(true);
        isNuevo=false;
    }

    public void bindForm(MsgUsuario item) {
        isLoading = true;

        isEdit = !GenUtil.strNullOrEmpty(item.getCodUsuario());
        beanItem = new BeanItem<>(item);
        fieldGroup = new FieldGroup(beanItem);
        fieldGroup.setItemDataSource(beanItem);
        fieldGroup.bind(getCodRol(), "codRol");
        fieldGroup.bind(txtAplicacion, "txtAplicacion");
        fieldGroup.bind(txtCorreo, "txtCorreo");
        fieldGroup.bind(txtNombre, "txtNombre");
        fieldGroup.bind(txtUsuario, "txtUsuario");
        //fieldGroup.bind(txtPass1, "txtPassword");

        fieldGroup.getFields().stream().filter(f -> f instanceof TextField).forEach(f -> ((TextField) f).setNullRepresentation(""));
        isLoading = false;
        isEdit = false;
    }

    public void cerrar() {
        if (fieldGroup!=null) fieldGroup.discard();
    }

    public MsgUsuario getMsgUsuario() throws FieldGroup.CommitException {
        fieldGroup.commit();
//


        return beanItem.getBean();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        //viewLogic.enter(event.getParameters());
    }

    public TextField getCodUsuario() {
        return codUsuario;
    }

    public TextField getTxtUsuario() {
        return txtUsuario;
    }

    public TextField getTxtNombre() {
        return txtNombre;
    }

    public ComboBox getCodRol() {
        return codRol;
    }

    public TextField getTxtAplicacion() {
        return txtAplicacion;
    }

    public TextField getTxtCorreo() {
        return txtCorreo;
    }

    public CheckBox getChkEnviarInvitacion() {
        return chkEnviarInvitacion;
    }

    public PasswordField getTxtPass1() {
        return txtPass1;
    }

    public PasswordField getTxtPass2() {
        return txtPass2;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Button getBtnAnular() {
        return btnAnular;
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
}
