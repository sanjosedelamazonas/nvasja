package org.sanjose.views.sys;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AboutView extends VerticalLayout implements Viewing {

    public static final String VIEW_NAME = "About";
    private final CustomLayout aboutContent;
    private Label label;

    public AboutView() {
        aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        aboutContent.addComponent(
                new Label(FontAwesome.INFO_CIRCLE.getHtml()
                        + " This application is using Vaadin "
                        + Version.getFullVersion() +"<br>"
                        + " Version of WEB APP: "
                        + getClass().getPackage().getImplementationVersion()
                        , ContentMode.HTML), "info");
        aboutContent.addComponent(
                new Label(FontAwesome.INFO_CIRCLE.getHtml()
                        + " Version of WEB APP: "
                        + getClass().getPackage().getImplementationVersion(), ContentMode.HTML), "info");
        setSizeFull();
        setStyleName("about-view");
        addComponent(aboutContent);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void init() {
    }

    public CustomLayout getAboutContent() {
        return aboutContent;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
