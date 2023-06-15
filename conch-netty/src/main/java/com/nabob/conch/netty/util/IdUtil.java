package com.nabob.conch.netty.util;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Adam
 * @since 2022/1/20
 */
public final class IdUtil {

    private static final AtomicLong IDX = new AtomicLong();

    private IdUtil() {
    }

    public static long nextId() {
        return IDX.incrementAndGet();
    }
}
