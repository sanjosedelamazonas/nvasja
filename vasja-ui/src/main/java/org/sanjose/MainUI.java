package org.sanjose;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.BasicAccessControl;
import org.sanjose.authentication.LoginScreen;
import org.sanjose.authentication.LoginScreen.LoginListener;
import org.sanjose.helper.ConfigurationUtil;
import org.sanjose.helper.GenUtil;
import org.sanjose.model.VsjCajabancoRep;
import org.sanjose.model.VsjPropiedad;
import org.sanjose.model.VsjPropiedadRep;
import org.sanjose.views.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

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

    private AccessControl accessControl = new BasicAccessControl();

    private ConfiguracionCtaCajaBancoView confView;

    private ConfiguracionCajaView configuracionCajaView;
    
    private CajaGridView cajaGridView;

    private PropiedadView propiedadView;

    private VsjPropiedadRep propRepo;

    private ComprobanteView comprobanteView;

    private CajaManejoView cajaManejoView;

    @Autowired
    private MainUI(VsjPropiedadRep propRepo, PropiedadView propiedadView, CajaGridView cajaGridView,
                   ConfiguracionCajaView configuracionCajaView, ConfiguracionCtaCajaBancoView confView,
                   ComprobanteView comprobanteView, CajaManejoView cajaManejoView) {
    	this.confView = confView;
    	this.cajaGridView = cajaGridView;
        this.propiedadView = propiedadView;
        this.propRepo = propRepo;
        this.comprobanteView = comprobanteView;
        this.configuracionCajaView = configuracionCajaView;
        this.cajaManejoView = cajaManejoView;
    }
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        ConfigurationUtil.setPropiedadRepo(propRepo);
        Responsive.makeResponsive(this);
        setLocale(ConfigurationUtil.getLocale());
        getPage().setTitle("Main");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(new MainScreen(MainUI.this, cajaManejoView, cajaGridView, confView, configuracionCajaView, propiedadView, comprobanteView));
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
}
