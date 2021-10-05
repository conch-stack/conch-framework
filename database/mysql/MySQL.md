## MySQL

### 基础架构

![MySQL架构](assets/MySQL架构.png)



### Redo Log & Binlog

##### Redo Log：重做日志

- 是**WAL（Write-Ahead Loggin）方案**的载体：先写日志（这里的Redo Log），再写存储引擎；分层操作，可提升性能，防止操作丢失（**crash-safe**）

- **工作在存储引擎层**：因为MySQL期初是没有InnoDB的，而Server的Binlog是没有crash-safe功能的，所有InnoDB实现的WAL

- 配置为一组4个文件，每个文件1G，从头开始写，写到末尾就又回到开 头循环写。
  - 两个指针操作日志偏移量（write pos）与持久化偏移量（checkpoint）：

<img src="assets/image-20210928010032431.png" alt="image-20210928010032431" style="zoom:70%;" />



##### BinLog：归档日志

- 工作在Server层



##### 对比：

| Redo Log                                     | Binlog                                                       |
| -------------------------------------------- | ------------------------------------------------------------ |
| InnoDB引擎特有                               | Server层通用                                                 |
| 物理日志（记录了：某个数据页上做了什么修改） | 逻辑日志（记录语句的原始逻辑，比如：给ID=2这一行的c字段加1） |
| 循环写，固定大小                             | 追加写，写到一定大小后，会创建下一个文件                     |



**问题：为什么有两套日志？**

答：期初MySQL无InnoDB引擎，只能利用Binlog进行日志归档，后期InnoDB引擎为支持crash-safe，实现了WAL方案

**问题：先写redo log还是先写binlog**

答：先写redo log，再写binlog：执行器写完数据后（redolog已写完），才会生成binlog记录操作归档，写入磁盘，重点是这两个流程需要一个两阶段提交来保证一致性



### Update/Insert执行流程

> update T set c=c+1 where ID=2;

1. 执行器先找引擎取 ID=2 这一行。ID 是主键，引擎直接用树搜索找到这一行。如果 ID=2 这一行所在的数据页本来就在内存中，就直接返回给执行器;否则，需要先从磁盘读入内 存，然后再返回。
2. 执行器拿到引擎给的行数据，把这个值加上 1，比如原来是 N，现在就是 N+1，得到新的 一行数据，再调用引擎接口写入这行新数据。
3. 引擎将这行新数据更新到内存中，同时将这个更新操作记录到 redo log 里面，此时 redo log 处于 **prepare** 状态。然后告知执行器执行完成了，随时可以提交事务。
4. 执行器生成这个操作的 binlog，并把 binlog 写入磁盘。
5. 执行器调用引擎的提交事务接口，引擎把刚刚写入的 redo log 改成提交(**commit**)状 态，更新完成。



### 两阶段提交

redo log 与 binlog存在依赖关系，任何一个中间出现问题，就会导致异常数据恢复时，出现不一致情况，所以使用两阶段提交（prepare、commit）来解决这个问题



### 事务

##### ACID：

- read uncommitted：一个事务还没提交时，它做的变更就能被别的事务看到
- read committed：一个事务提交之后，它做的变更才会被其他事务看到
- repeatable read：一个事务执行过程中看到的数据，总是跟这个事务在启动时看到的数据是一 致的（**零时视图**）。当然在可重复读隔离级别下，未提交变更对其他事务也是不可见的
- serializable：对于同一行记录，“写”会加“写锁”，“读”会加“读锁”。当出现 读写锁冲突的时候，后访问的事务必须等前一个事务执行完成，才能继续执行

##### Problem：

- dirty read
- nonrepeatable read
- phantom read

##### 实现：

- **快照**：MySQL通过全局事务id（row trx_id） + 数据多版本 来实现快照功能

  - 可重复读情况下：

    - 事务开启前就已经表明了他能接受的最大事务id

    - 如果某天数据在本事务更新之前，被别的事务改变了，则需要遵循：**更新数据都是先读后写的，而这个读，只能读当前的值，称 为“当前读(current read)”**

      - 除了 update 语句外，**select 语句如果加锁，也 是当前读**

      - 下面这两个 select 语句，分别加了**读锁(S 锁，共享锁)**和 **写锁(X 锁，排他锁)**

        ```sql
        mysql> select k from t where id=1 lock in share mode;
        # lock in share mode 的 SQL 语句，是当前读
        mysql> select k from t where id=1 for update;
        ```

    - 事务总能读到自己更新后的新值（发现数据的当前trx_id是自己的事务id，则直接读取）

- **回滚段**：每条记录在更新的时候都会同时记录一条回滚操作。记录上的最新值，通 过回滚操作，都可以得到前一个状态的值
  - undo log： 
    - 事务失败的时候，需要恢复到之前版本
    - 当没有事务再需要它的时候，就可以删掉

