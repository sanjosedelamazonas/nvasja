package org.sanjose.helper;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

/*
import org.sanjose.util.JRPrinterAWT;
import org.sanjose.web.VasjaApp;
*/

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import dk.apaq.vaadin.addon.printservice.PrintServiceListChangedEvent;
import dk.apaq.vaadin.addon.printservice.PrintServiceListChangedListener;
import dk.apaq.vaadin.addon.printservice.RemotePrintService;
import dk.apaq.vaadin.addon.printservice.RemotePrintServiceManager;
//import net.sf.jasperreports.engine.print.JRPrinterAWT;
import org.sanjose.views.MainScreen;

@SuppressWarnings("serial")
public class PrintHelper extends VerticalLayout implements View {
	
    private Table table = new Table();
    private RemotePrintServiceManager printServiceManager;

    public static final String VIEW_NAME = "Imprimir service";

    static final Logger logger = Logger
			.getLogger(PrintHelper.class.getName());
    
    Graphics2D g2d;
    
    PrintService printService = null;
    
    TextArea printOptions = new TextArea();
	
    BeanContainer c = new BeanContainer(RemotePrintService.class);
    
	public PrintHelper(MainScreen mainScreen) {
		if (ConfigurationUtil.is("REPORTS_COMPROBANTE_PRINT")) {
            addComponent(new Label("Imprmir Service"));
			//ReportHelper.getSqlConnection();
            logger.info("Printing reports");
		    printServiceManager = RemotePrintServiceManager.getInstance(mainScreen);
		    printServiceManager.addListener(new PrintServiceListChangedListener() {
		        public void onPrintServiceListChanged(PrintServiceListChangedEvent event) {
		        	c.removeAllItems();
		            c.setBeanIdProperty("id");
		            logger.info("Found printServices");
		            for (PrintService ps : event.getPrintServices()) {
                        logger.info("Trying to load printservices");
		            	mainScreen.printerLoaded();
		                c.addBean(ps);
		            }
		            table.setContainerDataSource(c);
		        }
		    });
		}
		table.setSelectable(true);
	    table.setMultiSelect(false);
	    //table.setVisibleColumns(new String[] { "id", "name", "resolution",
		//		"colorsupported", "defaultprinter" });
	    table.setColumnCollapsingAllowed(true);
/*	    table.setColumnCollapsed("ATTRIBUTES", true);
	    table.setColumnCollapsed("Attributes", true);
	    table.setColumnCollapsed("supportedAttributeCategories", true);
	    table.setColumnCollapsed("supportedAttributeCategories".toUpperCase(), true);
	    table.setColumnCollapsed("supportedDocFlavors", true);
	    table.setColumnCollapsed("supportedDocFlavors".toUpperCase(), true);
	    */
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

	public void drawPrinterTable() {
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
	            } catch (JRException e) {
	            	Logger.getLogger(PrintHelper.class.getName()).log(Level.SEVERE, null, e);				
	            } catch (ParseException e) {
	            	Logger.getLogger(PrintHelper.class.getName()).log(Level.SEVERE, null, e);
	            }
	        }
	    });	    
	    Button buttonText = new Button("Imprimir texto");
	    buttonText.addListener(new Button.ClickListener() {
	        public void buttonClick(ClickEvent event) {
	    	    DocFlavor flavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
				PrintRequestAttributeSet pras =	new HashPrintRequestAttributeSet();
				logger.info("Printing using printService: " + printService.getName());
				DocPrintJob job = printService.createPrintJob();
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(new File("text.txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				DocAttributeSet das = new HashDocAttributeSet();
				Doc doc = new SimpleDoc(fis, flavor, das);
				logger.info("Sending to job: " + job.toString() + " " + doc.toString());				
				try {
					job.print(doc, pras);
				} catch (PrintException e) {
					// TODO Auto-generated catch block
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
/*
	public boolean print(JasperPrint jrPrint, boolean isComprobante) throws JRException {
		final boolean isTxt = ConfigurationUtil.get("REPORTS_COMPROBANTE_TYPE")
				.equalsIgnoreCase("TXT");
		if (printService == null || printService.getName()==null)
			printService = printServiceManager.getDefaultPrintService();
		
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
			logger.info("Printing using printService: " + printService.getName());
			DocPrintJob job = printService.createPrintJob();
			DocAttributeSet das = new HashDocAttributeSet();
			Doc doc = new SimpleDoc(oStream.toByteArray(), flavor, das);
			try {
				job.print(doc, pras);
			} catch (PrintException e) {
				e.printStackTrace();
				app.getMainWindow().showNotification("Problema de impresora de texto", "Problema: " + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);				
			}
		}
		else			
			JRPrinterAWT.printPages(jrPrint, 0, jrPrint.getPages().size()-1, false, printService);
		return true;
	}
	
  */  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
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

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}