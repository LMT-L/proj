package com.proj.tookit.excel.excelExport;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 导出Excel DataModel
 *
 * @author zhenglei
 * @date Sep 13, 2012
 */
@Data
public class DataModel {

    /**
     * startRo 从哪行开始，默认为1w
     */
    private int startRow = 1;

    /**
     * headerStartColumn    Excel表头，默认第0列开始
     */
    private int headerStartColumn = 0;

    /**
     * explainStartColumn    注意说明，默认第0列开始
     */
    private int explainStartColumn = 0;

    /**
     * template    Excel导出模板
     */
    private String template;

    /**
     * data    填充到模板的数据
     */
    private List<Object[]> data;

    /**
     * fileName    浏览器下载显示的名称
     */
    private String fileName;

    /**
     * header Excel表头，默认第0行开始
     */
    private String header;

    /**
     * explain    注意说明，默认第一行开始
     */
    private String explain;

    /**
     * 根据分页写入excel时用
     */
    private Workbook workbook;

    /**
     * centerStyle    居中
     */
    private CellStyle centerStyle;

    /**
     * list
     */
    private List<Baobiao> list;
    // 合并单元格
    private List<Integer[]> merging;

    // sheet2用
    private int lastRowA;
    // sheet3用
    private int lastRowB;
}



