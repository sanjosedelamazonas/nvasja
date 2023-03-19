package org.sanjose.views.dict;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import org.sanjose.MainUI;
import org.sanjose.model.MsgUsuario;
import org.sanjose.repo.MsgRolRep;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.ScpDestinoRep;
import org.sanjose.util.DataFilterUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.Rot10;
import org.sanjose.validator.TwoPasswordfieldsValidator;
import org.sanjose.views.caja.ConfiguracionCtaCajaBancoLogic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private CompletableFuture<String> sendRes = null;

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
                "txtDescripcion");

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

        getTxtPass1().addValidator(new TwoPasswordfieldsValidator(getTxtPass2(), false, "Las contraseñas tienen no son iguales"));
        getTxtPass2().addValidator(new TwoPasswordfieldsValidator(getTxtPass1(), false, "Las contraseñas tienen no son iguales"));
        getTxtCorreo().addValidator(new EmailValidator("El correo no esta correcto"));

        getTxtCorreo().addValidator(new Validator() {
            @Override
            public void validate(Object o) throws InvalidValueException {
                if (!GenUtil.objNullOrEmpty(o)) {
                    MsgUsuario otroUs = usuarioRep.findByTxtCorreoIgnoreCase((String)o);
                    if (otroUs!=null && (item==null || !item.getCodUsuario().equals(otroUs.getCodUsuario())))
                        throw new InvalidValueException("Usuario con este correo ya existe!");
                }
            }
        });
        getTxtUsuario().addValidator(new Validator() {
            @Override
            public void validate(Object o) throws InvalidValueException {
                if (!GenUtil.objNullOrEmpty(o)) {
                    MsgUsuario otroUs = usuarioRep.findByTxtUsuarioIgnoreCase((String)o);
                    if (otroUs!=null && (item==null || !item.getCodUsuario().equals(otroUs.getCodUsuario())))
                        throw new InvalidValueException("Usuario con este correo ya existe!");
                }
            }
        });
    }


    public CompletableFuture<String> saveUsuario() {
        if (chkEnviarInvitacion.getValue() && !isNuevo) {
            MessageBox
                    .createWarning()
                    .withCaption("Reenviar")
                    .withMessage("!Quieres reenviar la invitacion a " + beanItem.getBean().getTxtCorreo() + "?")
                    .withYesButton( () -> saveUsuarioConfirmado(true))
                    .withNoButton(()-> saveUsuarioConfirmado(false))
                    .open();
        } else
            saveUsuarioConfirmado(chkEnviarInvitacion.getValue());
        return this.sendRes;
    }


    public void saveUsuarioConfirmado(boolean isEnviarInvitacion) {
        try {
            MsgUsuario usuario = getMsgUsuario();
            usuario.prepToSave();
            if (!GenUtil.strNullOrEmpty(getTxtPass1().getValue())) {
                usuario.setTxtPassword(Rot10.rot10(getTxtPass1().getValue()));
            }
            btnGuardar.setEnabled(false);
            btnAnular.setEnabled(false);
            log.info("Ready to save: " + usuario);
            MsgUsuario saved = usuarioRep.save(usuario);

            if (isEnviarInvitacion) {
                CompletableFuture<Void> sendRes = ((MainUI)UI.getCurrent()).getMailerSender().sendInvitation(usuario.getTxtCorreo());
                this.sendRes =
                    sendRes.handle((String, ex) -> {
                    if (ex != null) {
                        return "Problema al enviar mensaje a :"+ usuario.getTxtCorreo() + "\n" + ex.getMessage();
                    } else {
                        return null;
                    }
                });
            }
        } catch (FieldGroup.CommitException ce) {
            String errMsg = GenUtil.genErrorMessage(ce.getInvalidFields());
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el usuario")
                    .withMessage("!Error al guardar el usuario: " + errMsg)
                    .withOkButton(
                    )
                    .open();
        } catch (Exception ce) {
            MessageBox
                    .createError()
                    .withCaption("Problema al guardar el usuario")
                    .withMessage("!Error al guardar el usuario: " + ce.getLocalizedMessage())
                    .withOkButton(
                    )
                    .open();
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
    }


    public void editarUsuario(MsgUsuario vcb) {
        setNuevo(false);
        bindForm(vcb);
        item = vcb;
        btnGuardar.setEnabled(true);
        btnAnular.setEnabled(true);
        btnEliminar.setEnabled(true);
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
        fieldGroup.bind(codUsuario, "codUsuario");
        if (isNuevo){
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
            codUsuario.setEnabled(false);
        }
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
