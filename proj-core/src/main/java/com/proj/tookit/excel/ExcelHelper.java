package com.proj.tookit.excel;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.text.SimpleDateFormat;

/**
 * 通用excel导入数据库
 */
public class ExcelHelper {

    /**
     * 创建一行
     * @param sheet
     * @param rowIndex
     * @param startColumns
     * @param endColumns
     * @param cellStyle
     * @return
     */
    public static HSSFRow createRow(HSSFSheet sheet, int rowIndex, int startColumns, int endColumns, HSSFCellStyle cellStyle){
        HSSFRow row = sheet.createRow(rowIndex);
        for(int i = startColumns; i <= endColumns; i++){
            HSSFCell cell = row.createCell(i);
            if(cellStyle != null){
                cell.setCellStyle(cellStyle);
            }
        }
        return row;
    }


    /**
     * 创建单元格样式
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFCellStyle createCellStyle(HSSFWorkbook wb, ExcelCellStyle style){

        //创建单元格样式（水平居中）
        HSSFCellStyle cellStyle = wb.createCellStyle();
        if(style == null){
            style = new ExcelCellStyle();
        }

        //设置文字排列方式
        cellStyle.setAlignment(style.getAlign());
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);

        //设置边框样式
        cellStyle.setBorderBottom(style.getBorder());
        cellStyle.setBorderLeft(style.getBorder());
        cellStyle.setBorderTop(style.getBorder());
        cellStyle.setBorderRight(style.getBorder());

        //设置字体样式和大小
        HSSFFont font = wb.createFont();
        font.setFontName(style.getFontName());
        font.setFontHeightInPoints(style.getFontSize());
        font.setColor(style.getFontColor());
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * 获取单元格值，兼容合并单元格、非合并单元格
     * @param sheet
     * @param cell
     * @return
     */
    public static String getCellValue(Sheet sheet, Cell cell){
        if(sheet == null || cell == null){
            return "";
        }
        if(cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)){
            cell.setCellType(CellType.STRING);
        }

        boolean isMerge = isMergedRegion(sheet, cell);
        return isMerge ? ExcelHelper.getMergedRegionValue(sheet, cell) : ExcelHelper.getNonMergedCellValue(cell);
    }

    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @return  如果参数为null，直接返回null
     */
    public static Boolean isMergedRegion(Sheet sheet, Cell cell) {

        if(sheet == null || cell == null){
            return false;
        }

        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();


        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(rowIndex >= firstRow && rowIndex <= lastRow){
                if(columnIndex >= firstColumn && columnIndex <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 获取合并单元格的值
     * @param sheet
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, Cell cell){

        if(sheet == null || cell == null){
            return null;
        }

        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        int sheetMergeCount = sheet.getNumMergedRegions();

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if( rowIndex >= firstRow && rowIndex <= lastRow){
                if(columnIndex >= firstColumn && columnIndex <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getNonMergedCellValue(fCell) ;
                }
            }
        }

        return null;
    }
    /**
     * 获取非合并单元格的值  (去头尾空格)
     * @param cell
     * @return
     */
    public static String getNonMergedCellValue(Cell cell){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(cell == null){
            return null;
        }

        String rtn = "";
        if(cell.getCellType() == CellType.STRING){
            rtn = cell.getStringCellValue();
        }
        else if(cell.getCellType() == CellType.BOOLEAN){
            rtn = String.valueOf(cell.getBooleanCellValue());
        }
        else if(cell.getCellType() == CellType.FORMULA){
            rtn = cell.getCellFormula() ;
        }
        else if(cell.getCellType() == CellType.NUMERIC && !HSSFDateUtil.isCellDateFormatted(cell)){
            rtn = String.valueOf(cell.getNumericCellValue());
        }
        else if(cell.getCellType() == CellType.NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)){
            rtn = sdf.format(cell.getDateCellValue()) ;
        }
        return rtn == null || "".equals(rtn) ? "" : rtn.replace("\uE004","").trim();
    }
}
