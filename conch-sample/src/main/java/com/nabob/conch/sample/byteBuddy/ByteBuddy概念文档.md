## ByteBuddy

```java
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .implement(Serializable.class)
                .name("HelloWorld")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(Test.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println(dynamicType.newInstance().toString());
    }
```

ByteBuddy: 入口，提供 fluent API 对任何增强逻辑进行配置
- Subclassing some type：为目标类（即被增强的类）生成一个子类，在子类方法中插入动态代码
- Redefining a type：当重定义一个类时，Byte Buddy 可以对一个已有的类添加属性和方法，或者删除已经存在的方法实现
- Rebasing a type：保留目标类中的方法


ClassLoadingStrategy：类加载器策略
- Default.WRAPPER：创建一个新的 ClassLoader 来加载动态生成的类型。
- Default.CHILD_FIRST：创建一个子类优先加载的 ClassLoader，即打破了双亲委派模型。
- Default.INJECTION：使用反射将动态生成的类型直接注入到当前 ClassLoader 中。

matcher:
- ElementMatcher：ElementMatcher<T>，使用ElementMatchers静态帮助类
  - 很多实现类，例如：NameMatcher；StringMatcher
  - 如果有重载方法，可以添加更多方法描述:
  ```java
    ElementMatchers.named("a")
        .and(ElementMatchers.returns(String.class))
        .and(ElementMatchers.takesArguments(1));
  ```

method:
- MethodDescription
- ParameterDescription

field:
- FieldDescription

intercept:
- Implementation：对前面定义或者match的方法进行增强实现

MethodDelegation





