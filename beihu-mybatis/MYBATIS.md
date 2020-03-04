# Mybatis设计


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