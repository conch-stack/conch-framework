package ltd.beihu.sample.lifecycle;

/**
 * 生命周期 监听器
 *
 * @author Adam
 * @since 2023/3/10
 */
public interface LifecycleListener {

    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event LifecycleEvent that has occurred
     */
    void lifecycleEvent(LifecycleEvent event);
}
