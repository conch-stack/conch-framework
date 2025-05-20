package com.nabob.conch.sample.jackson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

/**
 * CacheTargetV1
 *
 * @author Adam
 * @since 2024/8/30
 */
@Getter
@JsonDeserialize(using = JacksonCacheTargetDeserializer.class)
public class CacheTargetV1<T> {

    /**
     * data
     */
    private T d;

    /**
     * the class name of data, if data is a collection, this will be the class name of item
     */
    private String c;

    /**
     * size of entity default null
     */
    private Integer s;

    //region for enableEntityHybrid
    /**
     * Real DB Name （SchemeName）
     */
    private String db;

    /**
     * Real Table Name
     */
    private String tb;
    //endregion for enableEntityHybrid

    /**
     * the class of data or list item
     */
    private Class<?> dc;

    private CacheTargetV1(T date, String className, Integer size) {
        this.d = date;
        this.c = className;
        this.s = size;
    }

    public CacheTargetV1<T> wrapForEntityHybrid(String schemaName, String tableName) {
        this.db = schemaName;
        this.tb = tableName;
        return this;
    }

    public static <T> CacheTargetV1<T> buildCache(T data, String className) {
        return new CacheTargetV1<>(data, className, null);
    }

    public static <T> CacheTargetV1<T> buildCache(T data, String className, Integer size) {
        return new CacheTargetV1<>(data, className, size);
    }

    public static <T> CacheTargetV1<T> buildCache(T data, String className, Integer size, Class<?> dataClass) {
        CacheTargetV1<T> cacheTarget = new CacheTargetV1<>(data, className, size);
        cacheTarget.dc = dataClass;
        return cacheTarget;
    }

}
