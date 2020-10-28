package com.proj.tookit.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ExcelCellStyle {

    private HorizontalAlignment align;

    private BorderStyle border;

    private String fontName;

    private Short fontSize;

    private Short fontColor;

    private Short fontWeight;

    public ExcelCellStyle(){
        this.align = HorizontalAlignment.CENTER;
        this.border = BorderStyle.THIN;
        this.fontName = "微软雅黑";
        this.fontColor = 8;
        this.fontSize = (short) 10;
        this.fontWeight = 400;
    }

    public HorizontalAlignment getAlign() {
        return align;
    }

    public void setAlign(HorizontalAlignment align) {
        this.align = align;
    }

    public BorderStyle getBorder() {
        return border;
    }

    public void setBorder(BorderStyle border) {
        this.border = border;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Short getFontSize() {
        return fontSize;
    }

    public void setFontSize(Short fontSize) {
        this.fontSize = fontSize;
    }

    public Short getFontColor() {
        return fontColor;
    }

    public void setFontColor(Short fontColor) {
        this.fontColor = fontColor;
    }

    public Short getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(Short fontWeight) {
        this.fontWeight = fontWeight;
    }
}
