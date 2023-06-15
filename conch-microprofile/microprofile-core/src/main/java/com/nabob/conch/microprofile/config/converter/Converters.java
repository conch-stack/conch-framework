package com.nabob.conch.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Converters
 *
 * @author Adam
 * @since 2021/8/2
 */
public class Converters {
    
    static final Converter<String> STRING_CONVERTER = value -> value;

    static final Converter<Boolean> BOOLEAN_CONVERTER = value -> {
        if (value != null) {
            return "TRUE".equalsIgnoreCase(value)
                    || "1".equalsIgnoreCase(value)
                    || "YES".equalsIgnoreCase(value)
                    || "Y".equalsIgnoreCase(value)
                    || "ON".equalsIgnoreCase(value)
                    || "JA".equalsIgnoreCase(value)
                    || "J".equalsIgnoreCase(value)
                    || "OUI".equalsIgnoreCase(value);
        }
        return null;
    };

    static final Converter<Double> DOUBLE_CONVERTER = value -> value != null ? Double.valueOf(value) : null;

    static final Converter<Float> FLOAT_CONVERTER = value -> value != null ? Float.valueOf(value) : null;
    
    static final Converter<Long> LONG_CONVERTER = value -> value != null ? Long.valueOf(value) : null;
    
    static final Converter<Integer> INTEGER_CONVERTER = value -> value != null ? Integer.valueOf(value) : null;
    
    static final Converter<Class<?>> CLASS_CONVERTER = value -> {
        try {
            return value != null ? Class.forName(value) : null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    };

    public static final Map<Type, Converter<?>> ALL_CONVERTERS = new HashMap<>();

    static {
        ALL_CONVERTERS.put(String.class, STRING_CONVERTER);

        ALL_CONVERTERS.put(Boolean.class, BOOLEAN_CONVERTER);
        // boolean | primitive type
        ALL_CONVERTERS.put(Boolean.TYPE, BOOLEAN_CONVERTER);

        ALL_CONVERTERS.put(Double.class, DOUBLE_CONVERTER);
        ALL_CONVERTERS.put(Double.TYPE, DOUBLE_CONVERTER);

        ALL_CONVERTERS.put(Float.class, FLOAT_CONVERTER);
        ALL_CONVERTERS.put(Float.TYPE, FLOAT_CONVERTER);

        ALL_CONVERTERS.put(Long.class, LONG_CONVERTER);
        ALL_CONVERTERS.put(Long.TYPE, LONG_CONVERTER);

        ALL_CONVERTERS.put(Integer.class, INTEGER_CONVERTER);
        ALL_CONVERTERS.put(Integer.TYPE, INTEGER_CONVERTER);

        ALL_CONVERTERS.put(Class.class, CLASS_CONVERTER);
    }
}
