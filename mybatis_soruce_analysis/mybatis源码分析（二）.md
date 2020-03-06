## MyBatis源码分析（二）

##### 基础支持层：

- XML解析

  - 封装XPath到XPathParser
  - 封装具体的XML的Document到XNode
    - XNode的attributes(属性值)会利用定义的(variables属性)解析字符串拼接：PropertyParser：VariableTokenHandler+GenericTokenParser
  - 封装所有占位符解析到GenericTokenParser，具体解析逻辑封装到TokenHandler（策略）
  - XMLConfigBuilder解析mybatis-config.xml
  - XMLMappedBuilder解析每个Mapper.xml文件，一个Mapper对应一个XMLMappedBuilder，借助XPath解析对应元素节点
  - XMLStatementBuilder专门用于解析Statement ( 即select|insert|update|delete这些XML节点 )
  - XMLScriptBuilder专门用于解析动态sql节点 ( 即trim|where|if|foreach等 )
  - SqlSourceBuilder专门用于构建具体SQL (SqlSource:BoundSql) ，会利用（ParameterMappingTokenHandler+GenericTokenParser）解析预编译参数
  - SqlSource策略，支持不同策略生成SQL（DynamicSqlSource，StaticSqlSource，RawSqlSource，ProviderSqlSource等）

- Refection反射

  - Refector：每个Reflector对象都对应一个类

    - 缓存了反射操作需要的类的元信息

    - cached set of class definition information that allows for easy mapping **between property name and getter/setter methods**

      ```java
      public class Reflector {
      
        /**
         * 源Class
         */
        private final Class<?> type;
      
        /**
         * 可读属性名:存在相应getter方法
         */
        private final String[] readablePropertyNames;
      
        /**
         * 可写属性名:存在相关setter方法
         */
        private final String[] writablePropertyNames;
      
        /**
         * Setter方法的invoker
         *      <property_name, setter_method_invoker>
         */
        private final Map<String, Invoker> setMethods = new HashMap<>();
      
        /**
         * Getter方法的invoker
         *      <property_name, getter_method_invoker>
         */
        private final Map<String, Invoker> getMethods = new HashMap<>();
      
        /**
         * Setter的参数类型
         *    <property_name, setter_method_param_class>
         */
        private final Map<String, Class<?>> setTypes = new HashMap<>();
      
        /**
         * Getter的返回值类型
         *    <property_name, getter_method_return_type_class>
         */
        private final Map<String, Class<?>> getTypes = new HashMap<>();
      
        /**
         * 默认构造器
         */
        private Constructor<?> defaultConstructor;
      
        /**
         * 记录所有属性的集合
         * 属性名转大写 <UpperCase_Property_name, Original_Property_name>
         */
        private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();
        ...
      }
      ```

      























##### other：

```
简析：Spring Boot MyBatis运行路径：(暂时放在这里，后面到具体章节会移走)

1. 自动配置
		MapperAutoConfiguration
2. 构建SqlSessionFactory
		2.1 创建SqlSessionFactoryBean
		2.2 创建Configuration，设置给SqlSessionFactoryBean
				创建DataSource，设置给SqlSessionFactoryBean
				读取Mapper Location文件Resource给到SqlSessionFactoryBean
				获取拦截器Interceptor给到SqlSessionFactoryBean的plugins
				获取别名等等...
    2.3 调用SqlSessionFactoryBean.getBean() 从而调用 
        afterPropertiesSet() -> buildSqlSessionFactory() 构建
```



