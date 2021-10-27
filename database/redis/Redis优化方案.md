## Redis优化方案



#### 优化方向

- 持久化方案
- 数据结构运用
- 阻塞Redis的内部操作
- CPU核数 和 NUMA架构的影响
- Redis关键配置
- Redis内存碎片
- Redis缓冲区



#### 优化手段

INFO命令：

- latest_fork_usec：最近一次fork耗时





#### 持久化方案优化

使用 RDB + AOF增量的方式，进行主从同步，数据备份



#### 数据结构优化方案

##### 使用集合类型保存单值的键值对

原理：利用 ziplist内部编码解决 多String key 的 distEntry的额外内存消耗

方案：分析String key规则，将key分割为两部分，一部分作为hash集合的key，一部分作为hash的值的key，value保存在hash值的value中

注意点：使用ziplist的条件是，要满足系统配置的两个阈值（当哈希类型元素个数小于`hash-max-ziplist-entries`配置（默认512个）同时所有值都小于`hash-max-ziplist-value`配置（默认64字节）时使用），所以在拆分String key时需要 保证 每个Hash集合中元素的个数，尽量不要超过阈值



#### 阻塞Redis的内部操作

- 客户端 O(N) 的命令操作，应当引起重视

  - 集合全量查询
  - 聚合统计
  - bigkey删除：集合全量删除导致内存释放，放入空闲管理；下方图片为不同集合的删除耗时

  <img src="assets/image-20211027212521550.png" alt="image-20211027212521550" style="zoom:50%;" />

  - 清空数据库
    - FLUSHDB 和 FLUSHALL

- AOF日志同步写盘

- 分片集群 Hash槽迁移



#### CPU核数 和 NUMA架构的影响







#### 大数据量优化方案

- Bitmap二值方案
- HyperLogLog方案

- 集群分片



