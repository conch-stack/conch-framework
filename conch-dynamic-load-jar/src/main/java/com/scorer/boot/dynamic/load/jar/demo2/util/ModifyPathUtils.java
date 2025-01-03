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
package com.scorer.boot.dynamic.load.jar.demo2.util;

/**
 * @author sususama
 * @since 2023/5/8
 */
public class ModifyPathUtils {
    /** When using SofaArk in the Windows environment, you will encounter a path error.
     * This tool class will judge the read file or the configured path,
     * and judge whether it is the file path of the Windows operating system.
     * If it is, this method will modify the path to suit WindowsOS,
     * otherwise the input path will be returned directly.
     *
     * @param path File Path
     * @return Modified file path
     */
    public static String modifyPath(String path) {
        if (path.charAt(2) == ':') {
            path = path.substring(1);
        }
        return path;
    }
}
