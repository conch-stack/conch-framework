package com.nabob.conch.sample.jackson;

public class SerializationException extends RuntimeException {

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(String msg) {
        super(msg);
    }
}
