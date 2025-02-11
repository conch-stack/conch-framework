package com.nabob.conch.sample;

import com.nabob.conch.sample.advice.agentv2.RpcLog;
import com.nabob.conch.sample.advice.agentv2.SelfRpcLog;
import com.nabob.conch.sample.dynamic.DynamicConfig;
import com.nabob.conch.sample.dynamic.DynamicConfigLoader;
import com.nabob.conch.sample.dynamic.DynamicService;
import com.nabob.conch.sample.dynamic.DynamicSpringBeanFactory;
import com.nabob.conch.sample.gc.GarbageCollection;
import com.nabob.conch.sample.job.Config;
import com.nabob.conch.sample.job.ConfigBean;
import com.nabob.conch.sample.job.ConfigV2;
import com.nabob.conch.sample.test.AgentTestService;
import com.nabob.conch.sample.timelimit.TestTimeLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Adam
 * @since 2022/11/15
 */
@RestController
public class TestController {

    @Resource
    private Config config;
    @Resource
    private ConfigV2 configV2;

    @Resource
    private TestTimeLimit testTimeLimit;

    @Resource
    private DynamicSpringBeanFactory dynamicSpringBeanFactory;

    @Resource
    private DynamicConfigLoader dynamicConfigLoader;

    @Resource
    private DynamicService dynamicService;
    @Resource
    private AgentTestService agentTestService;

    @RequestMapping("/test")
    @RpcLog
    public void test(@RequestParam String topic, @RequestParam String message) throws InvocationTargetException, IllegalAccessException {
        System.out.println("test start");
        Map<String, ConfigBean> configMap = config.getConfigMap();
        ConfigBean configBean = configMap.get(topic);

        configBean.getTargetMethod().invoke(configBean.getTargetBean(), message);
        System.out.println("test end");
    }


    @RequestMapping("/testv2")
    @RpcLog
    public void testv2(@RequestParam String topic, @RequestParam String message) throws InvocationTargetException, IllegalAccessException {
        System.out.println("testv2 start");
        Map<String, ConfigBean> configMap = configV2.getConfigMap();
        ConfigBean configBean = configMap.get(topic);

        configBean.getTargetMethod().invoke(configBean.getTargetBean(), message);
        System.out.println("testv2 end");
    }

    @RequestMapping("/testTimeLimit")
    @SelfRpcLog
    public void testTimeLimit(@RequestParam String key, @RequestParam Integer timeout) throws InvocationTargetException, IllegalAccessException {
        System.out.println("testTimeLimit start");
        testTimeLimit.test(key, timeout);
        System.out.println("testTimeLimit end");
    }

    @RequestMapping("/testDynamic")
    public void testDynamic(@RequestParam String name) throws InvocationTargetException, IllegalAccessException {
        System.out.println("testDynamic start");
        dynamicSpringBeanFactory.createBean(name);
        System.out.println("testDynamic end");
    }

    @RequestMapping("/testDynamic2")
    @ResponseBody
    public Collection<User> testDynamic2() throws InvocationTargetException, IllegalAccessException {
        System.out.println("testDynamic2 start");
        dynamicConfigLoader.register("dynamicConfig", DynamicConfig.class);
        Collection<User> users = dynamicService.getUsers();
        System.out.println("testDynamic2 end");
        return users;
    }

    @RequestMapping("/getDynamic")
    @ResponseBody
    public Collection<User> getDynamic() throws InvocationTargetException, IllegalAccessException {
        System.out.println("getDynamic start");
        Collection<User> users = dynamicSpringBeanFactory.getUsers();
        System.out.println("getDynamic end");
        return users;
    }

    @RequestMapping("/testVoid")
    public void testVoid() throws InvocationTargetException, IllegalAccessException {
        System.out.println("testVoid start");
        dynamicSpringBeanFactory.testVoid();
        System.out.println("testVoid end");
    }

    @RequestMapping("/testAgentAop")
    public void testAgentAop() throws InvocationTargetException, IllegalAccessException {
        System.out.println("testAgentAop start");
        agentTestService.testAgentPackageAop();
        System.out.println("testAgentAop end");
    }

    @RequestMapping("/testGC")
    public void testGC() throws InvocationTargetException, IllegalAccessException {
        System.out.println("testGC start");
        GarbageCollection.run();
        System.out.println("testGC end");
    }

//    @RequestMapping("/testAA")
//    public Object testAA() {
//        return new User("aa", 10);
//    }
//
//    /**
//     * 测试String
//     *
//     * @return response
//     */
//    @RequestMapping("/testAS")
//    public Object testAS() {
//        return "aaa";
//    }

}
