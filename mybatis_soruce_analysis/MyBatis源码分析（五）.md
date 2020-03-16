## MyBatis源码分析（五）

##### DataSource：数据源

MyBatis支持自己的数据源实现，同时也支持集成第三方数据源组件

工厂模式



##### 所有的数据源组件均实现了：javax.sql.DataSource 接口

- DataSource：数据源接口
  - PooledDataSource：简单的、同步的、线程安全的 datasource 连接池

    ​									（内部组合了UnpooledDataSource）

  - UnpooledDataSource：非常简单数据源（意思是啥都没有，只是包了一层，提供基础的数据源获取需要的属性及接口）；每次调用器getConnection() 接口都会 创建一个新的连接

- DataSourceFactory：工厂接口

  - UnpooledDataSourceFactory
    - PooledDataSourceFactory

  - JndiDataSourceFactory



##### DataSourceFactory源码：

```java
public interface DataSourceFactory {

  /** TODO 设置 DataSource 相关属性 */
  void setProperties(Properties props);

  /** TODO 获取 DataSource */
  DataSource getDataSource();
}
```



##### UnpooledDataSourceFactory源码：

```java
public class UnpooledDataSourceFactory implements DataSourceFactory {

  private static final String DRIVER_PROPERTY_PREFIX = "driver.";
  private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

  protected DataSource dataSource;

  /**
   * 直接实例化
   */
  public UnpooledDataSourceFactory() {
    this.dataSource = new UnpooledDataSource();
  }

  /**
   * 给 UnpooledDataSource 设置属性
   * @param properties
   */
  @Override
  public void setProperties(Properties properties) {
    Properties driverProperties = new Properties();
    // TODO 将DataSource对象 包装成 非常好操作  的 MetaObject（反射赋值）
    MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);
    for (Object key : properties.keySet()) {
      String propertyName = (String) key;
      if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
        String value = properties.getProperty(propertyName);
        driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
      } else if (metaDataSource.hasSetter(propertyName)) {
        String value = (String) properties.get(propertyName);
        // TODO 将属性类型进行转换：依据metaDataSource的对应propertyName的属性setter参数类型
        Object convertedValue = convertValue(metaDataSource, propertyName, value);
        // TODO 设置值
        metaDataSource.setValue(propertyName, convertedValue);
      } else {
        throw new DataSourceException("Unknown DataSource property: " + propertyName);
      }
    }
    if (driverProperties.size() > 0) {
      metaDataSource.setValue("driverProperties", driverProperties);
    }
  }

  @Override
  public DataSource getDataSource() {
    return dataSource;
  }
  // ...
}
```



##### PooledDataSourceFactory源码：

```java
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

  public PooledDataSourceFactory() {
    this.dataSource = new PooledDataSource();
  }

}
```



##### UnpooledDataSource源码：

```java
/**
 * TODO 需对数据库Driver有一定了解
 */
public class UnpooledDataSource implements DataSource {

  /** 加载 数据库 Driver 的ClassLoader */
  private ClassLoader driverClassLoader;
  /** 数据库 Driver 属性配置 */
  private Properties driverProperties;
  /** 缓存所有已经注册的数据库 Driver */
  private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();

  private String driver; // 数据库驱动的名称
  private String url;    // 数据库的URL
  private String username;  // 数据库的用户名
  private String password;  // 数据库的密码

  private Boolean autoCommit; // 是否自动提交
  private Integer defaultTransactionIsolationLevel;  // 默认事务隔离级别
  private Integer defaultNetworkTimeout; // 默认网络超时时间

	/** 初始化 注册数据库 Driver */ 
	private synchronized void initializeDriver() throws SQLException {
    if (!registeredDrivers.containsKey(driver)) {
      Class<?> driverType;
      try {
        if (driverClassLoader != null) {
          driverType = Class.forName(driver, true, driverClassLoader);
        } else {
          driverType = Resources.classForName(driver);
        }
        // DriverManager requires the driver to be loaded via the system ClassLoader.
        // http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
        Driver driverInstance = (Driver)driverType.getDeclaredConstructor().newInstance();
        DriverManager.registerDriver(new DriverProxy(driverInstance));
        registeredDrivers.put(driver, driverInstance);
      } catch (Exception e) {
        throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
      }
    }
  }

	/** 获取数据库连接 Connection */
	private Connection doGetConnection(String username, String password) throws SQLException {
    Properties props = new Properties();
    if (driverProperties != null) {
      props.putAll(driverProperties);
    }
    if (username != null) {
      props.setProperty("user", username);
    }
    if (password != null) {
      props.setProperty("password", password);
    }
    return doGetConnection(props);
  }
  private Connection doGetConnection(Properties properties) throws SQLException {
    initializeDriver();
    /** 真实创建连接 */
    Connection connection = DriverManager.getConnection(url, properties);
    /** 配置数据库连接和 和 隔离级别等 */
    configureConnection(connection);
    return connection;
  }
  // ...
}
```



