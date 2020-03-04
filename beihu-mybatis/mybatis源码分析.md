## MyBatis源码分析

##### 传统JDBC方式：

> db_url -> driver -> connection -> statement ->执行sql -> resultset -> 手动转换为JavaBean



##### 源码结构：

```shell
$ tree -L 1
.
├── annotations
├── binding
├── builder
├── cache
├── cursor
├── datasource
├── exceptions
├── executor
├── io
├── jdbc
├── lang
├── logging
├── mapping
├── package-info.java
├── parsing
├── plugin
├── reflection
├── scripting
├── session
├── transaction
└── type
```



##### XML方式解析：

- 加载XML文件 -> InputStream -> XPathParser

- 基于XPath解析 XML Node 

  - XMLMapperBuilder

    - mapper节点解析
    - parameterMap 节点解析
    - resultMap 节点解析
    - sql 节点解析
    - select|insert|update|delete 节点解析

  - XMLScriptBuilder

    - 先解析动态SQL脚本节点

      - > - trim      TrimHandler 
        > - where     WhereHandler
        > - set 、foreach  、if  、choose  、when  、otherwise 、bind     

    - 再解析SQL中的占位符 （#{xxx}）

      - > SqlSourceBuilder.parse()  -> ParameterMappingTokenHandler -> GenericTokenParser

- SQL执行

  - Executor
    - 维护一级、二级缓存；提供事务控制支持等
  - StatementHandler
    - Executor将数据库相关操作委托给StatementHandler
  - ParameterHandler
    - StatementHandler先通过ParameterHandler完成SQL语句的实参绑定
    - 之后StatementHandler调用java.sql.Statement对象执行SQL并得到返回结果集
  - ResultSetHandler
    - 最后ResultSetHandler完成结果集的映射



##### MyBatis执行一条语句的完整流程：

<img src="assets/image-20200304200333284.png" alt="image-20200304200333284" style="zoom:50%;" />

