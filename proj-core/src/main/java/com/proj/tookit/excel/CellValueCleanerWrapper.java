package com.proj.tookit.excel;


import com.proj.tookit.excel.cell.CellValueCleaner;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.Map;

public abstract class CellValueCleanerWrapper<T> {

    private Map<T, CellValueCleaner> cleanerMap = new HashMap<>();


    public CellValueCleanerWrapper() {

    }

    public CellValueCleanerWrapper(Map<T, CellValueCleaner> cleanerMap) {
        this.cleanerMap = cleanerMap;
    }

    public Map<T, CellValueCleaner> getCleanerMap() {
        return cleanerMap;
    }

    public void setCleanerMap(Map<T, CellValueCleaner> cleanerMap) {
        this.cleanerMap = cleanerMap;
    }

    public void putCleaner(T key, CellValueCleaner cleaner){
        this.cleanerMap.put(key, cleaner);
    }

    public void removeCleaner(T key){
        this.cleanerMap.remove(key);
    }

    public CellValueCleaner getCleaner(T key){
        return this.cleanerMap.get(key);
    }


    public abstract CellValueCleaner filter(Cell cell);

}
