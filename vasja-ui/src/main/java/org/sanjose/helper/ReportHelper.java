package org.sanjose.helper;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import org.hibernate.Session;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.bean.Caja;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.VsjBancocabecera;
import org.sanjose.model.VsjCajabanco;
import org.sanjose.model.VsjItem;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.util.DataUtil;
import org.sanjose.util.GenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SpringComponent
@EnableTransactionManagement
public class ReportHelper {

	private static final Logger logger = LoggerFactory.getLogger(ReportHelper.class.getName());
	private static ReportHelper instance;
	@PersistenceContext
    private EntityManager em;
    private Connection sqlConnection = null;

    private ReportHelper() {
          instance = this;
    }

    public static ReportHelper get() {
        if (instance==null)
            instance = new ReportHelper();
        return instance;
    }

	private static String getReportFromItem(VsjItem op) {
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT");
		return (op instanceof VsjCajabanco ? (isTxt ? "ComprobanteTxt" : "Comprobante") : "ComprobanteCheque");
	}

	private static Integer getIdFromItem(VsjItem op) {
		return (op instanceof VsjCajabanco ? ((VsjCajabanco) op).getCodCajabanco()
				: ((VsjBancocabecera) op).getCodBancocabecera());
	}

	@SuppressWarnings({"serial", "unchecked"})
	public static void generateComprobante(final VsjItem op) {
		final boolean isPdf = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("PDF") || op instanceof VsjBancocabecera;
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT") && !(op instanceof VsjBancocabecera);
		final String REPORT = getReportFromItem(op);
		StreamResource.StreamSource source = (StreamResource.StreamSource) () -> {
            byte[] b = null;
            try {
				InputStream rep = loadReport(REPORT);
                if (rep != null) {
                    JasperReport report = (JasperReport) JRLoader
                            .loadObject(rep);
                    report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
                    @SuppressWarnings("rawtypes")
                    HashMap paramMap = new HashMap();
                    paramMap.put("REPORT_LOCALE", ConfigurationUtil.LOCALE);
					paramMap.put("OP_ID", getIdFromItem(op));
					if (isPdf)
                        b = JasperRunManager.runReportToPdf(report,
                                paramMap, get().getSqlConnection());
                    else if (isTxt) {
                        return exportToTxt(REPORT, paramMap);
                    } else {
                        return exportToHtml(REPORT, paramMap);
                    }
                } else {
                    Notification.show("There is no report file!");
                }
            } catch (JRException ex) {
                logger.error("Error generating report", ex);

            }
            return new ByteArrayInputStream(b != null ? b : new byte[0]);
        };
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		StreamResource resource = new StreamResource(source,
				(GenUtil.isIngreso(op) ? "Ingreso_" : "Egreso_")
						+ op.getTxtAnoproceso() + "_" + op.getCodMes() + "_" + getIdFromItem(op) + "_"
						+ df.format(new Date(System.currentTimeMillis()))
						+ (isPdf ? ".pdf" : (isTxt ? ".txt" : ".html")));
		resource.setMIMEType((isPdf ? "application/pdf" : (isTxt ? "text/plain" : "text/html")));

		logger.info("Resource: " + resource.getFilename() + " "
				+ resource.getMIMEType());

		Embedded emb = new Embedded();
		emb.setSizeFull();
		emb.setType(Embedded.TYPE_BROWSER);
		emb.setSource(resource);

		Window repWindow = new Window();
		repWindow.setWindowMode(WindowMode.NORMAL);
		repWindow.setWidth(700, Sizeable.Unit.PIXELS);
		repWindow.setHeight(600, Sizeable.Unit.PIXELS);
		repWindow.setPositionX(200);
		repWindow.setPositionY(50);
		repWindow.setModal(false);
        repWindow.setContent(emb);
		UI.getCurrent().addWindow(repWindow);
        //Set<Window> windows = repWindow.getChildWindows();
        JavaScript.getCurrent().execute("window.onload = function() { window.print(); } ");
        //repWindow.ex
        /*for (Window win : windows) {
			logger.info("URL: " + win.getURL());
			win.executeJavaScript("window.onload = function() { window.print(); } ");
		}*/

	}

