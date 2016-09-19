package org.sanjose.views;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.sanjose.MainUI;
import org.sanjose.helper.ConfigurationUtil;
import org.sanjose.helper.PrintHelper;
import org.sanjose.authentication.Role;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.factory.parsing.ImportDefinition;

import java.util.List;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
@SpringComponent
@UIScope
public class MainScreen extends HorizontalLayout {
    private Menu menu;

    private PrintHelper printHelper = null;

    private Label printerIcon = new Label("");

    private ProgressBar printerLoading;

    private AboutView aboutView;

    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);

    @Autowired
    public MainScreen(MainUI ui, CajaManejoView cajaManejoView, CajaGridView cajaGridView, ConfiguracionCtaCajaBancoView confView,
                      ConfiguracionCajaView configuracionCajaView, PropiedadView propiedadView, ComprobanteView comprobanteView) {

        setStyleName("main-screen");
        JavaScript.eval("setTimeout(function() { document.getElementById('my-custom-combobox').firstChild.select(); }, 0);");

        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);

        if (ui.getAccessControl().isUserInRole(Role.CAJA) ||
                ui.getAccessControl().isUserInRole(Role.CONTADOR) ||
                ui.getAccessControl().isUserInRole(Role.ADMIN)
                ) {
            menu.addView(comprobanteView, ComprobanteView.VIEW_NAME,
                    ComprobanteView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(cajaManejoView, CajaManejoView.VIEW_NAME,
                    CajaManejoView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (ui.getAccessControl().isUserInRole(Role.CONTADOR) ||
                ui.getAccessControl().isUserInRole(Role.ADMIN)
                ) {
            menu.addView(cajaGridView, CajaGridView.VIEW_NAME,
                    CajaGridView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(configuracionCajaView, ConfiguracionCajaView.VIEW_NAME,
                    ConfiguracionCajaView.VIEW_NAME, FontAwesome.EDIT);
            menu.addView(confView, ConfiguracionCtaCajaBancoView.VIEW_NAME,
                    ConfiguracionCtaCajaBancoView.VIEW_NAME, FontAwesome.EDIT);
        }
        if (ui.getAccessControl().isUserInRole(Role.ADMIN)) {
            menu.addView(propiedadView, PropiedadView.VIEW_NAME,
                    PropiedadView.VIEW_NAME, FontAwesome.EDIT);
        }

        aboutView = new AboutView();
        menu.addView(new AboutView(), AboutView.VIEW_NAME, AboutView.VIEW_NAME,
                FontAwesome.INFO_CIRCLE);
        cajaManejoView.setComprobanteView(comprobanteView);
        comprobanteView.setCajaManejoView(cajaManejoView);
        navigator.addViewChangeListener(viewChangeListener);

        printHelper = new PrintHelper(this);
        if (ConfigurationUtil.is("PRINTER_LIST_SHOW"))
            menu.addView(printHelper, PrintHelper.VIEW_NAME, PrintHelper.VIEW_NAME, FontAwesome.PRINT);

        /*if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
            log.info("Adding printer icon and progressbar");
        }
        */
        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
    }

    public void printerLoaded(List<String> imprimeras) {
        log.info("Loaded " + imprimeras.size() + " printers");

        ImprimerasView imprimerasView = new ImprimerasView(imprimeras);
        menu.addView(imprimerasView, ImprimerasView.VIEW_NAME, ImprimerasView.VIEW_NAME,
                FontAwesome.PRINT);
    }

    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
            JavaScript.eval("setTimeout(function() { document.getElementById('my-custom-combobox').firstChild.select(); }, 0);");
        }

    };

    public PrintHelper getPrintHelper() {
        return printHelper;
    }
}
