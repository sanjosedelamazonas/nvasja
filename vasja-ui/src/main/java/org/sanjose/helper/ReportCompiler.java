package org.sanjose.helper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

public class ReportCompiler {

    public static void main(String[] args) {
        String sourceFileName = "/pol/dev/vasja/nvasja/vasja-reports/reports" +
                "/ReporteCajaDiario.jrxml";
        System.out.println("Compiling Report Design ...");
        try {
            /**
             * Compile the report to a file name same as
             * the JRXML file name
             */
            JasperCompileManager.compileReportToFile(sourceFileName);
        } catch (JRException e) {
            e.printStackTrace();
        }
        System.out.println("Done compiling!!! ...");
    }
}
