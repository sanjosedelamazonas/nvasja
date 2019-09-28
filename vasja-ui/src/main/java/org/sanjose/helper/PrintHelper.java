package org.sanjose.helper;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import com.vaadin.ui.TextArea;
import dk.apaq.vaadin.addon.printservice.PrintServiceListChangedEvent;
import dk.apaq.vaadin.addon.printservice.PrintServiceListChangedListener;
import dk.apaq.vaadin.addon.printservice.RemotePrintService;
import dk.apaq.vaadin.addon.printservice.RemotePrintServiceManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import org.sanjose.authentication.CurrentUser;
import org.sanjose.util.ConfigurationUtil;
import org.sanjose.views.sys.MainScreen;
import org.sanjose.views.sys.Viewing;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class PrintHelper extends VerticalLayout implements Viewing {

    public static final String VIEW_NAME = "Imprimir service";
	public String getWindowTitle() {
		return VIEW_NAME;
	}
    private static final Logger logger = Logger
			.getLogger(PrintHelper.class.getName());
	private final Table table = new Table();
	private final TextArea printOptions = new TextArea();
	@SuppressWarnings("unchecked")
	private final
	BeanContainer c = new BeanContainer(RemotePrintService.class);
	private RemotePrintServiceManager printServiceManager;
	private Graphics2D g2d;
	private PrintService printService = null;
	private MainScreen mainScreen;

	private boolean isReady = false;

	public PrintHelper(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
	}

	@Override
	public void init() {
		if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
            addComponent(new Label("Imprimir Service"));
			printServiceManager = RemotePrintServiceManager.getInstance(mainScreen);
		    printServiceManager.addListener(new PrintServiceListChangedListener() {
		        public void onPrintServiceListChanged(PrintServiceListChangedEvent event) {
		        	c.removeAllItems();
		            c.setBeanIdProperty("id");
		            logger.info("Found printServices");
					List<String> imprimeras = new ArrayList<>();
					for (PrintService ps : event.getPrintServices()) {
                        logger.fine("Printservice loaded: " + ps.getName());
						imprimeras.add(ps.getName());
						//noinspection unchecked
						c.addBean(ps);
		            }
					PrintService defPrintService = selectPrintService();
					if (defPrintService!=null)
						logger.info("Selected print service for user: " + CurrentUser.get() + " - "  + defPrintService.getName());
					else
						logger.info("No default printer for user: " + CurrentUser.get());
					if (!imprimeras.isEmpty()) {
						mainScreen.printerLoaded(imprimeras, defPrintService.getName());
						isReady = true;
					} else {
						isReady = false;
					}
					table.setContainerDataSource(c);
					table.setColumnCollapsingAllowed(true);
				}
		    });
		}
		printOptions.setColumns(80);
		table.setSelectable(true);
	    table.setMultiSelect(false);
	    table.addValueChangeListener((Table.ValueChangeListener) event -> {
            printService = (PrintService)c.getItem(event.getProperty().getValue()).getBean();
            logger.info("The bean got: " + c.getItem(event.getProperty().getValue()).getBean().getClass().getName());
            String sb = "PrintServiceAttr { ";
            for (Attribute attr : printService.getAttributes().toArray()) {
                sb += attr.getName() + "=" + attr.getCategory().getName() + ",\n ";
            }
            sb += " } SupCat { ";
            for (Class clas : printService.getSupportedAttributeCategories()) {
                sb += clas.getName() + ",\n ";
            }
            sb += " } SupDoc { ";
            for (DocFlavor docFlavor : printService.getSupportedDocFlavors()) {
                sb += docFlavor.toString() + ",\n ";
            }
            printOptions.setValue(sb);
            printOptions.requestRepaint();
        });
	    if (ConfigurationUtil.is("PRINTER_LIST_SHOW")) drawPrinterTable();
	}

	private void drawPrinterTable() {
	    setCaption("Imprimir");
	    Button button = new Button("Imprimir");
	    button.addListener(new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	            try {
	            	if (printService==null) Notification.show("Seleccione la impresora", Notification.Type.ERROR_MESSAGE);
	                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	                JasperPrint jrPrint = ReportHelper.printDiario(df.parse("20110911"), null, true);
	                logger.info("Report margins: " + jrPrint.getTopMargin() + " " + jrPrint.getBottomMargin() + " " + 
	                		jrPrint.getLeftMargin() + " " + jrPrint.getRightMargin());
	                
					JRPrinterAWT.printPages(jrPrint, 0, jrPrint.getPages().size()-1, false, printService);
	            } catch (JRException | ParseException e) {
	            	Logger.getLogger(PrintHelper.class.getName()).log(Level.SEVERE, null, e);				
	            }
			}
	    });	    
	    Button buttonText = new Button("Imprimir texto");
	    buttonText.addListener(new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	    	    DocFlavor flavor = DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8;
				PrintRequestAttributeSet pras =	new HashPrintRequestAttributeSet();
				logger.info("Printing using printService: " + printService.getName());
				DocPrintJob job = printService.createPrintJob();
				//FileInputStream fis = null;
				try {
					File testFile = new File(System.getProperty("java.io.tmpdir") + "/text.txt");
					testFile.createNewFile();
					FileWriter fw = new FileWriter(testFile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("Test text file printed by the VASJA Caja system.");
					bw.close();
					fw.close();
					Path path = Paths.get(testFile.getPath());
					byte[] data = Files.readAllBytes(path);
					DocAttributeSet das = new HashDocAttributeSet();
					Doc doc = new SimpleDoc(data, flavor, das);
					logger.info("Sending to job: " + job.toString() + " " + doc.toString());
					job.print(doc, pras);
				} catch (PrintException | IOException e) {
					e.printStackTrace();
				}
			}
	    });
	    printOptions.setImmediate(true);
	    addComponent(table);
	    addComponent(button);
	    addComponent(buttonText);
	    addComponent(printOptions);
	    table.setSelectable(true);
	    table.setMultiSelect(false);
	    //table.setVisibleColumns(new String[] { "id", "name", "resolution",
		//		"colorsupported", "defaultprinter" });
	}

	private PrintService selectPrintService() {
		if (ConfigurationUtil.get("DEFAULT_PRINTER_" + CurrentUser.get()) != null) {
			for (PrintService ps : printServiceManager.getPrintServices(null, null)) {
				if (ConfigurationUtil.get("DEFAULT_PRINTER_" + CurrentUser.get()).equals(ps.getName()))
					return ps;
			}
		}
		return printServiceManager.getDefaultPrintService();
	}

	public boolean print(JasperPrint jrPrint, boolean isComprobante) throws JRException, PrintException {
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT");
		if (printServiceManager == null)
			throw new JRException("No se podia conseguir un servicio de impresoras");
		if (printService == null || printService.getName()==null) {
			printService = selectPrintService();
		}
		if (printService==null)
			throw new JRException("No se podia conseguir una impresora");
		if (jrPrint==null)
			throw new JRException("No se podia conseguir el reporte");
		if (isComprobante && isTxt) {
			JRTextExporter txtExporter = new JRTextExporter();
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();

			txtExporter
					.setParameter(JRTextExporterParameter.JASPER_PRINT, jrPrint);
			txtExporter.setParameter(JRTextExporterParameter.OUTPUT_STREAM, oStream);
			//txtExporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
			txtExporter.exportReport();
			
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8;
			PrintRequestAttributeSet pras =	new HashPrintRequestAttributeSet();
			if (printService==null) return false;  
			//logger.info("Printing using printService: " + printService.getName());
			DocPrintJob job = printService.createPrintJob();
			DocAttributeSet das = new HashDocAttributeSet();
			Doc doc = new SimpleDoc(oStream.toByteArray(), flavor, das);
			job.print(doc, pras);
		}
		else {
			logger.info("Printing graphically using printService: " + printService.getName() + " pages: " + jrPrint.getPages().size());
			JRPrinterAWT.printPages(jrPrint, 0, jrPrint.getPages().size() - 1, false, printService);
		}
		return true;
	}
	
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        try {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            g2d = (Graphics2D) graphics;
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/highres_image.jpg"));
            logger.info("Image loaded " + (image!=null ? image.getHeight() + "x" + image.getWidth() : "NO" ));
            //Set us to the upper left corner
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            AffineTransform at = new AffineTransform();
            at.translate(0, 0);

            //We need to scale the image properly so that it fits on one page.
            double xScale = pageFormat.getImageableWidth() / image.getWidth();
            double yScale = pageFormat.getImageableHeight() / image.getHeight();
            // Maintain the aspect ratio by taking the min of those 2 factors and using it to scale both dimensions.
            double aspectScale = Math.min(xScale, yScale);
            at.setToScale(aspectScale, aspectScale);
            g2d.drawRenderedImage(image, at);
            return Printable.PAGE_EXISTS;

        } catch (IOException ex) {
            throw new PrinterException(ex.getMessage());
        }
    }

	public boolean isReady() {
		return isReady;
	}

	@Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}