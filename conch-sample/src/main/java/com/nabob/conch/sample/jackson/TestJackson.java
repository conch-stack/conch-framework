package com.nabob.conch.sample.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam
 * @since 2024/12/24
 */
public class TestJackson {

    /**
     * https://blog.csdn.net/qq_43437874/article/details/137085346
     */
    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        System.out.println(mapper.getTypeFactory().constructType(TestProps.class).toCanonical());

        TestProps data1 = new TestProps();
        data1.setTargetId(1);
        data1.setTaskName("task1");

        TestProps data2 = new TestProps();
        data2.setTargetId(2);
        data2.setTaskName("task2");

        // single
        TestTaskInfo<TestProps> info =  new TestTaskInfo<>();
        info.setType(null);
        info.setClassName(TestProps1.class.getName());
        info.setData(data1);

        String json = mapper.writeValueAsString(info);
        System.out.println(json);

        TestTaskInfo<?> rs = (TestTaskInfo<?>) mapper.readValue(json.getBytes(StandardCharsets.UTF_8), TestTaskInfo.class);
        TestProps1 dataRs = (TestProps1) rs.getData();
        System.out.println(dataRs);

        // null
        info.setData(null);
        String jsonNull = mapper.writeValueAsString(info);
        System.out.println(jsonNull);

        TestTaskInfo<?> rsjsonNull = (TestTaskInfo<?>) mapper.readValue(jsonNull.getBytes(StandardCharsets.UTF_8), TestTaskInfo.class);
        TestProps1 dataRsNull = (TestProps1) rsjsonNull.getData();
        System.out.println(dataRsNull);

        // list
        TestTaskInfo<List<TestProps>> list =  new TestTaskInfo<>();
        list.setType(0);
        list.setClassName(TestProps1.class.getName());
        list.setData(Lists.newArrayList(data1, data2));

        String json1 = mapper.writeValueAsString(list);
        System.out.println(json1);

        TestTaskInfo<?> rs1 = (TestTaskInfo<?>) mapper.readValue(json1.getBytes(StandardCharsets.UTF_8), TestTaskInfo.class);
        List<TestProps1> dataRs1 = (List<TestProps1>) rs1.getData();
        System.out.println(dataRs1);

        // empty list
        list.setData(Lists.newArrayList());
        String jsonEmptyList = mapper.writeValueAsString(list);
        System.out.println(jsonEmptyList);

        TestTaskInfo<?> rs1jsonEmptyList = (TestTaskInfo<?>) mapper.readValue(jsonEmptyList.getBytes(StandardCharsets.UTF_8), TestTaskInfo.class);
        List<TestProps1> dataRs1jsonEmptyList = (List<TestProps1>) rs1jsonEmptyList.getData();
        System.out.println(dataRs1jsonEmptyList);
    }

    @Data
    @JsonDeserialize(using = RawJsonDeserializer.class)
    public static class TestTaskInfo<T> {

        // 0-list
        private Integer type;

        // todo cache
        private String className;

        //        @JsonSerialize()
//        @JsonDeserialize(using = RawJsonDeserializer.class)
        private T data;
    }

    @Data
    public static class TestProps {
        private Integer targetId;
        private String taskName;
    }
    @Data
    public static class TestProps1 {
        private Integer targetId;
        private String taskName;
    }

    /**
     * 将字节数组转换成对象
     * 对于简单对象来说type就是其类的全限定名，如java.lang.String;
     * 对于复杂列表对象来说，type例子：java.util.Map<java.lang.String, java.util.List<test.Person>>
     */
    public static Object byteToObject(byte[] bytes, String type) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructFromCanonical(type);
        try {
            Object object = mapper.readValue(bytes, javaType);
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class RawJsonDeserializer extends JsonDeserializer<TestTaskInfo> {

        public RawJsonDeserializer() {
        }

        @Override
        public TestTaskInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode jsonNode = jp.getCodec().readTree(jp);

            Integer type = getType(jsonNode);
//            boolean isCollection = type != null && type == 0;

            String className = jsonNode.get("className").asText();
            JsonNode dateJsonNode = jsonNode.get("data");
            if (dateJsonNode == null || dateJsonNode.isNull()) {
                TestTaskInfo rs = new TestTaskInfo();
                rs.setType(type);
                rs.setClassName(className);
                rs.setData(null);
                return rs;
            }

            if (dateJsonNode.isEmpty()) {
                TestTaskInfo rs = new TestTaskInfo();
                rs.setType(type);
                rs.setClassName(className);
                rs.setData(Collections.emptyList());
                return rs;
            }

            boolean array = dateJsonNode.isArray();

            Class<?> entityClass = inferTypeClass(className);
            if (entityClass == null) {
                throw new IOException("entity class not found");
            }

            if (array) {
                Collection<?> data = deserializeListNode(jp, ctxt, entityClass, dateJsonNode);
                TestTaskInfo rs = new TestTaskInfo();
                rs.setType(type);
                rs.setClassName(className);
                rs.setData(data);
                return rs;
            } else {
                Object data = deserializeNode(jp, ctxt, entityClass, dateJsonNode);
                TestTaskInfo rs = new TestTaskInfo();
                rs.setType(type);
                rs.setClassName(className);
                rs.setData(data);
                return rs;
            }
        }

        private Class<?> inferTypeClass(String className) {
            // todo read from cache
            Class<?> aClass = doInferTypeClass(className);
            if (aClass == null) {
                // todo 寻找Entity
            }
            return aClass;
        }

        private Class<?> doInferTypeClass(String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        private Integer getType(JsonNode node) {
            JsonNode typeNode = node.get("type");
            if (typeNode != null) {
                return typeNode.asInt();
            }
            return null;
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
//
//        private <T> T deserializeComplexNode(JsonParser jp, DeserializationContext ctxt,
//                                      Class<T> targetTypeClass, JsonNode node) throws IOException {
//
//            JavaType javaType = ctxt.getTypeFactory().constructFromCanonical(targetTypeClass);
//            JsonDeserializer<?> deserializer = ctxt.findRootValueDeserializer(javaType);
//            JsonParser parser = node.traverse(jp.getCodec());
//            parser.nextToken();
//            return (T) deserializer.deserialize(parser, ctxt);
//        }

    }
}
