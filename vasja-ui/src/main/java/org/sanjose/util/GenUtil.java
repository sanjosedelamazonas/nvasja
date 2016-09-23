package org.sanjose.util;

//import org.vaadin.ui.NumberField;

import org.sanjose.model.VsjCajabanco;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenUtil {

    private static Map<String, String> symMoneda = new HashMap<>();

    public static boolean strNullOrEmpty(String s) {
        return s == null || "".equals(s) || "".equals(s.trim());
	}

	public static boolean objNullOrEmpty(Object s) {
        return s == null || strNullOrEmpty(s.toString());
    }

	public static boolean isNullOrZero(BigDecimal val) {
        return val == null || (val.compareTo(new BigDecimal(0.00))) == 0;
    }

	public static boolean isIngreso(VsjCajabanco vcb) {
        return vcb.getNumDebesol().compareTo(new BigDecimal(0))>0
                || vcb.getNumDebedolar().compareTo(new BigDecimal(0))>0;
    }

    public static String getTxtCorrelativo(Integer id) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<8-id.toString().length();i++) {
            sb.append("0");
        }
        sb.append(id.toString());
        return sb.toString();
    }

	/* Date and time utils */

    public static String getCurYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());
    }

    public static String getYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
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


    public static boolean isInvertedZero(Object value) {
        return value == null || !"0.00".equals(value.toString()) && !"0,00".equals(value.toString()) && !"0".equals(value.toString());
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-","").substring(0,16);
    }

    public static String getLitMoneda(String numMoneda) {
        switch (numMoneda) {
            case "0" :
                return "N";
            case "1" :
                return "D";
            default:
                return "E";
        }
    }

    public static String getNumMoneda(String litMoneda) {
        switch (litMoneda) {
            case "N" :
                return "0";
            case "D" :
                return "1";
            default:
                return "2";
        }
    }

    public static String getSymMoneda(String litMoneda) {
        if (symMoneda.isEmpty()) {
            symMoneda.put("N", "S/.");
            symMoneda.put("D", "$");
            symMoneda.put("N", "€");
        }
        return symMoneda.get(litMoneda);
    }
}