##### 区别：

读提交的逻辑和可重复读的逻辑类似，它们最主要的区别是:

- 在可重复读隔离级别下，只需要在事务开始的时候找到那个 **up_limit_id**，之后事务里的其他 查询都共用这个 up_limit_id;
- 在读提交隔离级别下，每一个语句执行前都会重新算一次 up_limit_id 的值。



##### Phantom Read（幻读）

在repeatable read模式下，在同一个事务内，两次 当前读 的结果出现不一致的情况

- **幻读在 “当前读” 下才会出现**
- **幻读仅专指 “新插入的行”**（更新数据，不算幻读，是正常的当前读）

<img src="assets/image-20211005170736130.png" alt="image-20211005170736130" style="zoom: 40%;" />

幻读引发问题：

- 语义不一致：不能锁住期望锁住的记录
- 数据不一致：binlog记录的操作会出现与数据不一致的问题（binlog在statement模式下）
  - 解决：要解决可能出现的数据和日志不一致问题，需要把 binlog 格式设 置为 row

解决：**间隙锁（Gap Lock）**

- 在一行行扫描的过程中，不仅将给行加上了行锁，还给行两边的空隙，也加上 了间隙锁
- **跟间隙锁存在冲突关系的，是“往这个间隙中插入一个记录”这个操作。**间 隙锁之间都不存在冲突关系
- **next-key lock**：间隙锁和行锁合称
- 间隙锁的引入虽然解决了幻读出现的问题，但是也增加了性能开销
  - 间隙锁是在可重复读隔离级别下才会生效的，可把隔离级别设置为读提交



### 索引

##### B+Tree

- 数据块：每个数据块可存储**1200条数据**
  - 层高一定的情况下，可存储更多数据
  - 层高减少，可减少访问磁盘的次数，如果有N层，则最多需要放完N-1层磁盘（减一：是因为一般MySQL会将第一层放入内存）
  - B+Tree读写性能优秀（读log(n)，写log(n)）
  - 页分裂：新增数据，B+Tree为保证一定的顺序性，需要移动数据，如果刚好一个数据块已经满了，则会发送页分裂，新开辟一个数据库存储
  - 页合并：由于删除了数据，空间利用率降低，则会触发数据页的合并
  - 在 InnoDB 中， 每个数据页的大小默认是 **16KB**
- 主键索引：叶子节点存储整行数据
- 非主键索引：叶子节点存储主键
  - 获取真实数据需要“回表”操作
- 如果对字段做了函数计算，就 用不上索引了，这是 MySQL 的规定
  - 因为 B+Tree 在寻找元素的时候，兄弟节点是有顺序的，如果用函数运算后的新值，不在树上维护，则无法知道下一次要往哪走
  - 索引还是会使用的，只是无法快速定位，只会触发索引的全扫描



### 锁

- 全局锁：
  - 使用场景：备份数据库
    - FTWRL方式：该方式下，会在全库增加一个全局锁，使得所有表都是只读状态
    - MVCC方式：依赖事务型引擎，可在并发场景下，保证前后视图逻辑一致：mysqldump -single-transaction
  
- 表锁：
  - lock tables .... read/write：正常表锁，一般也不用了，粒度还是太粗
  - MDL：metadata lock，对表的元数据进行读写限制
    - 不需要显式使用，在访问一个表的时候会被 自动加上
    
    - 保证读写的正确性：在读数据的时候，不能有语句对表的元数据进行修改
    
    -  MySQL 5.5 版本中引入了 MDL，当对一个表做增删改查操作的时候，加 MDL 读 锁;当要对表做结构变更操作的时候，加 MDL 写锁
      - 读锁之间不互斥，因此你可以有多个线程同时对一张表增删改查。
      - 读写锁之间，写锁之间是互斥的，用来保证变更表结构操作的安全性。因此，如果有两个线 程要同时给一个表加字段，其中一个要等另一个执行完才能开始执行。
      
    - 查询MDL写锁阻塞的线程pid：
    
      ```sql
      select blocking_pid from sys.schema_table_lock_waits
      ```
  
