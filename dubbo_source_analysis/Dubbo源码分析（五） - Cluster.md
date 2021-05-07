## Dubbo源码分析（三） - Cluster

##### Cluster && Cluster Invoker

集群 Cluster 用途是将多个服务提供者合并为一个 Cluster Invoker，并将这个 Invoker 暴露给服务消费者。这样一来，服务消费者只需通过这个 Invoker 进行远程调用即可，至于具体调用哪个服务提供者，以及调用失败后如何处理等问题，现在都交给集群模块去处理



- Failover Cluster - 失败自动切换
- Failfast Cluster - 快速失败
- Failsafe Cluster - 失败安全
- Failback Cluster - 失败自动恢复
- Failing Cluster - 并行调用多个服务提供者



集群工作过程可分为两个阶段：

- 第一个阶段是在服务消费者初始化期间，集群 Cluster 实现类为服务消费者创建 Cluster Invoker 实例，即上图中的 merge 操作
- 第二个阶段是在服务消费者进行远程调用时。以 FailoverClusterInvoker 为例，该类型 Cluster Invoker 首先会调用 Directory 的 list 方法列举 Invoker 列表（可将 Invoker 简单理解为服务提供者）。Directory 的用途是保存 Invoker，可简单类比为 List<Invoker>。其实现类 RegistryDirectory 是一个动态服务目录，可感知注册中心配置的变化，它所持有的 Invoker 列表会随着注册中心内容的变化而变化。每次变化后，RegistryDirectory 会动态增删 Invoker，并调用 Router 的 route 方法进行路由，过滤掉不符合路由规则的 Invoker。当 FailoverClusterInvoker 拿到 Directory 返回的 Invoker 列表后，它会通过 LoadBalance 从 Invoker 列表中选择一个 Invoker。最后 FailoverClusterInvoker 会将参数传给 LoadBalance 选择出的 Invoker 实例的 invoke 方法，进行真正的远程调用



##### Cluster Invoker：

FailoverClusterInvoker -> AbstractClusterInvoker -> ClusterInvoker



##### AbstractClusterInvoker：

- invoke

```java
@Override
public Result invoke(final Invocation invocation) throws RpcException {
    checkWhetherDestroyed();

    // binding attachments into invocation.
    Map<String, Object> contextAttachments = RpcContext.getContext().getObjectAttachments();
    if (contextAttachments != null && contextAttachments.size() != 0) {
        ((RpcInvocation) invocation).addObjectAttachments(contextAttachments);
    }

  	// 列举  Invoker
    List<Invoker<T>> invokers = list(invocation);
    LoadBalance loadbalance = initLoadBalance(invokers, invocation);
    RpcUtils.attachInvocationIdIfAsync(getUrl(), invocation);
    return doInvoke(invocation, invokers, loadbalance);
}
```



##### FailoverClusterInvoker：(循环调用失败重试)

```java
@Override
@SuppressWarnings({"unchecked", "rawtypes"})
public Result doInvoke(Invocation invocation, final List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
    List<Invoker<T>> copyInvokers = invokers;
    checkInvokers(copyInvokers, invocation);
    String methodName = RpcUtils.getMethodName(invocation);
  	// 获取重试次数
    int len = getUrl().getMethodParameter(methodName, RETRIES_KEY, DEFAULT_RETRIES) + 1;
    if (len <= 0) {
        len = 1;
    }
    // retry loop.
    RpcException le = null; // last exception.
    List<Invoker<T>> invoked = new ArrayList<Invoker<T>>(copyInvokers.size()); // invoked invokers.
    Set<String> providers = new HashSet<String>(len);
  	// 循环调用失败重试
    for (int i = 0; i < len; i++) {
        //Reselect before retry to avoid a change of candidate `invokers`.
        //NOTE: if `invokers` changed, then `invoked` also lose accuracy.
        if (i > 0) {
            checkWhetherDestroyed();
          	// 在进行重试前重新列举 Invoker，这样做的好处是，如果某个服务挂了，
            // 通过调用 list 可得到最新可用的 Invoker 列表
            copyInvokers = list(invocation);
            // check again
            checkInvokers(copyInvokers, invocation);
        }
      
      	// 通过负载均衡选择 Invoker
        Invoker<T> invoker = select(loadbalance, invocation, copyInvokers, invoked);
        invoked.add(invoker);
        RpcContext.getContext().setInvokers((List) invoked);
        try {
          	// 调用目标 Invoker 的 invoke 方法
            Result result = invoker.invoke(invocation);
            return result;
        } catch (RpcException e) {
            if (e.isBiz()) { // biz exception.
                throw e;
            }
            le = e;
        } catch (Throwable e) {
            le = new RpcException(e.getMessage(), e);
        } finally {
            providers.add(invoker.getUrl().getAddress());
        }
    }
  
  	// 若重试失败，则抛出异常
    throw new RpcException(le.getCode(), "Failed to invoke the method ...");
}
```



