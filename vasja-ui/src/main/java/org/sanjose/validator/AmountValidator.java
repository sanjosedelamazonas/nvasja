package org.sanjose.validator;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.vaadin.data.Validator;
import org.sanjose.util.ConfigurationUtil;

public class AmountValidator implements Validator {

			private static final long serialVersionUID = -9007565688398484768L;

			private String message;
		    
		    private BigDecimal minValue;
		    
		    private BigDecimal maxValue;

		    public AmountValidator(String message, BigDecimal minValue, BigDecimal maxValue) {
		        this.message = message;
		        this.minValue = minValue;
		        this.maxValue = maxValue;
		    }

		    public boolean isValid(Object value) {
		        if (value == null || !(value instanceof String)) {
		            return false;
		        }
		        try {
		        	String string = (String)value;
					DecimalFormat fmt = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"));
					fmt.setParseBigDecimal(true);
					BigDecimal nbr = (BigDecimal) fmt.parse(string);
					if (minValue!=null && nbr.compareTo(minValue)<0) return false;
					if (maxValue!=null && nbr.compareTo(maxValue)>0) return false;
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
