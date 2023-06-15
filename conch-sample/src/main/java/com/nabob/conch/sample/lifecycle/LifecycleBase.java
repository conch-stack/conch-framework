package com.nabob.conch.sample.lifecycle;

/**
 * Base 生命周期
 * <p>
 * 每个需要管理的组件都需要继承改类
 * <p>
 * todo impl
 *
 * @author Adam
 * @since 2023/3/10
 */
public abstract class LifecycleBase implements Lifecycle {

    @Override
    public void init() throws LifecycleException {

    }

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }

    @Override
    public void destroy() throws LifecycleException {

    }

    @Override
    public LifecycleState getState() {
        return null;
    }

    @Override
    public String getStateName() {
        return null;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }
}
