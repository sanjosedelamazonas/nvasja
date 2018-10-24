package org.sanjose.views.sys;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

/**
 * View shown when trying to navigate to a view that does not exist using
 * {@link com.vaadin.navigator.Navigator}.
 * 
 * 
 */
public class ErrorView extends VerticalLayout implements View {

    private final Label explanation;

    public ErrorView() {
        setMargin(true);
        setSpacing(true);

        Label header = new Label("The view could not be found");
        header.addStyleName(Reindeer.LABEL_H1);
        addComponent(header);
        addComponent(explanation = new Label());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        explanation.setValue(String.format(
                "You tried to navigate to a view ('%s') that does not exist.",
                event.getViewName()));
    }
}
