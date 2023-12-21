package com.nabob.conch.agent.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Adam
 * @since 2023/12/21
 */
@RestController
public class TestController {

    @Resource
    private DynamicService dynamicService;

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

}
