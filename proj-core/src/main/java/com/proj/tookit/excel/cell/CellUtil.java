package com.proj.tookit.excel.cell;

import cn.hutool.core.lang.Assert;
import com.proj.tookit.excel.style.StyleSet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.SheetUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CellUtil {

    // 空字符串
    private static final String EMPTY = "";


    /**
     * 获取已有单元格或创建新单元格
     * @param row Excel表的行
     * @param cellIndex 列号
     * @return {@link Row}
     */
    public static Cell getOrCreateCell(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (null == cell) {
            cell = row.createCell(cellIndex);
        }
        return cell;
    }


    /**
     * 获取单元格值<br>
     * 默认自动匹配单元格类型
     * @param cell {@link Cell}单元格
     * @param cleaner 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
     * @param autoRecogMerged 是否自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return 值，类型可能为：Date、Double、Long、Boolean、String
     */
    public static Object getCellValue(Cell cell, CellValueCleaner cleaner, final boolean autoRecogMerged) {
        Assert.notNull(cell,"[参数校验失败] - the cell argument must not be null");
        return getCellValue(cell, null, cleaner, autoRecogMerged);
    }


    /**
     * 获取单元格值<br>
     * @param cell {@link Cell}单元格
     * @param cellType 单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
     * @param cleaner 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
     * @param autoRecogMerged 是否自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return 值，类型可能为：Date、Double、Long、Boolean、String
     */
    public static Object getCellValue(Cell cell, CellType cellType, CellValueCleaner cleaner, final boolean autoRecogMerged) {
        Assert.notNull(cell,"[参数校验失败] - the cell argument must not be null");
        if(!autoRecogMerged){
            return getNonMergedRegionValue(cell, cellType, cleaner);
        }
        boolean isMerged = isMergedRegion(cell);
        return isMerged ? getMergedRegionValue(cell, cellType, cleaner) : getNonMergedRegionValue(cell, cellType, cleaner);
    }


    /**
     * 获取单元格值<br>
     * 如果cellType 为null，自动匹配单元格类型
     * @param cell {@link Cell}单元格
     * @param cellType 单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
     * @param cleaner 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
     * @return 值，类型可能为：Date、Double、Long、Boolean、String
     */
    public static Object getNonMergedRegionValue(Cell cell, CellType cellType, CellValueCleaner cleaner){
        Assert.notNull(cell,"[参数校验失败] - the cell argument must not be null");
        if (null == cellType) {
            cellType = cell.getCellType();
        }

        Object value;
        switch (cellType) {
            case NUMERIC:
                value = getNumericValue(cell);
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                // 遇到公式时查找公式结果类型
                value = getCellValue(cell, cell.getCachedFormulaResultType(), cleaner, false);
                break;
            case BLANK:
                value = EMPTY;
                break;
            case ERROR:
                final FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
                // TODO 公式错误处理
                value = (null == error) ? EMPTY : error.getString();
                break;
            default:
                value = cell.getStringCellValue();
        }
        return null == cleaner ? value : cleaner.edit(value);
    }




    /**
     * 获取合并单元格的值
     * @param cell {@link Cell}单元格
     * @param cellType 单元格值类型{@link CellType}枚举，如果为{@code null}默认使用cell的类型
     * @param cleaner 单元格值编辑器。可以通过此编辑器对单元格值做自定义操作
     * @return 合并单元格的值
     */
    public static Object getMergedRegionValue(Cell cell, CellType cellType, CellValueCleaner cleaner) {
        Assert.notNull(cell,"[参数校验失败] - the cell argument must not be null");

        Sheet sheet = cell.getSheet();
        final List<CellRangeAddress> addrs = sheet.getMergedRegions();

        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        int firstColumn;
        int lastColumn;
        int firstRow;
        int lastRow;
        for (CellRangeAddress ca : addrs) {
            firstColumn = ca.getFirstColumn();
            lastColumn = ca.getLastColumn();
            firstRow = ca.getFirstRow();
            lastRow = ca.getLastRow();

            if (rowIndex >= firstRow && rowIndex <= lastRow) {
                if (columnIndex >= firstColumn && columnIndex <= lastColumn) {
                    return getNonMergedRegionValue(SheetUtil.getCell(sheet, firstRow, firstColumn), cellType, cleaner);
                }
            }
        }

        return null;
    }



    /**
     * 设置单元格值，根据传入的styleSet自动匹配样式，不考虑Excel公式
     * @param cell 单元格
     * @param value 值
     * @param styleSet 单元格样式集，包括日期等样式
     * @param isHeader 是否为标题单元格
     */
    public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
        setCellValue(cell, value, styleSet, isHeader, false);
    }

    /**
     * 设置单元格值，根据传入的styleSet自动匹配样式<br>
     * @param cell 单元格
     * @param value 值
     * @param styleSet 单元格样式集，包括日期等样式
     * @param isHeader 是否为标题单元格
     */
    public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader, boolean isFormula) {
        if(null == cell) {
            return;
        }
        CellStyle cellStyle = null;
        if(null != styleSet){
            cellStyle = styleSet.getHeadCellStyle();
            if(!isHeader){
                if(value instanceof Date){
                    cellStyle = styleSet.getCellStyleForDate();
                }else if(value instanceof Number){
                    cellStyle = styleSet.getCellStyleForNumber();
                }else{
                    cellStyle = styleSet.getCellStyle();
                }
            }
        }

        setCellValue(cell, value, cellStyle, isFormula);
    }


    /**
     * 设置单元格值，指定单元格样式，不考虑公式
     * @param cell 单元格
     * @param value 值
     * @param cellStyle 单元格样式
     */
    public static void setCellValue(Cell cell, Object value, CellStyle cellStyle){
        setCellValue(cell, value, cellStyle, false);
    }

    /**
     * 设置单元格值, 指定单元格样式
     * @param cell 单元格
     * @param value 值
     * @param cellStyle 单元格样式
     */
    public static void setCellValue(Cell cell, Object value, CellStyle cellStyle, boolean isFormula){
        if(null == cell) {
            return;
        }
        // 设置单元格值
        if (null == value) {
            cell.setCellValue(EMPTY);
        } else if (isFormula) {
            cell.setCellFormula(value.toString());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
        // 设置单元格样式
        setCellStyle(cell, cellStyle);
    }


    /**
     * 设置单元格样式
     * @param cell 单元格
     * @param cellStyle 单元格样式
     */
    public static void setCellStyle(Cell cell, CellStyle cellStyle){
        if (null != cell && null != cellStyle) {
            cell.setCellStyle(cellStyle);
        }
    }


    /**
     * 判断指定单元格是否是合并单元格
     * @param cell {@link Cell}
     * @return 是否是合并单元格
     */
    public static boolean isMergedRegion(Cell cell){
        Assert.notNull(cell, "[参数校验失败] - the cell argument must not be null");
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        return isMergedRegion(cell.getSheet(), rowIndex, columnIndex);
    }


    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet {@link Sheet}
     * @param row 行号
     * @param column 列号
     * @return 是否是合并单元格
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        Assert.notNull(sheet, "[参数校验失败] - the sheet argument must not be null");
        final int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合并单元格，可以根据设置的值来合并行和列
     *
     * @param sheet 表对象
     * @param firstRow 起始行，0开始
     * @param lastRow 结束行，0开始
     * @param firstColumn 起始列，0开始
     * @param lastColumn 结束列，0开始
     * @param cellStyle 单元格样式，只提取边框样式
     * @return 合并后的单元格号
     */
    public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
        final CellRangeAddress cellRangeAddress = new CellRangeAddress(
                firstRow,
                lastRow,
                firstColumn,
                lastColumn
        );

        if (null != cellStyle) {
            RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
            RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
            RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
            RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
        }
        return sheet.addMergedRegion(cellRangeAddress);
    }

    // -------------------------------------------------------------------------------------------------------------- Private method start
    /**
     * 获取数字类型的单元格值
     *
     * @param cell 单元格
     * @return 单元格值，可能为Long、Double、Date
     */
    private static Object getNumericValue(Cell cell) {
        final double value = cell.getNumericCellValue();

        final CellStyle style = cell.getCellStyle();
        if (null == style) {
            return value;
        }

        // 判断是否为日期
        if (isDateType(cell, style.getDataFormat())) {
            return cell.getDateCellValue();
        }

        final String format = style.getDataFormatString();
        // 普通数字
        if (null != format && format.indexOf('.') < 0) {
            final long longPart = (long) value;
            if (longPart == value) {
                // 对于无小数部分的数字类型，转为Long
                return longPart;
            }
        }
        return value;
    }

    /**
     * 是否为日期格式<br>
     * 判断方式：
     *
     * <pre>
     * 1、指定序号
     * 2、org.apache.poi.ss.usermodel.DateUtil.isADateFormat方法判定
     * </pre>
     *
     * @param cell 单元格
     * @param formatIndex 格式序号
     * @return 是否为日期格式
     */
    private static boolean isDateType(Cell cell, int formatIndex) {
        // yyyy-MM-dd----- 14
        // yyyy年m月d日---- 31
        // yyyy年m月------- 57
        // m月d日 ---------- 58
        // HH:mm----------- 20
        // h时mm分 -------- 32
        if (formatIndex == 14 || formatIndex == 31 || formatIndex == 57 || formatIndex == 58 || formatIndex == 20 || formatIndex == 32) {
            return true;
        }

        return DateUtil.isCellDateFormatted(cell);
    }


}
