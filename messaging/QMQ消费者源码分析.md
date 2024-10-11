## QMQ消费者源码分析

### 方式：

利用一个partition-consumerGroup binding 为每个分区的消费者组创建一个PerBindingPullLoop，并使用状态值进行每个PerBindingPullLoop的轮询逻辑处理





### 核心类：

- MessageConsumerProvider： 构件手动Consumer，添加Consumer Listener or BatchListener
- SubscriptionManager → DefaultSubscriptionManager: 构件 PushSubscription 并注册到 DefaultSubscriptionManager#Register中，同时PushSubscription#initiateAsync触发自己初始化，并触发其管理的PullLoopGroup#initiateAsync
-  PushSubscription ： 创建 PullLoopGroup 与 MessageDeliveryHandler
- MetaInfoService：消费者元信息服务
- ConsumerStatusManager：监听MetaInfoService中的消费者上下线状态，并触发 onChange 处理管理的 PullLoopGroup
- PullLoopGroup： 创建 PerBindingPullLoop
  - 初始化 executorGroup 消费者线程池，并将PullLoopGroup实例给到 ConsumerStatusManager管理
- PerBindingPullLoop



### 消费流程：

初始状态 STANDBY：

-  STANDBY会获取pull的许可，最终将状态转移为 PERMITS_ACQUIRED   

state 到达 PERMITS_ACQUIRED 会被设置为 REQUESTING 状态，并触发一次Pull请求QMQ服务器

- 在超时时间内如果【返回了数据pullResult】则触发状态 再从 REQUESTING 流转到 RESPOND 并触发调用PerBindingPullLoop的run方法进入  RESPOND 处理流程
  - RESPOND  处理流程开始会将状态流转到 DELIVERING，然后将数据 pullResult 通过 handlePullResult方法
    - 使用 ackService.buildPulledMessages(pendingDeliveryQueue,  pullResult, ackSendQueue) 
      - 将BaseMessage包装成PulledMessage， PulledMessage中包含当前ackEntry信息，可用于手动进行ack操作（底层使用AckHelper） 最终将 PulledMessage 推入 pendingDeliveryQueue 
      - 将ackEntries加入 ackSendQueue，ackSendQueue其实只是一个TimerTask，在（ACK_INTERVAL_SECONDS = 10）周期性的尝试使用 AckService 发送ACK
    - 最终 再次  调用PerBindingPullLoop的run方法 进入  DELIVERING 处理流程
  - DELIVERING 开始会 循环从 pendingDeliveryQueue 中 poll数据，并调用 messageDeliverer.submitMessage(pm) 将数据包装成一个 MessageDeliveryTask 传递给下游的 监听器们 ( PerBindingMessageDeliverer#executorProxy 使用这个Executor来处理数据 )
    - 最终 会将状态转移为 STANDBY，并调用 PerBindingPullLoop的run方法 重新开始一轮调用
- 否则，会触发超时的TimerTaskWrapper工作；
  - TimerTaskWrapper会在超时发生后再次调用PerBindingPullLoop的run方法进行下一次轮询



### PS：

PerBindingPullLoop 是如何创建的：依靠DefaultMetaInfoService的metaInfoClient与QMQ的元数据服务（zk？）通信，对消费者的状态、数量进行管理

- updateConsumerState → triggerConsumerStateChanged → consumerStateChangedListener.onChange()  → allocationListeners → AllocationListener → onAllocate → PullLoopGroup → updatePullLoops  
  - statusManager.addAllocationListener(subscriptionKey, this::updatePullLoops);   删除，新增，已存在忽略   PerBindingPullLoop



状态：

  private static final int STATE_NEW = 0;
  private static final int STATE_STARTING = 1;
  private static final int STATE_STARTED = 2;
  private static final int STATE_CLOSING = 3;
  private static final int STATE_CLOSED = 4;
  private final AtomicInteger state = new AtomicInteger(STATE_NEW);



  static final int STANDBY = 0;
  static final int CHECKING = 1;
  static final int ACQUIRING_PERMITS = 2;
  static final int PERMITS_ACQUIRED = 3;
  static final int REQUESTING = 4;
  static final int RESPOND = 5;
  static final int DELIVERING = 6;
  private final AtomicInteger state = new AtomicInteger(STANDBY);