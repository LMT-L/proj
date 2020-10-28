package com.proj.tookit.util;

import java.util.ArrayList;
import java.util.List;

public class ListsUtil {

    /**
     * 按指定大小拆分List
     * @param list
     * @param unitSize
     * @param <T>
     * @return
     */
    public static  <T> List<List<T>> splitList(List<T> list, int unitSize) {
        if (list == null || list.isEmpty() || unitSize < 1)
        {
            return null;
        }

        int wholeSize = list.size();
        List<List<T>> result = new ArrayList<List<T>>();

        int start = 0;
        int end = 0;
        while(start < wholeSize){
            end = start + unitSize;
            if(end > wholeSize){
                end = wholeSize;
            }
            List<T> subList = list.subList(start, end);
            result.add(subList);
            start = start + unitSize;
        }
        return result;
    }

}
