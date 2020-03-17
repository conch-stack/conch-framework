## MyBatis源码分析（六）

事务

- Transaction：抽象数据库事务，用于管理
  - JdbcTransaction
  - ManagedTransaction
- TransactionFactory
  - JdbcTransactionFactory
  - ManagedTransactionFactory



##### Transaction

```java
/**
 * TODO 封装了一个数据库连接
 *      处理其生命周期：创建、准备、提交/回滚、关闭
 */
public interface Transaction {

  /**
   * TODO 获取（检索）对应数据库连接对象
   */
  Connection getConnection() throws SQLException;

  /**
   * TODO 提交事务
   */
  void commit() throws SQLException;

  /**
   * TODO 回滚事务
   */
  void rollback() throws SQLException;

  /**
   * TODO 关闭数据库连接
   */
  void close() throws SQLException;

  /**
   * TODO 获取事务超时时间，如果有
   */
  Integer getTimeout() throws SQLException;
}
```



##### JdbcTransaction

```java
public class JdbcTransaction implements Transaction {

  // TODO 事务对应的数据库连接 commit() 和 rollback() 和 setAutoCommit() 
  // 会调用Connection对应方法来实现
  protected Connection connection;
  // TODO 数据库连接所属的数据源
  protected DataSource dataSource;
  // TODO 隔离级别
  protected TransactionIsolationLevel level;
  // TODO 自动commit
  protected boolean autoCommit;
  
  @Override
  public void commit() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      if (log.isDebugEnabled()) {
        log.debug("Committing JDBC Connection [" + connection + "]");
      }
      connection.commit();
    }
  }
  // ...
}
```



##### ManagedTransaction：以及TransactionFactory和其实现类 代码比较简单

