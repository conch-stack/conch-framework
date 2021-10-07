## MySQL性能优化



> "树根的数据块总是在内存中的"



#### 慢查询

```sql
# 便于测试
set long_query_time=0;
# 设置慢查询时间为1   global下次连接生效
set global long_query_time=0.01;

# 慢查询数
show status like 'slow_queries';    

# 开启慢查询
set global slow_query_log=on;

# 是否记录未使用索引的SQL
set global log_queries_not_using_indexes=on;

# 查看全局慢SQL配置
show global variables like 'slow%';

# dump 慢SQL
/usr/local/mysql/bin/mysqldumpslow localhost-slow.log 

# 默认为FILE，改为TABLE
SET global log_output='TABLE'
```



#### 连接数及状态查看

```sql
mysql> show processlist;
```

- Sleep表示空闲连接
- 客户端如果太长时间没动静，连接器就会自动将它断开。这个时间是由参数 wait_timeout 控制 的，默认值是 8 小时

```sql
# 控制 InnoDB 的并发线程上限
# 通常情况下，我们建议把 innodb_thread_concurrency 设置为 64~128 之间的值
set global innodb_thread_concurrency=3;
```



#### 长连接

长连接可减少MySQL建立连接的操作，提升性能，但是存在一个问题：

- MySQL在执行过程中临时使用的内存是管理在连接对象里面的，这些资源会在连接断开的时候才释放，所以如果长连接累积使用下来，会导致内存占用增大
  - 解决：在MySQL5.7版本以后，可以在每次执行一个比较大的操作后，通过执行 mysql_reset_connection 来重新初始化连接资源，这个过程不需要重新连接和校验权限，但是会将连接重置到新创建时的状态



#### 禁用查询缓存

查询缓存失效频繁，只要对一个表进行更新，那么这个表上的所有查询缓存都会被清空，会加大数据库压力，除非你的表是一张静态表

好在 MySQL 也提供了这种“按需使用”的方式。你可以将参数 query_cache_type 设置成 DEMAND，这样对于默认的 SQL 语句都不使用查询缓存。而对于你确定要使用查询缓存的语

```sql
mysql> select SQL_CACHE * from T where ID=10;
```

需要注意的是，**MySQL 8.0 版本直接将查询缓存的整块功能删掉**了，也就是说 8.0 开始彻底没 有这个功能了



#### 全表扫描

给一个表加字段，或者修改字段，或者加索引，需要扫描全表的数据



#### MDL

由于MDL的存在，会导致读写表和修改表结构直接发生阻塞，阻塞之后的所有客户端都将无法访问表，解决：**WAIT N**

```sql
ALTER TABLE tbl_name NOWAIT add column ... 
ALTER TABLE tbl_name WAIT N add column ...
```

查询MDL写锁阻塞的线程pid：

```sql
select blocking_pid from sys.schema_table_lock_waits
```



#### 两阶段锁协议

如果你的事务中需要锁多个行，要把 最可能造成锁冲突、最可能影响并发度的锁尽量往后放



#### 死锁

方案：发起死锁检测：出现死锁，则失败其中一个事务，让另一个事务可以继续执行

缺点：死锁检测耗时时间，并发事务越多，检测耗时越长

缺点优化：怎么解决由这种热点行更新导致的性能问题呢?

- **控制并发度**：这个并发控制要做在数据库服务端。如果你有中间件，可以考虑在中间件实现;如果你的 团队有能修改 MySQL 源码的人，也可以做在 MySQL 里面。基本思路就是，对于相同行的更 新，在进入引擎之前排队。这样在 InnoDB 内部就不会有大量的死锁检测工作了。



#### 索引

普通索引和唯一索引应该怎么选择：

- 其实，这两类索引在查询能力上 是没差别的，主要考虑的是对更新性能的影响。所以，我建议你**尽量选择普通索引**

**索引失效：**

- 使用函数：如果对字段做了函数计算，那么该字段的索引的快速定位效果将失效，导致索引全扫描，这是 MySQL 的B+Tree决定的

