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

