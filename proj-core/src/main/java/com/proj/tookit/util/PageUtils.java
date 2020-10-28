package com.proj.tookit.util;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youth.common.exception.SystemRuntimeException;
import com.youth.common.global.bean.PageRequestBody;
import com.youth.common.global.cnst.SystemErrorEnum;

import java.util.List;


/**
 * @author wuliwei
 * @title: PageUtils
 * @projectName proj
 * @description: 分页工具类
 * @date 2020/4/23 15:06
 */
public class PageUtils {

    public static Page checkAndGenPage(PageRequestBody page){
        if(ObjectUtil.isNull(page) || ObjectUtil.isNull(page.getParams()) || 0 >= page.getPageNum()|| 0 >= page.getPageSize()){
            throw new SystemRuntimeException(SystemErrorEnum.PARAM_ERROR);
        }
        return new Page(page.getPageNum(),page.getPageSize());
    }

    /**
     * list转为page lds
     * @param page
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Page<T> listToPage(Page<T> page, List<T> list){
        page.setTotal(list.size());
        int size = (int)(page.getSize());
        int current = (int)(page.getCurrent());
        if(list.size()>current * size) {
            list =   list.subList((current - 1) * size, current * size);
        }else {
            list =   list.subList((current-1)*size, list.size());
        }
        return page.setRecords(list);
    }
}
