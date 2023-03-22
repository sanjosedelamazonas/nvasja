package org.sanjose.helper;

import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sanjose.util.GenUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

public class XlsExporter {

    private Sheet sheet = null;

    private Workbook workbook = null;

    private FormulaEvaluator formulaEvaluator = null;

    protected static final com.vaadin.external.org.slf4j.Logger log = LoggerFactory.getLogger(XlsExporter.class);

    public XlsExporter(String sheetName) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        //sheet.setColumnWidth(0, 6000);
        //sheet.setColumnWidth(1, 4000);
    }

    public void writeHeaderLine(List<String> headerCells, int rowAt) {
        Row row = sheet.createRow(rowAt);
        CellStyle style = getHeaderRowStyle();
        int cellCount = 0;
        for (String cellContent : headerCells) {
            createCell(row, cellCount, cellContent, style);
            cellCount++;
        }
    }

    public void writeTitleLine(List<String> headerCells, int rowAt) {
        Row row = sheet.createRow(rowAt);
        CellStyle style = getTitleRowStyle();
        int cellCount = 0;
        for (String cellContent : headerCells) {
            createCell(row, cellCount, cellContent, style);
            cellCount++;
        }
    }

    public void writeDataLine(List<String> cells, int rowAt) {
        Row row = sheet.createRow(rowAt);
        CellStyle style = getDataRowStyle();
        int cellCount = 0;
        for (String cellContent : cells) {
            createCell(row, cellCount, cellContent, style);
            cellCount++;
        }
    }


    public int writeDataLines(Class<?> clazz, List<String> columns, List<?> objects, int firstrow) {
        int rowCount = firstrow;
        CellStyle style = getDataRowStyle();

        for (Object pojo : objects) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            for (String col : columns) {
                createCell(row, columnCount++, getValueFromPojo(clazz, col, pojo), style);
            }
        }
        return rowCount;
    }

    protected void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue((Double) ((BigDecimal) value).doubleValue());
            style = getCurrencyCellStyle();
            style = getFontArial12(style);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
            style = getCenterAlignedCellStyle(style);
        } else {
            cell.setCellValue((String) value);
            style = getLeftAlignedCellStyle(style);
        }
        cell.setCellStyle(style);
    }

    protected CellStyle getTitleRowStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.FINE_DOTS);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        return style;
    }

    protected CellStyle getHeaderRowStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    protected CellStyle getDataRowStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    protected CellStyle getBorderedThinStyle(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    protected CellStyle getCenterAlignedCellStyle(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cellStyle;
    }

    protected CellStyle getFontArial12(CellStyle cellStyle) {
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeight(12);
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle getLeftAlignedCellStyle(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cellStyle;
    }

    protected CellStyle getRightAlignedCellStyle(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cellStyle;
    }

    private Object getValueFromPojo(Class<?> clazz, String fieldName, Object pojo) {
        String idCol = null;
        String colProp = null;
        if (fieldName.contains(".")) {
            idCol = fieldName.substring(0, fieldName.indexOf("."));
            colProp = fieldName.substring(fieldName.indexOf(".")+1);
        }
        BeanItem bItem = new BeanItem(pojo);
        Object value = null;
        if (fieldName.contains(".")) {
            Object idObj = bItem.getItemProperty(idCol).getValue();
            try {
                Method mth = idObj.getClass().getMethod("get" + colProp.substring(0,1).toUpperCase() + colProp.substring(1), new Class[] {});
                mth.setAccessible(true);
                value = mth.invoke(idObj);
            } catch (NoSuchMethodException nsm) {
                log.error("Problem getting no method found: " + fieldName + " " + "\n" + nsm.getMessage());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("Problem getting data from method: " + fieldName + " " + "\n" + e.getMessage());
            }
        } else {
            if (bItem.getItemProperty(fieldName)!=null && !GenUtil.objNullOrEmpty(bItem.getItemProperty(fieldName).getValue()))
                value = bItem.getItemProperty(fieldName).getValue();
        }
        return value;
    }

    private static String capitalize(String s) {
        if (s.length() == 0)
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private Method getMethod(Class<?> clazz, String xlsColumnField) throws NoSuchMethodException {
        Method method;
        try {
            method = clazz.getMethod("get" + capitalize(xlsColumnField));
        } catch (NoSuchMethodException nme) {
            method = clazz.getMethod(xlsColumnField);
        }

        return method;
    }

    protected Font getBoldFont() {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    protected Font getGenericFont() {
        Font font = workbook.createFont();
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    protected CellStyle getCurrencyCellStyle() {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setWrapText(true);
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#0.00"));
        return currencyStyle;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public FormulaEvaluator getFormulaEvaluator() {
        return formulaEvaluator;
    }
}
