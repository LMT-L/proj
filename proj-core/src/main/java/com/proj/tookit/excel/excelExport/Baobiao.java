package com.proj.tookit.excel.excelExport;

import java.util.HashMap;

/**
 * Baobiao  description
 *
 * @author hp
 * @version 18/05/18
 */
public class Baobiao {

    /**
     * rowNum
     */
    private int rowNum;

    /**
     * usageName
     */
    private String usageName;

    /**
     * provider
     */
    private HashMap<String, Integer> provider;

    /**
     * departType
     */
    private HashMap<String, Integer> departType;

    /**
     * getDepartType
     *
     * @return HashMap<String,Integer> r
     */
    public HashMap<String, Integer> getDepartType() {
        return departType;
    }

    /**
     * setDepartType
     *
     * @param departType p
     */
    public void setDepartType(HashMap<String, Integer> departType) {
        this.departType = departType;
    }

    /**
     * getProvider
     *
     * @return HashMap<String,Integer> r
     */
    public HashMap<String, Integer> getProvider() {
        return provider;
    }

    /**
     * setProvider
     *
     * @param provider p
     */
    public void setProvider(HashMap<String, Integer> provider) {
        this.provider = provider;
    }

    /**
     * getRowNum
     *
     * @return int r
     */
    public int getRowNum() {
        return rowNum;
    }

    /**
     * setRowNum
     *
     * @param rowNum p
     */
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    /**
     * getUsageName
     *
     * @return String r
     */
    public String getUsageName() {
        return usageName;
    }

    /**
     * setUsageName
     *
     * @param usageName p
     */
    public void setUsageName(String usageName) {
        this.usageName = usageName;
    }
}



