package org.sanjose.views;

import com.vaadin.ui.JavaScript;
import org.sanjose.MainUI;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
@SpringComponent
@UIScope
public class MainScreen extends HorizontalLayout {
    private Menu menu;

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
        menu.addView(cajaGridView, CajaGridView.VIEW_NAME,
        		CajaGridView.VIEW_NAME, FontAwesome.EDIT);
        menu.addView(comprobanteView, ComprobanteView.VIEW_NAME,
                ComprobanteView.VIEW_NAME, FontAwesome.EDIT);

        menu.addView(cajaManejoView, CajaManejoView.VIEW_NAME,
                CajaManejoView.VIEW_NAME, FontAwesome.EDIT);

        menu.addView(confView, ConfiguracionCtaCajaBancoView.VIEW_NAME,
        		ConfiguracionCtaCajaBancoView.VIEW_NAME, FontAwesome.EDIT);
        menu.addView(configuracionCajaView, ConfiguracionCajaView.VIEW_NAME,
                ConfiguracionCajaView.VIEW_NAME, FontAwesome.EDIT);
        menu.addView(propiedadView, PropiedadView.VIEW_NAME,
                PropiedadView.VIEW_NAME, FontAwesome.EDIT);
        menu.addView(new AboutView(), AboutView.VIEW_NAME, AboutView.VIEW_NAME,
                FontAwesome.INFO_CIRCLE);

        navigator.addViewChangeListener(viewChangeListener);

        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
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
}
