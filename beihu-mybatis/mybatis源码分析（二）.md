## MyBatis源码分析（二）

```
Spring Boot MyBatis运行路径：

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