	@SuppressWarnings({ "serial", "unchecked" })
	public static JasperPrint printComprobante(final VsjItem op) {
		final String REPORT = getReportFromItem(op);
			try {
					InputStream rep = loadReport(REPORT);
					if (rep != null) {
						JasperReport report = (JasperReport) JRLoader
								.loadObject(rep);
						report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
						@SuppressWarnings("rawtypes")
						HashMap paramMap = new HashMap();
						paramMap.put("REPORT_LOCALE", ConfigurationUtil.getLocale());
						paramMap.put("OP_ID", getIdFromItem(op));
						return prepareToPrint(REPORT, paramMap);
					} else {
						logger.warn("There is no report file!");
					}
				} catch (JRException ex) {
					logger.error("Error generating report", ex);
				}
			return null;
	}

	public static void generateDiarioCaja(Date fechaMin, Date fechaMax, String format) {
		if (fechaMax==null) fechaMax = fechaMin;
		generateDiario("ReporteCajaDiario",
			ConfigurationUtil.getBeginningOfDay(fechaMin),
				ConfigurationUtil.getEndOfDay(fechaMax),
				format,
				ConfigurationUtil.get("REPORTE_CAJA_REVISADOR_POR"));
	}

	public static void generateDiario(String reportName, final Date fechaMin, final Date fechaMax,
			String format, String revisado) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		HashMap paramMap = new HashMap();
		paramMap.put("REPORT_LOCALE", ConfigurationUtil.getLocale());
		List<Caja> cajas = DataUtil.getCajasList(MainUI.get().getComprobanteView().getService().getPlanRepo(), fechaMin, fechaMax);
		paramMap.put("SALDOS_INICIAL", cajas);
		logger.info("sendin to diario INICIAL: " + cajas);
		paramMap.put("DIARIO_FECHA_MIN", fechaMin);
		paramMap.put("DIARIO_FECHA_MAX", fechaMax);
		paramMap.put("DIARIO_ISPEN", true);
		paramMap.put("SUBREPORT_DIR", ConfigurationUtil.getReportsSourceFolder());
		paramMap.put("STR_FECHA_MIN", sdf.format(ConfigurationUtil.getBeginningOfDay(fechaMin)));
		if (fechaMax!=null) paramMap.put("STR_FECHA_MAX", sdf.format(ConfigurationUtil.getEndOfDay(fechaMax)));
		logger.info("STR_FECHA_MIN=" + paramMap.get("STR_FECHA_MIN"));
		logger.info("STR_FECHA_MAX=" + paramMap.get("STR_FECHA_MAX"));
		MsgUsuario usuario = MainUI.get().getMsgUsuarioRep().findByTxtUsuario(CurrentUser.get());
		paramMap.put("REPORTE_PREPARADO_POR", usuario.getTxtNombre());
		paramMap.put("REPORTE_REVISADOR_POR", revisado);
		//logger.info("ParamMap: " + paramMap.toString());
		generateReport(reportName, "REPORTS_DIARIO_CAJA_TYPE", paramMap, format);
	}

	public static void generateDiarioBanco(Character moneda, final Date fechaMin, final Date fechaMax,
									  String format) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");
		HashMap paramMap = new HashMap();
		paramMap.put("REPORT_LOCALE", ConfigurationUtil.getLocale());
		List<Caja> cajas = DataUtil.getCajasList(MainUI.get().getComprobanteView().getService().getPlanRepo(), fechaMin, fechaMax);
		paramMap.put("MONEDA", moneda.toString());
		paramMap.put("ANO", sdfAno.format(fechaMin));
		paramMap.put("SALDOS_INICIAL", cajas);
		paramMap.put("DIARIO_FECHA_MIN", fechaMin);
		paramMap.put("DIARIO_FECHA_MAX", fechaMax);
		paramMap.put("DIARIO_ISPEN", true);
		paramMap.put("SUBREPORT_DIR", ConfigurationUtil.getReportsSourceFolder());
		paramMap.put("STR_FECHA_MIN", sdf.format(ConfigurationUtil.getBeginningOfDay(fechaMin)));
		if (fechaMax!=null) paramMap.put("STR_FECHA_MAX", sdf.format(ConfigurationUtil.getEndOfDay(fechaMax)));
		logger.info("STR_FECHA_MIN=" + paramMap.get("STR_FECHA_MIN"));
		logger.info("STR_FECHA_MAX=" + paramMap.get("STR_FECHA_MAX"));
		MsgUsuario usuario = MainUI.get().getMsgUsuarioRep().findByTxtUsuario(CurrentUser.get());
		paramMap.put("REPORTE_PREPARADO_POR", usuario.getTxtNombre());
		paramMap.put("REPORTE_REVISADOR_POR", ConfigurationUtil.get("REPORTE_CAJA_REVISADOR_POR"));
		//logger.info("ParamMap: " + paramMap.toString());
		generateReport("ReporteDeBanco", "REPORTS_DIARIO_CAJA_TYPE", paramMap, format);
	}

	public static void generateLugarGasto(final Date fechaMin, final Date fechaMax,
										  Window window, String format, String type) {

		String reportName = "ReporteLugarYFuenteDeFinancDetallado";

		switch (type) {
			case "Detallado":
				reportName = "ReporteLugarYFuenteDeFinancDetallado";
				break;
			case "Sin lugar":
				reportName = "ReporteLugarYFuenteDeFinancNoTienen";
				break;
			case "Informe":
				reportName = "ReporteLugarYFuenteDeFinancInforme";
				break;
			case "DetalladoCuentaContable":
				reportName = "ReporteDetalladoCuentaContable";
				break;
			case "Detallado por rubro institucional":
				reportName = "ReporteDetalladoRubroInstitucional";
				break;
			case "Informe resumido por cuenta contable":
				reportName = "ReporteResumidoCuentaContable";
				break;
			case "Informe resumido por rubro institucional":
				reportName = "ReporteResumidoRubroInstitucional";
				break;
		}

		generateLG(fechaMin, fechaMax, window, format, reportName);
	}

	@SuppressWarnings("unchecked")
	public static void generateCC(final Date minfecha, final Date maxfecha, boolean isPen, Window window, String format, String grouping) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		logger.info("jestem w generateRep. params: " + minfecha + " maxfecha " + maxfecha + " isPen " + isPen);
		String reportName;
		HashMap paramMap = new HashMap();
		paramMap.put("REPORT_LOCALE", ConfigurationUtil.LOCALE);
		paramMap.put("in_isPen", isPen);
		paramMap.put("str_fecha_min", sdf.format(ConfigurationUtil.getBeginningOfDay(minfecha)));
		paramMap.put("SUBREPORT_DIR", ConfigurationUtil.getReportsSourceFolder());
		if (maxfecha != null) paramMap.put("str_fecha_max", sdf.format(ConfigurationUtil.getEndOfDay(maxfecha)));
		if (grouping == null || grouping.equals("NO GROUPING")) reportName = "ReporteCentroDeCostosNoGrouping";
		else if (grouping.equals("POR CATEGORIA")) reportName = "ReporteCentroDeCostosPorCategoria";
		else reportName = "ReporteCentroDeCostosPorCuenta";
		logger.info("ParamMap: " + paramMap.toString());
		generateReport(reportName, "REPORTS_DIARIO_CAJA_TYPE", paramMap, format);
