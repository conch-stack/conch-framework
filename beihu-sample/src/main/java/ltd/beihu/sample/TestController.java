package ltd.beihu.sample;

import ltd.beihu.sample.job.Config;
import ltd.beihu.sample.job.ConfigBean;
import ltd.beihu.sample.timelimit.TestTimeLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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

    @RequestMapping("/test")
    public void test(@RequestParam String topic, @RequestParam String message) throws InvocationTargetException, IllegalAccessException {
        System.out.println("test start");
        Map<String, ConfigBean> configMap = config.getConfigMap();
        ConfigBean configBean = configMap.get(topic);

        configBean.getTargetMethod().invoke(configBean.getTargetBean(), message);
        System.out.println("test end");
    }

    @RequestMapping("/testTimeLimit")
    public void test(@RequestParam String key, @RequestParam Integer timeout) throws InvocationTargetException, IllegalAccessException {
        System.out.println("test start");
        testTimeLimit.test(key, timeout);
        System.out.println("test end");
    }

}
