package com.nabob.conch.sample.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;

import java.io.IOException;

class Jackson2JsonRedisSerializer implements RedisSerializer {

    static final byte[] EMPTY_ARRAY = new byte[0];

    private final ObjectMapper mapper;

    public Jackson2JsonRedisSerializer(ObjectMapper mapper) {
        Assert.notNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;
    }

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @Override
    public <T> byte[] serialize(T source) throws SerializationException {
        if (source == null) {
            return EMPTY_ARRAY;
        }

        try {
            return mapper.writeValueAsBytes(source);
        } catch (IOException ex) {
            String message = String.format("Could not write JSON: %s", ex.getMessage());
            throw new SerializationException(message, ex);
        }
    }

    @Override
    public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
        Assert.notNull(type, "Deserialization type must not be null;"
            + " Please provide Object.class to make use of Jackson2 default typing.");
        if (isEmpty(source)) {
            return null;
        }

        try {
            return mapper.readValue(source, 0, source.length, type);
        } catch (Exception ex) {
            String message = String.format("Could not Deserialize:%s ", ex.getMessage());
            throw new SerializationException(message, ex);
        }
    }
}
