package com.proj.tookit.excel.cell;

import java.util.Date;

public class Date2LongCleaner implements CellValueCleaner {

    @Override
    public Object edit(Object value) {
        if(value instanceof Date) {
            return ((Date) value).getTime();
        }
        return value;
    }


}
