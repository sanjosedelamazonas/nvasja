package org.sanjose.helper;

//import org.vaadin.ui.NumberField;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	public static boolean isNullOrZero(BigDecimal val) {
		if (val==null) return true;
		return (val.compareTo(new BigDecimal(0.00)))==0;
	}


	public static void setDefaultsForNumberField(tm.kod.widgets.numberfield.NumberField numberField) {
		numberField.setConverter(new BigDecimalConverter());
		numberField.setLocale(ConfigurationUtil.getLocale());
		numberField.setDecimalLength(2);
        numberField.setUseGrouping(true);
		numberField.setDecimalSeparator(',');               // e.g. 1,5
		numberField.setNullRepresentation("");
		numberField.setGroupingSeparator('.');              // use '.' as grouping separator
		numberField.setSigned(false);
	}

    public static Date getBeginningOfMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM-ddHH:mm:ss");
        try {
            String d = format.format(date);
            d = d.substring(0, 6);
            d += "-" + "0100:00:00";
            return format.parse(d);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return date;
        }
    }

    public static Date getBeginningOfDay(Date date) {
        return getTimeOfDay(date, "00:00:00");
    }

    public static Date getEndOfDay(Date date) {
        return getTimeOfDay(date, "23:59:59");
    }

    public static Date getTimeOfDay(Date date, String hourMinutes) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        try {
            String d = format.format(date);
            d = d.substring(0, 8);
            d += "-" + hourMinutes;
            return format.parse(d);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return date;
        }
    }

    public static Date dateAddDays(Date d, int days) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        c1.add(Calendar.DAY_OF_YEAR, days);
        return c1.getTime();
    }

    public static Date dateAddSeconds(Date d, int secs) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        c1.add(Calendar.SECOND, secs);
        return c1.getTime();
    }

    public static Date dateAddMonths(Date d, int months) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        c1.add(Calendar.MONTH, months);
        return c1.getTime();
    }
	
}
