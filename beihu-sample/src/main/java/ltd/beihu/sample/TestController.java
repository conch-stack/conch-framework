package ltd.beihu.sample;

import ltd.beihu.sample.advice.agentv2.RpcLog;
import ltd.beihu.sample.advice.agentv2.SelfRpcLog;
import ltd.beihu.sample.dynamic.DynamicSpringBeanFactory;
import ltd.beihu.sample.job.Config;
import ltd.beihu.sample.job.ConfigBean;
import ltd.beihu.sample.job.TestService;
import ltd.beihu.sample.test.AgentTestService;
import ltd.beihu.sample.timelimit.TestTimeLimit;
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
    private TestTimeLimit testTimeLimit;

    @Resource
    private DynamicSpringBeanFactory dynamicSpringBeanFactory;

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

}