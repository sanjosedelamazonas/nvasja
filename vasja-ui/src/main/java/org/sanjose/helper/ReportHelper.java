package org.sanjose.helper;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import de.steinwedel.messagebox.MessageBox;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import org.hibernate.Session;
import org.sanjose.MainUI;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.bean.Caja;
import org.sanjose.model.MsgUsuario;
import org.sanjose.model.ScpCajabanco;
import org.sanjose.model.ScpBancocabecera;
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
import java.util.*;

@SpringComponent
@EnableTransactionManagement
public class ReportHelper {

	private static final Logger logger = LoggerFactory.getLogger(ReportHelper.class.getName());
	private static ReportHelper instance;
	@PersistenceContext
    private EntityManager em;
    private Connection sqlConnection = null;
    private static final String CUSTOM_REPORT_DESC_FILE = "customReports.txt";

    private List<CustomReport> customReports = new ArrayList<>();

	public List<CustomReport> getCustomReports() {
		return customReports;
	}

	private ReportHelper() {
          instance = this;
    }

    public static ReportHelper get() {
        if (instance==null)
            instance = new ReportHelper();
        return instance;
    }

	private FileInputStream getCustomReportsFile() {
		FileInputStream custRepFile = null;
		if (System.getenv("VASJA_HOME")!=null) {
			logger.debug("Trying to load from VASJA_HOME/reports");
			try {
				custRepFile = new FileInputStream(
						System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
								+ CUSTOM_REPORT_DESC_FILE);
				return custRepFile;
			} catch (FileNotFoundException fe) {
				Notification.show("Custom report description file not found under: " + System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
						+ CUSTOM_REPORT_DESC_FILE);
			}
		}
		logger.debug("Reports folder: " + ConfigurationUtil.getReportsSourceFolder());
		try {
			custRepFile = new FileInputStream(
				ConfigurationUtil.getReportsSourceFolder() + CUSTOM_REPORT_DESC_FILE);
			return custRepFile;
		} catch (FileNotFoundException fe) {
			Notification.show("Custom report description file not found under: " + ConfigurationUtil.getReportsSourceFolder() + CUSTOM_REPORT_DESC_FILE);
		}
		return null;
	}

	public void loadCustomReports() {
		Properties properties = new Properties();
		FileInputStream custRepFile = getCustomReportsFile();
		if (custRepFile==null) return;
		try {
			properties.load(custRepFile);
		} catch (IOException e) {
			Notification.show("Problem reading Custom Reports Desc File");
		}
		for (String repName : properties.stringPropertyNames()) {
			String repConf = (String)properties.get(repName);
			String[] repParams = repConf.split(";");
			customReports.add(new CustomReport(
					repName,
					repParams[0],
					isSetCustomReportParam(repParams[1]),
					isSetCustomReportParam(repParams[2]),
					isSetCustomReportParam(repParams[3])
			));
		}
	}

	private boolean isSetCustomReportParam(String param) {
    	if (param==null || param.length()==0) return false;
    	if (param.equals("1")) return true;
    	else return false;
	}

	private static String getReportFromItem(VsjItem op) {
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT");
		return (op instanceof ScpCajabanco ? (isTxt ? "ComprobanteTxt" : "Comprobante") : "ComprobanteCheque");
	}

	private static Integer getIdFromItem(VsjItem op) {
		return (op instanceof ScpCajabanco ? ((ScpCajabanco) op).getCodCajabanco()
				: ((ScpBancocabecera) op).getCodBancocabecera());
	}

