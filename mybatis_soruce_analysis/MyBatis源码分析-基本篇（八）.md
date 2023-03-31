## MyBatis源码分析-基本篇（八）

> MyBatis 缓存模块



MyBatis的缓存分为 一级缓存 、二级缓存；都是基于 Cache 接口

装饰器模式



##### Cache：

```java
/**
 * SPI for cache providers.
 * TODO 每个 NameSpace 会有一个 cache 对象实例
 */
public interface Cache {

  // 该缓存对象的 id
  String getId();

  /**
   * 添加一个缓存
   * @param key 缓存key，通常为 CacheKey 对象
   */
  void putObject(Object key, Object value);

  // 获取一个缓存
  Object getObject(Object key);

  // 删除一个缓存key
  Object removeObject(Object key);

  // 清空缓存
  void clear();

  // 获取缓存项的个数
  int getSize();

  // 获取读写锁，不会被MyBatis核心代码调用
  default ReadWriteLock getReadWriteLock() {
    return null;
  }
}
```



##### PerpetualCache: (Cache接口的实现类)

```java
// 比较简单，就是用 HashMap 去存储缓存
public class PerpetualCache implements Cache {

  private final String id;
  private final Map<Object, Object> cache = new HashMap<>();

  public PerpetualCache(String id) {
    this.id = id;
  }
  // ...
}
```



##### 装饰器们：（decorators包下）

这些接口都实现了Cache接口，并且组合了 Cache 的实现类（主要是 PerpetualCache 对象，也可多个装饰者互相组合，来完成更加完善的功能），在 PerpetualCache对象的基础上，提供额外的功能。

二级缓存（装饰器动态组合，后面详细看）

- **BlockingCache：阻塞缓存，保证只有一个线程 操作数据库中对应的key的值**

```java
public class BlockingCache implements Cache {

  private long timeout; // 锁的超时时间
  private final Cache delegate; // 被装饰的 Cache 实例
  private final ConcurrentHashMap<Object, ReentrantLock> locks; // 每个缓存key都对应一个可重入锁

  public BlockingCache(Cache delegate) {
    this.delegate = delegate;
    this.locks = new ConcurrentHashMap<>();
  }
  // ...
}
```



- **FifoCache：先进先出缓存**

```java
public class FifoCache implements Cache {

  private final Cache delegate;  // 代表
  private final Deque<Object> keyList; // 缓存key的队列
  private int size; // 队列大小限制

  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    this.keyList = new LinkedList<>();
    this.size = 1024;
  }

  @Override
  public void putObject(Object key, Object value) {
    cycleKeyList(key); // 处理先进先出逻辑，如果缓存区满了，则丢弃最先进入的key
    delegate.putObject(key, value);
  }
  // ...
  
  @Override
  public void clear() {
    delegate.clear();
    keyList.clear();
  }

  // 处理先进先出逻辑，如果缓存区满了，则丢弃最先进入的key
  private void cycleKeyList(Object key) {
    keyList.addLast(key);
    if (keyList.size() > size) {
      Object oldestKey = keyList.removeFirst();
      delegate.removeObject(oldestKey);
    }
  }
}
```



- **LruCache：least recently used 最近最少使用原则的 缓存策略**

```java
public class LruCache implements Cache {

  private final Cache delegate; // 代表
  // TODO 有序链表，用于记录key的最近使用情况
  private Map<Object, Object> keyMap;
  // TODO 记录最少被使用的缓存项的key
  private Object eldestKey;

  public LruCache(Cache delegate) {
    this.delegate = delegate;
    setSize(1024);
  }

  public void setSize(final int size) {
    // TODO 记录key的最近使用情况
    //      accessOrder=true 表示：LinkedHashMap.get()方法会改变其记录的顺序
    //      具体可参考 LinkedHashMap 的原理： 大致是get(key)时会将key的记录放到链表的尾部
    keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
      private static final long serialVersionUID = 4267176411845948333L;

      // TODO 超出链表大小后，标识最早（最少）使用的key
      @Override
      protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        boolean tooBig = size() > size;
        if (tooBig) {
          eldestKey = eldest.getKey();
        }
        return tooBig;
      }
    };
  }

  @Override
  public void putObject(Object key, Object value) {
    delegate.putObject(key, value);
    // TODO 检测最少使用的key，进行删除操作
    cycleKeyList(key);
  }

  @Override
  public Object getObject(Object key) {
    // TODO 改变记录顺序
    keyMap.get(key); //touch
    return delegate.getObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyMap.clear();
  }

  private void cycleKeyList(Object key) {
    keyMap.put(key, key);
    // 删除
    if (eldestKey != null) {
      delegate.removeObject(eldestKey);
      eldestKey = null;
    }
  }
}
```



