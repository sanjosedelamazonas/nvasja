package org.sanjose.test;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.sanjose.bean.VsjOperaciontercero;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class TestJasper {

    public Collection generateCollection() {

            List<VsjOperaciontercero> terc = new ArrayList<>();
            BigDecimal sumSaldosol = new BigDecimal(34);
            BigDecimal sumSaldodolar = new BigDecimal(588);
            BigDecimal sumSaldomo = new BigDecimal(969);


            terc.add(new VsjOperaciontercero(
                    null,
                    "151521512",
                    "Hello",
                    new Timestamp(new Date().getTime()),
                    "",
                    "000000",
                    "Saldo inicial SOLES",
                    "",
                    "",
                    "",
                    '0',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldosol,
                    null,
                    null,
                    "",
                    true
            ));

            terc.add(new VsjOperaciontercero(
                    null,
                    "Hi",
                    "Hi",
                    new Timestamp(new Date().getTime()),
                    "",
                    "000001",
                    "Saldo inicial DOLARES",
                    "",
                    "",
                    "",
                    '1',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldodolar,
                    null,
                    "",
                    true
            ));

            terc.add(new VsjOperaciontercero(
                    null,
                    "Test",
                    "Tescik",
                    new Timestamp(new Date().getTime()),
                    "",
                    "000002",
                    "Saldo inicial EUROS",
                    "",
                    "",
                    "",
                    '0',
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sumSaldomo,
                    "",
                    true
            ));


            //List<VsjTerceroreporte> reportes = new ArrayList<>();
            //reportes.add(terceroreporte);
            return terc;
    }

    public static void main(String[] args) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            InputStream input = new FileInputStream( new File("/pol/dev/nvasja/vasja-reports/reports/" + "ReporteTerceroOperacionesAll.jasper"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(input);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(new TestJasper().generateCollection()));

            JasperExportManager.exportReportToPdfFile(jasperPrint, "test.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
