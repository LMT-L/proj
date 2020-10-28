package com.proj.base.enums;

/**
 * 系统枚举
 *
 * @author lds
 * @date 2020/07/10
 */
public class SystemEnum {

    /**
     * 是否导入
     */
    public enum IfImport {

        IMPORT("导入","1"),
        UNIMPORT("非导入","0");

        private final String name;

        private final String code;

        IfImport(String name, String code) {
            this.name = name;
            this.code = code;
        }


        public String getName() {
            return this.name;
        }

        public String getCode() {
            return this.code;
        }
    }

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

    /**
     * 读取excel sheet页
     */
    public enum ReadExcelSheetIndex {

        FIRST(0), // 第一页
        SECOND(1), // 第二页
        THIRD(2), // 第三页
        FOURTH(3), // 第四页
        FIFTH(4), // 第五页
        SIXTH(5), // 第六页
        SEVENTH(6), // 第七页
        EIGHTH(7), // 第八页
        NINTH(8), // 第九页
        TENTH(9), // 第十页
        ELEVENTH(10), // 第十一页
        TWELFTH(11), // 第十二页
        THIRTEENTH(12), // 第十三页
        FOURTEENTH(13), // 第十四页
        FIFTEENTH(14), // 第十五页
        SIXTEENTH(15), // 第十六页
        SEVENTEENTH(16); // 第十七页


        private final int type;

        ReadExcelSheetIndex(int type){
            this.type = type;
        }

        public int getValue(){
            return this.type;
        }
    }
}
