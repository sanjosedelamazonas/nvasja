package org.sanjose.views.sys;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import java.util.List;

public class ImpresorasView extends VerticalLayout implements Viewing {

    public static final String VIEW_NAME = "Impresoras";

    private final Label label;

    private final CustomLayout aboutContent;

    public ImpresorasView(List<String> imprimeras, String defPrinter) {
        aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom
        // layout
        StringBuilder sb = new StringBuilder(" Printer Applet loaded correctly and found " + imprimeras.size() + " printers: <UL>" );
        for (String s : imprimeras) {
            if (s.equals(defPrinter))
                sb.append("<LI><b>").append(s).append("</b>");
            else
                sb.append("<LI>").append(s);
        }
        label = new Label(FontAwesome.PRINT.getHtml()
                + sb.toString()
                , ContentMode.HTML);
        aboutContent.addComponent(label
                , "info");
        setSizeFull();
        setStyleName("about-view");
        addComponent(aboutContent);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void init() {
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
