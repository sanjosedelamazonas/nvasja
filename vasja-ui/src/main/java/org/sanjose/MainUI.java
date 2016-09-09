package org.sanjose;

import com.vaadin.ui.Notification;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.BasicAccessControl;
import org.sanjose.authentication.LoginScreen;
import org.sanjose.authentication.LoginScreen.LoginListener;
import org.sanjose.helper.ConfigurationUtil;
import org.sanjose.model.VsjPropiedad;
import org.sanjose.model.VsjPropiedadRep;
import org.sanjose.views.CajaGridView;
import org.sanjose.views.ConfiguracionCtaCajaBancoView;
import org.sanjose.views.MainScreen;
import org.sanjose.views.PropiedadView;
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
    
    private CajaGridView cajaGridView;

    private PropiedadView propiedadView;

    private VsjPropiedadRep propRepo;
    @Autowired
    private MainUI(VsjPropiedadRep propRepo, PropiedadView propiedadView, CajaGridView cajaGridView, ConfiguracionCtaCajaBancoView confView) {
    	this.confView = confView;
    	this.cajaGridView = cajaGridView;
        this.propiedadView = propiedadView;
        this.propRepo = propRepo;
    }
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        ConfigurationUtil.setPropiedadRepo(propRepo);
        if (ConfigurationUtil.getProperty("LOCALE")==null) {
            Notification.show("Initializing Configuracion del Sistema", Notification.Type.ERROR_MESSAGE);
            ConfigurationUtil.storeDefaultProperties();
        }
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
        setContent(new MainScreen(MainUI.this, cajaGridView, confView, propiedadView));
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    /*@WebServlet(urlPatterns = "/*", name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends SpringVaadinServlet {
    }*/

    /*    @WebServlet(urlPatterns = "/*", name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends VaadinServlet {
    }*/
}