//		generateReport("ReporteCentroDeCostosStructure", "REPORTS_DIARIO_CAJA_TYPE", paramMap, window, format);
	}

	@SuppressWarnings("unchecked")
	private static void generateLG(final Date minfecha, final Date maxfecha, Window window, String format, String reportName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		logger.info("jestem w generateLG. params: "+minfecha+" maxfecha "+maxfecha);
		HashMap paramMap = new HashMap();
		paramMap.put("REPORT_LOCALE", ConfigurationUtil.LOCALE);
		paramMap.put("str_fecha_min", sdf.format(ConfigurationUtil.getBeginningOfDay(minfecha)));
		paramMap.put("SUBREPORT_DIR", ConfigurationUtil.getReportsSourceFolder());
		if (maxfecha != null)
			paramMap.put("str_fecha_max", sdf.format(ConfigurationUtil.getEndOfDay(maxfecha)));
		else
			paramMap.put("str_fecha_max", sdf.format(ConfigurationUtil.getEndOfDay(minfecha)));
		logger.info("ParamMap: " + paramMap.toString());
		generateReport(reportName, "REPORTS_DIARIO_CAJA_TYPE", paramMap, format);
//		generateReport("ReporteCentroDeCostosStructure", "REPORTS_DIARIO_CAJA_TYPE", paramMap, window, format);
	}

	@SuppressWarnings({ "serial", "unchecked" })
	public static JasperPrint printDiario(final Date fechaMin, final Date fechaMax,
			boolean isPen) {
		final String REPORT = "ReporteDiario" + (isPen ? "PEN" : "USD");
			try {
					InputStream rep = loadReport(REPORT);
					if (rep != null) {
						JasperReport report = (JasperReport) JRLoader
								.loadObject(rep);
						report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
						@SuppressWarnings("rawtypes")
						HashMap paramMap = new HashMap();
						paramMap.put("REPORT_LOCALE", ConfigurationUtil.LOCALE);
						paramMap.put("DIARIO_FECHA_MIN", fechaMin);
						if (fechaMax != null)
							paramMap.put("DIARIO_FECHA_MAX", fechaMax);
						return prepareToPrint(REPORT, paramMap);
					} else {
                        Notification.show("There is no report file: " + REPORT);
						logger.warn("There is no report file!");
					}
				} catch (JRException ex) {
					logger.error("Serious error generating report printDiario", ex);
				}
			return null;
	}

	@SuppressWarnings("unchecked")
	private static void generateReport(final String reportName, String typeParamName,
									   final HashMap paramMap, final String inFormat) {
		final String format;
		if (inFormat==null) format = ConfigurationUtil.get(typeParamName);
		else format = inFormat;
		StreamResource.StreamSource source = (StreamResource.StreamSource) () -> {
            byte[] b = null;
            try {
                InputStream rep = loadReport(reportName);
                if (rep != null) {
                    JasperReport report = (JasperReport) JRLoader
                            .loadObject(rep);
                    report.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
                    if (format.equalsIgnoreCase("pdf"))
                        b = JasperRunManager.runReportToPdf(report,
                                paramMap, get().getSqlConnection());
                    else if (format.equalsIgnoreCase("html"))
                        return exportToHtml(reportName, paramMap);
                    else
                        return exportToXls(reportName, paramMap);
                } else {
                    Notification.show(
                            "There is no report file: "  + reportName);
                }
            } catch (JRException ex) {
                logger.error(ex.getMessage());
                ex.printStackTrace();
            }
            return new ByteArrayInputStream(b != null ? b : new byte[0]);
        };
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		StreamResource resource = new StreamResource(source, reportName + "_"
				+ df.format(new Date()) + "." + format.toLowerCase());
		if (format.equalsIgnoreCase("PDF"))
			resource.setMIMEType("application/pdf");
		else if (format.equalsIgnoreCase("XLS"))
			resource.setMIMEType("application/xls");
		else
			resource.setMIMEType("text/html");

		Embedded emb = new Embedded();
		emb.setSizeFull();
		emb.setType(Embedded.TYPE_BROWSER);
		emb.setSource(resource);

		Window repWindow = new Window();
		repWindow.setWindowMode(WindowMode.NORMAL);
		repWindow.setWidth(700, Sizeable.Unit.PIXELS);
		repWindow.setHeight(600, Sizeable.Unit.PIXELS);
		repWindow.setPositionX(200);
		repWindow.setPositionY(50);
		repWindow.setModal(false);
        repWindow.setContent(emb);
		UI.getCurrent().addWindow(repWindow);
	}

	private static InputStream loadReport(String reportName) {
		InputStream rep = null;
        logger.info("Trying to load report " + reportName);
		rep = (UI.getCurrent().getClass()).getResourceAsStream(ConfigurationUtil
					.get("REPORTS_SOURCE_URL").trim() + "/" + reportName + ".jasper");
		if (rep!=null) return rep;

		if (System.getenv("VASJA_HOME")!=null) {
			try {
				logger.info("Trying to load from VASJA_HOME/reports");
				rep = new FileInputStream(
						System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
								+ reportName + ".jasper");
			} catch (FileNotFoundException e) {
				Notification.show("Report file not found under: " + System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
						+ reportName + ".jasper");
			}
		}

		try {
			logger.info("Reports folder: " + ConfigurationUtil.getReportsSourceFolder());

			rep = new FileInputStream(
					ConfigurationUtil.getReportsSourceFolder() + reportName + ".jasper");
		} catch (FileNotFoundException e) {
			Notification.show("Report file not found!");
		}
		return rep;
	}

	@SuppressWarnings("rawtypes")
	private static ByteArrayInputStream exportToHtml(String reportName,
			HashMap paramMap) throws JRException {
		@SuppressWarnings("unchecked")
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				ConfigurationUtil.getReportsSourceFolder() + reportName
						+ ".jasper", paramMap, get().getSqlConnection());
		JRHtmlExporter htmlExporter = new JRHtmlExporter();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();

		htmlExporter
				.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		htmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, oStream);

		htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
				ConfigurationUtil.get("REPORTS_IMAGE_SERVLET"));
		htmlExporter.exportReport();

		return new ByteArrayInputStream(oStream.toByteArray());
	}

	@SuppressWarnings("rawtypes")
	private static ByteArrayInputStream exportToXls(String reportName,
			HashMap paramMap) throws JRException {
		@SuppressWarnings("unchecked")
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				ConfigurationUtil.getReportsSourceFolder() + reportName
						+ ".jasper", paramMap, get().getSqlConnection());
		JRXlsExporter xlsExporter = new JRXlsExporter();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();

		xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, oStream);
		xlsExporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
		xlsExporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, oStream);
		xlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
		xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		xlsExporter.exportReport();

		return new ByteArrayInputStream(oStream.toByteArray());
	}

	@SuppressWarnings("rawtypes")
	private static ByteArrayInputStream exportToTxt(String reportName,
			HashMap paramMap) throws JRException {
		@SuppressWarnings("unchecked")
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				ConfigurationUtil.getReportsSourceFolder() + reportName
						+ ".jasper", paramMap, get().getSqlConnection());
		JRTextExporter txtExporter = new JRTextExporter();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();

		txtExporter
				.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		txtExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, oStream);
		txtExporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");

		txtExporter.exportReport();

		return new ByteArrayInputStream(oStream.toByteArray());
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static JasperPrint prepareToPrint(String reportName,
			HashMap paramMap) throws JRException {
		logger.info("REPORT: " + ConfigurationUtil.getReportsSourceFolder() + reportName
				+ ".jasper" + "\n" + paramMap.toString());
		return JasperFillManager.fillReport(
				ConfigurationUtil.getReportsSourceFolder() + reportName
						+ ".jasper", paramMap, get().getSqlConnection());
	}

	@Autowired
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

    @Transactional
	private Connection getSqlConnection() {
		try {
			if (sqlConnection != null && sqlConnection.isValid(100))
				return sqlConnection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        em=em.getEntityManagerFactory().createEntityManager();
        logger.info("Got entity manager: " + em.getProperties().toString());
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            logger.info("setting connection: " + connection);
            sqlConnection = connection;
        });
		return sqlConnection;
	}
}
