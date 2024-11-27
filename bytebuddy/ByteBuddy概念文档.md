## ByteBuddy

### Simple

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

### Agent
```java
public class ByteBuddyAgentDemo {

    private static final Logger log = LoggerFactory.getLogger(ByteBuddyAgentDemo.class);

    private final static String scanPackage = "com.nabob.conch.sample";

    private final static String targetMethod = "sync";

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(">>>>> ByteBuddyAgentDemo - premain()");
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));
        new AgentBuilder.Default(byteBuddy)
                .type(nameStartsWith(scanPackage))
                .transform(new Transformer()) // update the byte code
                .with(new Listener())
                .installOn(inst);
    }

    /**
     *
     */
    private static class Transformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            if (typeDescription.getPackage().getActualName().equals(scanPackage)
//                    && typeDescription.getInterfaces().size() > 0
//                    && typeDescription.getInterfaces().get(0).getActualName().equals(implInterface)
            ) {
                String targetClassName = typeDescription.getSimpleName();
                System.out.println("----------------------- target class:" + targetClassName);

                // 委托
                return builder.method(named(targetMethod)
                        .and(isPublic())).intercept(MethodDelegation.to(MethodCostTime.class));

            }
            return builder;
        }
    }

    /**
     * Listener
     */
    private static class Listener implements AgentBuilder.Listener {

        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onDiscovery ---" + typeName);
            }
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            if (typeDescription.getSimpleName().startsWith(scanPackage)) {
                System.out.println("--- onTransformation ---" + typeDescription.getSimpleName());
            }
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onError ---" + throwable);
            }
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onComplete ---" + typeName);
            }
        }
    }

}
```

type: ElementMatcher  匹配
transform: AgentBuilder.Transformer  再次前面匹配的基础上，可以再次进行匹配 + 增强实现
with: AgentBuilder.Listener  监听