Java引用类型：

- Strong Reference：强引用；new Object() 等常见的都是强引用，内存不足是，GC无法直接回收这些对象。
- Soft Reference：软引用；介于强引用和弱引用之前的引用类型，当内存不足时，GC会回收那些只被软引用的对象；适合引用那些容易通过其他方式恢复的引用，例如从数据库中缓存的对象就可以从数据库中恢复。如果对象被GC回收，则get时会返回null
- Weak Reference：弱引用；如果对象只有弱引用指向，那么GC时就一定会回收掉，不用等到内存不足。
- Phantom Reference：幽灵引用（虚引用）；始终返回null



引用队列：Reference Queue

> 很多场景下，我们应用程序需要在一个对象的可达性（是否被GC回收）发生**变化时得到通知**，可以通过给该对象关联一个**引用队列** 来实现；
>
> 当SoftReference所引用的对象被GC回收时，JVM会将该SoftReference对象添加到与之关联的引用队列中，当需要检测时，就可以从这些队列中取出SoftReference对象；
>
> 不止是SoftReference可以这样做，WeakReference 和 PhantomReference都可以；
>
> 可参考 WeakHashMap的实现；



- **SoftCache：软引用缓存，基于 SoftReference**

```java
public class SoftCache implements Cache {
  // 在SoftCache中，最近被使用的一部分缓存不会被GC掉，通过将其value添加到 这个队列（将强引用指向该value）
  private final Deque<Object> hardLinksToAvoidGarbageCollection;
  // TODO 用于记录已经被GC掉的缓存项所对应的SoftEntry对象
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
  private final Cache delegate;
  // TODO 强引用个数
  private int numberOfHardLinks;

  public SoftCache(Cache delegate) {
    this.delegate = delegate;
    this.numberOfHardLinks = 256;
    this.hardLinksToAvoidGarbageCollection = new LinkedList<>(); // 有序链表
    this.queueOfGarbageCollectedEntries = new ReferenceQueue<>(); // 引用队列
  }

  @Override
  public int getSize() {
    removeGarbageCollectedItems();
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object value) {
    // TODO 清除已经被GC的缓存项
    removeGarbageCollectedItems();
    // key value 包装成 SoftEntry 对象，塞入缓存
    delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
  }

  @Override
  public Object getObject(Object key) {
    Object result = null;
    // TODO 查询缓存项
    SoftReference<Object> softReference = (SoftReference<Object>) delegate.getObject(key);
    if (softReference != null) { // TODO 是否存在该缓存项
      result = softReference.get();
      if (result == null) { // TODO value被GC了
        delegate.removeObject(key);
      } else {
        synchronized (hardLinksToAvoidGarbageCollection) {
          // TODO 将value加入最近使用队列 强引用他
          hardLinksToAvoidGarbageCollection.addFirst(result); 
          if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
            hardLinksToAvoidGarbageCollection.removeLast(); // TODO 删除最老的缓冲项，取消强引用
          }
        }
      }
    }
    return result;
  }

  @Override
  public Object removeObject(Object key) {
    removeGarbageCollectedItems();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    synchronized (hardLinksToAvoidGarbageCollection) {
      hardLinksToAvoidGarbageCollection.clear();
    }
    removeGarbageCollectedItems();
    delegate.clear();
  }

  // TODO 从 引用队列中 poll 出来被gc掉的SoftEntry对象，然后在缓存中remove掉对应的缓存实例
  private void removeGarbageCollectedItems() {
    SoftEntry sv;
    while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
      delegate.removeObject(sv.key);
    }
  }

  // TODO SoftCache 中缓存 value 为 SoftEntry 对象
  private static class SoftEntry extends SoftReference<Object> {
    private final Object key;

    SoftEntry(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
      // TODO value 为 软引用
      super(value, garbageCollectionQueue);
      // TODO key 为 强引用
      this.key = key;
    }
  }
}
```



