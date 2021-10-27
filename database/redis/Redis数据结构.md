## Redis的“数据结构”

### 前置知识

- 内存分配库：jemalloc
  - jemalloc分配内存时，会根据我们申请的字节数N，找一个比N大，但是最接近N的 **2的幂次方的数** 作为分配空间，这样可以减少频繁分配的次数



### Redis内部对象

##### RedisDb



##### dist



##### distht



##### distEntry（24B）

Redis全局Hash表的每个item定义为 dictEntry 结构体

- *key（8B）：指向key
- *value（8B）：指向value
- *next（8B）：指向下一个dictEntry

使用String存储数据，每个key都对应一个distEntry，占用空间大



##### RedisObject（16B）

> 最后一次访问时间

- 元数据（8B）
  - type：对象类型，例如：string
  - encoding：编码方式，例如 raw
  - lru:LRU_BITS：
  - refcount：被引用次数
- *ptr（8B）：指向真实对象（real object），例如SDS

设计优化点：

- 如果保存的Long类型的整数时，*ptr直接赋值为整数数据，节约内存空间



##### SDS（Simple Dynamic String）：简单动态字符串

- len(4B)：占4个字节，表示buf的已用长度
- alloc(4B)：占4个字节，表示buf的实际分配长度
- buf：字节数组，保存实际数据（为了表示字节数组的结尾，Redis会自动在数组的最后一个加上一个 "\0"，额外消耗一个字节的开销）



##### ziplist：压缩列表

利用一段连续的entry保存数据
