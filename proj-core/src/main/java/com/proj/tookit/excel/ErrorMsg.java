package com.proj.tookit.excel;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ErrorMsg {

    private int sheetIndex;

    private int rowIndex;

    private String proName;

    private String errorMsg;


    public ErrorMsg(int rowIndex, String proName, String errorMsg) {
        this.rowIndex = rowIndex;
        this.proName = proName;
        this.errorMsg = errorMsg;
    }

    public ErrorMsg(int sheetIndex, int rowIndex, String proName, String errorMsg) {
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.proName = proName;
        this.errorMsg = errorMsg;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public String getProName() {
        return proName;
    }


    public String getErrorMsg() {
        return errorMsg;
    }


}