##### 重点：PooledDataSource源码：

> PooledDataSource 通过 PoolState 管理 PooledConnection

- PooledConnection：代理 java.sql.Connection

```java
class PooledConnection implements InvocationHandler {

  private static final String CLOSE = "close";
  private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

  private final int hashCode;
  // TODO 持有 PooledDataSource 对象，用于归还 Connection 等
  private final PooledDataSource dataSource;
  private final Connection realConnection;   // 真正的数据库连接
  private final Connection proxyConnection;  // 代理的数据库连接
  private long checkoutTimestamp;         // 从连接池中取出该连接的时间
  private long createdTimestamp;        // 该连接创建时间
  private long lastUsedTimestamp;       // 最后一次使用时间
  // TODO 由数据库URL、用户名、密码计算出来的Hash值，用于标识该连接所在的连接池
  private int connectionTypeCode;
  // TODO 检测当前PooledConnection是否有效，防止程序通过 close() 归还连接后，依然使用该连接操作数据库
  private boolean valid;
  //...
  
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();
    // TODO 调用Connection的Close方法时，归还连接
    if (CLOSE.equals(methodName)) {
      dataSource.pushConnection(this);
      return null;
    }
    try {
      if (!Object.class.equals(method.getDeclaringClass())) {
        checkConnection(); // 校验连接valid
      }
      // 调用真正数据库连接对象的方法
      return method.invoke(realConnection, args);
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }
}
```

- **PoolState：管理PooledConnection对象及其状态**

```java
public class PoolState {
  protected PooledDataSource dataSource;

  // TODO 管理闲置状态连接
  protected final List<PooledConnection> idleConnections = new ArrayList<>();
  // TODO 管理活跃状态连接  
  // PooledDataSource的所有连接都是放在这个里面存储的
  protected final List<PooledConnection> activeConnections = new ArrayList<>();
  /**
   * 统计
   */
  protected long requestCount = 0;  // 请求数据库连接次数
  protected long accumulatedRequestTime = 0;  // 累计请求时间
  protected long accumulatedCheckoutTime = 0; // 所有连接累计借出时长
  protected long claimedOverdueConnectionCount = 0; // 超时的连接个数
  protected long accumulatedCheckoutTimeOfOverdueConnections = 0;  // 累计超时时间
  protected long accumulatedWaitTime = 0;  // 累计等待时间
  protected long hadToWaitCount = 0;  // 等待次数
  protected long badConnectionCount = 0;  // 无效连接次数
  // ...
}
```

- PooledDataSource：
- 核心方法：借助 poolState 对象锁，继续wait()和notifyAll()的操作，实现类似阻塞队列的效果
  - popConnection(String username, String password)   // 取出连接
  - pushConnection(PooledConnection conn)  // 归还连接
  - pingConnection(PooledConnection conn) // SQL检测连接
  - forceCloseAll()  // 强制关闭所有连接，当修改数据库属性（URL，用户名，密码，autoCommit等）触发
    - 将所有PooledConnection置为无效
    - 清空：activeConnections 和 idleConnections

```java
public class PooledDataSource implements DataSource {
  
  // TODO 持有一个 PoolState 对象，管理所有连接
  private final PoolState state = new PoolState(this);
	// TODO 持有一个 UnpooledDataSource 对象，用于创建新连接
  private final UnpooledDataSource dataSource;

  // OPTIONAL CONFIGURATION FIELDS
  protected int poolMaximumActiveConnections = 10;    // 最大活跃连接数
  protected int poolMaximumIdleConnections = 5;       // 最大空闲连接数
  protected int poolMaximumCheckoutTime = 20000;      // 最大取出时长
  protected int poolTimeToWait = 20000;               // 无法获取连接时，线程等待时间
  protected int poolMaximumLocalBadConnectionTolerance = 3;   // 最大错误连接容忍数
  protected String poolPingQuery = "NO PING QUERY SET"; // 测试数据库连接是否可用，测试SQL
  protected boolean poolPingEnabled; // 是否允许发送测试SQL语句
  protected int poolPingConnectionsNotUsedFor;  // 当连接超过该值（毫秒）未使用时，发送一次测试SQL，检测连接是否正常

  // TODO 根据数据库URL、用户名、密码生成一个hash值，该hash值标识当前的连接池
  //（意味着可能还有连接池的池：多数据源）在构造函数中初始化
  private int expectedConnectionTypeCode;

  public PooledDataSource() {
    dataSource = new UnpooledDataSource();
  }
  // ...
}
```