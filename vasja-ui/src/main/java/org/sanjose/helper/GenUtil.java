package org.sanjose.helper;

//import org.vaadin.ui.NumberField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenUtil {

	public static String getCurYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(new Date());
	}


	public static boolean strNullOrEmpty(String s) {
		if (s == null || "".equals(s))
			return true;
		else
			return false;
	}

	public static boolean objNullOrEmpty(Object s) {
		if (s == null)
			return true;
		else
			return strNullOrEmpty(s.toString());
	}
	
/*
	public static void setDefaultsForNumberField(org.vaadin.ui.NumberField numberField) {
		numberField.setConverter(new BigDecimalConverter());
		numberField.setLocale(ConfigurationUtil.getLocale());
		numberField.setDecimalAllowed(true);
		numberField.setDecimalPrecision(2);                 // maximum 2 digits after the decimal separator
		numberField.setDecimalSeparator(',');               // e.g. 1,5
		numberField.setDecimalSeparatorAlwaysShown(true);   // e.g. 12345 -> 12345,
		numberField.setMinimumFractionDigits(2);            // e.g. 123,4 -> 123,40
		numberField.setGroupingUsed(true);	                 // use grouping (e.g. 12345 -> 12.345)
		numberField.setGroupingSeparator('.');              // use '.' as grouping separator
		numberField.setGroupingSize(3);                     // 3 digits between grouping separators: 12.345.678
		//numberField.setMinValue(0.00);                         // valid values must be >= 0 ...
		//numberField.setMaxValue(999999.99);                     // ... and <= 999.9
		//numberField.setE
		//numberField.setErrorText("Numero invalido!"); // feedback message on bad input
		numberField.setNegativeAllowed(false);              // prevent negative numbers (defaults to true)
	}
*/

	public static void setDefaultsForNumberField(tm.kod.widgets.numberfield.NumberField numberField) {
		numberField.setConverter(new BigDecimalConverter());
		numberField.setLocale(ConfigurationUtil.getLocale());
		numberField.setDecimalLength(2);
        numberField.setUseGrouping(true);
		numberField.setDecimalSeparator(',');               // e.g. 1,5
//		numberField.set
//		numberField.setMinimumFractionDigits(2);            // e.g. 123,4 -> 123,40
//		numberField.setGroupingUsed(true);	                 // use grouping (e.g. 12345 -> 12.345)
		numberField.setGroupingSeparator('.');              // use '.' as grouping separator
//		numberField.setGroupingSize(3);                     // 3 digits between grouping separators: 12.345.678
		//numberField.setMinValue(0.00);                         // valid values must be >= 0 ...
		//numberField.setMaxValue(999999.99);                     // ... and <= 999.9
		//numberField.setE
		//numberField.setErrorText("Numero invalido!"); // feedback message on bad input
		numberField.setSigned(false);
//		numberField.setNegativeAllowed(false);              // prevent negative numbers (defaults to true)
	}
	
}
