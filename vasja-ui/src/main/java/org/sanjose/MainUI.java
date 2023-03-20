package org.sanjose;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.sanjose.authentication.AccessControl;
import org.sanjose.authentication.LoginScreen;
import org.sanjose.authentication.LoginScreen.LoginListener;
import org.sanjose.authentication.MsgAccessControl;
import org.sanjose.authentication.Role;
import org.sanjose.mail.MailerSender;
import org.sanjose.repo.MsgUsuarioRep;
import org.sanjose.repo.VsjPasswordresettokenRep;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.TipoCambio;
import org.sanjose.util.GenUtil;
import org.sanjose.util.ProcUtil;
import org.sanjose.views.banco.*;
import org.sanjose.views.caja.*;
import org.sanjose.views.dict.DestinoListView;
import org.sanjose.views.dict.TerceroListView;
import org.sanjose.views.dict.TipoCambioManejoView;
import org.sanjose.views.dict.UsuarioView;
import org.sanjose.views.rendicion.RendicionManejoView;
import org.sanjose.views.rendicion.RendicionOperView;
import org.sanjose.views.rendicion.RendicionSimpleManejoView;
import org.sanjose.views.rendicion.RendicionSimpleOperView;
import org.sanjose.views.sys.*;
import org.sanjose.views.terceros.EnviarDiarioTercerosView;
import org.sanjose.views.terceros.OperacionesListView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SpringUI(path="/")
@Viewport("user-scalable=yes,initial-scale=1.0")
@Theme("mytheme")
@Widgetset("org.sanjose.MyAppWidgetset")
@PreserveOnRefresh
public class MainUI extends UI {

    private final ConfiguracionCtaCajaBancoView confView;
    private final ConfiguracionCajaView configuracionCajaView;
    private final DestinoListView destinoListView;
    private final TerceroListView terceroListView;
    private final CajaGridView cajaGridView;
    private final CajaOperacionesView cajaOperacionesView;
    private final PropiedadView propiedadView;
    private final ComprobanteView comprobanteView;
    private final CajaManejoView cajaManejoView;
    private final TransferenciaView transferenciaView;
    private final BancoOperView bancoOperView;
    private final BancoManejoView bancoManejoView;
    private final BancoConciliacionView bancoConciliacionView;
    private final BancoOperacionesView bancoOperacionesView;
    private final RendicionManejoView rendicionManejoView;
    private final RendicionSimpleManejoView rendicionSimpleManejoView;
    private final RendicionOperView rendicionOperView;
    private final RendicionSimpleOperView rendicionSimpleOperView;
    private final ReportesView reportesView;
    private final OperacionesListView operacionesListView;
    private final TipoCambioManejoView tipoCambioManejoView;
    private final EnviarDiarioTercerosView enviarDiarioTercerosView;
    private final UsuarioView usuarioView;

    private final MsgUsuarioRep msgUsuarioRep;
    private final VsjPasswordresettokenRep vsjPasswordresettokenRep;
    private ProcUtil procUtil;
    private AccessControl accessControl;
    private MainScreen mainScreen;

    private final MailerSender mailerSender;

    private static final Logger log = LoggerFactory.getLogger(MainUI.class);

