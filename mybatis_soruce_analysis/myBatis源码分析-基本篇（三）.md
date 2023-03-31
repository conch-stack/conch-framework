## MyBatis源码分析-基本篇（三）



##### MyBatis日志集成源码分析

适配器模式 （TODO 参考我的设计模式篇）

Log4j、 Log4j2、 Apache Commons Log、 java.util.logging、Slf4j等



- LogFactory工厂：负责创建对应日志组件适配器
- Log：接口，定义MyBatis的日志接口
- Jdk14LoggingImpl：适配JDK日志的实现，内部全部由JDK的Log进行日志的方法调用

- Other

  - ```java
    java.util.logging.Logger  源码解析
    ```



##### MyBatis SQL日志打印的实现

**原理**：JDK动态代理

**描述**：将JDBC操作通过指定日志框架打印出来 包括数据SQL语句，用户传入参数，SQL影响行数等

**实现**：通过对 JDBC 的 Connection、PreparedStatement、Statement、ResultSet的代理，实现对SQL及返回值的打印逻辑

**细节**：

- BaseJdbcLogger：封装了基础的Jdbc日志代理元信息和公共特性

  - ```java
    public abstract class BaseJdbcLogger {
    
      /**
       * 记录 PreparedStatement 中的 set*() 方法
       *    PreparedStatement.setInt(int a) {xxx}
       */
      protected static final Set<String> SET_METHODS;
      /**
       * 记录 Statement 和 PreparedStatement 中的 执行SQL 的方法名
       *    executeQuery() 等
       */
      protected static final Set<String> EXECUTE_METHODS = new HashSet<>();
    
      /**
       * 存放 PreparedStatement.set*() 方法设置的 键值对
       *    用于后期打印用
       */
      private final Map<Object, Object> columnMap = new HashMap<>();
    
      /**
       * 存放 PreparedStatement.set*() 方法设置的 key
       */
      private final List<Object> columnNames = new ArrayList<>();
    
      /**
       * 存放 PreparedStatement.set*() 方法设置的 value
       */
      private final List<Object> columnValues = new ArrayList<>();
    
      /**
       * 用于输出 日志 的 Log 对象
       */
      protected final Log statementLog;
    
      /**
       * 记录 SQL 的层数，用于格式化输出SQL
       */
      protected final int queryStack;
      // ...
    }
    ```

- ConnectionLogger：实现了BaseJdbcLogger，提供对 java.sql.Connection 的代理

  - 核心代理逻辑：

    ```java
    @Override
    public Object invoke(Object proxy, Method method, Object[] params)
        throws Throwable {
      /**
       * TODO 核心的代理逻辑
       */
      try {
        if (Object.class.equals(method.getDeclaringClass())) {
          return method.invoke(this, params);
        }
        if ("prepareStatement".equals(method.getName())) {
          if (isDebugEnabled()) {
            debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
          }
          /**
           * TODO 直接代理掉 对应对象的创建
           */
          PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
          stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
          return stmt;
        } else if ("prepareCall".equals(method.getName())) {
          if (isDebugEnabled()) {
            /**
             * TODO debug下 格式化输出 PreparedStatement 的 预SQL
             */
            debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
          }
          PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
          stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
          return stmt;
        } else if ("createStatement".equals(method.getName())) {
          Statement stmt = (Statement) method.invoke(connection, params);
          stmt = StatementLogger.newInstance(stmt, statementLog, queryStack);
          return stmt;
        } else {
          return method.invoke(connection, params);
        }
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
    ```

- PreparedStatementLogger：

  - ```java
    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
      try {
        if (Object.class.equals(method.getDeclaringClass())) {
          return method.invoke(this, params);
        }
        /**
         * TODO 如果调用的是 执行SQL语句的方法，则打印其参数值
         */
        if (EXECUTE_METHODS.contains(method.getName())) {
          if (isDebugEnabled()) {
            debug("Parameters: " + getParameterValueString(), true);
          }
          /**
           * TODO 清除参数信息
           */
          clearColumnInfo();
          if ("executeQuery".equals(method.getName())) {
            /**
             * TODO 对返回值 进行 代理
             */
            ResultSet rs = (ResultSet) method.invoke(statement, params);
            return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog, queryStack);
          } else {
            return method.invoke(statement, params);
          }
        } else if (SET_METHODS.contains(method.getName())) {
          /**
           * TODO 记录 参数的设置， 缓存进入 BaseJdbcLogger 中
           */
          if ("setNull".equals(method.getName())) {
            setColumn(params[0], null);
          } else {
            setColumn(params[0], params[1]);
          }
          return method.invoke(statement, params);
        } else if ("getResultSet".equals(method.getName())) {
          ResultSet rs = (ResultSet) method.invoke(statement, params);
          return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog, queryStack);
        } else if ("getUpdateCount".equals(method.getName())) {
          /**
           * TODO 获取 影响的 记录数
           */
          int updateCount = (Integer) method.invoke(statement, params);
          if (updateCount != -1) {
            debug("   Updates: " + updateCount, false);
          }
          return updateCount;
        } else {
          return method.invoke(statement, params);
        }
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
    ```

- StatementLogger：实现基本类似PreparedStatementLogger

- ResultSetLogger：对返回值进行打印

  - ```java
    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
      try {
        if (Object.class.equals(method.getDeclaringClass())) {
          return method.invoke(this, params);
        }
        Object o = method.invoke(rs, params);
        /**
         * TODO 对 next() 方法进行处理
         */
        if ("next".equals(method.getName())) {
          if ((Boolean) o) {
            /**
             * TODO 行数 ++
             */
            rows++;
            /**
             * TODO 如果日志级别是 trace 则打印返回值的 元信息 + 返回的值
             */
            if (isTraceEnabled()) {
              ResultSetMetaData rsmd = rs.getMetaData();
              final int columnCount = rsmd.getColumnCount();
              if (first) {
                first = false;
                printColumnHeaders(rsmd, columnCount);
              }
              // TODO 会特殊处理 BLOB 类型的大数据级 ：其实就是不打印具体值，只大于 "<<BLOB>>" 字符串
              printColumnValues(columnCount);
            }
          } else {
            /**
             * 总行数
             */
            debug("     Total: " + rows, false);
          }
        }
        clearColumnInfo();
        return o;
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
    ```

    

