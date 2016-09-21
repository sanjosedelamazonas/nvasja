package org.sanjose.validator;

import com.vaadin.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import org.sanjose.util.GenUtil;


/**
 * VASJA class
 * User: prubach
 * Date: 09.09.16
 */
public class NotNullNotBoundValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(NotNullNotBoundValidator.class);

    final String message;

    public NotNullNotBoundValidator(String message) {
        if (message!=null) message = "Tiene que ser selecionado";
        this.message = message;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        //log.info("validating: " + value);
        if (value==null)
            throw new InvalidValueException(message);
    }
}
