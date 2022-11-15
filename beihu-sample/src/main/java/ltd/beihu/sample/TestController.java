package ltd.beihu.sample;

import ltd.beihu.sample.job.Config;
import ltd.beihu.sample.job.ConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/test")
    public void test(@RequestParam String topic, @RequestParam String message) throws InvocationTargetException, IllegalAccessException {
        System.out.println("test start");
        Map<String, ConfigBean> configMap = config.getConfigMap();
        ConfigBean configBean = configMap.get(topic);

        configBean.getTargetMethod().invoke(configBean.getTargetBean(), message);
        System.out.println("test end");
    }

}
