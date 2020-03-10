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

  - Invoker
  
    - MyBatis将方法、属性的反射包装了一层，统一调用模式
  
    - ```
      Invoker
        |_ AmbiguousMethodInvoker 模糊方法反射 抛异常
        |_ MethodInvoker          有效方法反射
        |_ GetFieldInvoker        获取字段值的反射
        |_ SetFieldInvoker        设置字段值的反射
      ```
  
  - ReflectorFactory：构造并缓存Refector
  
  - TypeParameterResolver：
  
    - Type
  
      - ```
        Class:
        
        ParameterizedType:参数化类型,List<String>这种带泛型的类型 (代表整个泛型)
        		getRawType():获取原始数据类型，List<string>的原始类型为List
        		getActualTypeArguments():获取参数化类型的类型变量（实际类型列表），String
        		getOwnerType():类型所属类型,Map.Entry<String, String>的所属类型：Map
        		
        TypeVariable:类型变量：反映JVM在编译该泛型前的信息 (代表着泛型中的变量)
        		getBounds():类型变量的上边界，未明确，默认为Object,class Test<K extends Person>									中 K 的上界就是 Person
            getGenericDeclaration():获取该变量的原始类型，class Test<K extends Person>的原									始类型为Test
            getName():获取在源码中定义的名字, class Test<K extends Person>的名字为 K
            
        GenericArrayType:泛型数组(用来描述ParameterizedType、TypeVariable类型的数组；即        									List<T>[] 、T[]等；)
        		getGenericComponentType(): List<String>[] test的泛型数组类型为          																		java.util.List<java.lang.String>
        
        WildcardType:通配符泛型 形如：TestType<? super String>;
        							<? super T>表示包括T在内的任何T的父类，<? extends T>表示包括T在内的任何							T的子类;
        							TTest<T extends Type & Serializable>;
        		getLowerBounds():表达式下边界：[class java.lang.String]
        		getUpperBounds():表达式上边界：[class java.lang.Object]
        		
        番外篇：
        GenericDeclaration；
             含义为：声明类型变量的所有实体的公共接口；
                    也就是说该接口定义了哪些地方可以定义类型变量（泛型）；
             通过查看源码发现，GenericDeclaration下有三个子类，
                 分别为Class、Method、Constructor；
                 也就是说，我们定义泛型只能在一个类中这3个地方自定义泛型；		
        ```
  
  - ObjectFactory：封装对象创建
  
    - 指定Class，反射其构造器进行创建
  
  - PropertyTokenizer：属性分词，根据传入的字符串，进行分词，借助**迭代器**进行迭代解析
  
  - MetaClass：类包装器，整合Reflector和PropertyTokenizer的功能，提供指定属性的获取，解析，反射Invoker的获取，getter/setter的Invoker获取
  
  - ObjectWrapper：
  
    - BeanWrapper：包装了MetaClass和Object类型对象，提供对象的属性，getter/setter的设置和获取
    - MapWrapper：包装了MetaClass和Map<String, Object>类型对象
  
  - MetaObject：
  
    - 主要工作：创建 ObjectWrapper
  
    - ```java
      /**
       * 元对象 定义
       *
       * @author Clinton Begin
       */
      public class MetaObject {
      
        /**
         * 原始对象
         */
        private final Object originalObject;
      
        /**
         * 对象包装器,包装originalObject
         */
        private final ObjectWrapper objectWrapper;
      
        /**
         * 负责实例化originalObject的工厂
         */
        private final ObjectFactory objectFactory;
      
        /**
         * 负责构建ObjectWrapper的工厂
         */
        private final ObjectWrapperFactory objectWrapperFactory;
      
        /**
         * 负责构建Reflector的工厂
         */
        private final ReflectorFactory reflectorFactory;
        ...
      }
      ```
  
- TypeHandler类型转换

  - JdbcType：维护(缓存)了 java.sql.Types (JDBC) 中的常量 与JdbcType的关联关系
  - TypeHandler: 策略模式，类型转换器
    - 







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



