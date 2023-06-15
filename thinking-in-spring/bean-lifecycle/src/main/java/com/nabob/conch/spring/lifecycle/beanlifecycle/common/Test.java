package com.nabob.conch.spring.lifecycle.beanlifecycle.common;

import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.List;

/**
 * @author Adam
 * @date 2020/5/14
 */
public class Test {

    private HashMap<Integer, List<String>> myMap;

    public void example() throws NoSuchFieldException {
        ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
        t.getSuperType(); // AbstractMap<Integer, List<String>>
        t.asMap(); // Map<Integer, List<String>>
        t.getGeneric(0).resolve(); // Integer
        t.getGeneric(1).resolve(); // List
        t.getGeneric(1); // List<String>
        t.resolveGeneric(1, 0); // String
    }

}
