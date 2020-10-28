package com.proj.tookit.excel;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.proj.tookit.excel.row.RowData;
import com.proj.tookit.excel.row.RowUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Excel读取器<br>
 * 读取Excel工作簿
 */
public class ExcelReader extends ExcelBase<ExcelReader> {

    // 是否忽略空行，默认为true
    private boolean ignoreEmptyRow = true;
    // 是否自动处理合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
    private boolean autoRecogMerged = true;
    // 开始列，默认从第一列开始读（包含，0开始）
    private int startColumnIndex = 0;
    // 单元格数据处理包装对象，默认使用按ColumnIndex设置包装类
    private CellValueCleanerWrapper cleanerWrapper = new ColumnIndexCleanerWrapper();
    // 属性名，默认使用列序号
    private List<String> propNames = new ArrayList<>();


    /**
     * 构造
     * @param book {@link Workbook} 表示一个Excel文件
     * @param sheetIndex sheet序号，0表示第一个sheet
     */
    public ExcelReader(Workbook book, int sheetIndex) {
        this(book.getSheetAt(sheetIndex));
    }

    /**
     * 构造
     *
     * @param book {@link Workbook} 表示一个Excel文件
     * @param sheetName sheet名，第一个默认是sheet1
     */
    public ExcelReader(Workbook book, String sheetName) {
        this(book.getSheet(sheetName));
    }

    /**
     * 构造
     *
     * @param sheet Excel中的sheet
     */
    public ExcelReader(Sheet sheet) {
        super(sheet);
    }
    // ------------------------------------------------------------------------------------------------------- Constructor end

    // ------------------------------------------------------------------------------------------------------- Getters and Setters start
    /**
     * 是否忽略空行
     *
     * @return 是否忽略空行
     */
    public boolean isIgnoreEmptyRow() {
        return ignoreEmptyRow;
    }

