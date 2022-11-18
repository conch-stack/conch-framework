package ltd.beihu.sample.timelimit;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Adam
 * @since 2022/11/18
 */
public class TestConsumer {

    private Consumer consumer;
    private Thread thread;

    private LocalDateTime timeout;

    private Set<String> prodConsumerSet;

    public TestConsumer(Consumer consumer, LocalDateTime timeout, Set<String> prodConsumerSet) {
        this.prodConsumerSet = prodConsumerSet;
        this.consumer = consumer;
        this.timeout = timeout;
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (timeout.isBefore(LocalDateTime.now())) {
                            System.out.println("我超时了，退出循环");
                            throw new InterruptedException();
                        }
                        if (Thread.interrupted()) {
                            System.out.println("已经是停止状态了，我要退出了！");
                            throw new InterruptedException();
                        }
                        consumer.consume(); // 具体的consumer可用池进行管理
                    } catch (InterruptedException e) {
                        consumer.stop(); // todo 销毁
                        prodConsumerSet.remove(consumer.getKey());
                        break;
                    }
                }
            }
        });
        thread.start();
    }
}
