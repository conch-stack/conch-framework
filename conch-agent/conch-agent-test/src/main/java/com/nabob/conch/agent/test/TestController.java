package com.nabob.conch.agent.test;

import com.nabob.conch.agent.attach.spring.support.zookeeper.SpringBeanUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

/**
 * @author Adam
 * @since 2023/12/21
 */
@RestController
public class TestController {

    @Resource
    private DynamicService dynamicService;

    @RequestMapping("/testMxBean")
    public String testMxBean() {
        // JVM 运行信息
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        // JVM 来加载信息
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        // JVM 内存信息
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        // JVM 线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // JVM 编译器信息
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        // 操作系统信息
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        // JVM 各分区内存信息
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        // JVM 内存管理器
        List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        // JVM 垃圾回收器信息
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        return "success";
    }

    @RequestMapping("/getJvmInfo")
    public String getJvmInfo() {
        JvmInfo.printAll();
        return "success";
    }

    /**
     * demo： beanName=dynamicService
     */
    @RequestMapping("/testDynamicBean")
    public String testDynamicBean(@RequestParam String beanName) {
        System.out.println("testDynamicBean start");
        Object beanByName = dynamicService.getBeanByName(beanName);
        System.out.println("testDynamicBean end");
        return beanByName.toString();
    }

    @RequestMapping("/testSpring")
    public String testSpring() {
        DynamicService dynamicService = SpringBeanUtil.getBeanT(DynamicService.class);
        return dynamicService.toString();
    }
}