- 隐式类型转换：如果SQL中触发MySQL的String隐式转换为Int，则会导致索引失效

  ```sql
  mysql> select * from tradelog where tradeid=110717;
  # 因为这样，MySQL会触发隐式转换函数 CAST
  mysql> select * from tradelog where CAST(tradid AS signed int) = 110717;
  ```

- 隐式字符编码转换：两个表的字符集不同，一个是 utf8，一个是 utf8mb4，所以做表连接查询的时候用不上关联字段的索引

  ```sql
  mysql> select * from trade_detail where tradeid=$L2.tradeid.value;
  # 因为这样，MySQL会触发隐式转换函数 CONVERT
  mysql> select * from trade_detail where CONVERT(traideid USING utf8mb4)=$L2.tradeid.value;
  ```

  

#### 脏页

磁盘数据块与内存数据库不一致的情况，我们称为脏页

MySQL控制脏页刷盘更新：

- redo log 空间不够用
- 脏页比例大了
- 内存不够用

查询脏页比例情况：

```sql
select VARIABLE_VALUE into @a from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_dirty';
select VARIABLE_VALUE into @b from global_status where VARIABLE_NAME = 'Innodb_buffer_pool_pages_total'; 
select @a/@b;
```

防止MySQL抖动：合理设置 innodb_io_capacity 的值（控制脏页刷盘），并且**平时要多关注脏页比 例，不要让它经常接近 75%**。

**蔓延策略**：在准备刷一个脏页的时候，如果这个数据页旁 边的数据页刚好是脏页，就会把这个“邻居”也带着一起刷掉

在 InnoDB 中，**innodb_flush_neighbors** 参数就是用来控制这个行为的，值为 1 的时候会有上 述的“连坐”机制，值为 0 时表示不找邻居，自己刷自己的。

找“邻居”这个优化在机械硬盘时代是很有意义的，可以减少很多随机 IO。机械硬盘的随机 IOPS 一般只有几百，相同的逻辑操作减少随机 IO 就意味着系统性能的大幅度提升。

而如果使用的是 SSD 这类 IOPS 比较高的设备的话，我就建议你把 innodb_flush_neighbors 的值设置成 0。因为这时候 IOPS 往往不是瓶颈，而“只刷自己”，就能更快地执行完必要的刷 脏页操作，减少 SQL 语句响应时间。

在 MySQL 8.0 中，**innodb_flush_neighbors** 参数的默认值已经是 0 了。



#### Count

**show table status** 命令显 示的行数也不能直接使用，官方文档说误差可能达到 40% 到 50%

对于 count(*) 这样的操作，遍历哪个索引树 得到的结果逻辑上都是一样的。因此，MySQL 优化器会找到最小的那棵树来遍历。**在保证逻辑 正确的前提下，尽量减少扫描的数据量，是数据库系统设计的通用法则之一**

**对于 count(主键 id) 来说**，InnoDB 引擎会遍历整张表，把每一行的 id 值都取出来，返回给 server 层。server 层拿到 id 后，判断是不可能为空的，就按行累加。

**对于 count(1) 来说**，InnoDB 引擎遍历整张表，但不取值。server 层对于返回的每一行，放一 个数字“1”进去，判断是不可能为空的，按行累加。

单看这两个用法的差别的话，你能对比出来，count(1) 执行得要比 count(主键 id) 快。因为从 引擎返回 id 会涉及到解析数据行，以及拷贝字段值的操作。

所以结论是:按照效率排序的话，count(字段)<count(主键 id)<count(1)≈count(*)，所以我 建议你，尽量使用 count(*)。



#### 事务

避免长事务

undo log

事务的值：

- 如果非当前读，则需要依据undo log 计算事务开始时的 trx_id 对应的值
- 如果是当前读，则直接取当前值



#### 内存命中率

执行 show engine innodb status ，可以看到“Buffer pool hit rate”字样，显示的就是 当前的命中率

