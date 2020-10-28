package com.proj.tookit.excel;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.proj.tookit.excel.cell.CellValueCleaner;
import org.apache.poi.ss.usermodel.Cell;

public class ColumnIndexCleanerWrapper extends CellValueCleanerWrapper<Integer> {


    public ColumnIndexCleanerWrapper() {
        super();
    }

    public ColumnIndexCleanerWrapper(int startIndexCloumn, CellValueCleaner... cellValueCleaners) {
        super();

        if (ObjectUtil.isEmpty(cellValueCleaners)) {
            return;
        }


        for (int i = 0; i < cellValueCleaners.length; i++) {
            super.putCleaner(startIndexCloumn + i, cellValueCleaners[i]);
        }
    }

    public ColumnIndexCleanerWrapper(int startIndexCloumn, int endIndexCloum, CellValueCleaner... cellValueCleaners) {
        super();

        if (ObjectUtil.isEmpty(cellValueCleaners)) {
            return;
        }


        for (int j = 0; j < endIndexCloum; j++) {
            for (int i = 0; i < cellValueCleaners.length; i++) {
                super.putCleaner(startIndexCloumn + j, cellValueCleaners[i]);
            }
        }

    }

    @Override
    public CellValueCleaner filter(Cell cell) {
        Assert.notNull(cell, "[参数校验失败] - the cell argument must not be null");
        int columnIndex = cell.getColumnIndex();
        return this.getCleaner(columnIndex);
    }
}
