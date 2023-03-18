package org.sanjose.util;

import org.sanjose.model.VsjPropiedad;
import org.sanjose.repo.VsjPropiedadRep;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ConfigurationUtil {

	public final static Locale LOCALE = new Locale("es", "PE");
	public final static String CSS_RED = "red";
	private final static HashMap<String, String> defaultParamMap = new HashMap<>();
	private static final HashMap<String, String> paramMap = new HashMap<>();
	private static VsjPropiedadRep propRepo = null;
	private static String OS = null;
	private static Locale locale = null;

	private static void init() {
		defaultParamMap.put("LOCALE", "es_PE");
		defaultParamMap.put("DECIMAL_FORMAT", "#,##0.00");
		defaultParamMap.put("SHORT_DATE_FORMAT", "MM/dd");
		defaultParamMap.put("DEFAULT_DATE_FORMAT", "yyyy/MM/dd");
		defaultParamMap.put("DEFAULT_REPORTS_DATE_FORMAT", "yyyy-MM-dd HH:mm:ss");
		defaultParamMap.put("DEFAULT_DATE_RENDERER_FORMAT","%1$td/%1$tm/%1$ty");
		defaultParamMap.put("DEFAULT_FILTER_WIDTH", "5");
		defaultParamMap.put("COMMON_FIELD_WIDTH", "12em");

		//defaultParamMap.put("CSS_STYLE", "iso3166");
		//defaultParamMap.put("THEME", "mytheme");
		defaultParamMap.put("DEV_MODE", "1");
		defaultParamMap.put("DEV_USER", "");

		defaultParamMap.put("ALLOW_OVERDRAW", "TRUE");
		//defaultParamMap.put("IMPORTS_ENCODING", "UTF-8");

		/* Reports */
		defaultParamMap.put("REPORTS_SOURCE_URL", "reports/");
		defaultParamMap.put("REPORTS_SOURCE_FOLDER","");
		defaultParamMap.put("REPORTS_SOURCE_FOLDER_UNIX", "/pol/dev/nvasja/vasja-reports/reports");
		defaultParamMap.put("REPORTS_SOURCE_FOLDER_WIN", "C:\\vasja_caja2\\vasja-reports\\reports\\");
		defaultParamMap.put("REPORTS_IMAGE_SERVLET",
				"../../servlets/image?image=");
		defaultParamMap.put("REPORTS_DIARIO_CAJA_TYPE", "PDF");
		defaultParamMap.put("REPORTS_DIARIO_BANCARIA_TYPE", "PDF");
		//defaultParamMap.put("REPORTS_cuenta_TYPE", "PDF");
		defaultParamMap.put("REPORTS_COMPROBANTE_TYPE", "PDF");
		defaultParamMap.put("REPORTS_COMPROBANTE_OPEN", "TRUE");
		defaultParamMap.put("REPORTS_COMPROBANTE_PRINT", "FALSE");

		defaultParamMap.put("REPORTE_CAJA_PREPARADO_POR", "Gilmer G�mez Ochoa");
		defaultParamMap.put("REPORTE_CAJA_REVISADOR_POR", "Claudia Urrunaga R�os");
		defaultParamMap.put("REPORTE_BANCOS_PREPARADO_POR", "Cinthia del Castillo Segovia");
		defaultParamMap.put("REPORTE_BANCOS_REVISADOR_POR", "Claudia Urrunaga R�os");
		defaultParamMap.put("PRINTER_LIST_SHOW", "FALSE");
		defaultParamMap.put("DEFAULT_PRINTER_test", "PDF");
		// Terceros
		defaultParamMap.put("REPORTS_TERCEROS_TYPE", "PDF");
		// SENDING EMAILS
		defaultParamMap.put("MAIL_FROM", "p.rubach@cent.uw.edu.pl");
		defaultParamMap.put("MAIL_SMTP_SERVER", "smtp.gmail.com");
		defaultParamMap.put("MAIL_SMTP_SERVER_PORT", "587");
        // OAUTH2, SSL, TLS, NO
		defaultParamMap.put("MAIL_SMTP_AUTH", "OAUTH2");
		defaultParamMap.put("MAIL_SMTP_USER", "p.rubach@cent.uw.edu.pl");
		defaultParamMap.put("MAIL_SMTP_PASS", "ya29.a0AVvZVsrX5egANSShswP95XTqjf_DAL5KR90a71fqhVRvMljdF1Gmz41T2lfz71avVoCKGG8mrHMl9Cizg21bxWHkyqVjYynIbkpUVIy4Z2t9TL2QkrQuYpxK3jfB08igiuFW_3CxiheJ7WWp8M4yA4MnCSVfaCgYKAQwSARASFQGbdwaIc5rsGulbysqS41kqxQwLNA0163");
	}

	public static Locale getLocale() {
        return getLocale(false);
    }

    public static Locale getLocale(boolean refresh) {
		if (locale==null || refresh) {
			String locStr = get("LOCALE");
			locale = new Locale(locStr.substring(0, 2), locStr.substring(3, 5));
		}
		return locale;
	}

	private static String getDefaultValue(String name) {
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

	public static String getOsName() {
		if(OS == null) { OS = System.getProperty("os.name"); }
		return OS;
	}

	public static String getReportsSourceFolder() {

		if (!GenUtil.strNullOrEmpty(get("REPORTS_SOURCE_FOLDER"))) {
			return get("REPORTS_SOURCE_FOLDER").trim() + File.separator;
		}
		if (getOsName().startsWith("Win")) {
			return get("REPORTS_SOURCE_FOLDER_WIN").trim() + File.separator;
		} else {
			return get("REPORTS_SOURCE_FOLDER_UNIX").trim() + File.separator;
		}
	}

	public static Boolean is(String name) {
		return get(name) != null && (get(name).equalsIgnoreCase("TRUE") || get(name).equalsIgnoreCase("1"));
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

	private static Date getTimeOfDay(Date date, String hourMinutes) {
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
