package org.sanjose.views.rendicion;

import org.apache.poi.ss.usermodel.*;
import org.sanjose.model.ScpRendiciondetalle;
import org.sanjose.util.GenUtil;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RendicionImport {

    private FormulaEvaluator evaluator;

    private Workbook workbook;

    private DataFormatter formatter;

    private int maxRowWidth;

    private Character moneda;

    private List<ScpRendiciondetalle> rendDetalles = new ArrayList<>();

    public RendicionImport(File file, Character moneda) throws IOException {
        this.moneda = moneda;
        openWorkbook(file);
        importData();
    }

    /**
     * Open an Excel workbook ready for conversion.
     *
     * @param file An instance of the File class that encapsulates a handle
     *             to a valid Excel workbook. Note that the workbook can be in
     *             either binary (.xls) or SpreadsheetML (.xlsx) format.
     * @throws java.io.FileNotFoundException Thrown if the file cannot be located.
     * @throws java.io.IOException           Thrown if a problem occurs in the file system.
     */
    private void openWorkbook(File file) throws FileNotFoundException,
            IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            this.workbook = WorkbookFactory.create(fis);
            this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
            this.formatter = new DataFormatter(true);
        }
    }

    /**
     * Called to convert the contents of the currently opened workbook into
     * a CSV file.
     */
    private void importData() {
        Sheet sheet;
        Row row;
        int lastRowNum;
        // Discover how many sheets there are in the workbook....
        int numSheets = this.workbook.getNumberOfSheets();
        // and then iterate through them.
        for (int i = 0; i < numSheets; i++) {

            // Get a reference to a sheet and check to see if it contains
            // any rows.
            sheet = this.workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                lastRowNum = sheet.getLastRowNum();
                for (int j = 1; j <= lastRowNum; j++) {
                    row = sheet.getRow(j);
                    this.rowToRenddet(row);
                }
            }
        }
    }

    /**
     * Called to convert a row of cells into a line of data that can later be
     * output to the CSV file.
     *
     * @param row An instance of either the HSSFRow or XSSFRow classes that
     *            encapsulates information about a row of cells recovered from
     *            an Excel workbook.
     */
    private void rowToRenddet(Row row) {
        Cell cell;
        int lastCellNum;
        List<String> strCells = new ArrayList<>();

        //List<String> csvLine = new ArrayList<>();

        // Check to ensure that a row was recovered from the sheet as it is
        // possible that one or more rows between other populated rows could be
        // missing - blank. If the row does contain cells then...
        if (row != null) {

            // Get the index for the right most cell on the row and then
            // step along the row from left to right recovering the contents
            // of each cell, converting that into a formatted String and
            // then storing the String into the csvLine ArrayList.
            lastCellNum = row.getLastCellNum();
            //rendDetalles.add()

            for (int i = 0; i <= lastCellNum; i++) {
                cell = row.getCell(i);
                if (cell == null) {
                    strCells.add("");
                } else {
                    if (cell.getCellType() != CellType.FORMULA) {
                        strCells.add(this.formatter.formatCellValue(cell));
                    } else {
                        strCells.add(this.formatter.formatCellValue(cell, this.evaluator));
                    }
                }
            }
            // Make a note of the index number of the right most cell. This value
            // will later be used to ensure that the matrix of data in the CSV file
            // is square.
            if (lastCellNum > this.maxRowWidth) {
                this.maxRowWidth = lastCellNum;
            }

            ScpRendiciondetalle det = strCellToDetalle(strCells);
            if (det!=null) rendDetalles.add(det);
        }
    }

    private ScpRendiciondetalle strCellToDetalle(List<String> strCells) {
        if (strCells.size() < 7)
            return null;
        ScpRendiciondetalle det = new ScpRendiciondetalle();
        det.setCodCtaproyecto(strCells.get(1));
        det.setCodDestino(strCells.get(2));
        det.setCodCtaespecial(strCells.get(3));
        det.setTxtGlosaitem(strCells.get(4));
        try {
            det.setFecComprobantepago(parseDate(strCells.get(0)));
            switch (this.moneda) {
                case '0':
                    det.setNumDebesol(new BigDecimal(strCells.get(5)));
                    break;
                case '1':
                    det.setNumDebedolar(new BigDecimal(strCells.get(5)));
                    break;
                case '2':
                    det.setNumDebemo(new BigDecimal(strCells.get(5)));
                    break;
            };
        } catch (NumberFormatException | ParseException pe) {
            //det.setTxtGlosaitem("Problema al importar la fecha: " + pe.getLocalizedMessage());
        }
        det.setCodTipomoneda(this.moneda);
        return det;
    }

    private Timestamp parseDate(String val) throws ParseException {
        String[] dateFormats = new String[] { "yyyy-MM-dd", "MM/dd/yy", "dd/MM/yyyy" };
        ParseException p = null;
        for (String df : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(df);
                Date res = sdf.parse(val);
                return new Timestamp(res.getTime());
            } catch (ParseException pe) {
                p = pe;
                continue;
            }
        }
        throw p;
    }

    public List<ScpRendiciondetalle> getRendDetalles() {
        return rendDetalles;
    }
}