- 行锁
  - 两阶段锁协议：**在 InnoDB 事务中，行锁是在需要的时候才加上的，但并不是不需要了就立刻释放，而是要等到事务结束时才释放。这个就是两阶段锁协议。**
    - 知道了这个设定，对我们使用事务有什么帮助呢? **那就是，如果你的事务中需要锁多个行，要把 最可能造成锁冲突、最可能影响并发度的锁尽量往后放。**

  - 死锁：

    - 两个事务，互相在等待对方释放行锁资源
    - 解决方案：
      - 直接进入等待，直到超时。这个超时时间可以通过参数 innodb_lock_wait_timeout 来设置
        - **在 InnoDB 中，innodb_lock_wait_timeout 的默认值是 50s，太久了，业务无法接受**
      - 发起死锁检测，发现死锁后，主动回滚死锁链条中的某一个事务，让其他事 务得以继续执行。将参数 innodb_deadlock_detect 设置为 on，表示开启这个逻辑
        - **正常情况下我们还是要采用第二种策略**

  - 查询行锁阻塞位置：

    ```sql
    select * from t sys.innodb_lock_waits where locked_table=`'test'.'t'`\G
    ```

    **blocking_pid: 4**

    **sql_kill_blocking_query: KILL QUERY 4**

    **sql_kill_blocking_connection: KILL 4**

- Flush Table：关闭MySQL表

```sql
# 如果 flush table 被别的操作阻塞，则会导致我们其他的select阻塞
flush tables t with read lock;
flush tables with read lock;
```



### Change Buffer

更新数据时，如果数据块在内存中，则直接操作内存，如果数据库不在内存中，为避免磁盘操作，提升性能，MySQL设计了Change Buffer，改Change Buffer具备持久化功能（merge），MySQL后台会定时merge Change Buffer到磁盘，同时，如果需要从磁盘读取数据块到内存时，也会触发merge

使用场景：只有 **“普通索引”** 可以使用，对于写多读少的业务来说性能更优秀（账单类、日志类）

配置：change buffer 的大小，可 以通过参数 **innodb_change_buffer_max_size** 来动态设置。这个参数设置为 50 的时候，表示 change buffer 的大小最多只能占用 buffer pool 的 50%



### 空间回收

**参数** **innodb_file_per_table**

- drop命令可回收磁盘空间

- 设置表数据存储在**共享表空间**或者**单独文件**
- OFF：表示存储在共享空间（与数据字典放在一起）- **即使drop掉表，也不会回收空间**
- ON：以单独文件存储，drop表后，直接删除文件
- 从 MySQL 5.6.6 版本开始，它的默认值就是 ON 了

**删除数据行，空间未减少问题：**

InnoDB引擎，delete操作导致：

- 对应数据位置标记为删除，新记录可复用该位置；当整个数据块都被删除，则整个数据块被标记为可复用，如果相邻数据块的利用率都不高，就会合并数据块，标记被合并的区域为可复用，磁盘空间不会释放
- 解决：
  - 重建表
    - alter table t engine=InnoDB
    - analyze table
    - optimize table
  - 重建表的实时方式：
    - 非Online DDL
    - Online DDL
    - Ghost：[gh-ost](https://www.cnblogs.com/zhoujinyi/p/9187502.html)为github开源项目，模拟MySQL slave来达到数据回放
  - 在重建表的时候，InnoDB 不会把 整张表占满，每个页留了 1/16 给后续的更新用。也就是说，其实重建表之后不是“最”紧凑 的。



### 排序

##### order by

**全字段排序 VS rowid 排序**

sort_buffer：排序缓存区，未超过排序缓存区大小（**sort_buffer_size**），则直接 在内存中使用**快排**，超过缓存区大小，则使用磁盘临时文件（生成多个磁盘文件，使用**归并排序**）

如果查询的所有字段加起来的长度很长，超过了 **max_length_for_sort_data** 设置的长度大小，则MySQL会优化：只将 需要排序的字段 + id放入sort_buffer或者临时文件，排序后，再语句id回表获取剩余需要的字段（称之为：rowid排序）

体现了 MySQL 的一个设计思想:**如果内存够，就要多利用内存，尽量减少磁盘访问**

##### 优先队列排序算法

对**快排**和**归并排序**的优化，因为在MySQL中，如果你使用了 order by xxx **limit x** ，如果x的数据量小于**sort_buffer_size**，那么排序时就不要维护所有数据的顺序，使用优先级队列能够减少不必要的排序消耗

##### order by rand() 

使用了**内存临时表**，内存临时表排序的时候使用 了 rowid 排序方法。

##### 磁盘临时表

tmp_table_size 这个配置限制了内存临时表的大小，默认值是 16M。如果临时表 大小超过了 **tmp_table_size**，那么内存临时表就会转成磁盘临时表

##### 严格随机

1. 取得整个表的行数，并记为 C。
2. 取得 Y = floor(C * rand())。 floor 函数在这里的作用，就是取整数部分。 
3. 再用 limit Y,1 取得一行。

```sql
# limit 后面的参数不能直接跟变量
mysql> select count(*) into @C from t;
set @Y = floor(@C * rand());
set @sql = concat("select * from t limit ", @Y, ",1"); 4 prepare stmt from @sql;
execute stmt;
DEALLOCATE prepare stmt;
```

