## Redis存储时间序列方案



### 时间序列数据的特点：

写入快、查询方式多（单值查询、范围查询、聚合计算、均值、最大/最小值、求和）



### 方案：

- Hash存储 + Sorted Set存储
  - Hash存储单值插入查询的速度很快，但是不支持范围查询
  - Sorted Set可以进行范围查询
  - 使用Redis提供的简单事务 MULTI 和 EXEC 来保证两个集合的新增一致性
  - 无法实现聚合计算
- 基于RedisTimeSeries模块（Redis的扩展模块）
  - 支持时间范围的聚合计算
  - 需要将模块源码编译成动态链接库 redistimeseries.so 再使用 loadmodule 进行加载

