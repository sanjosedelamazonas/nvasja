package org.sanjose.views.rendicion;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.sanjose.helper.XlsExporter;
import org.sanjose.model.ScpDestino;
import org.sanjose.model.ScpRendicioncabecera;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.util.GenUtil;
import org.sanjose.views.sys.PersistanceService;

public class RendicionExportXLS extends XlsExporter {

    private ScpRendicioncabecera cabecera;

    private PersistanceService service;

    public RendicionExportXLS(ScpRendicioncabecera cabecera, PersistanceService service) {
        super(cabecera.getTxtGlosa());
        this.cabecera = cabecera;
        this.service = service;
        exportRendicion();
        //sheet.setColumnWidth(0, 6000);
        //sheet.setColumnWidth(1, 4000);
    }

    public void exportRendicion() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        log.info("running export " + cabecera.getCodRendicioncabecera());
        writeTitleLine(Arrays.asList(new String[]{ " ", cabecera.getTxtGlosa(), " ", " "}), 0);
        ScpDestino destino = service.getDestinoRepo().findByCodDestino(cabecera.getCodDestino());
        writeDataLine(Arrays.asList(new String[]{ "Fecha de rendicion: ", sdf.format(cabecera.getFecComprobante()), "", ""}), 1);
        writeDataLine(Arrays.asList(new String[]{ "Destino", destino.getCodDestino() + " " + destino.getTxtNombredestino(), "", ""}), 2);
        writeDataLine(Arrays.asList(new String[]{ "Moneda", GenUtil.getSymMoneda(GenUtil.getLitMoneda(cabecera.getCodTipomoneda())), "", ""}), 3);

        writeHeaderLine(Arrays.asList(new String[]{ "Nro", "Descripcion", "Ingreso", "Egreso"}), 5);

        List<ScpRendiciondetalle> detalles = service.getRendiciondetalleRep().findById_CodRendicioncabecera(cabecera.getCodRendicioncabecera());
        detalles.sort(new Comparator<ScpRendiciondetalle>() {
            @Override
            public int compare(ScpRendiciondetalle o1, ScpRendiciondetalle o2) {
                return o1.getId().getNumNroitem().compareTo(o2.getId().getNumNroitem());
            }
        });
        writeDataLines(ScpRendiciondetalle.class,
                Arrays.asList(new String[]{"id.numNroitem", "txtGlosaitem", "numHaber" + GenUtil.getDescMoneda(cabecera.getCodTipomoneda()), "numDebe" + GenUtil.getDescMoneda(cabecera.getCodTipomoneda())}),
                detalles,
                6);
        createSumRow(2, detalles.size()+6);
//
//        int last = detalles.size() + 7;
//        String strFormula= "SUM(C7:C"+ last + ")";
//
//
//
//        Row row = getSheet().createRow(6+ detalles.size());
//        CellStyle style = getDataRowStyle();
//        int cellCount = 0;
//        for (int i=0;i<2;i++) {
//            createCell(row, cellCount, "", style);
//            cellCount++;
//        }
//        XSSFCell formulaCell = row.createCell(2);
//        //XSSFCell formulaCell = sheet.getRow(0).createCell(lastCellNum + 1);
//        formulaCell.setCellFormula("SUM(C7:C"+ last + ")");
//        //cell.setCellType(XCELL_TYPE_FORMULA);
////        cell.setCellFormula(strFormula);
//        writeDataLine(Arrays.asList(new String[] {"", "", "SUM(C7:C"+ last + ")", "SUM(D7:D"+ last + ")" }), );
//
//        XSSFFormulaEvaluator formulaEvaluator =
//                excel.getCreationHelper().createFormulaEvaluator();
//        formulaEvaluator.evaluateFormulaCell(formulaCell);

//        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
//        cell.setCellFormula(strFormula);
        //writeDataLines(ScpRendiciondetalle.class,


                //openExported();
    }

    private void createSumRow(int col, int r) {
        Row row = getSheet().createRow(r);
        CellStyle style = getDataRowStyle();
        int cellCount = 0;
        for (int i=0;i<col;i++) {
            createCell(row, cellCount, "", style);
            cellCount++;
        }
        Cell formulaCell = row.createCell(2);
        //XSSFCell formulaCell = sheet.getRow(0).createCell(lastCellNum + 1);
        formulaCell.setCellFormula("SUM(C7:C"+ row + 1 + ")");
        formulaCell = row.createCell(3);
        //XSSFCell formulaCell = sheet.getRow(0).createCell(lastCellNum + 1);
        formulaCell.setCellFormula("SUM(D7:D"+ row + 1 + ")");
        //XSSFFormulaEvaluator formulaEvaluator =
        //        excel.getCreationHelper().createFormulaEvaluator();
        //formulaEvaluator.evaluateFormulaCell(formulaCell);

    }

    public ByteArrayOutputStream getExported() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            getWorkbook().write(outputStream);
            getWorkbook().close();
        } catch (IOException ex) {
            log.error("Error exporting to XLS");

        }
        return outputStream;
    }


    public StreamResource openExported() {
        StreamResource.StreamSource source = (StreamResource.StreamSource) () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                getWorkbook().write(outputStream);
                getWorkbook().close();
            } catch (IOException ex) {
                log.error("Error exporting to XLS");

            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        };
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        StreamResource resource = new StreamResource(source, "RendicionExport_"
                        + cabecera.getCodComprobante() + "_"
                        + df.format(new Date(System.currentTimeMillis()))
                        + ".xlsx");
        resource.setMIMEType("application/xls");
        return resource;
    }
}
