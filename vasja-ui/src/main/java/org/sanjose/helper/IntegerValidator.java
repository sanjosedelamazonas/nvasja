package org.sanjose.helper;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class IntegerValidator implements Validator {

    private String message;

    public IntegerValidator(String message) {
        this.message = message;
    }

    public boolean isValid(Object value) {
        if (value == null || !(value instanceof String)) {
            return false;
        }
        try {
            Integer.parseInt((String) value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value)) {
            throw new InvalidValueException(message);
        }
    }
}
