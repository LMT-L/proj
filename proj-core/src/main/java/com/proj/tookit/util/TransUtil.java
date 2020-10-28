package com.proj.tookit.util;

import com.proj.tookit.excel.row.RowData;

import com.youth.common.exception.SystemRuntimeException;
import com.youth.common.global.cnst.SystemErrorEnum;
import com.youth.tool.bean.tran.TranUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 将map转化为实体类
 *
 * @author zhanghe
 * @date 2019-04-18 13:42
 */
public class TransUtil {
    // 需要模糊查询的属性集合
    private static String[] likeFields = {"no", "name", "contractName", "flowNodeName", "recepveunitNo", "customer", "recepveunitName", "projectName",
            "proName", "applicant","customerName", "tenderNo","managerName","manager","userName","admissionUser","departureUser","address", "incomeProName", "incomeProNo","travelLocation"};
    private static final Logger LOGGER = LoggerFactory.getLogger(TransUtil.class);
    /**
     * Map转化为实体类，替换查询条件的%和&
     *
     * @param params
     * @param target
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T copyBeanByQueryMap(Map<String, Object> params, T target, Class<T> clazz) {
        try {
            Map<String, Object> queryFilter = (Map<String, Object>) params.get("queryFilter");
            for (String field : likeFields) {
                Object str = queryFilter.get(field);
                if (str != null) {
                    String queryStr = str.toString().trim();
                    // 替换%和&，添加转义
                    queryFilter.put(field, "%" + queryStr.replaceAll("\u0025", "\u005c\u005c\u005c\u005c\u0025").replaceAll("\u005f", "\u005c\u005c\u005c\u005c\u005f") + "%");
                }
            }
            return TranUtil.copyBeanBySMap(queryFilter, target, clazz);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new SystemRuntimeException(SystemErrorEnum.PARAM_ERROR);
        }
    }

    /**
     * Map转化为实体类
     *
     * @param params
     * @param target
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T copyBeanByMap(Map<String, Object> params, T target, Class<T> clazz) {
        try {
            return TranUtil.copyBeanBySMap(params, target, clazz);
        } catch (Exception e) {
            throw new SystemRuntimeException("数据格式不正确，请修改后重试", e);
        }
    }

    /**
     * List<Map></>转换为实体类数组
     *
     * @param target
     * @param maps
     * @param clazz
     * @param <T>
     */
    public static <T> void copyBeansByMaps(List<T> target, List<Map<String, Object>> maps, Class<T> clazz) {
        try {
            for (Map<String, Object> map : maps) {
                T t = clazz.newInstance();
                TranUtil.copyBeanBySMap(map, t, clazz);
                target.add(t);
            }
        } catch (Exception e) {
            throw new SystemRuntimeException("数据格式不正确，请修改后重试", e);
        }
    }

    /**
     * List<RowData></>转换为实体类数组
     *
     * @param target
     * @param maps
     * @param clazz
     * @param <T>
     */
    public static <T> void copyBeansByRowDatas(List<T> target, List<RowData> maps, Class<T> clazz) {
        try {
            for (RowData map : maps) {
                T t = clazz.newInstance();
                TranUtil.copyBeanBySMap(map.getData(), t, clazz);
                target.add(t);
            }
        } catch (Exception e) {
            throw new SystemRuntimeException("数据格式不正确，请修改后重试", e);
        }
    }
}
