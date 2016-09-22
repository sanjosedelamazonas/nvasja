package org.sanjose.validator;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.vaadin.data.Validator;
import org.sanjose.util.ConfigurationUtil;

@SuppressWarnings("serial")
public class BigDecimalValidator implements Validator {

	    private final String message;

	    public BigDecimalValidator(String message) {
	        this.message = message;
	    }

	    public boolean isValid(Object value) {
	        if (value == null || !(value instanceof String)) {
	            return false;
	        }
	        try {
	        	String string = (String)value;
	        	string = string.replace(",", ".");
				DecimalFormat fmt = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"));
				fmt.setParseBigDecimal(true);
				@SuppressWarnings("unused")
				BigDecimal nbr = (BigDecimal) fmt.parse(string);				
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
