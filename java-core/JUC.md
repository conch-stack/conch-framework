## JUC



##### ConcurrentHashMap VS ConcurrentSkipListMap

在4线程1.6万数据的条件下，ConcurrentHashMap 存取速度是ConcurrentSkipListMap 的4倍左右。

但ConcurrentSkipListMap有几个ConcurrentHashMap 不能比拟的优点：

1、ConcurrentSkipListMap 的key是有序的。

2、ConcurrentSkipListMap 支持更高的并发。ConcurrentSkipListMap 的存取时间是log（N），和线程数几乎无关。也就是说在数据量一定的情况下，并发的线程越多，ConcurrentSkipListMap越能体现出他的优势。 

