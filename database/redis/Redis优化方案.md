## Redis优化方案



### 优化方向

持久化方案、数据结构运用



### 优化手段

INFO命令：

- latest_fork_usec：最近一次fork耗时







#### 数据结构优化方案

##### 使用集合类型保存单值的键值对

原理：利用 ziplist内部编码解决 多String key 的 distEntry的额外内存消耗

方案：分析String key规则，将key分割为两部分，一部分作为hash集合的key，一部分作为hash的值的key，value保存在hash值的value中

注意点：使用ziplist的条件是，要满足系统配置的两个阈值（当哈希类型元素个数小于`hash-max-ziplist-entries`配置（默认512个）同时所有值都小于`hash-max-ziplist-value`配置（默认64字节）时使用），所以在拆分String key时需要 保证 每个Hash集合中元素的个数，尽量不要超过阈值



### 大数据量优化方案

##### 集群分片



