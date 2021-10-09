## Redis

### 五大核心数据结构

- **string**

  - 内部编码
    - int：8个字节的长整型
    - embstr：小于等于39个字节的字符串
    - raw：大于39个字节的字符串
  - 场景
    - 缓存
    - 计数
    - 共享session
    - 限速

- **hash**

  - 内部编码
    - ziplist：压缩列表，当哈希类型元素个数小于`hash-max-ziplist-entries`配置（默认512个）同时所有值都小于`hash-max-ziplist-value`配置（默认64字节）时使用，ziplist使用更加紧凑的结构实现多个元素的连续存储，所以比hashtable更加节省内存。
    - hashtable：哈希表，当ziplist不能满足要求时，会使用hashtable

- **list**

  - 内部编码
    - todo
  - 场景
    - 轻量消息队列
    - 栈
    - 文章列表：`lrange key 0 9`分页获取文章列表

- **set**

  - 内部编码

    - intset：整数集合，当集合中的元素都是整数且元素个数小于`set-max-intset-entries`配置（默认512个）时，redis会选用intset来作为集合的内部实现，从而减少内存的使用。
    - hashtable：哈希表，当intset不能满足要求时，会使用hashtable。

  - 场景

    - **用户标签**

      ```shell
      # 给用户打标签
      sadd user:1:tags tag1 tag2
      
      # 给标签添加用户
      sadd tag1:users user:1
      sadd tag2:users user:1
      
      # 使用交集（sinter）求两个user的共同标签
      sinter user:1:tags user:2:tags
      ```

    - 随机

      ```shell
      # 随机获取count个元素，集合元素个数不变
      srandmember key [count]
      
      # 随机弹出count个元素，元素从集合弹出，集合元素个数改变
      spop key [count]
      ```

- **zset**

  - 内部编码

    - ziplist：压缩列表，当有序集合的元素个数小于`list-max-ziplist-entries`配置（默认128个）同时所有值都小于`list-max-ziplist-value`配置（默认64字节）时使用。
    - skiplist：跳跃表，当不满足ziplist的要求时，会使用skiplist。

  - 场景

    - 排行榜Top

      ```shell
      # 用户发布一篇文章，初始点赞数为0，即score为0
      zadd user:article 0 a
      
      # 有人给文章a点赞，递增1
      zincrby user:article 1 a
      
      # 查询点赞前三篇文章
      zrevrange user:article 0 2
      
      # 查询点赞后三篇文章
      zrange user:article 0 2
      ```







### todo list：

- bitmap？

- 大数据量，如何优化？