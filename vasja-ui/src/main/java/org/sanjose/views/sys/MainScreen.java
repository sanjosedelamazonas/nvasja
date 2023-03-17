package org.sanjose.views.sys;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.authentication.Role;
import org.sanjose.helper.PrintHelper;
import org.sanjose.model.ScpDestino;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.banco.BancoConciliacionView;
import org.sanjose.views.banco.BancoManejoView;
import org.sanjose.views.banco.BancoOperView;
import org.sanjose.views.banco.BancoOperacionesView;
import org.sanjose.views.caja.*;
import org.sanjose.views.dict.DestinoListView;
import org.sanjose.views.dict.TerceroListView;
import org.sanjose.views.rendicion.RendicionManejoView;
import org.sanjose.views.rendicion.RendicionOperView;
import org.sanjose.views.rendicion.RendicionSimpleManejoView;
import org.sanjose.views.rendicion.RendicionSimpleOperView;
import org.sanjose.views.terceros.OperacionesListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
public class MainScreen extends HorizontalLayout {
    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);
    private final Menu menu;
    private PrintHelper printHelper = null;


    public MainScreen(MainUI ui, CajaManejoView cajaManejoView, ComprobanteView comprobanteView,
                      TransferenciaView transferenciaView, CajaOperacionesView cajaOperacionesView,
                      CajaGridView cajaGridView, ConfiguracionCtaCajaBancoView confView,
                      ConfiguracionCajaView configuracionCajaView, PropiedadView propiedadView,
                      BancoOperView bancoOperView, BancoManejoView bancoManejoView,
                      BancoConciliacionView bancoConciliacionView, BancoOperacionesView bancoOperacionesView,
                      RendicionManejoView rendicionManejoView, RendicionOperView rendicionOperView,
                      RendicionSimpleManejoView rendicionSimpleManejoView, RendicionSimpleOperView rendicionSimpleOperView,
                      ReportesView reportesView, DestinoListView destinoListView, TerceroListView terceroListView,
                      OperacionesListView operacionesListView) {

        setStyleName("main-screen");
        JavaScript.eval("setTimeout(function() { document.getElementById('my-custom-combobox').firstChild.select(); }, 0);");

        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);

        List<View> viewsToIgnoreWhenInit = new ArrayList<>();
        menu = new Menu(navigator);

        List<ScpDestino> destinosTerc = operacionesListView.getService().getDestinoRepo().findByTxtUsuario(CurrentUser.get());
        if (!destinosTerc.isEmpty()) {
            menu.addSeparator("Cuentas");
            menu.addView(operacionesListView, OperacionesListView.VIEW_NAME,
                    OperacionesListView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (Role.isCaja()) {
            menu.addSeparator("Caja");
            comprobanteView.init();
            viewsToIgnoreWhenInit.add(comprobanteView);
            transferenciaView.init();
            viewsToIgnoreWhenInit.add(transferenciaView);
            menu.addView(cajaManejoView, CajaManejoView.VIEW_NAME,
                    CajaManejoView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (Role.isPrivileged()) {
            menu.addView(cajaOperacionesView, CajaOperacionesView.VIEW_NAME,
                    CajaOperacionesView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(cajaGridView, CajaGridView.VIEW_NAME,
                    CajaGridView.VIEW_NAME, FontAwesome.EDIT);
                    }

        if (Role.isBanco()) {
            bancoOperView.init();
            viewsToIgnoreWhenInit.add(bancoOperView);
            menu.addSeparator("Banco");
            menu.addView(bancoManejoView, BancoManejoView.VIEW_NAME,
                    BancoManejoView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(bancoConciliacionView, BancoConciliacionView.VIEW_NAME,
                    BancoConciliacionView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (Role.isPrivileged()) {
            menu.addView(bancoOperacionesView, BancoOperacionesView.VIEW_NAME,
                    BancoOperacionesView.VIEW_NAME, FontAwesome.EDIT);
        }
        // Temporarily disabled access to Rendiciones
        if (Role.isDigitador()) {
            rendicionSimpleOperView.init();
            viewsToIgnoreWhenInit.add(rendicionSimpleOperView);
            menu.addSeparator("Rendiciones");
            menu.addView(rendicionSimpleManejoView, RendicionSimpleManejoView.VIEW_NAME,
                    RendicionSimpleManejoView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (Role.isAdmin()) {
            //rendicionOperView.init();
            //viewsToIgnoreWhenInit.add(rendicionOperView);
            //menu.addView(rendicionManejoView, RendicionManejoView.VIEW_NAME,
            //        RendicionManejoView.VIEW_NAME, FontAwesome.EDIT);
        }

        menu.addSeparator("Diccionarios");
        menu.addView(destinoListView, DestinoListView.VIEW_NAME,
                DestinoListView.VIEW_NAME, FontAwesome.EDIT);

        if (Role.isPrivileged()) {
            menu.addView(terceroListView, TerceroListView.VIEW_NAME,
                    TerceroListView.VIEW_NAME, FontAwesome.EDIT);
            menu.addSeparator("Configuracion");
            menu.addView(configuracionCajaView, ConfiguracionCajaView.VIEW_NAME,
                    ConfiguracionCajaView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(confView, ConfiguracionCtaCajaBancoView.VIEW_NAME,
                    ConfiguracionCtaCajaBancoView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (Role.isAdmin()) {
            menu.addSeparator("Sistema");
            menu.addView(reportesView, ReportesView.VIEW_NAME,
                    ReportesView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(propiedadView, PropiedadView.VIEW_NAME,
                    PropiedadView.VIEW_NAME, FontAwesome.EDIT);
        }

        menu.addView(new AboutView(), AboutView.VIEW_NAME, AboutView.VIEW_NAME,
                FontAwesome.INFO_CIRCLE);
        navigator.addViewChangeListener(viewChangeListener);

        printHelper = new PrintHelper(this);
        /*if (ConfigurationUtil.is("PRINTER_LIST_SHOW"))
            menu.addView(printHelper, PrintHelper.VIEW_NAME, PrintHelper.VIEW_NAME, FontAwesome.PRINT);
        else
            printHelper.init();
        */
        for (Viewing view : menu.getViews()) {
            if (!viewsToIgnoreWhenInit.contains(view))
                view.init();
        }
        addComponent(menu);
        // Disable Menu for "Gilmer" and open Caja Manejo
        if (Role.isOnlyCaja()) {
            menu.setShowMenu(false);
            MainUI.get().getNavigator().navigateTo(CajaManejoView.VIEW_NAME);
        }
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
    }


    // notify the view menu about view changes so that it can display which view
    // is currently active
    private final ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };

    public void printerLoaded(List<String> imprimeras, String defaultPrinter) {
        log.info("Loaded " + imprimeras.size() + " printers");

        ImpresorasView impresorasView = new ImpresorasView(imprimeras, defaultPrinter);
        menu.addView(impresorasView, ImpresorasView.VIEW_NAME, ImpresorasView.VIEW_NAME,
                FontAwesome.PRINT);
    }

    public PrintHelper getPrintHelper() {
        return printHelper;
    }
}
