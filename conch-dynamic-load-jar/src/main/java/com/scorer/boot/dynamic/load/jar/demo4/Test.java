package com.scorer.boot.dynamic.load.jar.demo4;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author Adam
 * @since 2024/12/18
 */
@RestController
public class Test {

    @Resource
    private DCacheLoader dCacheLoader;
    @Resource
    private DCacheClientLocal dCacheClientLocal;

    @GetMapping("/test")
    public String test() {
        try {
            dCacheLoader.load();
            return "success";
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "error";
    }

    @GetMapping("/test1")
    public String test1() {
        try {
            dCacheClientLocal.test();
            return "success";
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "error";
    }

    @GetMapping("/test1/jar")
    public String test11() {
        try {
            DCacheProxy proxy = dCacheClientLocal.getProxy();

            File file1 = new File("D:\\tmp\\conch-dynamic-jar-0.0.1.jar");
            File file2 = new File("D:\\tmp\\conch-dynamic-jar-0.0.2.jar");

            proxy.put("v1", dCacheLoader.getClassLoader(file2));
            proxy.put("v2", dCacheLoader.getClassLoader(file1));
            return "success";
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "error";
    }

    @GetMapping("/test1/switch")
    public String test111(@RequestParam String version) {
        try {
            DCacheProxy proxy = dCacheClientLocal.getProxy();
            proxy.setCurrent(version);
            return "success";
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "error";
    }
}
