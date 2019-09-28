package org.sanjose.views.sys;

import com.vaadin.navigator.View;

/**
 * Created by pol on 06.10.16.
 */
public interface Viewing extends View {

    void init();

    String getWindowTitle();

    enum Mode {
        NEW, EDIT, VIEW, EMPTY
    }
}


