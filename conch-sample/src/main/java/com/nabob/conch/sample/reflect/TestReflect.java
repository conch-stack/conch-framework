package com.nabob.conch.sample.reflect;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.uitl.ClassHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Adam
 * @since 2024/10/14
 */
public class TestReflect {


    /**
     * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
     */
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);

    public static void main(String[] args) throws NoSuchFieldException {

        List<User> users = new ArrayList<>();
        User user1 = new User("原始name1", 1000);
        User user2 = new User("原始name2", 2000);

        users.add(user1);
        users.add(user2);

        reflect(user1, "name_zjz");

        reflect(user1, "name_zjz");

        reflect(user2, "name_zjz");

        reflect(users, "name_zjz");
    }

    public static <T> void reflect(T target, String columnName) throws NoSuchFieldException {
        if (target instanceof Collection) {
            Collection collection = (Collection) target;
            for (Object object : collection) {
                Field declaredField = getDeclaredField(object.getClass(), columnName);
                System.out.println(safeGet(object, declaredField).toString());
            }
        } else {
            Field declaredField = getDeclaredField(target.getClass(), columnName);
            System.out.println(safeGet(target, declaredField).toString());
        }
    }

    public static Field getDeclaredField(Class<?> clazz, String columnName) {
//        Field[] declaredFields = clazz.getDeclaredFields();
        Field[] declaredFields = getAllFields(clazz);
//            declaredFieldsCache.put(object.getClass(), declaredFields);

        for (Field field : declaredFields) {
//            field.setAccessible(true);
            FirstAnnotation annotation = field.getAnnotation(FirstAnnotation.class);
            if (annotation != null) {
                if (annotation.value().equals(columnName)) {
                    return field;
                }
            }
        }
        return null;
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

    public static Field[] getAllFields(Class<?> clazz) {
        try {
            Field[] cacheFields = declaredFieldsCache.get(clazz);
            if (Objects.nonNull(cacheFields)) {
                return cacheFields;
            }

            Field[] declaredFields = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, declaredFields);
            return declaredFields;
        } catch (Throwable t) {
            return null;
        }
    }
}
