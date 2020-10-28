package com.proj.tookit.excel.row;

import java.util.Map;

public class RowData {
    private int rowIndex;
    private int mergeIndex;//合并单元格的第几行
    private Map<String, Object> data;


    public RowData(int rowIndex,int mergeIndex,Map<String, Object> data) {
        this.rowIndex = rowIndex;
        this.data = data;
        this.mergeIndex = mergeIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getMergeIndex() { return mergeIndex; }

    public void setMergeIndex(int mergeIndex) { this.mergeIndex = mergeIndex; }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
