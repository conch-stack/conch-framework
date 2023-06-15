package com.nabob.conch.spring.dependency.injection.setter.alias;

import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Adam
 * @date 2020/4/28
 */
public class TestGetChildClass {

    public static void main(String[] args) {

        Reflections reflections = new Reflections("com.nabob.conch.spring");
        Set<Class<? extends AliasInterface>> subTypes = reflections.getSubTypesOf(AliasInterface.class);
        subTypes.stream().forEach(a -> System.out.println(a.getSimpleName()));

        System.out.println("============================================================");

        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);
        typesAnnotatedWith.stream().forEach(a -> System.out.println(a.getSimpleName()));

    }
}
