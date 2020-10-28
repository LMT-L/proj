package com.proj.tookit.excel.cell;

/**
 * POI中NUMRIC类型的值默认返回的是Double类型，此编辑器用于转换其为int型
 */
public class NumericToIntCleaner implements CellValueCleaner {

    @Override
    public Object edit(Object value) {
        if(value instanceof Number) {
            return ((Number)value).intValue();
        }
        return value;
    }

}
