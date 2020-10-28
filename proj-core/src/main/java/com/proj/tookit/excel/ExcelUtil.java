package com.proj.tookit.excel;

import cn.hutool.core.io.FileUtil;
import com.proj.tookit.excel.row.RowData;
import com.youth.common.exception.SystemRuntimeException;
import com.youth.tool.bean.tran.TranUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelUtil {

    public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";


    public static ExcelReader getReader(String filePath){
        Workbook workbook = getWorkbook(filePath);
        return new ExcelReader(workbook, 0);
    }


    public static ExcelReader getReader(File bookFile) {
        Workbook workbook = getWorkbook(bookFile);
        return new ExcelReader(workbook, 0);
    }


    public static ExcelReader getReader(InputStream inputStream) {
        Workbook workbook = getWorkbook(inputStream);
        return new ExcelReader(workbook, 0);
    }

    public static ExcelReader getReader(InputStream inputStream, int sheetIndex) {
        Workbook workbook = getWorkbook(inputStream);
        return new ExcelReader(workbook, sheetIndex);
    }



    public static Workbook getWorkbook(String filePath){
        File excelFile = FileUtil.file(filePath);
        return getWorkbook(excelFile);
    }


    public static Workbook getWorkbook(File excelFile){
        return getWorkbook(FileUtil.getInputStream(excelFile));
    }

    public static Workbook getWorkbook(InputStream inputStream){
        try {
            return WorkbookFactory.create(toMarkSupportStream(inputStream));
        } catch (Exception e) {
            throw new SystemRuntimeException("Poi读取文件报错");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ioe) {
                System.out.println();
            }
        }
    }

    /**
     * 将{@link InputStream}转换为支持mark标记的流<br>
     * 若原流支持mark标记，则返回原流，否则使用{@link BufferedInputStream} 包装之
     *
     * @param in 流
     * @return {@link InputStream}
     * @since 4.0.9
     */
    private static InputStream toMarkSupportStream(InputStream in) {
        if (null == in) {
            return null;
        }
        if (!in.markSupported()) {
            return new BufferedInputStream(in);
        }
        return in;
    }

    /**
     * List<RowData></>转换为实体类数组
     *
     * @param target
     * @param maps
     * @param clazz
     * @param <T>
     */
    public static <T> void copyBeansByRowDatas(List<T> target, List<RowData> maps, Class<T> clazz) {
        try {
            for (RowData map : maps) {
                T t = clazz.newInstance();
                TranUtil.copyBeanBySMap(map.getData(), t, clazz);
                target.add(t);
            }
        } catch (Exception e) {
            throw new SystemRuntimeException("数据格式不正确，请修改后重试", e);
        }
    }

}
