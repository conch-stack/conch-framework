# Mybatis设计

#### 需要愿景

- 用户只需要定义持久层接口、方法对应的SQL语句
- 用户需指明接口方法的参数与语句参数的对应关系
- 用户需指明查询结果集与对象属性的映射关系
- 框架完成接口对象的生成，JDBC执行过程

#### 设计

##### 需求1：用户只需要定义持久层接口、方法对应的SQL语句

1、我们该提供什么样的方式来让用户定义SQL语句?

- XML:独立于代码，修改很方便(不需改代码)

- 注解:直接加在方法上，零xml配置。

2、SQL语句怎么与接口方法对应?

- Mapper与Dao的关联关系定义
- 扫描（包下）@Mapper注解的类
- 扫描到Mapper后同时加载其xml

3、这些SQL语句、对应关系我们框架需要获取到，谁来获取?又该如何表示存储

- 定义信息存储到Configuration中



##### 需求2：用户需指明接口方法的参数与语句参数的对应关系

JDBC的SQL预编译功能

- 定义@Param注解解析
- 直接反射获取方法参数名（name）

- \#{name}不用于参数预编译、${name}用于字符串替换

参数映射：

- 正则表达式
- antlr

表示：

- 预编译问好的index下标
- 对应值来源

解析触发：

- 设计如何执行一个Mapper接口
- 定义SqlSession & SqlSessionFactory



##### 需求3：



##### 需求4：框架完成接口对象的生成，JDBC执行过程


### 1. 设计一个类存放从xml和注解获得的SQL映射信息

```java
public class MappedStatement {

    /**
     * 唯一编号：完整类名+方法名
     */
    private String id;

    /**
     * sql
     */
    private String sql;

    /**
     * sql命令类型
     */
    private SqlCommandType sqlCommandType;

}

```



### 2. 存放MappedStatement

```java
public class Configuration {

    /**
     * key = MappedStatement.id
     */
    private Map<String, MappedStatement> mappedStatements;

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public boolean hasMappedStatement(String id) {
        return mappedStatements.containsKey(id);
    }

}
```