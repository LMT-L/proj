package com.proj.tookit.excel;

public class Const {

    /**
     * 金额
     */
    public static class Amount{
        public static final double MIN_AMOUNT = 0.01;           //最小金额（两位小数）

        public static final double MAX_AMOUNT = 9999999999.99;   //最大金额（十位整数以及两位小数）

        public static final double MIN_SCHEDULE = 0;         //最小比列

        public static final double MAX_SCHEDULE = 100;         //最小比列
    }

    /**
     * 时间戳，方便确定时间区间
     */
    public static class Time{
        public static final long MILLI_SECOND_OF_DAY = ((24*60*60*1000) - 1);
    }

    /**
     * Excel错误提示
     */
    public static class ExcelException{

        //数据源中没有任何数据提示
        public static final String EMPTY_RECOURCE = "数据源中没有任何数据";
        //存在重复数据
        public static final String REPEAT_ROW ="Excel中有重复行，请检查";
        //excel导入失败提示
        public static final String IMPORT_ERROR = "导入Excel失败";
    }

    /**
     * 保存提示信息
     */
    public static class SaveInfo{

        // 成本项目
        public static final String COST_PROJECT = "0301";
    }
}
