### Tomcat优雅关闭连接

 

### StandardService.stopInternal():

```java
protected void stopInternal() throws LifecycleException {

    synchronized (connectorsLock) {
        // Initiate a graceful stop for each connector
        // This will only work if the bindOnInit==false which is not the
        // default.
        for (Connector connector: connectors) {
            // 优雅关闭ServerSocket
            connector.getProtocolHandler().closeServerSocketGraceful();
        }

        // Wait for the graceful shutdown to complete
        long waitMillis = gracefulStopAwaitMillis;
        if (waitMillis > 0) {
            for (Connector connector: connectors) {
                // 等待优雅关闭结束
                waitMillis = connector.getProtocolHandler().awaitConnectionsClose(waitMillis);
            }
        }

        // Pause the connectors
        for (Connector connector: connectors) {
            connector.pause();
        }
    }

    if(log.isInfoEnabled())
        log.info(sm.getString("standardService.stop.name", this.name));
    setState(LifecycleState.STOPPING);

    // Stop our defined Container once the Connectors are all paused
    if (engine != null) {
        synchronized (engine) {
            engine.stop();
        }
    }

    // Now stop the connectors
    synchronized (connectorsLock) {
        for (Connector connector: connectors) {
            if (!LifecycleState.STARTED.equals(
                    connector.getState())) {
                // Connectors only need stopping if they are currently
                // started. They may have failed to start or may have been
                // stopped (e.g. via a JMX call)
                continue;
            }
            connector.stop();
        }
    }

    // If the Server failed to start, the mapperListener won't have been
    // started
    if (mapperListener.getState() != LifecycleState.INITIALIZED) {
        mapperListener.stop();
    }

    synchronized (executors) {
        for (Executor executor: executors) {
            executor.stop();
        }
    }
}
```



### ProtocolHandler

细节 TODO