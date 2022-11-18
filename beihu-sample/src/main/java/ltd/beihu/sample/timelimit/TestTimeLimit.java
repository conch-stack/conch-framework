package ltd.beihu.sample.timelimit;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Adam
 * @since 2022/11/18
 */
@Component
public class TestTimeLimit {

    private static final Set<String> prodConsumerSet = new HashSet<>();

    public void test(String key, int timeout) {
        if (prodConsumerSet.contains(key)) {
            return;
        }
        prodConsumerSet.add(key);
        System.out.println("创建对象"+key);

        Consumer consumer = new Consumer(key);
        new TestConsumer(consumer, LocalDateTime.now().plusSeconds(timeout), prodConsumerSet);
    }

}
