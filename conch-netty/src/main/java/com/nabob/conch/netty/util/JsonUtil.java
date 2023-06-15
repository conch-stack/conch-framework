package com.nabob.conch.netty.util;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Json Util
 *
 * @author Adam
 * @since 2022/1/20
 */
public final class JsonUtil {

    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return GSON.fromJson(jsonStr, clazz);
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
}
