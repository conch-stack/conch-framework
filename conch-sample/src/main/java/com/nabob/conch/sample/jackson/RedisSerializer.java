package com.nabob.conch.sample.jackson;

interface RedisSerializer {

    <T> byte[] serialize(T source) throws SerializationException;

    <T> T deserialize(byte[] source, Class<T> type) throws SerializationException;
}
