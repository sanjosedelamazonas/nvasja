package org.sanjose.views.rendicion;

import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class RendicionImport {


    private FormulaEvaluator evaluator;

    if(source.isDirectory()) {
        // Get a list of all of the Excel spreadsheet files (workbooks) in
        // the source folder/directory
        filesList = source.listFiles(new ExcelFilenameFilter());
    }

    /**
     * Open an Excel workbook ready for conversion.
     *
     * @param file An instance of the File class that encapsulates a handle
     *        to a valid Excel workbook. Note that the workbook can be in
     *        either binary (.xls) or SpreadsheetML (.xlsx) format.
     * @throws java.io.FileNotFoundException Thrown if the file cannot be located.
     * @throws java.io.IOException Thrown if a problem occurs in the file system.
     */
    private void openWorkbook(File file) throws FileNotFoundException,
            IOException {
        System.out.println("Opening workbook [" + file.getName() + "]");
        try (FileInputStream fis = new FileInputStream(file)) {

            // Open the workbook and then create the FormulaEvaluator and
            // DataFormatter instances that will be needed to, respectively,
            // force evaluation of forumlae found in cells and create a
            // formatted String encapsulating the cells contents.
            this.workbook = WorkbookFactory.create(fis);
            this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
            this.formatter = new DataFormatter(true);
        }
    }

    /**
     * Called to convert the contents of the currently opened workbook into
     * a CSV file.
     */
    private void convertToCSV() {
        Sheet sheet;
        Row row;
        int lastRowNum;
        this.csvData = new ArrayList<>();

        System.out.println("Converting files contents to CSV format.");

        // Discover how many sheets there are in the workbook....
        int numSheets = this.workbook.getNumberOfSheets();

        // and then iterate through them.
        for(int i = 0; i < numSheets; i++) {

            // Get a reference to a sheet and check to see if it contains
            // any rows.
            sheet = this.workbook.getSheetAt(i);
            if(sheet.getPhysicalNumberOfRows() > 0) {

                // Note down the index number of the bottom-most row and
                // then iterate through all of the rows on the sheet starting
                // from the very first row - number 1 - even if it is missing.
                // Recover a reference to the row and then call another method
                // which will strip the data from the cells and build lines
                // for inclusion in the resylting CSV file.
                lastRowNum = sheet.getLastRowNum();
                for(int j = 0; j <= lastRowNum; j++) {
                    row = sheet.getRow(j);
                    this.rowToCSV(row);
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
    private void rowToCSV(Row row) {
        Cell cell;
        int lastCellNum;
        ArrayList<String> csvLine = new ArrayList<>();

        // Check to ensure that a row was recovered from the sheet as it is
        // possible that one or more rows between other populated rows could be
        // missing - blank. If the row does contain cells then...
        if(row != null) {

            // Get the index for the right most cell on the row and then
            // step along the row from left to right recovering the contents
            // of each cell, converting that into a formatted String and
            // then storing the String into the csvLine ArrayList.
            lastCellNum = row.getLastCellNum();
            for(int i = 0; i <= lastCellNum; i++) {
                cell = row.getCell(i);
                if(cell == null) {
                    csvLine.add("");
                }
                else {
                    if(cell.getCellType() != CellType.FORMULA) {
                        csvLine.add(this.formatter.formatCellValue(cell));
                    }
                    else {
                        csvLine.add(this.formatter.formatCellValue(cell, this.evaluator));
                    }
                }
            }
            // Make a note of the index number of the right most cell. This value
            // will later be used to ensure that the matrix of data in the CSV file
            // is square.
            if(lastCellNum > this.maxRowWidth) {
                this.maxRowWidth = lastCellNum;
            }
        }
        this.csvData.add(csvLine);
    }

    /**
     * An instance of this class can be used to control the files returned
     * be a call to the listFiles() method when made on an instance of the
     * File class and that object refers to a folder/directory
     */
    class ExcelFilenameFilter implements FilenameFilter {

        /**
         * Determine those files that will be returned by a call to the
         * listFiles() method. In this case, the name of the file must end with
         * either of the following two extension; '.xls' or '.xlsx'. For the
         * future, it is very possible to parameterise this and allow the
         * containing class to pass, for example, an array of Strings to this
         * class on instantiation. Each element in that array could encapsulate
         * a valid file extension - '.xls', '.xlsx', '.xlt', '.xlst', etc. These
         * could then be used to control which files were returned by the call
         * to the listFiles() method.
         *
         * @param file An instance of the File class that encapsulates a handle
         *             referring to the folder/directory that contains the file.
         * @param name An instance of the String class that encapsulates the
         *             name of the file.
         * @return A boolean value that indicates whether the file should be
         *         included in the array retirned by the call to the listFiles()
         *         method. In this case true will be returned if the name of the
         *         file ends with either '.xls' or '.xlsx' and false will be
         *         returned in all other instances.
         */
        @Override
        public boolean accept(File file, String name) {
            return(name.endsWith(".xls") || name.endsWith(".xlsx"));
        }
    }

}
