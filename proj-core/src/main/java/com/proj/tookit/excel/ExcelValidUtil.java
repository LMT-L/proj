package com.proj.tookit.excel;

import com.proj.tookit.excel.row.RowData;
import com.proj.tookit.util.ReUtil;
import com.proj.tookit.util.SpringUtil;
import com.youth.web.system.system.pojo.domain.DicDO;
import com.youth.web.system.system.pojo.domain.DicItemDO;
import com.youth.web.system.system.service.DicItemService;
import com.youth.web.system.system.service.DicService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelValidUtil {
    // 基础service名称
    public static final String CACHE_SERVICE = "cacheImpl";
    // 字典实现service名称
    public static final String DIC_ITEM_SERVICE = "dicItemServiceImpl";
    // 字典service名称
    public static final String DIC_SERVICE = "dicServiceImpl";


    private ExcelValidUtil() {
    }

    /**
     * 验证和数据库字段重名
     *
     * @param list         读取的excel数据
     * @param errorMsgList 错误信息集合
     * @param nameList     重名列list
     * @param propAliasMap 属性别名map
     */
    public static void isDuplicate(List<RowData> list, List<ErrorMsg> errorMsgList,
                                   List<String> nameList, Map<String, String> propAliasMap, String proName) {

        Map<String, Integer> duplicateName = new HashMap<>();
        List<String> fileNameList = new ArrayList<>();
        String name;
        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            name = data.get(proName) == null ? "" : data.get(proName).toString();
            // 判断文档中的数据是否有重复
            if (!duplicateName.containsKey(name)) {
                duplicateName.put(name, rowIndex);
            } else {
                errorMsgList.add(new ErrorMsg(rowIndex, proName, "文档中" + propAliasMap.get(proName) +
                        "与第" + (duplicateName.get(name) + 1) + "行数据重复"));
            }
            // 判断与数据库中的数据是否有重复
            if (nameList.contains(name)) {
                errorMsgList.add(new ErrorMsg(rowIndex, proName, propAliasMap.get(proName) + "与系统中已有数据重复"));
            }
        }

    }

    /**
     * 验证和数据库字段重名,该列为合并单元格
     *
     * @param list         读取的excel数据
     * @param errorMsgList 错误信息集合
     * @param nameList     重名列list
     * @param propAliasMap 属性别名map
     */
    public static void isDuplicateMerge(List<RowData> list, List<ErrorMsg> errorMsgList,
                                        List<String> nameList, Map<String, String> propAliasMap, String proName) {

        Map<String, Integer> duplicateName = new HashMap<>();
        String name;
        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            name = data.get(proName) == null ? "" : data.get(proName).toString();

            if (row.getMergeIndex() < 2) {//若此单元格是合并单元格且不是第一行，则不判断
                // 判断文档中的数据是否有重复
                if (!duplicateName.containsKey(name)) {
                    duplicateName.put(name, rowIndex);
                } else {
                    errorMsgList.add(new ErrorMsg(rowIndex, proName, "文档中" + propAliasMap.get(proName) +
                            "与第" + (duplicateName.get(name) + 1) + "行数据重复"));
                }
                // 判断与数据库中的数据是否有重复
                if (nameList.contains(name)) {
                    errorMsgList.add(new ErrorMsg(rowIndex, proName, propAliasMap.get(proName) + "与系统中已有数据重复"));
                }
            }
        }

    }

    /**
     * 验证和数据库字段重名
     *
     * @param list         读取的excel数据
     * @param errorMsgList 错误信息集合
     * @param nameList     重名列list
     * @param propAliasMap 属性别名map
     */
    public static void isDuplicateInBase(List<RowData> list, List<ErrorMsg> errorMsgList,
                                         List<String> nameList, Map<String, String> propAliasMap, String proName) {
        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            // 判断与数据库中的数据是否有重复
            if (nameList.contains(data.get(proName))) {
                errorMsgList.add(new ErrorMsg(rowIndex, proName, propAliasMap.get(proName) + "重复导入"));
            }
        }
    }

    /**
     * 验证同一合并的单元格下某些行数据是否重复
     *
     * @param list         excel数据
     * @param errorMsgList 错误信息集合
     * @param theField     同一合并的单元格
     * @param propAliasMap 实体属性与中文字段的映射集合
     * @param proName      要校验的字段
     */
    public static void isDuplicateInField(List<RowData> list, List<ErrorMsg> errorMsgList, String theField,
                                          Map<String, String> propAliasMap, String proName) {

        Map<String, Map<Integer, String>> map = new HashMap<>();
        Map<Integer, String> indexPros = new HashMap<>();

        for (RowData row : list) {
            // 行号
            int rowIndex = row.getRowIndex();
            // excel数据
            Map<String, Object> data = row.getData();
            String costProjectName = data.get(theField).toString();
            String incomeProjectName = data.get(proName).toString();

            if (map.size() != 0 && map.get(costProjectName) != null && !map.get(costProjectName).isEmpty()) {
                if (map.get(costProjectName).containsValue(incomeProjectName)) {
                    errorMsgList.add(new ErrorMsg(rowIndex, proName, propAliasMap.get(proName) + "重复导入"));
                }
            }

            if (!map.containsKey(costProjectName)) {
                indexPros.put(rowIndex, incomeProjectName);
                map.put(costProjectName, indexPros);
            } else {
                indexPros.put(rowIndex, incomeProjectName);
            }
        }
    }

    /**
     * 验证字典值是否存在
     *
     * @param list
     * @param errorMsgList
     * @param code
     * @param propName
     */
    public static void isDicItem(List<RowData> list, List<ErrorMsg> errorMsgList,
                                 String code, String propName) {
        // 取得字典service
        DicItemService dicItemService = (DicItemService) SpringUtil.getObject(DIC_ITEM_SERVICE);
        DicService dicService = (DicService) SpringUtil.getObject(DIC_SERVICE);
        DicDO dicEntity = dicService.findByCode(code).orElse(new DicDO());
        String dicId = null;
        String dicName = null;
        if (dicEntity != null) {
            dicId = dicEntity.getId();
            dicName = dicEntity.getName() == null ? "" : dicEntity.getName();
        }
        // 通过code查字典参数
        List<DicItemDO> byDicIds = new ArrayList<>();
        if (!StringUtils.isEmpty(dicId)) {
            byDicIds = dicItemService.findByDicId(dicId);
        }
        Map<String, DicItemDO> map = new HashMap<>();
        String name;
        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            if (data == null) {
                continue;
            }
            if (StringUtils.isEmpty(data.get(propName))) {
                continue;
            }
            name = data.get(propName) == null ? "" : data.get(propName).toString();
            if (byDicIds != null && !byDicIds.isEmpty()) {
                // 将字典对象转map
                map = byDicIds.stream().collect(Collectors.toMap(DicItemDO::getName, item -> item));
            }
            if (map.isEmpty() || !map.containsKey(name)) {
                errorMsgList.add(new ErrorMsg(rowIndex, propName, "系统中" + "不存在" + "“" + name + "”" + dicName));
            } else {
                data.put(propName, map.get(name).getCode());
            }
        }
    }

    /**
     * 比较属性大小
     *
     * @param list
     * @param errorMsgList
     * @param propAliasMap
     * @param proName1     数字类型
     * @param proName2     数字类型
     */
    public static void compare(List<RowData> list, List<ErrorMsg> errorMsgList,
                               Map<String, String> propAliasMap, String proName1, String proName2) {
        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            String num1 = StringUtils.isEmpty(data.get(proName1)) ? "" : data.get(proName1).toString();
            String num2 = StringUtils.isEmpty(data.get(proName1)) ? "" : data.get(proName2).toString();
            if (!ReUtil.validDateNumber(num1) || !ReUtil.validDateNumber(num2)) {
                continue;
            }
            if (!StringUtils.isEmpty(num1) && !StringUtils.isEmpty(num2)) {
                Long param1 = Long.valueOf(num1);
                Long param2 = Long.valueOf(num2);
                if (param1 > param2) {
                    errorMsgList.add(new ErrorMsg(rowIndex, proName1,
                            propAliasMap.get(proName2) + "不得小于" + propAliasMap.get(proName1)));
                }
            }
        }
    }

}
