package org.sanjose.validator;

import com.vaadin.data.Validator;
import com.vaadin.ui.ComboBox;


/**
 * VASJA class
 * User: prubach
 * Date: 30.10.19
 */
public class NotBoundComboBoxValidator implements Validator {

    private final String message;
    private final ComboBox comboBox;

    public NotBoundComboBoxValidator(String message, ComboBox comboBox) {
        if (message==null) message = "Tiene que ser selecionado";
        this.message = message;
        this.comboBox = comboBox;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (value==null) {
            //comboBox.markAsDirty();
            throw new InvalidValueException(message);
        }
        for (Object v : comboBox.getItemIds()) {
            if (v.equals(value)) return;
        }
        //comboBox.markAsDirty();
        throw new InvalidValueException(message);
    }
}
