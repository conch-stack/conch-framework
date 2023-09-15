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