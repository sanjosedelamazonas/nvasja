package org.sanjose;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.LoginScreen;
import org.sanjose.authentication.LoginScreen.LoginListener;
import org.sanjose.authentication.MsgAccessControl;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.banco.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.sys.MainScreen;
import org.sanjose.views.sys.PropiedadService;
import org.sanjose.views.sys.PropiedadView;
import org.springframework.beans.factory.annotation.Autowired;

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
@PreserveOnRefresh
public class MainUI extends UI {

    private final ConfiguracionCtaCajaBancoView confView;
    private final ConfiguracionCajaView configuracionCajaView;
    private final CajaGridView cajaGridView;
    private final PropiedadView propiedadView;
    private final ComprobanteView comprobanteView;
    private final CajaManejoView cajaManejoView;
    private final TransferenciaView transferenciaView;
    private final BancoOperView bancoOperView;
    private final BancoManejoView bancoManejoView;
    private final BancoConciliacionView bancoConciliacionView;
    private final BancoOperacionesView bancoOperacionesView;
    private final MsgUsuarioRep msgUsuarioRep;
    private ProcUtil procUtil;
    private AccessControl accessControl;
    private MainScreen mainScreen;

    @Autowired
    private MainUI(PropiedadService propiedadService,
                   ComprobanteService comprobanteService,
                   BancoService bancoService,
                   MsgUsuarioRep msgUsuarioRep,
                   ProcUtil procUtil,
                   //EntityManager em,
                   ConfiguracionCajaView configuracionCajaView,
                   ConfiguracionCtaCajaBancoView confView) {
        this.propiedadView = new PropiedadView(propiedadService);
        this.msgUsuarioRep = msgUsuarioRep;
        this.procUtil = procUtil;
        this.confView = confView;
        this.cajaGridView = new CajaGridView(comprobanteService);
        this.comprobanteView = new ComprobanteView(comprobanteService);
        this.transferenciaView = new TransferenciaView(comprobanteService);
        this.configuracionCajaView = configuracionCajaView;
        this.bancoOperView = new BancoOperView(bancoService);
        this.cajaManejoView = new CajaManejoView(comprobanteService);
        this.bancoManejoView = new BancoManejoView(bancoService);
        this.bancoOperacionesView = new BancoOperacionesView(bancoService);
        this.bancoConciliacionView = new BancoConciliacionView(bancoService);
    }

    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //accessControl = new BasicAccessControl();
        ConfigurationUtil.setPropiedadRepo(propiedadView.repo);
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
        mainScreen = new MainScreen(MainUI.this, cajaManejoView, cajaGridView, confView, configuracionCajaView,
                propiedadView, comprobanteView, transferenciaView, bancoOperView, bancoManejoView, bancoConciliacionView, bancoOperacionesView);
        setContent(mainScreen);
        if (GenUtil.strNullOrEmpty(getNavigator().getState()))
            getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
        else
            getNavigator().navigateTo(getNavigator().getState());
    }

    public AccessControl getAccessControl() {
        return accessControl;
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

    public MsgUsuarioRep getMsgUsuarioRep() {
        return msgUsuarioRep;
    }

    public ProcUtil getProcUtil() {
        return procUtil;
    }

    public BancoOperView getBancoOperView() {
        return bancoOperView;
    }

    public BancoManejoView getBancoManejoView() {
        return bancoManejoView;
    }

    public BancoOperacionesView getBancoOperacionesView() {
        return bancoOperacionesView;
    }
}
