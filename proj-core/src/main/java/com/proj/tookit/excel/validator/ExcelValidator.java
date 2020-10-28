package com.proj.tookit.excel.validator;

import com.proj.tookit.excel.ErrorMsg;
import com.proj.tookit.excel.row.RowData;

import java.util.List;
import java.util.Map;


public abstract class ExcelValidator implements Comparable<ExcelValidator>{

    private Integer order;


    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(ExcelValidator other) {
        return this.getOrder().compareTo(other.getOrder());
    }



    public abstract boolean supports();

    /**
     *
     * @param list Excel 行数据
     * @param propClazzMap 属性类型
     * @param propAliasMap 属性别名
     * @param beanType 对象Class
     * @return 校验错误信息
     */
    public abstract List<ErrorMsg> valid(List<RowData> list, Map<String, Class> propClazzMap, Map<String, String> propAliasMap, Class beanType);


}
