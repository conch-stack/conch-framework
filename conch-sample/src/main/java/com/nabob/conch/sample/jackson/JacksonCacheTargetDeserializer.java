package com.nabob.conch.sample.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * JacksonDeserializer
 * <p>
 * 为不同项目中对于同一个库的同一个表，使用不同的Entity定义
 * 考虑SQL场景只有T 与 List<T>
 *
 * @author Adam
 * @since 2024/12/25
 */
@SuppressWarnings("all")
public final class JacksonCacheTargetDeserializer extends JsonDeserializer<CacheTargetV1> {

    private static final String DATA = "d";
    private static final String CLASS_NAME = "c";
    private static final String SIZE = "s";

    private static final String DB_NAME = "db";
    private static final String TABLE_NAME = "tb";

    private static final Map<String, Class<?>> CACHE = Maps.newHashMap();

    public JacksonCacheTargetDeserializer() {
    }

    @Override
    public CacheTargetV1 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootJsonNode = jp.getCodec().readTree(jp);

        JsonNode dateJsonNode = rootJsonNode.get(DATA);

        // null
        if (dateJsonNode == null || dateJsonNode.isNull()) {
            return CacheTargetV1.buildCache(null, null);
        }
        // empty collection
        if (dateJsonNode.isArray() && dateJsonNode.isEmpty()) {
            return CacheTargetV1.buildCache(Collections.emptyList(), null);
        }

        JsonNode classNameJsonNode = rootJsonNode.get(CLASS_NAME);
        if (classNameJsonNode == null || classNameJsonNode.isNull()) {
            throw new IOException("entity class name missed");
        }

        String className = classNameJsonNode.asText();
        Class<?> entityClass = inferTypeClass(className, rootJsonNode);
        if (entityClass == null) {
            throw new IOException("entity class not found");
        }

        JsonNode sizeJsonNode = rootJsonNode.get(SIZE);
        Integer size = null;
        if (sizeJsonNode != null && !sizeJsonNode.isNull()) {
            size = sizeJsonNode.asInt();
        }

        if (dateJsonNode.isArray()) {
            Collection<?> data = deserializeListNode(jp, ctxt, entityClass, dateJsonNode);
            return CacheTargetV1.buildCache(data, className, size, entityClass);
        } else {
            Object data = deserializeNode(jp, ctxt, entityClass, dateJsonNode);
            return CacheTargetV1.buildCache(data, className, size, entityClass);
        }
    }

    private Class<?> inferTypeClass(String className, JsonNode rootJsonNode) {
        // read from cache
        Class<?> cacheClass = CACHE.get(className);
        if (cacheClass != null) {
            if (cacheClass == Object.class) {
                return null;
            }
            return cacheClass;
        }

        Class<?> aClass = doInferTypeClass(className);
        if (aClass == null) {
            // try to guess entity class
            aClass = guessEntityClass(rootJsonNode);
        }

        if (aClass != null) {
            CACHE.put(className, aClass);
        } else {
            CACHE.put(className, Object.class);
        }
        return aClass;
    }

    private Class<?> guessEntityClass(JsonNode rootJsonNode) {
        JsonNode dbNameJsonNode = rootJsonNode.get(DB_NAME);
        if (dbNameJsonNode == null || dbNameJsonNode.isNull()) {
            return null;
        }

        JsonNode tableNameJsonNode = rootJsonNode.get(TABLE_NAME);
        if (tableNameJsonNode == null || tableNameJsonNode.isNull()) {
            return null;
        }

        String dbName = dbNameJsonNode.asText();
        String tableName = tableNameJsonNode.asText();

        // todo load from other system
//        return DCacheObjectHolder.guessEntityClass(dbName, tableName);
        return null;
    }

    private Class<?> doInferTypeClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private <T> Collection<T> deserializeListNode(JsonParser jp, DeserializationContext ctxt,
                                                  Class<T> targetTypeClass, JsonNode node) throws IOException {

        JavaType javaType = ctxt.getTypeFactory().constructCollectionType(Collection.class, targetTypeClass);
        JsonDeserializer<?> deserializer = ctxt.findRootValueDeserializer(javaType);
        JsonParser parser = node.traverse(jp.getCodec());
        parser.nextToken();
        return (Collection<T>) deserializer.deserialize(parser, ctxt);
    }

    private <T> T deserializeNode(JsonParser jp, DeserializationContext ctxt,
                                  Class<T> targetTypeClass, JsonNode node) throws IOException {

        JavaType javaType = ctxt.getTypeFactory().constructType(targetTypeClass);
        JsonDeserializer<?> deserializer = ctxt.findRootValueDeserializer(javaType);
        JsonParser parser = node.traverse(jp.getCodec());
        parser.nextToken();
        return (T) deserializer.deserialize(parser, ctxt);

    }
}
