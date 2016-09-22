package org.sanjose.validator;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class DateValidator implements Validator {

    private String message;
    private final String format;
    private final Date min;
    private final Date max;

    public DateValidator(String message, String format, Date min, Date max) {
        this.message = message;
        this.format = format;
        this.min = min;
        this.max = max;
    }

    public boolean isValid(Object value) {
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat(format);
        	Date val;
        	if (value instanceof String)
        		val = sdf.parse((String)value);
        	else
        		val = (Date)value;
        	if (min!=null && (val.before(min))) {
        		message = "Fecha no puede ser mas temprana que " + min.toLocaleString();
        		return false;        	
        	}
        	if (max!=null && (val.after(max))) {
        		message = "Fecha no puede ser mas tarde que " + max.toLocaleString();
        		return false;        	            
        	}
        } catch (Exception e) {
        	message = "Problem parsing date for the validator";
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
