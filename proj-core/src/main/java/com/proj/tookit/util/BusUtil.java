package com.proj.tookit.util;

import com.google.gson.Gson;
import com.youth.common.exception.SystemRuntimeException;

import java.util.List;

/**
 * @author smf
 * @description 业务工具类
 * @date 2020/4/30
 */
public class BusUtil {

    // 字典实现service名称
    public static final String DIC_ITEM_SERVICE = "dicItemServiceImpl";
    // 字典service名称
    public static final String DIC_SERVICE = "dicServiceImpl";




    /*  *//**
     * json转实体
     *
     * @param target
     * @param json
     * @param clazz
     *//*
    public static void ison2Entity(T target, String json, Class<T> clazz) {

        target = new Gson().fromJson(json, clazz);
    }*/

    /**
     * json转实体批量
     *
     * @param target
     * @param jsons
     * @param clazz
     */
    public static <T> void json2Entities(List<T> target, List<String> jsons, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            for (String json : jsons) {
                T t = clazz.newInstance();
                target.add(gson.fromJson(json, clazz));
            }
        } catch (Exception e) {
            throw new SystemRuntimeException("数据格式不正确，请修改后重试", e);
        }
    }


}
