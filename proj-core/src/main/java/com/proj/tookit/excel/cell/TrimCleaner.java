package com.proj.tookit.excel.cell;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此编辑器用于去除String类型的单元格值两边的空格
 */
public class TrimCleaner implements CellValueCleaner {
    @Override
    public Object edit(Object value) {
        if (value == null){
            return null;
        }
        if(value instanceof String) {
            String val = value.toString();
            // 去除字符串中的换行、回车、制表符
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(val);
            val = m.replaceAll("");

            return StrUtil.trim(val);
        }
        return value;
    }
}