- select (invoke使用select进行负载均衡)

```java
// 粘滞连接Invoker
private volatile Invoker<T> stickyInvoker = null;

protected Invoker<T> select(LoadBalance loadbalance, Invocation invocation,
                            List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException {

    if (CollectionUtils.isEmpty(invokers)) {
        return null;
    }
  	// 获取调用方法名
    String methodName = invocation == null ? StringUtils.EMPTY_STRING : invocation.getMethodName();
    // 获取 sticky 配置，sticky 表示粘滞连接。所谓粘滞连接是指让服务消费者尽可能的
    // 调用同一个服务提供者，除非该提供者挂了再进行切换
    boolean sticky = invokers.get(0).getUrl()
            .getMethodParameter(methodName, CLUSTER_STICKY_KEY, DEFAULT_CLUSTER_STICKY);

    // 检测 invokers 列表是否包含 stickyInvoker，如果不包含，
    // 说明 stickyInvoker 代表的服务提供者挂了，此时需要将其置空
    //ignore overloaded method
    if (stickyInvoker != null && !invokers.contains(stickyInvoker)) {
        stickyInvoker = null;
    }
    // 在 sticky 为 true，且 stickyInvoker != null 的情况下。如果 selected 包含 
    // stickyInvoker，表明 stickyInvoker 对应的服务提供者可能因网络原因未能成功提供服务。
    // 但是该提供者并没挂，此时 invokers 列表中仍存在该服务提供者对应的 Invoker。
    //ignore concurrency problem
    if (sticky && stickyInvoker != null && (selected == null || !selected.contains(stickyInvoker))) {
        if (availablecheck && stickyInvoker.isAvailable()) {
            return stickyInvoker;
        }
    }

    // 如果线程走到当前代码处，说明前面的 stickyInvoker 为空，或者不可用。
    // 此时继续调用 doSelect 选择 Invoker
    Invoker<T> invoker = doSelect(loadbalance, invocation, invokers, selected);

   // 如果 sticky 为 true，则将负载均衡组件选出的 Invoker 赋值给 stickyInvoker
    if (sticky) {
        stickyInvoker = invoker;
    }
    return invoker;
}

private Invoker<T> doSelect(LoadBalance loadbalance, Invocation invocation,
                            List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException {

    if (CollectionUtils.isEmpty(invokers)) {
        return null;
    }
    if (invokers.size() == 1) {
        return invokers.get(0);
    }
    // 通过负载均衡组件选择 Invoker
    Invoker<T> invoker = loadbalance.select(invokers, getUrl(), invocation);

    // 如果 selected 包含负载均衡选择出的 Invoker，或者该 Invoker 无法经过可用性检查，此时进行重选
    if ((selected != null && selected.contains(invoker))
            || (!invoker.isAvailable() && getUrl() != null && availablecheck)) {
        try {
          	// 进行重选
            Invoker<T> rInvoker = reselect(loadbalance, invocation, invokers, selected, availablecheck);
            if (rInvoker != null) {
              	// 如果 rinvoker 不为空，则将其赋值给 invoker
                invoker = rInvoker;
            } else {
                // rinvoker 为空，定位 invoker 在 invokers 中的位置
                int index = invokers.indexOf(invoker);
                try {
                  	// 获取 index + 1 位置处的 Invoker，以下代码等价于：
                    // Avoid collision 避免碰撞
                    invoker = invokers.get((index + 1) % invokers.size());
                } catch (Exception e) {
                    logger.warn(e.getMessage() + " may because invokers list dynamic change, ignore.");
                }
            }
        } catch (Throwable t) {
            logger.error("cluster reselect fail reason is ...");
        }
    }
    return invoker;
}
```



- reselect

```java
private Invoker<T> reselect(LoadBalance loadbalance, Invocation invocation,
                            List<Invoker<T>> invokers, List<Invoker<T>> selected, boolean availablecheck) throws RpcException {

    //Allocating one in advance, this list is certain to be used.
    List<Invoker<T>> reselectInvokers = new ArrayList<>(
            invokers.size() > 1 ? (invokers.size() - 1) : invokers.size());

    // First, try picking a invoker not in `selected`.
    for (Invoker<T> invoker : invokers) {
        if (availablecheck && !invoker.isAvailable()) {
            continue;
        }

        if (selected == null || !selected.contains(invoker)) {
            reselectInvokers.add(invoker);
        }
    }

    if (!reselectInvokers.isEmpty()) {
        return loadbalance.select(reselectInvokers, getUrl(), invocation);
    }

    // Just pick an available invoker using loadbalance policy
    if (selected != null) {
        for (Invoker<T> invoker : selected) {
            if ((invoker.isAvailable()) // available first
                    && !reselectInvokers.contains(invoker)) {
                reselectInvokers.add(invoker);
            }
        }
    }
    if (!reselectInvokers.isEmpty()) {
        return loadbalance.select(reselectInvokers, getUrl(), invocation);
    }

    return null;
}
```