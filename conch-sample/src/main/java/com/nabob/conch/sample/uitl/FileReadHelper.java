package com.nabob.conch.sample.uitl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * File Read Helper
 *
 * @author Adam
 * @since 2023/2/17
 */
public class FileReadHelper {

    private static final Map<String, String> CACHE_DATA = new HashMap<>(8);

    public static String getJsonFileData(String fileName) {
        try {
            if (!StringUtils.endsWith(fileName, ".json")) {
                return null;
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            String fullPath = FileReadHelper.class.getResource("/").getPath() + fileName;
            return getFileData(fullPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileData(String fullPath) {
        try {
            System.out.printf("mock data path: %s%n", fullPath);
            String content = "";
            if (StringUtils.isNotBlank(CACHE_DATA.get(fullPath))) {
                System.out.println("load data from cache");
                content = CACHE_DATA.get(fullPath);
            } else {
                content = FileUtils.readFileToString(new File(fullPath), "UTF-8");
                CACHE_DATA.putIfAbsent(fullPath, content);
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
