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

多核CPU优化方案：将Redis与CPU某个核绑定

原理：减少CPU的上下文切换的时间

操作：taskset命令

```shell
# 将redis server 与 编号为 0 的CPU绑定
taskset -c 0 ./redis-server
```



NUMA架构：非统一内存访问架构

同样是将Redis绑定到同一个CPU Socket上

问题：绑定CPU带来的问题是，Redis的一些其他线程，比如AOF、RDB的进程将和主线程竞争资源

解决：

- 一个Redis Server绑定一个物理核，而非逻辑核
- 修改Redis源码，将子线程与主线程绑定到不同CPU



#### 延迟监控

```shell
# 打印120秒内监控到的最大延迟 （基线延迟）
./redis-cli --intrinsic-latency 120

# 运行延迟 - 执行一个命令的延迟
# 如果运行延迟达到的基线延迟的两倍，则可认为Redis变慢了
```

- 当你发现 Redis 性能变慢时，可以通过 Redis 日志，或者是 latency monitor 工具，查询变慢的请求，根据请求对应的具体命令以及官方文档，确认下是否采用了复杂度高的慢查询命令。

- 禁用 KEYS命令

- 删除过期key，如果数据量大，将导致Redis变慢
  - 使用过期时间不同的算法
- 慢查询 - 例如聚合 - 优化方案，或放到客户端聚合



#### Swap

```
# 获取 Redis进程pid
./redis-cli info | grep process_id
# 到机器的对应目录下
cd /proc/pid
# 查看Redis进程使用情况
cat smaps | egrep '^(Swap|Size)'
```



#### 内存大页

操作系统提供的机制

问题：内存大页虽然可以给Redis的内存分配带来好处，但是 在 Redis进行快照复制的时候，对复制过程中的 新变更，需要 使用 写时复制 技术，这样将会导致很小的变动，需要复制整个内存页，所以需要关闭内存大页功能

```shell
# 查看
cat /sys/kernel/mm/transparent_hugepage/enabled
# 设置
echo never /sys/kernel/mm/transparent_hugepage/enabled
```



#### 内存碎片

```shell
# 使用Redis：info memory 命令，查看内存使用情况： 
# Redis为保存数据实际申请的空间
used_memory:1073741736
# 操作系统实际分配给Redis的物理内存空间
used_memory_rss:1997159792
# Redis当前内存碎片率
mem_fragmentation_ratio:1.86
```

mem_fragmentation_ratio：经验阈值

- 大于1 但小于 1.5 ： 合理
- 大于1.5：表明内存碎片已经超过50%，需要采取措施来降低内存碎片率

##### 清理内存碎片：

Redis4.0后，提供了自动清理内存碎片的方法：（基本思路：拷贝、合并空间）

```shell
# 开启Redis内存碎片自动清理
config set activedefrag yes

# 开始清理的条件 （两个需同时满足）
active-defrag-ignore-bytes 100mb  # 表示内存碎片的字节数达到100MB时，开始清理
active-defrag-threshold-lower 10  # 表示内存碎片空间占操作系统分配给Redis的总空间的比例达到 10% 时，开始清理

# 清理的CPU占用控制
active-defrag-cycle-min 25        # 自动清理过程所用CPU时间的比例不低于25%，保证清理工作正常开展
active-defrag-cycle-max 75        # 自动清理过程所用CPU时间的比例不高于75%，一旦超过，就停止清理，避免大量内存拷贝阻塞Redis
```









#### 大数据量优化方案

- Bitmap二值方案
- HyperLogLog方案

- 集群分片