- **WeakCache：实现基本类似SoftCache**

- **ScheduledCache：周期缓存**

```java
public class ScheduledCache implements Cache {

  private final Cache delegate;
  protected long clearInterval; // 缓存周期
  protected long lastClear; // 最近清空缓存时间

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
    this.clearInterval = TimeUnit.HOURS.toMillis(1); // 默认1小时
    this.lastClear = System.currentTimeMillis();
  }

  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale(); // 先检测是否需要清除缓存
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public void clear() {
    lastClear = System.currentTimeMillis();
    delegate.clear();
  }

  // TODO 处理周期情况缓存的逻辑 （全部清空）
  private boolean clearWhenStale() {
    if (System.currentTimeMillis() - lastClear > clearInterval) { // easy
      clear();
      return true;
    }
    return false;
  }
}
```



- **TransactionalCache：二级缓存的事务buffer**

> 以事务的commit为清空缓存的标准



##### CacheKey: (封装多个影响缓存项的因素)

> MyBatis中涉及动态sql等诸多因素，索引不能单纯以String为缓存key

```java
public class CacheKey implements Cloneable, Serializable {

  private static final long serialVersionUID = 1146682552656046210L;

  // null Cache key ？？？ TODO 啥时候会用？
  public static final CacheKey NULL_CACHE_KEY = new CacheKey() {
    @Override
    public void update(Object object) {
      throw new CacheException("Not allowed to update a null cache key instance.");
    }
    @Override
    public void updateAll(Object[] objects) {
      throw new CacheException("Not allowed to update a null cache key instance.");
    }
  };

  private static final int DEFAULT_MULTIPLIER = 37;
  private static final int DEFAULT_HASHCODE = 17;

  // TODO 参与计算hashcode的算子：默认为37
  private final int multiplier;
  // TODO CacheKey对象的hashcode：初始值为 17
  private int hashcode;
  // TODO 校验和
  private long checksum;
  // TODO updataList集合个数
  private int count;
  // 8/21/2017 - Sonarlint flags this as needing to be marked transient.  While true if content is not serializable, this is not always true and thus should not be marked transient.
  // TODO 由该集合中的所有对象共同决定两个CacheKey是否相同
  // 存放：
  // 		1. MappedStatement的id
  //		2. 指定查询结果集的范围 RowBounds.offset 和 RowBounds.limit
  //		3. 查询所使用的sql语句，就是 boundSql.getSql() 返回的SQL语句，可能包含 "?" 占位符
  //		4. 用户传递给该SQL的语句的实际参数
  private List<Object> updateList;

  public CacheKey() {
    this.hashcode = DEFAULT_HASHCODE;
    this.multiplier = DEFAULT_MULTIPLIER;
    this.count = 0;
    this.updateList = new ArrayList<>();
  }

  // TODO 添加 对象 到 updateList中
  public void update(Object object) {
    // ArrayUtil会识别object的类型，并进行对应的hashcode的生成
    int baseHashCode = object == null ? 1 : ArrayUtil.hashCode(object);
		// 计算一些 校验标识
    count++;
    checksum += baseHashCode;
    baseHashCode *= count;

    hashcode = multiplier * hashcode + baseHashCode;

    updateList.add(object);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) { // 同一对象
      return true;
    }
    if (!(object instanceof CacheKey)) { // 不属于CacheKey
      return false;
    }

    final CacheKey cacheKey = (CacheKey) object; 
    if (hashcode != cacheKey.hashcode) {  // 校验hashcode
      return false;
    }
    if (checksum != cacheKey.checksum) {  // 校验 checksum
      return false;
    }
    if (count != cacheKey.count) {  // 校验count
      return false;
    }

    for (int i = 0; i < updateList.size(); i++) { // 校验updateList中的每个选项
      Object thisObject = updateList.get(i);
      Object thatObject = cacheKey.updateList.get(i);
      if (!ArrayUtil.equals(thisObject, thatObject)) {
        return false;
      }
    }
    return true;
  }
  // ...
}
```