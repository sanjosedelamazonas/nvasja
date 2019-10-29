package org.sanjose.validator;

import com.vaadin.data.Validator;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import javafx.scene.control.ComboBox;

import java.util.List;


/**
 * VASJA class
 * User: prubach
 * Date: 09.09.16
 */
public class NotBoundComboBoxValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(NotBoundComboBoxValidator.class);

    private final String message;
    private final ComboBox comboBox;

    public NotBoundComboBoxValidator(String message, ComboBox comboBox) {
        if (message!=null) message = "Tiene que ser selecionado";
        this.message = message;
        this.comboBox = comboBox;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        //log.info("validating: " + value);
        //List<Object> comboBox.getItems().
        if (value==null)
            throw new InvalidValueException(message);
    }
}
