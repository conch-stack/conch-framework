## Conch Hystrix设计



### 阈值与统计数据：

- 数据结构选择
  - 循环数组（环形数组）
  - 链表（环形链表）

### 断路器：

三种状态：CLOSE、OPEN、HALF_OPEN

状态流转：

- tryMarkOpen ： CLOSE -> OPEN

- attemptExecution ： OPEN -> HALF_OPEN
- makeNonSuccess ： HALF_OPEN -> OPEN
- makeSuccess : HALF_OPEN -> CLOSE



### Hystrix原理

特性：

- 资源隔离：限制调用服务使用的资源，当某个下游服务出现问题，不会影响整个调用链
  - 为每个服务场景单独的线程池；信号量
- 熔断机制：当失败率达到阈值自动触发熔断，熔断器触发不再进行调用
- 降级机制：超时、资源或触发熔断后，调用预设的降级接口返回兜底数据



将Hystrix集成到自研RPC框架中，减少业务方使用成本，但是响应的failback逻辑，依然需要按需实现