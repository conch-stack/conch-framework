package ltd.beihu.sample.timelimit;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Adam
 * @since 2022/11/18
 */
@Component
public class TestTimeLimit {

    @Resource
    private TestConsumerManager testConsumerManager;

    public void test(String key, int targetTimeout) {
        System.out.println("创建对象"+key);
        testConsumerManager.addNewDynamicQmqTestConsumer(key, targetTimeout);
    }

}
