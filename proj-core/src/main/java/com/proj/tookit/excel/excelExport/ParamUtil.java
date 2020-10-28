package com.proj.tookit.excel.excelExport;

import cn.hutool.core.collection.CollUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ParamUtil {

    // Map<String, String[]>转为Map<String, Object>
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map properties = request.getParameterMap() == null ? new HashMap<>() : request.getParameterMap();
        if (CollUtil.isEmpty(properties)) {
            return new HashMap<>();
        }
        // 返回值Map
        Map<String, Object> returnMap = new HashMap<>();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (String value1 : values) {
                    value = value1 + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * obj转list
     *
     * @param obj   obj
     * @param clazz 实体
     * @param <T>   实体
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
