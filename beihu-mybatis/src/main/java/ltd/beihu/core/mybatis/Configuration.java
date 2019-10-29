package ltd.beihu.core.mybatis;

import java.util.Map;

/**
 * @Author: zjz
 * @Desc: 配置：存放MappedStatement
 * @Date: 2019/10/29
 * @Version: V1.0.0
 */
public class Configuration {

    /**
     * key: MappedStatement.id
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
