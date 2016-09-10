package org.sanjose.helper;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.sanjose.model.VsjPropiedad;
import org.sanjose.model.VsjPropiedadRep;

public class ConfigurationUtil {

	public final static HashMap<String, String> defaultParamMap = new HashMap<String, String>();
	private static HashMap<String, String> paramMap = new HashMap<String, String>();

	private static VsjPropiedadRep propRepo = null;

	public final static Locale LOCALE = new Locale("es", "PE");
	public final static String CSS_RED = "red";

	public static void init() {
		defaultParamMap.put("LOCALE", "es_PE");
		defaultParamMap.put("DECIMAL_FORMAT", "#,##0.00");
		defaultParamMap.put("SHORT_DATE_FORMAT", "MM/dd");
		defaultParamMap.put("DEFAULT_DATE_FORMAT", "yyyy/MM/dd");
		defaultParamMap.put("DEFAULT_DATE_RENDERER_FORMAT","%1$td/%1$tm/%1$ty");
		defaultParamMap.put("COMMON_FIELD_WIDTH", "12em");
		defaultParamMap.put("CSS_STYLE", "iso3166");
		defaultParamMap.put("THEME", "mytheme");
		defaultParamMap.put("DEV_MODE", "1");
		defaultParamMap.put("ALLOW_OVERDRAW", "TRUE");
		defaultParamMap.put("IMPORTS_ENCODING", "UTF-8");
		
		/* Reports */
		defaultParamMap.put("REPORTS_SOURCE_URL", "reports/");
//		defaultParamMap.put("REPORTS_SOURCE_FOLDER", "/d/java/workspaces/workspace_vasja/VasjaEXT/Reports/");
		defaultParamMap.put("REPORTS_SOURCE_FOLDER", "C:\\Users\\ab\\workspace\\VasjaEXT\\Reports\\");
		defaultParamMap.put("REPORTS_IMAGE_SERVLET",
				"../../servlets/image?image=");
		defaultParamMap.put("REPORTS_DIARIO_CAJA_TYPE", "PDF");
		defaultParamMap.put("REPORTS_DIARIO_BANCARIA_TYPE", "PDF");
		defaultParamMap.put("REPORTS_cuenta_TYPE", "PDF");
		defaultParamMap.put("REPORTS_COMPROBANTE_TYPE", "TXT");
		defaultParamMap.put("REPORTS_COMPROBANTE_OPEN", "TRUE");
		defaultParamMap.put("REPORTS_COMPROBANTE_PRINT", "FALSE");
		defaultParamMap.put("REPORTE_CAJA_PREPARADO_POR", "Gilmer G�mez Ochoa");
		defaultParamMap.put("REPORTE_CAJA_REVISADOR_POR", "Claudia Urrunaga R�os");
		defaultParamMap.put("REPORTE_BANCOS_PREPARADO_POR", "Cinthia del Castillo Segovia");
		defaultParamMap.put("REPORTE_BANCOS_REVISADOR_POR", "Claudia Urrunaga R�os");
		defaultParamMap.put("PRINTER_LIST_SHOW", "TRUE");
	}

	public static Locale getLocale() {
	    String locStr = get("LOCALE");
        return new Locale(locStr.substring(0,2), locStr.substring(3,5));
    }

	public static String getDefaultValue(String name) {
		if (defaultParamMap.isEmpty())
			init();
		return defaultParamMap.get(name);
	}

	public static String getProperty(String name) {
		VsjPropiedad prop = propRepo.findByNombre(name);
		if (prop!=null) return prop.getValor();
        else return null;
	}
	
	public static void storeDefaultProperties() {
        if (defaultParamMap.isEmpty())
            init();
		for (String key : defaultParamMap.keySet()) {
			propRepo.save(new VsjPropiedad(key, defaultParamMap.get(key)));
		}		
	}

	public static void resetConfiguration() {
		paramMap.clear();
	}

	public static String get(String name) {
		if (paramMap.containsKey(name))
			return paramMap.get(name);
		else {
			String prop = getProperty(name);
			String param = (prop != null ? prop : getDefaultValue(name));
			paramMap.put(name, param);
			return param;
		}
	}

	public static Boolean is(String name) {
		if (get(name)!=null)
			return (get(name).equalsIgnoreCase("TRUE") || get(name)
				.equalsIgnoreCase("1"));
		else
			return false;
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

	public static void setPropiedadRepo(VsjPropiedadRep repo) {
		propRepo = repo;
	}
}
