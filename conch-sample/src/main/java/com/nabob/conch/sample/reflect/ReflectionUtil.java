package com.nabob.conch.sample.reflect;

import com.nabob.conch.sample.uitl.ClassHelper;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam
 * @since 2024/10/14
 */
public class ReflectionUtil {

    /**
     * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
     */
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);


    /**
     * 获取class上的所有属性,包括其父类的私有属性
     *
     * @param onlyCustom -是否只包含自定义的属性
     *                   即:true表示不包含从Object继承的属性
     */
    public static Field[] getAllFields(Class cls, boolean onlyCustom) {

        Set<Field> fields = new HashSet<>();
        while (cls != Object.class && cls != null) {
            for (Field method : cls.getDeclaredFields()) {
                fields.add(method);
            }
            cls = cls.getSuperclass();
        }
        if (!onlyCustom && cls == Object.class) {
            for (Field method : cls.getDeclaredFields()) {
                fields.add(method);
            }
        }
        return fields.toArray(new Field[fields.size()]);
    }

    public static Object safeGet(Object target, Field field) {
        try {
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field.get(target);
            }
        } catch (IllegalAccessException e) {
            return ClassHelper.getDefaultValue(field.getType());//返回默认值
        }
    }
}
