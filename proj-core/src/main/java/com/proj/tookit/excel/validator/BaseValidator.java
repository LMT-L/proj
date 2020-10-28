package com.proj.tookit.excel.validator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.proj.tookit.excel.ErrorMsg;
import com.proj.tookit.excel.row.RowData;
import com.proj.tookit.excel.validation.ValidationUtil;
import com.proj.tookit.util.ReUtil;
import com.youth.common.exception.SystemRuntimeException;
import com.youth.tool.common.ReflectUtil;
import org.springframework.core.annotation.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Order(-1)
public class BaseValidator extends ExcelValidator {

    @Override
    public boolean supports() {
        return true;
    }

    @Override
    public List<ErrorMsg> valid(List<RowData> list, Map<String, Class> propClazzMap, Map<String, String> propAliasMap, Class beanType) {

        List<ErrorMsg> errorMsgList = new ArrayList<>();
        if (CollectionUtil.isEmpty(list)) {
            return errorMsgList;
        }

        Map<String, Boolean> dateTimePropMap = getDateTimePropMap(list.get(0), beanType);

        for (RowData row : list) {
            int rowIndex = row.getRowIndex();
            Map<String, Object> data = row.getData();
            for (String key : data.keySet()) {
                Object val = data.get(key);
                Field field = ReflectUtil.findField(beanType, key);
                if (field == null) {
                    throw new SystemRuntimeException("请在Excel规定位置填入数据");
                }
                /*
                 * 校验时间格式
                 */
                if (dateTimePropMap.get(key) != null) {
                    try {
                        if (val == null) {
                        //    errorMsgList.add(new ErrorMsg(rowIndex, key, propAliasMap.get(key) + "格式错误"));
                            continue;
                        } else if (StrUtil.isNotEmpty(val.toString())) {
                            boolean re = val.toString().matches(ReUtil.DATE_VALID);
                            if (!re) {
                                errorMsgList.add(new ErrorMsg(rowIndex, key, propAliasMap.get(key) + "格式错误"));
                                continue;
                            }
                            DateTime parse = DateUtil.parse(val.toString(), "yyyy-MM-dd");
                            val = parse.getTime();
                            data.put(key, val);

                        }

                    } catch (DateException dex) {
                        errorMsgList.add(new ErrorMsg(rowIndex, key, propAliasMap.get(key) + "格式错误"));
                        continue;
                    }
                }
                /*
                 * 校验数据类型
                 */
                try {
                    val = Convert.convert(propClazzMap.get(key), val);
                } catch (Exception ex) {
                    errorMsgList.add(new ErrorMsg(rowIndex, key, propAliasMap.get(key) + "数据类型错误"));
                    continue;
                }

                /*
                 * 校验数据长度、范围、正则……
                 */
                ValidationUtil.ValidResult validResult = ValidationUtil.validValue(beanType, key, val);
                if (validResult == null || !validResult.hasErrors()) {
                    continue;
                }

                List<ValidationUtil.ErrorMessage> errors = validResult.getAllErrors();
                for (ValidationUtil.ErrorMessage em : errors) {
                    errorMsgList.add(new ErrorMsg(rowIndex, key, propAliasMap.get(key) + em.getMessage()));
                }
            }
        }

        return errorMsgList;
    }

    /**
     * 获取时间格式属性
     *
     * @param rowData
     * @return Map<String   ,       Boolean>
     */
    private Map<String, Boolean> getDateTimePropMap(RowData rowData, Class beanType) {

        Map<String, Boolean> dateTimePropMap = new HashMap<>();
        for (String key : rowData.getData().keySet()) {
            Field field = ReflectUtil.findField(beanType, key);
            DateTimeFormat annotation = field.getAnnotation(DateTimeFormat.class);
            if (annotation != null) {
                dateTimePropMap.put(key, true);
            }
        }

        return dateTimePropMap;
    }
}