InnoDB Buffer Pool 的大小是由参数 **innodb_buffer_pool_size** 确定的，一般建议设置 成可用物理内存的 60%~80%



#### Join

Index Nested-Loop Join（NLJ）

Block Nested-Loop Join（BNL）

如果业务需要join，则需要在join的被驱动表上的字段上添加索引，以命中MySQL的 NLJ

Join的驱动表要选择：**小表**

##### 小表：

小表的定义：不仅仅是表的数据量少

- 可以是命中查询结果集的数据量小，比如添加上where的条件限定
- 可以是在同样数据量的情况下，需要查询的表的字段少的，比如 select  t1.id , t2.* ........  

**在决定哪个表做驱动表的时候，应该是两个表按照各自的条件过滤， 过滤完成之后，计算参与 join 的各个字段的总数据量，数据量小的那个表，就是“小 表”，应该作为驱动表。**

##### 优化：

前提知识：**Multi-Range Read 优化(MRR)** ，这个优化的主要目的是尽量使用**顺序读盘**

MRR 优化的设计思路：**因为大多数的数据都是按照主键递增顺序插入得到的，所以我们可以认为，如果按照主键 的递增顺序查询的话，对磁盘的读比较接近顺序读，能够提升读性能。**

**问题：回表过程是一行行地查数据，还是批量地查数据？**

答：通过 在 read_rnd_buffer 排序后，顺序的回表

1. 根据索引 a，定位到满足条件的记录，将 id 值放入 **read_rnd_buffer** 中 ; 
2. 将 read_rnd_buffer 中的 id 进行递增排序;
3. 排序后的 id 数组，依次到主键 id 索引中查记录，并作为结果返回。

这里，read_rnd_buffer 的大小是由 **read_rnd_buffer_length** 参数控制的。如果步骤 1 中，read_rnd_buffer 放满了，就会先执行完步骤 2 和 3，然后清空 read_rnd_buffer。 之后继续找索引 a 的下个记录，并继续循环

如果你想要稳定地使用 MRR 优化的话，需要设置**set optimizer_switch="mrr_cost_based=off"**。(官方文档的说法，是现在的优化器 策略，判断消耗的时候，会更倾向于不使用 MRR，把 mrr_cost_based 设置为 off，就是 固定使用 MRR 了。)

explain 结果中，我们可以看到 Extra 字段多了 Using MRR，表示的是用上了 MRR 优化

前提只是：**Batched Key Access（BKA）**

BKA算法是对 NLJ算法的优化：因为NLJ算法每次都是去被驱动表读取一条记录，所有就无法使用到MRR优化

那怎么才能一次性地多传些值给表 t2 呢?

方法就是，从表 t1 里一次性地多拿些行出来， 一起传给表 t2。

既然如此，我们就把表 t1 的数据取出来一部分，先放到一个临时内存。这个临时内存不是 别人，就是 **join_buffer**。

开启：

```sql
set optimizer_switch='mrr=on,mrr_cost_based=off,batched_key_access=on';
```

##### 临时表优化

如果你的业务场景没必要浪费索引去满足NJL，走的是BNL，那么可以选择使用 **临时表** 来优化

思路：

1. 把表 t2 中满足条件的数据放在临时表 tmp_t 中;
2. 为了让 join 使用 BKA 算法，给临时表 tmp_t 的字段 b 加上索引;
3. 让表 t1 和 tmp_t 做 join 操作。

```sql
create temporary table temp_t(id int primary key, a int, b int, index(b))engine=innodb;
insert into temp_t select * from t2 where b>=1 and b<=2000;
select * from t1 join temp_t on (t1.b=temp_t.b);
```

总体来看，不论是在原表上加索引，还是用有索引的临时表，我们的思路都是让 join 语句 能够用上被驱动表上的索引，来触发 BKA 算法，提升查询性能。

**扩展** **-hash join**

如果 join_buffer 里面维护的不是一个无序数组，而是一个哈希表的话，那么查询的效果会有质的提升，但是MySQL 的优化器和执行器一直被诟病的一个原因: 不支持哈希 join
