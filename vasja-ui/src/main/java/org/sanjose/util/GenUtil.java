package org.sanjose.util;

//import org.vaadin.ui.NumberField;

import com.vaadin.data.Validator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.VsjBancoItem;
import org.sanjose.model.VsjItem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenUtil {

    public final static Character PEN='0';

    public final static Character USD='1';

    public static final Character EUR='2';

    public static final Character _PEN='N';

    public static final Character _USD='D';

    public static final Character _EUR='E';

    public static final String T_PROY="VASJA/Proyectos";

    public static final String T_TERC="Terceros";

    private static Map<Character, String> symMoneda = new HashMap<>();

    public static boolean strNullOrEmpty(String s) {
        return s == null || "".equals(s) || "".equals(s.trim());
	}

	public static boolean objNullOrEmpty(Object s) {
        return s == null || strNullOrEmpty(s.toString());
    }

	public static boolean isNullOrZero(BigDecimal val) {
        return val == null || (val.compareTo(new BigDecimal(0.00))) == 0;
    }

    public static boolean isIngreso(VsjItem vcb) {
        if (vcb instanceof ScpCajabanco)
            return ((ScpCajabanco) vcb).getNumDebesol().compareTo(new BigDecimal(0)) > 0
                    || ((ScpCajabanco) vcb).getNumDebedolar().compareTo(new BigDecimal(0)) > 0;
        if (vcb instanceof VsjBancoItem)
            return ((VsjBancoItem) vcb).getNumDebesol().compareTo(new BigDecimal(0)) > 0
                    || ((VsjBancoItem) vcb).getNumDebedolar().compareTo(new BigDecimal(0)) > 0
                    || ((VsjBancoItem) vcb).getNumDebemo().compareTo(new BigDecimal(0)) > 0;
        return false;
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

    public static Date getBegin20thCent() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse("1900-01-02");
        } catch (ParseException pe) {
            System.out.println("Problem parsing date in getBeginning20thCentury");
        }
        return new Date();
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

    public static Character getLitMoneda(Character numMoneda) {
        switch (numMoneda) {
            case '0' :
                return 'N';
            case '1' :
                return 'D';
            case '2':
                return 'E';
            default:
                Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
                Thread.dumpStack();
                return 'U';
        }
    }

    public static Character getNumMoneda(Character litMoneda) {
        switch (litMoneda) {
            case 'N' :
                return '0';
            case 'D' :
                return '1';
            case 'E':
                return '2';
            default:
                Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
                Thread.dumpStack();
                return '9';
        }
    }

    public static String getSymMoneda(Character litMoneda) {
        if (symMoneda.isEmpty()) {
            symMoneda.put('N', "S/.");
            symMoneda.put('D', "$");
            symMoneda.put('E', "€");
        }
        return symMoneda.get(litMoneda);
    }

    public static boolean verifyLitMoneda(Character moneda) {
        return !getNumMoneda(moneda).equals('9');
    }

    public static boolean verifyNumMoneda(Character moneda) {
        return !getLitMoneda(moneda).equals('U');
    }

    public static String validIsNull(String in) {
        if (in==null || in.equalsIgnoreCase("null"))
            return "no puede estar vacío";
        else
            return in;
    }

    public static String genErrorMessage(Map<Field<?>, Validator.InvalidValueException> fieldMap) {
        StringBuilder sb = new StringBuilder();
        for (Field f : fieldMap.keySet()) {
            if (f instanceof ComboBox) {
                sb.append(((ComboBox) f).getInputPrompt()).append(" - ").append(GenUtil.validIsNull(fieldMap.get(f).getMessage())).append("\n");
            } else {
                sb.append(f.getDescription() != "" ? f.getDescription() : "Campo desconocido").append(" - ").append(GenUtil.validIsNull(fieldMap.get(f).getMessage())).append("\n");
            }
        }
        return sb.toString();
    }

}
