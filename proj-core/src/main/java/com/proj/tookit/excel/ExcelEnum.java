package com.proj.tookit.excel;

/**
 * 枚举
 * @ author liudeshuai
 * @ date 2020/7/2
 */
public class ExcelEnum {
    /**
     * 读取excel 开始列
     */
    public enum ReadExcelStartColumn {

        FIRST(0), // 第一列
        SECOND(1); // 第二列

        private final int type;

        ReadExcelStartColumn(int type){
            this.type = type;
        }

        public int getValue(){
            return this.type;
        }
    }
}
