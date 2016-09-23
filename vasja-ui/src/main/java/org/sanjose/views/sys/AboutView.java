package org.sanjose.views.sys;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AboutView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "About";

    private Label label;

    private final CustomLayout aboutContent;

    public AboutView() {
        aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom
        // layout
        aboutContent.addComponent(
                new Label(FontAwesome.INFO_CIRCLE.getHtml()
                        + " This application is using Vaadin "
                        + Version.getFullVersion(), ContentMode.HTML), "info");
        setSizeFull();
        setStyleName("about-view");
        addComponent(aboutContent);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
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