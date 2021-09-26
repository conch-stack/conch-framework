## Java日志



### Java Logging

从Java1.4引入，部分设计参考了Log4j



##### 日志数据类型：LogRecord



> Log4j中命名为 LogEvent



##### 控制流程

<img src="assets/image-20210704142120884.png" alt="image-20210704142120884" style="zoom: 67%;" />



<img src="assets/image-20210926110237807.png" alt="image-20210926110237807" style="zoom:50%;" />

##### 日志Filter：决定日志是否输出（java.util.logging.Filter）

```java
/**
 * Check if a given log record should be published.
 * @param record  a LogRecord
 * @return true if the log record should be published.
 */
public boolean isLoggable(LogRecord record);
```



##### 日志处理Handler：处理（java.util.logging.Handler）

- StreamHandler - 写入OutputStream中
- ConsoleHandler
- FileHandler
- SocketHandler
- MemoryHandler



##### 日志级别（java.util.logging.Level）

Logger有级别，Handler也有级别



##### 日志格式化：Formatters（java.util.logging.Formatter）

JavaSE：

- SimpleFormatter - 编写简要的“人类可读”的日志记录摘要
- XMLFormatter - 编写详细的XML结构信息

Apache：

- JdkLoggerFormatter

- OneLineFormatter

Spring：

- org.springframework.boot.logging.java.SimpleFormatter



##### 日志管理器（LogManager）- 重要

全局的 LogManager 对象，用于跟踪全局日志记录信息。LogManager 对象可以使用 静态 LogManager.getLogManager 方法获得。在 LogManager 初始化期间根据系统属 性创建的。此属性允许容器应用程序(如EJB容器)用其自身的LogManager子类代替默认 类。

LogManager 对象包括:

- 命名的 Logger 分层名称空间
- 从配置文件中读取的一组日志记录控制属性
  - readConfiguration()





##### 日志配置文件（Configuration File）

JDK的JRE的lib目标下有默认全局的Log配置logging.properties



##### 面试：

为啥Java Logging中的日志级别 Level 不使用枚举实现？  - Level是1.4就有了，而枚举在1.5才有