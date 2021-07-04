## Java配置管理



- 本地配置

- 远程配置

- 内部配置

- 外部配置（优先级高）

  - ```java
    // 例如：ForkJoinPool
    String pp = System.getProperty
        ("java.util.concurrent.ForkJoinPool.common.parallelism");
    ```



##### 操作本机全局配置（Preferences.java）

- windows：注册表
- linux、unix：文件



##### 扩展（外部化）配置:

- Apache Commons Configuration

  - org.apache.commons.configuration2.convert.PropertyConverter

    ```pom
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-configuration2</artifactId>
        <version>2.7</version>
    </dependency>
    ```

- MircoProfile
  - 配置统一门面接口 **- org.eclipse.microprofile.config.Config**
    - 与 配置来源 - ConfigSource 一对多
    - 相较于 ConfigSource 获取配置而言，它增加了类型转换
  - 配置来源 **- ConfigSource**
    - 属性键值对信息 - getProperties() 
    - 当前配置的绝对顺序 - getOrdinal() 
    - 获取配置方法 - getValue()
  - 配置 **SPI - org.eclipse.microprofile.config.spi.ConfigPr oviderResolver**
    - 利用 Java ServiceLoader 来加载对应的实现类



##### 技巧：

- 内部可变集合，不要暴露到外部

- Java 系统属性最好通过本地变量保存，使用Map保存，尽可能运行期不去调整

  ```java
  System.getProperties()
  ```



##### 作业：

实现配置监听变化
