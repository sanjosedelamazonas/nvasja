package org.sanjose.util;

import org.json.simple.parser.JSONParser;

public class JasperTextPrint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/*
	public void convertToText() {
	JRTextExporter exporter = new JRTextExporter();

	File file = new File("C:\\bill.html");
	try {

	exporter.setParameter(JRTextExporterParameter.JASPER_PRINT,report.getReport());
	exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE, file);
	exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT,new Integer(15));
	exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH,new Integer(22));
	exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH,new Integer(30));
	exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT,new Integer(30));
	exporter.exportReport();

	getPrinter(file);

	} catch (Exception ex) {
	ex.printStackTrace();
	}
	}

	public void getPrinter(File file)
	throws PrintException, FileNotFoundException {

	javax.print.DocFlavor flavor = javax.print.DocFlavor.INPUT_STREAM.AUTOSENSE;

	javax.print.attribute.PrintRequestAttributeSet pras =
	new javax.print.attribute.HashPrintRequestAttributeSet();

	PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
	javax.print.DocPrintJob job = printService.createPrintJob();

	java.io.FileInputStream fis = new java.io.FileInputStream(file);
	javax.print.attribute.DocAttributeSet das = new javax.print.attribute.HashDocAttributeSet();
	javax.print.Doc doc = new javax.print.SimpleDoc(fis, flavor, das);
	job.print(doc, pras);

	}
	}
	*/
}
