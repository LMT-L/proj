package com.proj.tookit.excel.excelImport;


import cn.hutool.core.collection.CollectionUtil;

import com.proj.tookit.excel.ColumnIndexCleanerWrapper;
import com.proj.tookit.excel.ExcelReader;
import com.proj.tookit.excel.ExcelUtil;
import com.proj.tookit.excel.ExcelValidUtil;
import com.proj.tookit.excel.cell.TrimCleaner;
import com.proj.tookit.excel.row.RowData;
import com.proj.tookit.util.SpringUtil;
import com.youth.common.exception.SystemRuntimeException;

import com.youth.common.global.cnst.SystemErrorEnum;
import com.youth.web.system.system.pojo.domain.DicItemDO;
import com.youth.web.system.system.service.DicItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImportUtil {
    // 定义异常处理
    private static final Logger logger = LoggerFactory.getLogger(ImportUtil.class);

    /**
     * 读取excel数据
     *
     * @param multipartFile
     * @param propNames
     * @return
     */
    public static List<RowData> readExcel(MultipartFile multipartFile, List<String> propNames, int startColumnIndex) {
        List<RowData> list = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ExcelReader excelReader = ExcelUtil.getReader(inputStream);
            // 判断属性名为空抛异常
            if(CollectionUtil.isEmpty(propNames)){
                throw new SystemRuntimeException(SystemErrorEnum.UNKNOWN_ERROR);
            }
            // 去空格
            ColumnIndexCleanerWrapper cleanerWrapper =
                    new ColumnIndexCleanerWrapper(startColumnIndex,propNames.size(), new TrimCleaner());
            // 存入读取起始行
            excelReader.setStartColumnIndex(startColumnIndex);
            excelReader.setCleanerWrapper(cleanerWrapper);
            excelReader.setPropNames(propNames);
            // 从excel中读取数据
            list = excelReader.read(1);
            for (String propName : propNames) {
                for (RowData rowData : list) {
                    Map<String, Object> data = rowData.getData();
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    if (data.get(propName) == null) {
                        data.put(propName, null);
                    }
                    rowData.setData(data);
                }
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        if(CollectionUtil.isEmpty(list)){
            throw new SystemRuntimeException("文档数据为空，请填写数据后重试");
        }
        return list;
    }

    /**
     * 读取excel数据
     *
     * @param multipartFile
     * @param propNames
     * @return
     */
    public static List<RowData> readExcel4Sheet(MultipartFile multipartFile, List<String> propNames, int startColumnIndex, int sheetIndex) {
        List<RowData> list = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ExcelReader excelReader = ExcelUtil.getReader(inputStream, sheetIndex);
            // 判断属性名为空抛异常
            if(CollectionUtil.isEmpty(propNames)){
                throw new SystemRuntimeException(SystemErrorEnum.UNKNOWN_ERROR);
            }
            // 去空格
            ColumnIndexCleanerWrapper cleanerWrapper =
                    new ColumnIndexCleanerWrapper(startColumnIndex,propNames.size(), new TrimCleaner());
            // 存入读取起始行
            excelReader.setStartColumnIndex(startColumnIndex);
            excelReader.setCleanerWrapper(cleanerWrapper);
            excelReader.setPropNames(propNames);
            // 从excel中读取数据
            list = excelReader.read(2);
            for (String propName : propNames) {
                for (RowData rowData : list) {
                    Map<String, Object> data = rowData.getData();
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    if (data.get(propName) == null) {
                        data.put(propName, null);
                    }
                    rowData.setData(data);
                }
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        return list;
    }



    /**
     * 读取excel数据(判断该列是否存在合并单元格)
     *
     * @param multipartFile
     * @param propNames
     * @return
     */
    public static List<RowData> readExcel(MultipartFile multipartFile, List<String> propNames, int startColumnIndex, int mergeCellIndex) {
        List<RowData> list = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ExcelReader excelReader = ExcelUtil.getReader(inputStream);
            // 判断属性名为空抛异常
            if(CollectionUtil.isEmpty(propNames)){
                throw new SystemRuntimeException(SystemErrorEnum.UNKNOWN_ERROR);
            }
            // 去空格
            ColumnIndexCleanerWrapper cleanerWrapper =
                    new ColumnIndexCleanerWrapper(startColumnIndex,propNames.size(), new TrimCleaner());
            // 存入读取起始行
            excelReader.setStartColumnIndex(startColumnIndex);
            excelReader.setCleanerWrapper(cleanerWrapper);
            excelReader.setPropNames(propNames);
            // 从excel中读取数据
            list = excelReader.read(2,mergeCellIndex,true);
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        if(CollectionUtil.isEmpty(list)){
            throw new SystemRuntimeException("文档数据为空，请填写数据后重试");
        }
        return list;
    }


    /**
     * 验证字符串是否超长
     *
     * @param propName
     * @param param
     * @param length
     * @return
     */
    public static boolean OverLengthValidate(String propName, Object param, int length) {
        if (StringUtils.isEmpty(param)) {
            throw new SystemRuntimeException(propName + "不能为空");
        }
        if (String.valueOf(param).length() > length) {
            throw new SystemRuntimeException(param + "长度过长");
        } else {
            return true;
        }
    }

    /**
     * 判断导入的是否有重复数据
     *
     * @param list
     * @param dataList
     * @return
     */
    public static boolean proNameValidate(List<String> list, List<String> dataList) {
        // 去重
        List<String> distinctList = list.stream().distinct().collect(Collectors.toList());
        if (distinctList.size() != list.size()) {
            throw new SystemRuntimeException("不能导入重复的数据");
        }
        for (String name : list) {
            if (dataList.contains(name)) {
                throw new SystemRuntimeException("数据库已存在该数据，不得重复导入");
            }
        }
        return true;
    }

    /**
     * 查看是否存在该字典参数
     *
     * @param propName
     * @param param
     * @param dicCode
     * @return
     */
    public static String dicItemValidate(String propName, Object param, String dicCode) {
        if (StringUtils.isEmpty(param)) {
            throw new SystemRuntimeException(propName + "不能为空");
        }
        // 取得字典service
        DicItemService dicItemService = (DicItemService) SpringUtil.getObject(ExcelValidUtil.DIC_ITEM_SERVICE);
        // 通过code查字典参数
        List<DicItemDO> byDicIds = dicItemService.findByDicId(dicCode);
        if (byDicIds == null || byDicIds.isEmpty()) {
            throw new SystemRuntimeException("无" + param + "参数");
        }
        // 将字典对象转map
        Map<String, DicItemDO> map = byDicIds.stream().collect(Collectors.toMap(DicItemDO::getName, item -> item));
        if (map.isEmpty() || !map.containsKey(param)) {
            throw new SystemRuntimeException("无" + param + "参数");
        }
        return map.get(param).getCode();
    }


}
