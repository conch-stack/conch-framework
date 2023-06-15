package ltd.beihu.sample.timelimit;

import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 * @since 2022/11/18
 */
public class Consumer {

    private String key;

    public Consumer(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void consume() {
        try {
            System.out.println("处理数据中" + key);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (Exception e) {

        }
    }

    public void stop() {
        System.out.println("stopped" + key);
    }

}
