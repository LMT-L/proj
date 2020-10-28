package com.proj.tookit.excel.validator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.proj.tookit.excel.AliasText;
import com.proj.tookit.excel.ErrorInfo;
import com.proj.tookit.excel.ErrorMsg;
import com.proj.tookit.excel.row.RowData;
import com.youth.common.exception.SystemRuntimeException;
import com.youth.tool.common.ReflectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;


@Component
public class ExcelValidatorChain implements InitializingBean, ApplicationContextAware {

    private static List<ExcelValidator> chain = new ArrayList<>();
    private static ApplicationContext applicationContext = null;


    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }


    @Override
    public void afterPropertiesSet(){
        Assert.notNull(applicationContext, "应用上下文不能为空");
        Map<String, ExcelValidator> validatorMap = applicationContext.getBeansOfType(ExcelValidator.class);
        Assert.notEmpty(validatorMap, "validatorMap 获取失败");

        for(String key : validatorMap.keySet()){
            ExcelValidator validator = validatorMap.get(key);
            Order orderTarget =validator.getClass().getAnnotation(Order.class);
            int order = orderTarget.value();
            validator.setOrder(order);
            chain.add(validatorMap.get(key));
        }
        Collections.sort(chain);
    }

    public List<ErrorInfo> valid(List<RowData> list, Class beanType, List<Class> excelValidators){
        if(CollectionUtil.isEmpty(list)){
            return null;
        }

        Map<String, Class> propClazzMap = new HashMap<>();
        Map<String, String> propAliasMap = new HashMap<>();

        /*
         *  空字段处理
         */
        Iterator<String> iter = list.get(0).getData().keySet().iterator();
        while (iter.hasNext()){
            String key = iter.next();
            Field field = ReflectUtil.findField(beanType, key);
            if (field == null){
                throw new SystemRuntimeException("请在Excel规定位置填入数据");
            }
            Class fieldType = field.getType();
            propClazzMap.put(key, fieldType);
            AliasText annotation = field.getAnnotation(AliasText.class);
            if (annotation != null){
                propAliasMap.put(key, annotation.value());
            }
        }


        Map<Integer, ErrorInfo> errorInfoMap = new HashMap<>();
        List<ErrorInfo> results = new ArrayList<>();
        for(ExcelValidator validator : chain){
            // 判断是否为需要用到的校验器
            if(!validator.supports() && !judgmentType(validator, excelValidators)) {
                continue;
            }
            List<ErrorMsg> errorMsgs = validator.valid(list, propClazzMap, propAliasMap, beanType);

            for(ErrorMsg errorMsg : errorMsgs){
                int rowIndex = errorMsg.getRowIndex();
                ErrorInfo errorInfo = errorInfoMap.get(rowIndex);
                if(errorInfo == null){
                    errorInfo = new ErrorInfo(rowIndex);
                    errorInfoMap.put(rowIndex, errorInfo);
                }
                errorInfo.addErrorMsg(errorMsg);
            }
        }

        for(Integer key : errorInfoMap.keySet()){
            results.add(errorInfoMap.get(key));
        }



        return results;
    }


    public List<ErrorInfo> valid4sheet(List<RowData> list, Class beanType, List<Class> excelValidators,int sheetIndex){
        if(CollectionUtil.isEmpty(list)){
            return null;
        }

        Map<String, Class> propClazzMap = new HashMap<>();
        Map<String, String> propAliasMap = new HashMap<>();

        /*
         *  空字段处理
         */
        Iterator<String> iter = list.get(0).getData().keySet().iterator();
        while (iter.hasNext()){
            String key = iter.next();
            Field field = ReflectUtil.findField(beanType, key);
            if (field == null){
                throw new SystemRuntimeException("请在Excel规定位置填入数据");
            }
            Class fieldType = field.getType();
            propClazzMap.put(key, fieldType);
            AliasText annotation = field.getAnnotation(AliasText.class);
            if (annotation != null){
                propAliasMap.put(key, annotation.value());
            }
        }


        Map<Integer, ErrorInfo> errorInfoMap = new HashMap<>();
        List<ErrorInfo> results = new ArrayList<>();
        for(ExcelValidator validator : chain){
            // 判断是否为需要用到的校验器
            if(!validator.supports() && !judgmentType(validator, excelValidators)) {
                continue;
            }
            List<ErrorMsg> errorMsgs = validator.valid(list, propClazzMap, propAliasMap, beanType);
            errorMsgs.forEach(
                    item -> {
                        item.setSheetIndex(sheetIndex);
                    }
            );

            for(ErrorMsg errorMsg : errorMsgs){
                int rowIndex = errorMsg.getRowIndex();
                ErrorInfo errorInfo = errorInfoMap.get(rowIndex);
                if(errorInfo == null){
                    errorInfo = new ErrorInfo(rowIndex);
                    errorInfoMap.put(rowIndex, errorInfo);
                }
                errorInfo.addErrorMsg(errorMsg);
            }
        }

        for(Integer key : errorInfoMap.keySet()){
            results.add(errorInfoMap.get(key));
        }



        return results;
    }

    /**
     * 判断业务校验器在此次导入过程中是否可用
     * @param excelValidator
     * @param excelValidators
     * @return
     */
    private boolean judgmentType(ExcelValidator excelValidator, List<Class> excelValidators) {
        if (excelValidator == null || excelValidators == null || excelValidators.size() == 0) {
            return false;
        }
        for (Class item : excelValidators) {
            if (item  == excelValidator.getClass()) {
                return true;
            }
        }
        return false;
    }
}
