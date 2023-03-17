package org.sanjose.authentication;

import java.io.Serializable;
import java.util.List;

import com.vaadin.event.ShortcutAction;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.sanjose.helper.MailerSender;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.VsjPasswordresettoken;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.VsjPasswordresettokenRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.sys.MainScreen;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;

/**
 * UI content when the user is not logged in yet.
 */
public class LoginScreen extends CssLayout {

    private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
    private Button recoverPassword;
    private Button returnToLogin;
    private TextField email;
    private final LoginListener loginListener;
    private final AccessControl accessControl;
    private FormLayout loginForm = new FormLayout();
    private MsgUsuarioRep msgUsuarioRep;
    private VsjPasswordresettokenRep vsjPasswordresettokenRep;
    private VaadinRequest vaadinRequest;
    private static final Logger log = LoggerFactory.getLogger(LoginScreen.class);

    public LoginScreen(AccessControl accessControl, MsgUsuarioRep msgUsuarioRep,
                       VsjPasswordresettokenRep vsjPasswordresettokenRep, VaadinRequest vaadinRequest, LoginListener loginListener) {
        this.loginListener = loginListener;
        this.accessControl = accessControl;
        this.msgUsuarioRep = msgUsuarioRep;
        this.vaadinRequest = vaadinRequest;
        this.vsjPasswordresettokenRep = vsjPasswordresettokenRep;
        buildUI();
        username.focus();
    }

    private void buildUI() {
        addStyleName("login-screen");

        // login form, centered in the available part of the screen
        Component loginForm = buildLoginForm();

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen
        // sizes
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm,
                Alignment.MIDDLE_CENTER);

        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);
    }

    private Component buildLoginForm() {
        loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        loginForm.setMargin(false);

        //loginForm.addComponent(username = new TextField("Usuario", "ggomez"));
        loginForm.addComponent(username = new TextField("Usuario", ""));
        username.setWidth(15, Unit.EM);
        loginForm.addComponent(password = new PasswordField("Clave"));
        password.setWidth(15, Unit.EM);
        password.setDescription("");
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        loginForm.addComponent(buttons);

        buttons.addComponent(login = new Button("Login"));
        login.setDisableOnClick(true);
        login.addClickListener((Button.ClickListener) event -> {
            try {
                login();
            } finally {
                login.setEnabled(true);
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        buttons.addComponent(forgotPassword = new Button("?Clave Olvidado?"));
        forgotPassword.addClickListener(clickEvent -> showForgotPassForm());
        forgotPassword.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        return loginForm;
    }

    private void showForgotPassForm() {
        username.setVisible(false);
        password.setVisible(false);
        login.setVisible(false);
        forgotPassword.setVisible(false);
        loginForm.addComponent(email = new TextField("Email:"));
        email.setWidth(300, Unit.PIXELS);
        loginForm.addComponent(recoverPassword = new Button("Recupera su clave"));
        recoverPassword.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        recoverPassword.addClickListener(clickEvent -> remindPassword());
        loginForm.addComponent(returnToLogin = new Button("Regresa a al entrada"));
        returnToLogin.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        returnToLogin.addClickListener(clickEvent -> showLoginForm());
    }

    private void showLoginForm() {
        username.setVisible(true);
        password.setVisible(true);
        login.setVisible(true);
        forgotPassword.setVisible(true);
        email.setVisible(false);
        recoverPassword.setVisible(false);
        returnToLogin.setVisible(false);
    }

    private void remindPassword() {
        MsgUsuario usuario = msgUsuarioRep.findByTxtCorreoIgnoreCase(email.getValue());
        if (usuario==null) {
            showNotification(new Notification("No se podia encontrar un usuario con este correo: " + email.getValue()));
        } else {
            VsjPasswordresettoken token = new VsjPasswordresettoken(usuario);
            vsjPasswordresettokenRep.save(token);
            String link = Page.getCurrent().getLocation().getScheme() + ":" +
                    Page.getCurrent().getLocation().getSchemeSpecificPart() +
                    "resetpass/?token=" + token.getToken();
            Email message = EmailBuilder.startingBlank()
                    .to(email.getValue())
                    .from("Vicariato San Jose del Amazonas", ConfigurationUtil.get("MAIL_FROM"))
                    .withSubject("Reset clave")
                    .withHTMLText("<p>Hello,</p>"
                            + "<p>You have requested to reset your password.</p>"
                            + "<p>Click the link below to change your password:</p>"
                            + "<p><a href=\"" + link + "\">Change my password</a></p>"
                            + "<br>"
                            + "<p>Ignore this email if you do remember your password, "
                            + "or you have not made the request.</p>")
                    .buildEmail();

            log.info(message.toString());
            //new MailerSender().sendEmail(message);

            showNotification(new Notification("Reset link enviado a: " + email.getValue()));
            showLoginForm();
        }
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                //"<h1>Vicariato San Jose del Amazonas</h1>" +
        		"<h2>Vicariato San Jose del Amazonas</h2>"
                        + "Bienvenido al Sistema de Gestion de Caja y Bancos",
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    private void login() {
        if (accessControl.signIn(username.getValue(), password.getValue())) {
            loginListener.loginSuccessful();
        } else {
            showNotification(new Notification("No se podia entrar",
                    "Por favor verifica el usuario y clave.",
                    Notification.Type.HUMANIZED_MESSAGE));
            username.focus();
        }
    }

    private void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

    public interface LoginListener extends Serializable {
        void loginSuccessful();
    }
}