    /**
     * 设置是否忽略空行
     *
     * @param ignoreEmptyRow 是否忽略空行
     */
    public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
        this.ignoreEmptyRow = ignoreEmptyRow;
    }

    /**
     * 是否自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return 是否自动处理合并单元格
     */
    public boolean isAutoRecogMerged() {
        return autoRecogMerged;
    }

    /**
     * 设置是否自动识别合并单元格
     * @param autoRecogMerged 是否自动识别合并单元格
     */
    public void setAutoRecogMerged(boolean autoRecogMerged) {
        this.autoRecogMerged = autoRecogMerged;
    }

    /**
     * 获取开始列
     * @return 开始列
     */
    public int getStartColumnIndex() {
        return startColumnIndex;
    }

    /**
     * 设置开始列
     * @param startColumnIndex 开始列
     */
    public void setStartColumnIndex(int startColumnIndex) {
        this.startColumnIndex = startColumnIndex;
    }

    /**
     * 设置单元格值处理逻辑<br>
     * 当Excel中的值并不能满足我们的读取要求时，通过传入一个编辑接口，可以对单元格值自定义，例如对数字和日期类型值转换为字符串等
     * @param cleanerWrapper 单元格值处理接口包装类
     */
    public void setCleanerWrapper(CellValueCleanerWrapper cleanerWrapper) {
        this.cleanerWrapper = cleanerWrapper;
    }

    /**
     * 获取数据属性名
     * @return 属性名List
     */
    public List<String> getPropNames() {
        return propNames;
    }

    /**
     * 设置属性名
     * @param propNames 属性名List
     */
    public void setPropNames(List<String> propNames) {
        this.propNames = propNames;
    }

    // ------------------------------------------------------------------------------------------------------- Getters and Setters end


    /**
     * 读取Excel为Map的列表<br>
     * 默认自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return RowData列表
     */
    public List<RowData> read() {
        return read(0);
    }


    /**
     * 读取Excel为Map的列表<br>
     * 默认自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @return RowData列表
     */
    public List<RowData> read(int startRowIndex) {
        return read(startRowIndex, Integer.MAX_VALUE);
    }


    /**
     * 存在合并单元格时，判断单元格是否合并，且处于合并单元格的第几行
     * 读取Excel为Map的列表<br>
     * 默认自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @return RowData列表
     */
    public List<RowData> read(int startRowIndex,int mergeCellIndex,boolean invalid) {
        return read(startRowIndex, Integer.MAX_VALUE,mergeCellIndex);
    }

    /**
     * 读取Excel为Map的列表<br>
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @param endRowIndex 读取结束行（包含，从0开始计数）
     * @return RowData列表
     */
    public List<RowData> read(int startRowIndex, int endRowIndex) {
        checkNotClosed();

        startRowIndex = Math.max(startRowIndex, sheet.getFirstRowNum());// 读取起始行（包含）
        endRowIndex = Math.min(endRowIndex, sheet.getLastRowNum());// 读取结束行（包含）
        List<RowData> result = new ArrayList<>(endRowIndex - startRowIndex + 1);

        Map<String, Object> rowMap;
        int MergeIndex =0;
        for (int i = startRowIndex; i <= endRowIndex; i++) {

            rowMap = RowUtil.readRow(sheet.getRow(i), startColumnIndex, cleanerWrapper, autoRecogMerged, propNames);
            if(CollectionUtil.isEmpty(rowMap) && ignoreEmptyRow){
                continue;
            }
            if (null == rowMap) {
                rowMap = new HashMap<>();
            }
            result.add(new RowData(i,MergeIndex ,rowMap));
        }
        return result;
    }


    /**
     * 存在合并单元格时，判断单元格是否合并，且处于合并单元格的第几行
     * 读取Excel为Map的列表<br>
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @param endRowIndex 读取结束行（包含，从0开始计数）
     * @return RowData列表
     */
    public List<RowData> read(int startRowIndex, int endRowIndex,int mergeCellIndex) {
        checkNotClosed();

        startRowIndex = Math.max(startRowIndex, sheet.getFirstRowNum());// 读取起始行（包含）
        endRowIndex = Math.min(endRowIndex, sheet.getLastRowNum());// 读取结束行（包含）
        List<RowData> result = new ArrayList<>(endRowIndex - startRowIndex + 1);

        Map<String, Object> rowMap;
        int MergeIndex;
        for (int i = startRowIndex; i <= endRowIndex; i++) {

            rowMap = RowUtil.readRow(sheet.getRow(i), startColumnIndex, cleanerWrapper, autoRecogMerged, propNames);
            if(CollectionUtil.isEmpty(rowMap) && ignoreEmptyRow){
                continue;
            }
            if (null == rowMap) {
                rowMap = new HashMap<>();
            }
            MergeIndex = RowUtil.isMergedRegion(sheet.getRow(i).getCell(mergeCellIndex));
            result.add(new RowData(i,MergeIndex ,rowMap));
        }
        return result;
    }



    /**
     * 读取Excel为Map的列表<br>
     * 默认自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @return 行的集合，一行使用List表示
     */
    public List<Map<String, Object>> readMap() {
        return readMap(0);
    }


    /**
     * 读取Excel为Map的列表<br>
     * 默认自动识别合并单元格，如果是合并单元格则读取合并单元格的第一行第一列数据值
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @return 行的集合，一行使用List表示
     */
    public List<Map<String, Object>> readMap(int startRowIndex) {
        return readMap(startRowIndex, Integer.MAX_VALUE);
    }



    /**
     * 读取Excel为Map的列表<br>
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @param endRowIndex 读取结束行（包含，从0开始计数）
     * @return Map的列表, Map表示一行，标题为key，如果propNames为空使用列序号为Key，单元格内容为value
     */
    public List<Map<String, Object>> readMap(int startRowIndex, int endRowIndex) {
        checkNotClosed();
        startRowIndex = Math.max(startRowIndex, sheet.getFirstRowNum());// 读取起始行（包含）
        endRowIndex = Math.min(endRowIndex, sheet.getLastRowNum());// 读取结束行（包含）
        List<Map<String, Object>> result = new ArrayList<>(endRowIndex - startRowIndex + 1);

        Map<String, Object> rowMap;
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            rowMap = RowUtil.readRow(sheet.getRow(i), startColumnIndex, cleanerWrapper, autoRecogMerged, propNames);
            if(CollectionUtil.isEmpty(rowMap) && ignoreEmptyRow){
                continue;
            }
            if (null == rowMap) {
                rowMap = new HashMap<>();
            }
            result.add(rowMap);
        }
        return result;
    }

    /**
     * 检查是否未关闭状态
     */
    private void checkNotClosed() {
        Assert.isFalse(this.isClosed, "ExcelReader has been closed!");
    }

}
