package org.sanjose;

import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.LoginScreen;
import org.sanjose.authentication.LoginScreen.LoginListener;
import org.sanjose.authentication.MsgAccessControl;
import org.sanjose.views.sys.MainScreen;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.VsjPropiedadRep;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.PropiedadView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SpringUI(path="/*")
@Viewport("user-scalable=yes,initial-scale=1.0")
@Theme("mytheme")
@Widgetset("org.sanjose.MyAppWidgetset")
public class MainUI extends UI {

    private AccessControl accessControl;

    private final ConfiguracionCtaCajaBancoView confView;

    private final ConfiguracionCajaView configuracionCajaView;
    
    private final CajaGridView cajaGridView;

    private final PropiedadView propiedadView;

    private final VsjPropiedadRep propRepo;

    private final ComprobanteView comprobanteView;

    private final CajaManejoView cajaManejoView;

    private final TransferenciaView transferenciaView;

    private final MsgUsuarioRep msgUsuarioRep;

    @PersistenceContext
    private final EntityManager em;

    private MainScreen mainScreen;

    @Autowired
    private MainUI(VsjPropiedadRep propRepo, PropiedadView propiedadView, CajaGridView cajaGridView,
                   ConfiguracionCajaView configuracionCajaView, ConfiguracionCtaCajaBancoView confView,
                   ComprobanteView comprobanteView, TransferenciaView transferenciaView,
                   CajaManejoView cajaManejoView, MsgUsuarioRep msgUsuarioRep,
                   EntityManager em) {
    	this.confView = confView;
    	this.cajaGridView = cajaGridView;
        this.propiedadView = propiedadView;
        this.propRepo = propRepo;
        this.comprobanteView = comprobanteView;
        this.transferenciaView = transferenciaView;
        this.configuracionCajaView = configuracionCajaView;
        this.cajaManejoView = cajaManejoView;
        this.msgUsuarioRep = msgUsuarioRep;
        this.em = em;
    }
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //accessControl = new BasicAccessControl();
        ConfigurationUtil.setPropiedadRepo(propRepo);
        accessControl = new MsgAccessControl(msgUsuarioRep, ConfigurationUtil.is("DEV_MODE"));
        Responsive.makeResponsive(this);
        setLocale(ConfigurationUtil.getLocale());
        getPage().setTitle("Main");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, (LoginListener) () -> showMainView()));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        mainScreen = new MainScreen(MainUI.this, cajaManejoView, cajaGridView, confView, configuracionCajaView, propiedadView, comprobanteView, transferenciaView);
        setContent(mainScreen);
        if (GenUtil.strNullOrEmpty(getNavigator().getState()))
            getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
        else
            getNavigator().navigateTo(getNavigator().getState());
    }

    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public ComprobanteView getComprobanteView() {
        return comprobanteView;
    }

    public CajaManejoView getCajaManejoView() {
        return cajaManejoView;
    }

    public TransferenciaView getTransferenciaView() {
        return transferenciaView;
    }
}
