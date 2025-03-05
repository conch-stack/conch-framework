//package com.nabob.conch.sample.jackson;
//
//import com.ctrip.train.ztrain.dcache.support.util.JsonUtil;
//import redis.clients.util.SafeEncoder;
//
//public class RedisSerializationUtils {
//
//    private static Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(JsonUtil.objectMapper);
//
//    public static String toJSONString(Object object) {
//        return toString(serializer.serialize(object));
//    }
//
//    public static byte[] toJSONByte(Object object) {
//        return serializer.serialize(object);
//    }
//
//    public static <T> T parseObject(String text, Class<T> clazz) {
//        return serializer.deserialize(toBytes(text), clazz);
//    }
//
//    public static <T> T parseObject(byte[] textByte, Class<T> clazz) {
//        return serializer.deserialize(textByte, clazz);
//    }
//
//    public static byte[] toBytes(String source) {
//        return source == null ? null : SafeEncoder.encode(source);
//    }
//
//    public static String toString(byte[] source) {
//        return source == null ? null : SafeEncoder.encode(source);
//    }
//}