    @Autowired
    private MainUI(PropiedadService propiedadService,
                   PersistanceService persistanceService,
                   MsgUsuarioRep msgUsuarioRep,
                   VsjPasswordresettokenRep vsjPasswordresettokenRep,
                   ProcUtil procUtil,
                   MailerSender mailerSender) {
        this.propiedadView = new PropiedadView(propiedadService);
        this.reportesView = new ReportesView(persistanceService);
        this.msgUsuarioRep = msgUsuarioRep;
        this.vsjPasswordresettokenRep = vsjPasswordresettokenRep;
        this.procUtil = procUtil;
        this.mailerSender = mailerSender;
        this.confView = new ConfiguracionCtaCajaBancoView(persistanceService);
        this.cajaGridView = new CajaGridView(persistanceService);
        this.comprobanteView = new ComprobanteView(persistanceService);
        this.transferenciaView = new TransferenciaView(persistanceService);
        this.configuracionCajaView = new ConfiguracionCajaView(persistanceService);
        this.bancoOperView = new BancoOperView(persistanceService);
        this.cajaManejoView = new CajaManejoView(persistanceService);
        this.cajaOperacionesView = new CajaOperacionesView(persistanceService);
        this.bancoManejoView = new BancoManejoView(persistanceService);
        this.bancoOperacionesView = new BancoOperacionesView(persistanceService);
        this.bancoConciliacionView = new BancoConciliacionView(persistanceService);
        this.rendicionManejoView = new RendicionManejoView(persistanceService);
        this.rendicionSimpleManejoView = new RendicionSimpleManejoView(persistanceService);
        this.rendicionOperView = new RendicionOperView(persistanceService);
        this.rendicionSimpleOperView = new RendicionSimpleOperView(persistanceService);
        this.destinoListView = new DestinoListView(persistanceService);
        this.terceroListView = new TerceroListView(persistanceService);
        this.operacionesListView = new OperacionesListView(persistanceService);
        this.tipoCambioManejoView = new TipoCambioManejoView(persistanceService);
        this.enviarDiarioTercerosView = new EnviarDiarioTercerosView(persistanceService);
        this.usuarioView = new UsuarioView(persistanceService);
    }

    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //accessControl = new BasicAccessControl();
        ConfigurationUtil.setPropiedadRepo(propiedadView.repo);
        accessControl = new MsgAccessControl(msgUsuarioRep, ConfigurationUtil.is("DEV_MODE"), ConfigurationUtil.get("DEV_USER"));
        Responsive.makeResponsive(this);
        setLocale(ConfigurationUtil.getLocale(true));
        getPage().setTitle("Vicariato San Jose del Amazonas - Sistema de Gestion de Caja y Bancos");
        if (ConfigurationUtil.is("DEV_MODE")) {
         //   accessControl.signIn(ConfigurationUtil.get("DEV_USER"), "");
        }
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, msgUsuarioRep, vsjPasswordresettokenRep, vaadinRequest, (LoginListener) () -> showMainView()));
        } else {
            showMainView();
        }
        if (Role.isPrivileged() && !ConfigurationUtil.is("DEV_MODE")) {
            try {
                TipoCambio.checkTipoCambio(new Date(), this.getBancoOperacionesView().getService().getTipocambioRep());
            } catch (TipoCambio.TipoCambioNoExiste e) {
                log.info(e.getMessage());
            } catch (TipoCambio.TipoCambioNoSePuedeBajar e) {
                log.info(e.getMessage());
            }
        }
    }

    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        mainScreen = new MainScreen(MainUI.this, cajaManejoView, comprobanteView, transferenciaView, cajaOperacionesView,
                cajaGridView, confView, configuracionCajaView, propiedadView, bancoOperView, bancoManejoView,
                bancoConciliacionView, bancoOperacionesView, rendicionManejoView, rendicionOperView,
                rendicionSimpleManejoView, rendicionSimpleOperView, reportesView, destinoListView, terceroListView,
                operacionesListView, enviarDiarioTercerosView, tipoCambioManejoView, usuarioView);
        setContent(mainScreen);
        if (GenUtil.strNullOrEmpty(getNavigator().getState()))
            getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
        else
            getNavigator().navigateTo(getNavigator().getState());
        //getNavigator().getCurrentView().
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

    public RendicionOperView getRendicionOperView() {
        return rendicionOperView;
    }

    public RendicionManejoView getRendicionManejoView() {
        return rendicionManejoView;
    }

    public RendicionSimpleOperView getRendicionSimpleOperView() {
        return rendicionSimpleOperView;
    }

    public RendicionSimpleManejoView getRendicionSimpleManejoView() {
        return rendicionSimpleManejoView;
    }

    public DestinoListView getDestinoListView() {
        return destinoListView;
    }

    public MailerSender getMailerSender() {
        return mailerSender;
    }
}
