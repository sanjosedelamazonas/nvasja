/**
 * 
 */
package org.sanjose.helper;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;

public class DoubleDecimalFormatter extends PropertyFormatter {
    private static final long serialVersionUID = -8487454652016030363L;
    private static final Logger logger = Logger.getLogger(DoubleDecimalFormatter.class.getName());
    
    DecimalFormat df;

    public DoubleDecimalFormatter() {
        super();
    }
    
    public DoubleDecimalFormatter(Property propertyDataSource, String formatString) {
        // Must call the parameterless super-constructor, because otherwise
        // the setPropertyDataSource() is called during this call, which 
        // calls format(), which would be a problem as this class (the 'df'
        // variable) is not initialized yet at this point.
        super();

        df = new DecimalFormat(formatString);
        
        // Must be set after the format is set, as this causes a format() call
        setPropertyDataSource(propertyDataSource);
    }

    @Override
    public Object parse(String formattedValue) throws Exception {
        return df.parse(formattedValue);
    }
    
    @Override
    public String format(Object value) {
        if (value == null) {
            logger.severe("Null value to format");
            return "";
        }
        if (df == null) {
            logger.severe("DecimalFormat not initialized yet in constructor - bug #4484");
            return "";
        }
        
        String result = df.format((Double) value);
        return result;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }
}