	@SuppressWarnings({"serial", "unchecked"})
	public static void generateComprobante(final VsjItem op) {
		// Skip when nothing selected
		if (op==null) return;
		final boolean isPdf = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("PDF") || op instanceof ScpBancocabecera;
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT") && !(op instanceof ScpBancocabecera);
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

		logger.debug("Resource: " + resource.getFilename() + " "
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
        repWindow.setDraggable(true);
		UI.getCurrent().addWindow(repWindow);
        //JavaScript.getCurrent().execute("window.onload = function() { window.print(); } ");
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

	public static void generateCustomReport(String reportName, String codProyecto, String codTercero, String codCategoriaproy, final Date fechaMin, final Date fechaMax) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		HashMap paramMap = new HashMap();
		paramMap.put("REPORT_LOCALE", ConfigurationUtil.getLocale());
		paramMap.put("COD_PROYECTO", codProyecto!=null ? codProyecto : "");
		paramMap.put("COD_TERCERO", codTercero!=null ? codTercero : "");
		paramMap.put("COD_CATEGORIA_PROYECTO", codCategoriaproy!=null ? codCategoriaproy : "");
		paramMap.put("FECHA_MIN", fechaMin!=null ? ConfigurationUtil.getBeginningOfDay(fechaMin) : "");
		paramMap.put("FECHA_MAX", fechaMax!=null ? ConfigurationUtil.getEndOfDay(fechaMax) : "");
		paramMap.put("STR_FECHA_MIN", fechaMin!=null ? sdf.format(ConfigurationUtil.getBeginningOfDay(fechaMin)) : "");
		if (fechaMax!=null) paramMap.put("STR_FECHA_MAX", sdf.format(ConfigurationUtil.getEndOfDay(fechaMax)));
		logger.debug("STR_FECHA_MIN=" + paramMap.get("STR_FECHA_MIN"));
		logger.debug("STR_FECHA_MAX=" + paramMap.get("STR_FECHA_MAX"));
		logger.info("Generating Custom Report: " + reportName);
		logger.info("ParamMap: " + paramMap.toString());
		generateReport(reportName, "REPORTS_CUSTOM_TYPE", paramMap, "pdf");
	}

	public static void generateDiarioCaja(Date fechaMin, Date fechaMax, String format) {
		if (fechaMin==null) {
			MessageBox.setDialogDefaultLanguage(ConfigurationUtil.getLocale());
			MessageBox
					.createQuestion()
					.withMessage("Por favor rellena la fecha del inicio")
					.open();
			return;
		}
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
		logger.debug("sendin to diario INICIAL: " + cajas);
		paramMap.put("DIARIO_FECHA_MIN", fechaMin);
		paramMap.put("DIARIO_FECHA_MAX", fechaMax);
		paramMap.put("DIARIO_ISPEN", true);
		paramMap.put("SUBREPORT_DIR", ConfigurationUtil.getReportsSourceFolder());
		paramMap.put("STR_FECHA_MIN", sdf.format(ConfigurationUtil.getBeginningOfDay(fechaMin)));
		if (fechaMax!=null) paramMap.put("STR_FECHA_MAX", sdf.format(ConfigurationUtil.getEndOfDay(fechaMax)));
		logger.debug("STR_FECHA_MIN=" + paramMap.get("STR_FECHA_MIN"));
		logger.debug("STR_FECHA_MAX=" + paramMap.get("STR_FECHA_MAX"));

		sdf = new SimpleDateFormat("yyyy-MM-dd");
		paramMap.put("STR_SALDO_FECHA_MIN", sdf.format(ConfigurationUtil.getBeginningOfDay(fechaMin)));
		if (fechaMax!=null) paramMap.put("STR_SALDO_FECHA_MAX", sdf.format(ConfigurationUtil.getEndOfDay(fechaMax)));

		MsgUsuario usuario = MainUI.get().getMsgUsuarioRep().findByTxtUsuario(CurrentUser.get());
		paramMap.put("REPORTE_PREPARADO_POR", usuario.getTxtNombre());
		paramMap.put("REPORTE_REVISADOR_POR", revisado);
		logger.debug("ParamMap: " + paramMap.toString());
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
		logger.debug("STR_FECHA_MIN=" + paramMap.get("STR_FECHA_MIN"));
		logger.debug("STR_FECHA_MAX=" + paramMap.get("STR_FECHA_MAX"));
		MsgUsuario usuario = MainUI.get().getMsgUsuarioRep().findByTxtUsuario(CurrentUser.get());
		paramMap.put("REPORTE_PREPARADO_POR", usuario.getTxtNombre());
		paramMap.put("REPORTE_REVISADOR_POR", ConfigurationUtil.get("REPORTE_CAJA_REVISADOR_POR"));
		logger.debug("ParamMap: " + paramMap.toString());
		generateReport("ReporteDeBanco", "REPORTS_DIARIO_CAJA_TYPE", paramMap, format);
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
        logger.debug("Trying to load report " + reportName);
		rep = (UI.getCurrent().getClass()).getResourceAsStream(ConfigurationUtil
					.get("REPORTS_SOURCE_URL").trim() + "/" + reportName + ".jasper");
		if (rep!=null) return rep;

		if (System.getenv("VASJA_HOME")!=null) {
			try {
				logger.debug("Trying to load from VASJA_HOME/reports");
				rep = new FileInputStream(
						System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
								+ reportName + ".jasper");
			} catch (FileNotFoundException e) {
				Notification.show("Report file not found under: " + System.getenv("VASJA_HOME") + File.separator + "reports" + File.separator
						+ reportName + ".jasper");
			}
		}

		try {
			logger.debug("Reports folder: " + ConfigurationUtil.getReportsSourceFolder());
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
		JRExporter htmlExporter = new HtmlExporter();
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
		logger.debug("REPORT: " + ConfigurationUtil.getReportsSourceFolder() + reportName
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
        logger.debug("Got entity manager: " + em.getProperties().toString());
        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            logger.debug("setting connection: " + connection);
            sqlConnection = connection;
        });
		return sqlConnection;
	}
}
