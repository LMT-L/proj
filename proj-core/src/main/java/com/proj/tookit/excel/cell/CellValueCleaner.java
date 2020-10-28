package com.proj.tookit.excel.cell;


public interface CellValueCleaner {

    /**
     * 加工数据
     * @param value 数据
     * @return 编辑后的对象
     */
    Object edit(Object value);

}
