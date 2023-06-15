package com.nabob.conch.core.ioc;

import com.nabob.conch.core.ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import com.nabob.conch.core.beans.BeanContainer;
import com.nabob.conch.core.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
public class Ioc {

    /**
     * Bean 容器
     */
    private BeanContainer beanContainer;

    public Ioc() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行 Ioc
     */
    public void doIoc() {
        for (Class<?> clz : beanContainer.getClasses()) { //遍历 Bean 容器中所有的 Bean
            final Object targetBean = beanContainer.getBean(clz);
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) { //遍历 Bean 中的所有属性
                if (field.isAnnotationPresent(Autowired.class)) {// 如果该属性被 Autowired 注解，则对其注入
                    final Class<?> fieldClass = field.getType();
                    Object fieldValue = getClassInstance(fieldClass);
                    if (null != fieldValue) {
                        ClassUtil.setField(field, targetBean, fieldValue);
                    } else {
                        throw new RuntimeException("无法注入对应的类，目标类型：" + fieldClass.getName());
                    }
                }
            }
        }
    }

    /**
     * 根据 Class 获取其实例或者实现类
     */
    private Object getClassInstance(final Class<?> clz) {
        return Optional
                .ofNullable(beanContainer.getBean(clz))
                .orElseGet(() -> {
                    Class<?> implementClass = getImplementClass(clz);
                    if (null != implementClass) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    /**
     * 获取接口的实现类
     */
    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        return beanContainer.getClassesBySuper(interfaceClass)
                .stream()
                .findFirst()
                .orElse(null);
    }

}