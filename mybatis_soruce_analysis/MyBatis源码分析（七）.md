## MyBatis源码分析（七）

binding模块：将Mapper接口与映射配置文件关联，并自动为接口生成动态代理对象



- MapperRegistry：Mapper接口的注册器
  - MapperProxyFactory：为每个Mapper接口创建一个工厂用于创建MapperProxy
    - MapperProxy：Mapper接口的动态代理对象
      - MethodInvoker：Mapper接口中的方法的自定义代理实现
        - MapperMethod：包装对应的SqlCommand (todo) 以及方法签名**（重点）**
        - MethodHandle：？？？



