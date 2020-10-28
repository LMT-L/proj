package com.proj.tookit.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorInfo {

    private int rowIndex;

    private List<ErrorMsg> errors = new ArrayList<>();


    public ErrorInfo(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void addErrorMsg(ErrorMsg errorMsg){
        // TODO 校验参数
        errors.add(errorMsg);
    }

}
