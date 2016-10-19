package org.sanjose.util;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

/**
 * Created by pol on 19.10.16.
 */
public class StateUtil {

    private static final Logger log = LoggerFactory.getLogger(StateUtil.class);
    private boolean isEdited = false;

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isSaved() {
        return !isEdited;
    }

    public void edit() {
        if (isEdited) {
            log.debug("Already in state: " + isEdited);
        }
        isEdited = true;
    }

    public void save() {
        if (!isEdited) {
            log.error("Wasn't edited - not possible to SAVE: " + isEdited);
            return;
        }
        isEdited = false;
    }

    public void reset() {
        log.debug("reseting state edit to FALSE");
        isEdited = false;
    }
}
