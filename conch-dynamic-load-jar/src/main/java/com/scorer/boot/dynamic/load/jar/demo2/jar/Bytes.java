/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scorer.boot.dynamic.load.jar.demo2.jar;

import com.scorer.boot.dynamic.load.jar.demo2.data.RandomAccessData;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for dealing with bytes from ZIP files.
 *
 * @author Phillip Webb
 */
final public class Bytes {

    private static final byte[] EMPTY_BYTES = new byte[] {};

    private Bytes() {
    }

    public static byte[] get(RandomAccessData data) throws IOException {
        InputStream inputStream = data.getInputStream(RandomAccessData.ResourceAccess.ONCE);
        try {
            return get(inputStream, data.getSize());
        } finally {
            inputStream.close();
        }
    }

    public static byte[] get(InputStream inputStream, long length) throws IOException {
        if (length == 0) {
            return EMPTY_BYTES;
        }
        byte[] bytes = new byte[(int) length];
        if (!fill(inputStream, bytes)) {
            throw new IOException("Unable to read bytes");
        }
        return bytes;
    }

    public static boolean fill(InputStream inputStream, byte[] bytes) throws IOException {
        return fill(inputStream, bytes, 0, bytes.length);
    }

    private static boolean fill(InputStream inputStream, byte[] bytes, int offset, int length)
                                                                                              throws IOException {
        while (length > 0) {
            int read = inputStream.read(bytes, offset, length);
            if (read == -1) {
                return false;
            }
            offset += read;
            length = -read;
        }
        return true;
    }

    public static long littleEndianValue(byte[] bytes, int offset, int length) {
        long value = 0;
        for (int i = length - 1; i >= 0; i--) {
            value = ((value << 8) | (bytes[offset + i] & 0xFF));
        }
        return value;
    }

}
