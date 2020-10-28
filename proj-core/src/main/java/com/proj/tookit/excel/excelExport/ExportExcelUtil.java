package com.proj.tookit.excel.excelExport;

import cn.hutool.core.collection.CollUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * 导出Excle
 *
 * @author zhenglei
 * @date Sep 13, 2012
 */
public class ExportExcelUtil {

    private static final String PR_INTEGER = "java.lang.Integer";
    private static final String PR_DOUBLE = "java.lang.Double";
    private static final String PR_BIGDECIMAL = "java.math.BigDecimal";

    /**
     * 文件下载
     *
     * @param dataModel p
     * @param response  p
     * @throws IOException e
     */
    public static void download(DataModel dataModel, HttpServletResponse response)
            throws IOException {
        downloadFile(response, dataModel.getFileName(), ExportExcelUtil.writeToExcelTemplate(dataModel));
    }
    public static void downloadCompliance(DataModel dataModel, HttpServletResponse response)
            throws IOException {
        downloadFile(response, dataModel.getFileName(), ExportExcelUtil.writeComplianceToExcel(dataModel));
    }

    /**
     * 文件下载
     *
     * @param response    p
     * @param fileName    p
     * @param inputStream p
     * @throws IOException e
     */
    public static void downloadFile(HttpServletResponse response, String fileName, InputStream inputStream)
            throws IOException {
        fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("cache-control", "public");
        response.setHeader("Pragma", "public");
        response.setContentType("application/x-msdownload;charset=UTF-8");

        ServletOutputStream servletOutputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int i = 0;

        while ((i = inputStream.read(b)) > 0) {
            servletOutputStream.write(b, 0, i);
        }

        inputStream.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    /**
     * 根据模版生成数据表
     *
     * @param dataModel p
     * @return ByteArrayInputStream r
     * @throws IOException e
     */
    public static ByteArrayInputStream writeToExcelTemplate(DataModel dataModel)
            throws IOException {
        /*String template = getRealPathModelExcel(dataModel.getTemplate(), request);*/
        Resource resource = new ClassPathResource(dataModel.getTemplate());
        File sourceFile = resource.getFile();


        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        Sheet sheet = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //读取模版
        try {
            fileInputStream = new FileInputStream(sourceFile);
        } catch (Exception e) {

        }

        //实例化工作簿
        workbook = new HSSFWorkbook(fileInputStream);
        sheet = workbook.getSheetAt(0);

        //表头样式
        CellStyle headerStyle = null;
        Font headerFont = null;

        //设置表头和字体
        if (dataModel.getHeader() != null) {
            headerStyle = workbook.createCellStyle();
            headerFont = workbook.createFont();
//            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setBold(true);
            headerFont.setFontName("宋体");
            headerFont.setFontHeightInPoints((short) 20);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Row titleRow = sheet.createRow(0);

            titleRow.setHeight((short) 600);

            Cell cell = titleRow.createCell(dataModel.getHeaderStartColumn());

            cell.setCellValue(dataModel.getHeader());
            cell.setCellStyle(headerStyle);
        }

        //表头样式
        CellStyle explainStyle = null;
        Font explainFont = null;

        //设置说明
        if (dataModel.getExplain() != null) {
            explainStyle = workbook.createCellStyle();
            explainFont = workbook.createFont();
            explainFont.setColor(Font.COLOR_RED);
//            explainFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            if (headerFont != null) {
                headerFont.setBold(true);
            }
            explainFont.setFontName("宋体");
            explainFont.setFontHeightInPoints((short) 11);
            explainStyle.setFont(explainFont);

            Row explainRow = sheet.createRow(1);
            Cell cell = explainRow.createCell(dataModel.getExplainStartColumn());

            cell.setCellValue(dataModel.getExplain());
            cell.setCellStyle(explainStyle);
        }

        List<Object[]> data = dataModel.getData();
        int startRow = dataModel.getStartRow();

        for (int i = 0; i < data.size(); i++) {
            Object[] objArray = data.get(i);

            //创建行
            Row row = sheet.createRow(startRow);
            Cell[] cell = new HSSFCell[objArray.length];
            for (int j = 0; j < objArray.length; j++) {

                //创建列
                cell[j] = row.createCell(j);

                if (objArray[j] == null) {
                    cell[j].setCellValue("");
                }else {
                    setCellValue(cell[j], objArray[j]);
                }
            }

            startRow++;
        }
        List<Integer[]> merging = dataModel.getMerging();
        if (CollUtil.isNotEmpty(merging)){
            // 合并单元格
            for (Integer[] m : merging) {
                    // 合并单元格的方法
                    CellRangeAddress region = new CellRangeAddress(m[0], m[1], m[2], m[3]);
                    sheet.addMergedRegion(region);
                }
        }

        try {
            workbook.write(byteArrayOutputStream);
        } catch (Exception e) {

        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        byteArrayOutputStream.close();

        return byteArrayInputStream;
    }

    /**
     * 根据模版生成数据表
     *
     * @param dataModel p
     * @return ByteArrayInputStream r
     * @throws IOException e
     */
    public static ByteArrayInputStream writeComplianceToExcel(DataModel dataModel)
            throws IOException {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //实例化工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFCellStyle setBorder = workbook.createCellStyle();

        sheet.setColumnWidth(0, 4100);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 4500);
        sheet.setColumnWidth(3, 4100);
        sheet.setColumnWidth(4, 3900);
        sheet.setColumnWidth(5, 4800);

        setBorder.setVerticalAlignment(VerticalAlignment.CENTER);

        setBorder.setBorderBottom(BorderStyle.THIN);
        setBorder.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        setBorder.setBorderLeft(BorderStyle.THIN);
        setBorder.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        setBorder.setBorderRight(BorderStyle.THIN);
        setBorder.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        setBorder.setBorderTop(BorderStyle.THIN);
        setBorder.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

        setBorder.setAlignment(HorizontalAlignment.CENTER); // 居中

        HSSFFont font = workbook.createFont();
        font.setFontName("宋体"); // 字体
        font.setBold(false);//粗体显示
        font.setFontHeightInPoints((short) 11);//设置字体大小
        setBorder.setFont(font);//选择需要用到的字体格式

        List<Object[]> data = dataModel.getData();
        int startRow = dataModel.getStartRow();

        HSSFFont afont = workbook.createFont();
        afont.setFontHeightInPoints((short) 20);//设置字体大小
        afont.setFontName("宋体"); // 字体
        afont.setBold(true);//粗体显示
        HSSFCellStyle astyle = workbook.createCellStyle();
        astyle.setVerticalAlignment(VerticalAlignment.CENTER);
        astyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        astyle.setFont(afont);

        HSSFFont bfont = workbook.createFont();
        bfont.setFontHeightInPoints((short) 16);//设置字体大小
        bfont.setFontName("宋体"); // 字体
        bfont.setBold(true);//粗体显示
        HSSFCellStyle bstyle = workbook.createCellStyle();
        bstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bstyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        bstyle.setBorderBottom(BorderStyle.THIN);
        bstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderLeft(BorderStyle.THIN);
        bstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderRight(BorderStyle.THIN);
        bstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderTop(BorderStyle.THIN);
        bstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setFont(bfont);

        HSSFFont cfont = workbook.createFont();
        cfont.setFontHeightInPoints((short) 11);//设置字体大小
        cfont.setFontName("宋体"); // 字体
        cfont.setBold(true);//粗体显示
        HSSFCellStyle cstyle = workbook.createCellStyle();
        cstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cstyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cstyle.setBorderBottom(BorderStyle.THIN);
        cstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderLeft(BorderStyle.THIN);
        cstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderRight(BorderStyle.THIN);
        cstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderTop(BorderStyle.THIN);
        cstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setFont(cfont);

        for (int i = 0; i < data.size(); i++) {
            Object[] objArray = data.get(i);

            //创建行
            Row row = sheet.createRow(startRow);
            row.setHeight((short) 700);

            Cell[] cell = new HSSFCell[objArray.length];
            for (int j = 0; j < objArray.length; j++) {

                //创建列
                cell[j] = row.createCell(j);

                if (objArray[j] == null) {
                    cell[j].setCellValue("");
                    cell[j].setCellStyle(setBorder);
                }else if (objArray[j].equals("青岛银行济南分行" + year + "年度员工合规档案登记表")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（一）基本信息")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("姓名")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("性别")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("出生日期")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("证件号码")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("员工编号")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("参加工作时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("入行时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("政治面貌")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("所在行部")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("所在岗位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("职  务")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("家庭住址")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("婚姻状况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("紧急联系人")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("与本人关系")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("联系电话")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("本行岗位经历")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("工作起始时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("支行（部门）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("岗位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("职务")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("家庭主要成员")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("姓  名")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("工作单位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（二）财产状况")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("购置房产信息")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("房屋坐落")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("建筑面积")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("总价格（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("产权归属")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("购置车辆信息")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("车牌号码")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("车型")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（三）案防测试")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("案防测试成绩")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（四）违规积分")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("累计违规积分")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（五）合规奖惩")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("合规奖励")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("日期")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("奖励事项")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("荣誉称号")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("奖励金额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("不良及核销问责")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("涉及金额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("经济处罚")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("纪律处分")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他处理")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他内部问责处罚")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("处罚事项")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("外部问责处罚")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（六）动态监测")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("个人申报异常")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("申报方式")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("申报事项")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("单位申报异常")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("条线申报异常")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("信用卡透支情况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("近六个月透支金额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("是否套现")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("涉案情况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("涉案类型")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("涉案案由")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("是否结案")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("对外担保情况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("被担保人")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("担保余额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("与被担保人关系")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("消费贷款情况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("贷款日期")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("贷款余额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("贷款用途")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("非法集资")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("金额（万元）")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("涉黑涉恶")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("表现")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("飞单")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("产品名称")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("发行机构")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("出（租）借账户")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("账户数量")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("对象")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("与借予人关系")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("与客户资金往来")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("与客户关系")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("经商办企业")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("企业名称")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("企业身份")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("黄赌毒")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("被举报情况")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("举报方式")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("举报事项")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他1")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他2")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("其他3")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("（七）总体评价")){
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("合规风险等级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("正常")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("关注一级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("关注二级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("关注三级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("备注")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else {
                    cell[j].setCellStyle(setBorder);
                    setCellValue(cell[j], objArray[j]);
                }
            }
            startRow++;
        }
        List<Integer[]> merging = dataModel.getMerging();
        if (CollUtil.isNotEmpty(merging)){
            // 合并单元格
            for (Integer[] m : merging) {
                // 合并单元格的方法
                CellRangeAddress region = new CellRangeAddress(m[0], m[1], m[2], m[3]);
                sheet.addMergedRegion(region);
            }
        }

        try {
            workbook.write(byteArrayOutputStream);
        } catch (Exception e) {

        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        byteArrayOutputStream.close();

        return byteArrayInputStream;
    }

    /**
     * 根据模版生成数据表
     *
     * @param dataModel p
     * @return ByteArrayInputStream r
     * @throws IOException e
     */
    public static ByteArrayInputStream writeGrowthToExcelTemplate(DataModel dataModel)
            throws IOException {
        /*String template = getRealPathModelExcel(dataModel.getTemplate(), request);*/
        Resource resource = new ClassPathResource(dataModel.getTemplate());
        File sourceFile = resource.getFile();


        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        Sheet sheet = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //读取模版
        try {
            fileInputStream = new FileInputStream(sourceFile);
        } catch (Exception e) {

        }

        //实例化工作簿
        workbook = new XSSFWorkbook(fileInputStream);
        sheet = workbook.getSheetAt(0);
        Sheet sheet2 = workbook.getSheetAt(1);
        Sheet sheet3 = workbook.getSheetAt(2);
        //自动计算
        sheet.setForceFormulaRecalculation(true);
        //初始化当前的索引，设为当前sheet的最后一行行数
        int allRows = sheet.getLastRowNum();

        //表头样式
        CellStyle headerStyle = null;
        Font headerFont = null;

        //设置表头和字体
        if (dataModel.getHeader() != null) {
            headerStyle = workbook.createCellStyle();
            headerFont = workbook.createFont();
//            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setBold(true);
            headerFont.setFontName("宋体");
            headerFont.setFontHeightInPoints((short) 20);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Row titleRow = sheet.createRow(0);

            titleRow.setHeight((short) 700);

            Cell cell = titleRow.createCell(dataModel.getHeaderStartColumn());

            cell.setCellValue(dataModel.getHeader());
            cell.setCellStyle(headerStyle);
        }
        //设置表头和字体
        if (dataModel.getHeader() != null) {
            headerStyle = workbook.createCellStyle();
            headerFont = workbook.createFont();
//            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setBold(true);
            headerFont.setFontName("宋体");
            headerFont.setFontHeightInPoints((short) 20);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Row titleRow = sheet2.createRow(0);

            titleRow.setHeight((short) 700);

            Cell cell = titleRow.createCell(dataModel.getHeaderStartColumn());

            cell.setCellValue(dataModel.getHeader());
            cell.setCellStyle(headerStyle);
        }
        //设置表头和字体
        if (dataModel.getHeader() != null) {
            headerStyle = workbook.createCellStyle();
            headerFont = workbook.createFont();
//            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setBold(true);
            headerFont.setFontName("宋体");
            headerFont.setFontHeightInPoints((short) 20);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Row titleRow = sheet2.createRow(0);

            titleRow.setHeight((short) 700);

            Cell cell = titleRow.createCell(dataModel.getHeaderStartColumn());

            cell.setCellValue(dataModel.getHeader());
            cell.setCellStyle(headerStyle);
        }

        Font afont = workbook.createFont();
        afont.setFontHeightInPoints((short) 12);//设置字体大小
        afont.setFontName("宋体"); // 字体
        afont.setBold(true);//粗体显示
        CellStyle astyle = workbook.createCellStyle();
        astyle.setBorderBottom(BorderStyle.THIN);
        astyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        astyle.setBorderLeft(BorderStyle.THIN);
        astyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        astyle.setBorderRight(BorderStyle.THIN);
        astyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        astyle.setBorderTop(BorderStyle.THIN);
        astyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        astyle.setFont(afont);

        Font bfont = workbook.createFont();
        bfont.setFontHeightInPoints((short) 12);//设置字体大小
        bfont.setFontName("宋体"); // 字体
        bfont.setBold(false);//粗体显示
        CellStyle bstyle = workbook.createCellStyle();
        bstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bstyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        bstyle.setBorderBottom(BorderStyle.THIN);
        bstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderLeft(BorderStyle.THIN);
        bstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderRight(BorderStyle.THIN);
        bstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setBorderTop(BorderStyle.THIN);
        bstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        bstyle.setFont(bfont);

        Font cfont = workbook.createFont();
        cfont.setFontHeightInPoints((short) 12);//设置字体大小
        cfont.setFontName("宋体"); // 字体
        cfont.setBold(true);//粗体显示
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cstyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cstyle.setBorderBottom(BorderStyle.THIN);
        cstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderLeft(BorderStyle.THIN);
        cstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderRight(BorderStyle.THIN);
        cstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setBorderTop(BorderStyle.THIN);
        cstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        cstyle.setFont(cfont);

        //表头样式
        CellStyle explainStyle = null;
        Font explainFont = null;

        //设置说明
        if (dataModel.getExplain() != null) {
            explainStyle = workbook.createCellStyle();
            explainFont = workbook.createFont();
            explainFont.setColor(Font.COLOR_RED);
//            explainFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            if (headerFont != null) {
                headerFont.setBold(true);
            }
            explainFont.setFontName("宋体");
            explainFont.setFontHeightInPoints((short) 11);
            explainStyle.setFont(explainFont);
            explainStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            explainStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            explainStyle.setBorderBottom(BorderStyle.THIN);
            explainStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            explainStyle.setBorderLeft(BorderStyle.THIN);
            explainStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            explainStyle.setBorderRight(BorderStyle.THIN);
            explainStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            explainStyle.setBorderTop(BorderStyle.THIN);
            explainStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

            Row explainRow = sheet.createRow(1);
            Cell cell = explainRow.createCell(dataModel.getExplainStartColumn());

            cell.setCellValue(dataModel.getExplain());
            cell.setCellStyle(explainStyle);
        }

        List<Object[]> data = dataModel.getData();
        int startRow = dataModel.getStartRow();

        for (int i = 0; i < dataModel.getLastRowA(); i++) {
            Object[] objArray = data.get(i);

            //创建行
            //sheet.shiftRows(1, data.size(), data.size(),true,false);
            Row row = sheet.createRow(startRow);
            Cell[] cell = new XSSFCell[objArray.length];
            for (int j = 0; j < objArray.length; j++) {

                //创建列
                cell[j] = row.createCell(j);

                if (objArray[j] == null) {
                    cell[j].setCellValue("");
                    cell[j].setCellStyle(cstyle);
                }else if (objArray[j].equals("姓名")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("民族")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("所在行部")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("政治面貌")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("所在岗位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("职级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("身份证号")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("出生日期")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("毕业院校")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("最高学历")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("在我行从事市场营销岗时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("教育经历：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("开始时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("结束时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("学校及专业")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("是否全日制")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("资格证书：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("证书名称")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("颁发单位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("获取时间")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("工作履历：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("任职经历：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("年度考评及荣誉：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("年度")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("考评等级")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("荣誉")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("培训情况：")){
                    cell[j].setCellStyle(astyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("培训项目")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("组织单位")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                }else if (objArray[j].equals("成绩")){
                    cell[j].setCellStyle(cstyle);
                    setCellValue(cell[j], objArray[j]);
                } else {
                    cell[j].setCellStyle(bstyle);
                    setCellValue(cell[j], objArray[j]);
                }
            }
            startRow++;
        }
        int startRowA = dataModel.getStartRow();
        CellStyle dateStyle=sheet2.getRow(1).getCell(0).getCellStyle();
        CellStyle oneStyle=sheet2.getRow(1).getCell(1).getCellStyle();
        CellStyle twoStyle=sheet2.getRow(1).getCell(2).getCellStyle();
        for (int i = dataModel.getLastRowA(); i < dataModel.getLastRowB(); i++) {
            Object[] objArray = data.get(i);

            //创建行
            Row row = sheet2.createRow(startRowA);
            Cell[] cell = new XSSFCell[objArray.length];
            for (int j = 0; j < objArray.length; j++) {

                //创建列
                cell[j] = row.createCell(j);

                if(j == 0){
                    if (objArray[j] == null) {
                        cell[j].setCellValue("");
                    }else{
                        setCellValue(cell[j], objArray[j]);
                    }
                    cell[j].setCellStyle(dateStyle);
                }else if(j == 1){
                    if (objArray[j] == null) {
                        cell[j].setCellValue("");
                    }else{
                        setCellValue(cell[j], objArray[j]);
                    }
                    cell[j].setCellStyle(oneStyle);
                }else if(j == 2){
                    if (objArray[j] == null) {
                        cell[j].setCellValue("");
                    }else{
                        setCellValue(cell[j], objArray[j]);
                    }
                    cell[j].setCellStyle(twoStyle);
                }else {
                    if (objArray[j] == null) {
                        cell[j].setCellValue("");
                    }else{
                        setCellValue(cell[j], objArray[j]);
                    }
                    cell[j].setCellStyle(cstyle);
                }
            }
            startRowA++;
        }
        int startRowB = dataModel.getStartRow();
        CellStyle yearStyle=sheet3.getRow(1).getCell(0).getCellStyle();
        CellStyle pointStyle=sheet3.getRow(1).getCell(1).getCellStyle();
        if (dataModel.getLastRowB()!=0){
            for (int i = dataModel.getLastRowB(); i < data.size(); i++) {
                Object[] objArray = data.get(i);

                //创建行
                Row row = sheet3.createRow(startRowB);
                Cell[] cell = new XSSFCell[objArray.length];
                for (int j = 0; j < objArray.length; j++) {

                    //创建列
                    cell[j] = row.createCell(j);

                    if (j==0){
                        if (objArray[j] == null) {
                            cell[j].setCellValue("");
                        }else{
                            setCellValue(cell[j], objArray[j]);
                        }
                        cell[j].setCellStyle(yearStyle);
                    }else if (j==1){
                        if (objArray[j] == null) {
                            cell[j].setCellValue("");
                        }else{
                            setCellValue(cell[j], objArray[j]);
                        }
                        cell[j].setCellStyle(pointStyle);
                    }else {
                        if (objArray[j] == null) {
                            cell[j].setCellValue("");
                        }else{
                            setCellValue(cell[j], objArray[j]);
                        }
                        cell[j].setCellStyle(cstyle);
                    }
                }
                startRowB++;
            }
        }

        List<Integer[]> merging = dataModel.getMerging();
        if (CollUtil.isNotEmpty(merging)){
            // 合并单元格
            for (Integer[] m : merging) {
                // 合并单元格的方法
                CellRangeAddress region = new CellRangeAddress(m[0], m[1], m[2], m[3]);
                sheet.addMergedRegion(region);
            }
        }

        try {
            workbook.write(byteArrayOutputStream);
        } catch (Exception e) {

        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        byteArrayOutputStream.close();

        return byteArrayInputStream;
    }

    /**
     * 根据数据类型，将数据放入到cell中
     *
     * @param cell     p
     * @param valueObj p
     */
    private static void setCellValue(Cell cell, Object valueObj) {
        Class<?> cla = valueObj.getClass();
        String claName = cla.getName();

        //int
        if (PR_INTEGER.equals(claName)) {
            cell.setCellValue(Integer.parseInt(valueObj.toString()));
        } else if (PR_DOUBLE.equals(claName)) {
            cell.setCellValue(Double.parseDouble(valueObj.toString()));
        } else if (PR_BIGDECIMAL.equals(claName)) {
            cell.setCellValue(((BigDecimal) valueObj).doubleValue());
        } else {
            cell.setCellValue(valueObj.toString());
        }
    }

}



