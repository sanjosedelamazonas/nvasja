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
import java.math.RoundingMode;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

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

    private static Map<Character, Character> monedaLitNum = new HashMap<>();

    private static Map<Character, Character> monedaNumLit = new HashMap<>();

    private static Map<Character, String> monedaNumDesc = new HashMap<>();

    private static Map<String, Character> monedaDescNum = new HashMap<>();


    static {
        monedaLitNum.put('N', '0');
        monedaLitNum.put('D', '1');
        monedaLitNum.put('E', '2');

        monedaNumLit = monedaLitNum.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        monedaNumDesc.put('0', "sol");
        monedaNumDesc.put('1', "dolar");
        monedaNumDesc.put('2', "mo");

        monedaDescNum = monedaNumDesc.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        symMoneda.put('N', "S/.");
        symMoneda.put('D', "$");
        symMoneda.put('E', "€");
    }

    public static boolean strNullOrEmpty(String s) {
        return s == null || "".equals(s) || "".equals(s.trim());
	}

	public static boolean objNullOrEmpty(Object s) {
        return s == null || strNullOrEmpty(s.toString());
    }

	public static boolean isNullOrZero(BigDecimal val) {
        return val == null || (val.compareTo(new BigDecimal(0.00))) == 0;
    }

    public static boolean isZero(BigDecimal val) {
        return (val.compareTo(new BigDecimal(0.00))) == 0;
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

    public static String getCodComprobante(Integer id) {
        return getTxtCorrelativoLen(id, 6);
    }

    public static String getTxtCorrelativo(Integer id) {
        return getTxtCorrelativoLen(id, 8);
    }

    public static String getTxtCorrelativoLen(Integer id, int maxLen) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<maxLen-id.toString().length();i++) {
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
            return sdf.parse("1900-01-01");
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

    public static Date getEndOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM-ddHH:mm:ss");
        try {
            String d = format.format(date);
            d = d.substring(0, 6);
            d += "-" + getTxtCorrelativoLen(lastDay, 2) + "23:59:59";
            return format.parse(d);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return date;
        }
    }

    public static Date getBeginningOfDay(Date date) {
        return getTimeOfDay(date, "00:00:00");
    }

    public static Date getBeginningOfWorkingDay(Date date) {
        return getTimeOfDay(date, "08:00:00");
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

    public static List getDatesBetween(Date startDate, Date endDate) {
        List datesInRange = new ArrayList<>();
        Calendar calendar = getCalendarWithoutTime(startDate);
        Calendar endCalendar = getCalendarWithoutTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return datesInRange;
    }

    private static Calendar getCalendarWithoutTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String capitalizeEachWord(String message) {
        char[] charArray = message.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }
            else {
                foundSpace = true;
            }
        }
        message = String.valueOf(charArray);
        return message;
    }

    public static boolean isInvertedZero(Object value) {
        return value == null || !"0.00".equals(value.toString()) && !"0,00".equals(value.toString()) && !"0".equals(value.toString());
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-","").substring(0,16);
    }

    public static Character getLitMoneda(Character numMoneda) {
        if (!monedaNumLit.containsKey(numMoneda)) {
            Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
            Thread.dumpStack();
            return 'U';
        };
        return monedaNumLit.get(numMoneda);
    }

    public static Character getNumMoneda(Character litMoneda) {
        if (!monedaLitNum.containsKey(litMoneda)) {
            Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
            Thread.dumpStack();
            return '9';
        };
        return monedaLitNum.get(litMoneda);
    }

    public static String getDescMoneda(Character numMoneda) {
        if (!monedaNumDesc.containsKey(numMoneda)) {
            Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
            Thread.dumpStack();
            return "UNKNOWN";
        };
        return monedaNumDesc.get(numMoneda);
    }

    public static Character getNumMonedaFromDescContaining(String propertyName) {
        for (String key : monedaDescNum.keySet()) {
            if (propertyName.contains(key))
                return monedaDescNum.get(key);
        }
        Notification.show("Moneda no es PEN, USD o EUR", Notification.Type.ERROR_MESSAGE);
        Thread.dumpStack();
        return '9';
    }

    public static String getSymMoneda(Character litMoneda) {
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
            return "esta vacío o demasiado largo";
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

    public static String numFormat(BigDecimal bd) {
        DecimalFormat df = new DecimalFormat(ConfigurationUtil.get("DECIMAL_FORMAT"), DecimalFormatSymbols.getInstance());
        return df.format(bd);
    }


    public static BigDecimal parseNumber(String num) {
        try {
            Number n = NumberFormat.getNumberInstance(ConfigurationUtil.getLocale()).parse(num);
            BigDecimal newNum = new BigDecimal(n.doubleValue());
            return newNum.setScale(2, RoundingMode.HALF_EVEN);
        } catch (ParseException pe) {
            Notification.show("Problem parsing number: " + num, Notification.Type.ERROR_MESSAGE);
            Thread.dumpStack();
        }
        return new BigDecimal(0);
    }
}
