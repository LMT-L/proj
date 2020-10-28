package com.proj.tookit.excel.row;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.proj.tookit.excel.CellValueCleanerWrapper;
import com.proj.tookit.excel.cell.CellUtil;
import com.proj.tookit.excel.cell.CellValueCleaner;
import com.proj.tookit.excel.style.StyleSet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Excel中的行{@link Row}封装工具类
 */
public class RowUtil {



    /**
     * 获取已有行或创建新行
     *
     * @param sheet Excel表
     * @param rowIndex 行号
     * @return {@link Row}
     */
    public static Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }


    /**
     * 读取一行
     * @param row 行
     * @param startColumnIndex 开始列
     * @param wrapper 单元格编辑器包装类
     * @param autoRecogMerged 是否自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @param keys keys map<Excel列序号，指定key名称>
     * @return 单元格值Map
     */
    public static Map<String, Object> readRow(Row row, int startColumnIndex, CellValueCleanerWrapper wrapper, boolean autoRecogMerged, List<String> keys) {
        List<Object> rowList = readRow(row, startColumnIndex, wrapper, autoRecogMerged);
        return rowList2Map(startColumnIndex, rowList, keys);
    }


    /**
     * 读取一行
     * @param row 行
     * @param wrapper 单元格编辑器包装类
     * @param autoRecogMerged 是否自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return 单元格值列表
     */
    public static List<Object> readRow(Row row, int startColumnIndex, CellValueCleanerWrapper wrapper, boolean autoRecogMerged) {
        Assert.notNull(row,"[参数校验失败] - the row argument must not be null");
        final short length = row.getLastCellNum();
        if (length < 0) {
            return new ArrayList<>(0);
        }
        final List<Object> cellValues = new ArrayList<>((int) length);
        Object cellValue;
        boolean isAllNull = true;
        for (int i = startColumnIndex; i < length; i++) {
            Cell cell = row.getCell(i);
            if(cell == null){
                cellValues.add(null);
                continue;
            }

            CellValueCleaner cleaner = wrapper == null ? null : wrapper.filter(cell);
            cellValue = CellUtil.getCellValue(cell, cleaner, autoRecogMerged);
            isAllNull &= ObjectUtil.isEmpty(cellValue);
            cellValues.add(cellValue);
        }

        if (isAllNull) {
            // 如果每个元素都为空，则定义为空行
            return new ArrayList<>(0);
        }
        return cellValues;
    }








    /**
     * 写一行数据
     *
     * @param row 行
     * @param rowData 一行的数据
     * @param styleSet 单元格样式集，包括日期等样式
     * @param isHeader 是否为标题行
     */
    public static void writeRow(Row row, Iterable<?> rowData, StyleSet styleSet, boolean isHeader) {
        int i = 0;
        Cell cell;
        for (Object value : rowData) {
            cell = row.createCell(i);
            CellUtil.setCellValue(cell, value, styleSet, isHeader);
            i++;
        }
    }


    /**
     * 将独自的row转化为map
     * @param rowList 一行数据
     * @param keys map键值
     * @return Map<String, Object>
     */
    private static Map<String, Object> rowList2Map(int startColumnIndex, List<Object> rowList, List<String> keys){

        if(rowList == null){
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        for(int i = 0; i < rowList.size(); i++){
            Object item = rowList.get(i);
            // 优先使用指定key值（propName）
            String key = (keys == null || i >= keys.size() || keys.get(i) == null) ? Integer.toString(i) : keys.get(i);
            result.put(key, item);
        }
        return result;
    }



    /**
     * 判断指定行是否是存在合并单元格，且处于合并单元格的第几列（0:不存在 else：处于合并单元格的第几列）
     * @param cell {@link Cell}  传入一行中指定的一列
     * @return 是否是合并单元格
     */
    public static int isMergedRegion(Cell cell){
        Assert.notNull(cell, "[参数校验失败] - the cell argument must not be null");
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        return isMergedRegion(cell.getSheet(), rowIndex, columnIndex);
    }


    /**
     * 判断指定的单元格是否是合并单元格，且处于合并单元格的第几列（0:不存在 else：处于合并单元格的第几列）
     * @param sheet {@link Sheet}
     * @param row 行号
     * @param column 列号
     * @return 是否是合并单元格
     */
    public static int isMergedRegion(Sheet sheet, int row, int column) {
        Assert.notNull(sheet, "[参数校验失败] - the sheet argument must not be null");
        final int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
        int num=0;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
                num=row-ca.getFirstRow()+1;
                return num;
            }
        }
        return num;
    }



}