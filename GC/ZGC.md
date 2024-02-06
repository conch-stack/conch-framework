## ZGC

### Allocation Stall

ZGC日志中可以看到**大量的秒级Allocation Stall**，已经类似于FGC了。Allocation Stall是一种**GC吞吐量不够时触发的线程级的STW**：当没有剩余内存可供申请时，ZGC会暂停当前用户线程并专注于回收内存。