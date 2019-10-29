package ltd.beihu.core.mybatis;

/**
 * @Author: zjz
 * @Desc: 存放注解获得的sql映射信息
 * @Date: 2019/10/29
 * @Version: V1.0.0
 */
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }
}
