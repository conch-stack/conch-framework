## GC调优

！！！分析内存情况！！！

- 减少Minor GC次数：
  - 如果Eden区短期存活对象较多 ，可以通过增大Eden区大小来减少Minor GC
- 减少Full GC (甚至规避)
  - 减少创建大对象（某些大对象如果触发了JVM的机制会直接被放入老年代）
    - 如果确定业务中大对象确实是朝生夕死的，可通过【-XX:PretenureSizeThreshold】参数调大大对象的阈值
  - 增大堆空间