package org.sanjose.views.sys;

import com.vaadin.navigator.View;

/**
 * Created by pol on 06.10.16.
 */
public interface VsjView extends View {

    public enum Mode {
        NEW, EDIT, VIEW, EMPTY
    }

    void init();
}


