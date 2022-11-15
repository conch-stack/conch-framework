package ltd.beihu.sample.job;

import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @since 2022/11/15
 */
@Service
public class TestService1 {

    @QmqConsumer(topic = "test-topic2")
    public void test(String message) {
        System.out.printf("test2 + %s%n", message);
    }

    @QmqConsumer(topic = "test-topic3")
    public void test1(String message) {
        System.out.printf("test3 + %s%n", message);
    }
}
