## Spring源码分析（四）

依赖查找



- 单一类型依赖查找：BeanFactory

  ```
  1. 按照Bean的名称查找
  2. 按照Bean的类型查找
           实时
           延迟：
               ObjectFactory
               ObjectProvider （Spring 5.1）
  3. 按照Bean名称+类型查找
  ```

- 集合类型依赖查找





层次依赖查找

延迟依赖查找

安全依赖查找

内建可查找的依赖

依赖查找中的经典异常

