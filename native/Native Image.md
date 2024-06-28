## Native Image

### Native Image 是什么？

Native Image 是一种提前将 Java 代码编译为本地可执行文件的技术。其输出的本地可执行文件仅会包含运行时所需要用到的部分，包括应用自身的类，标准库中的类，Java 语言运行时以及 JDK 中静态链接了的本地代码，还包括运行时会用到的一些资源文件。

这种本地可执行文件相较于传统的 Java 程序来说有以下几点优势：

1. JVM 自身占用资源小
2. 启动速度快
3. 性能达峰快，无需预热
4. 便于部署
5. 安全攻击难度较大

#### 封闭世界假定（Closed-World Assumption）

这个封闭世界假定指的是一个应用在运行时会用到的所有字节码，都必须在构建时明确下来。所以在运行时，我们是没有办法动态添加或者修改任何代码的。所有相关功能都需要提前到构建时进行，或者重新进行逻辑设计。

### 类初始化阶段

在一般的 Java 程序中，各个类的初始化（比如静态字段的初始化、静态代码块的执行等）都是在**运行时**进行的。为了提升性能，缩短应用启动速度，Native Image 可能会在构建过程中对一些类进行初始化，也就是**构建时**初始化。

通常，大部分 JDK 内置的类，比如一些基础的类，还有 GC 组件等，都是在**构建时**进行初始化的。这一初始化操作是在构建时启动的 JVM 中进行的。初始化后类的内部状态都会被记录输出的可执行文件中，只需要在运行时将这些状态恢复到内存中即可。

构建时初始化是一个比较进阶的功能。Native Image 构建工具会通过扫描字节码来判断一个类是否可以在构建时进行初始化，简单的说就是判断它的初始化操作是否会输出稳定的结果，是否会依赖或者影响到 JVM 以外的运行环境。

我们也可以在构建 Native Image 时传入以下参数来强制指定对应类的初始化阶段：

- --initialize-at-build-time=<a comma-separated list of packages and classes>
- --initialize-at-run-time=<a comma-separated list of packages and classes>

### 可达性元数据

Java 本身提供了一些动态语言特性，使得我们可以在运行时再决定要调用哪个方法或访问哪个资源（比如通过反射）。虽然 Native Image 构建工具可以通过静态字节码分析的方式来进行判断，但这种判断可能会出现错漏，而这些错漏就会导致相应的代码无法正常运行。

所以 Native Image 支持用户单独向其提供可达性元数据信息。目前提供可达性信息的方式共有两种：

1. 通过代码
2. 通过 JSON 配置文件

通过代码这种方式应该相对比较少会用到，所以这里将不会展开介绍。感兴趣的同学可以参阅官方文档。

下面重点介绍通过 JSON 配置文件来提供可达性数据这一方式。

### 通过 JSON 配置文件配置可达性元数据

这些配置文件应放置在项目资源目录中的 META-INF/native-image/<group.id>/<artifact.id> 子目录内。构建工具会自动进行读取。

可达性元数据共分为 6 种，分别也对应了不同的配置文件名：

1. 反射：reflect-config.json
2. 资源：resource-config.json
3. JNI：jni-config.json
4. 动态代理：proxy-config.json
5. 序列化：serialization-config.json
6. 预生成类：predefined-classes-config.json

这里比较常用的是 1 和 2。它们也是下文重点要介绍的。关于其它的元数据类型，建议大家查阅官方文档。

注意：虽然我们在代码中经常会用到序列化功能，但这里的序列化配置针对的是使用 Java 内置的对象序列化器序列化一个实现了 Serializable 接口的对象的操作。我们通常使用的 JSON、XML 序列化操作实际依赖的是反射配置。

##### 反射元数据配置：reflect-config.json

所有通过 Class.fromName 获取 Class 对象以及基于 Class 对象通过反射获取、访问和调用类内定义的方法、字段的操作都需要在 reflect-config.json 中进行配置，只有这样才能保证反射操作能够成功执行。

reflect-config.json 内部包含一个 JSON 数组。数组中的每个元素对应一个类（不支持使用通配符进行批量配置）所允许对齐执行的反射操作。

```json
[
    {
        "condition": {
            "typeReachable": "<condition-class>"
        },
        "name": "<class>",
        "methods": [
            {"name": "<methodName>", "parameterTypes": ["<param-one-type>"]}
        ],
        "queriedMethods": [
            {"name": "<methodName>", "parameterTypes": ["<param-one-type>"]}
        ],
        "fields": [
            {"name": "<fieldName>"}
        ],
        "allDeclaredClasses": true,
        "allDeclaredMethods": true,
        "allDeclaredFields": true,
        "allDeclaredConstructors": true,
        "allPublicClasses": true,
        "allPublicMethods": true,
        "allPublicFields": true,
        "allPublicConstructors": true,
        "allRecordComponents": true,
        "allNestMembers": true,
        "allSigners": true,
        "allPermittedSubclasses": true,
        "queryAllDeclaredMethods": true,
        "queryAllDeclaredConstructors": true,
        "queryAllPublicMethods": true,
        "queryAllPublicConstructors": true,
        "unsafeAllocated": true
    }
]
```

##### 资源元数据配置：resource-config.json

所有需要在代码中通过 Class.getResource、Class.getResourceAsStream 等方法获取的内嵌资源都必需在 resource-config.json 进行配置，否则它们将不会被写入可执行文件中，运行时也就将无法读取。

需要注意的是，resource-config.json 使用的是正则表达式进行配置，所以可以比较方便的进行批量配置。

resource-config.json 的具体格式如下：

```json
{
  "resources": {
    "includes": [
      {
        "condition": {
          "typeReachable": "<condition-class>"
        },
        "pattern": ".*\\.txt"
      }
    ],
    "excludes": [
      {
        "condition": {
          "typeReachable": "<condition-class>"
        },
        "pattern": ".*\\.txt"
      }
    ]
  },
  "bundles": [
    {
      "condition": {
        "typeReachable": "<condition-class>"
      },
      "name": "fully.qualified.bundle.name",
      "locales": ["en", "de", "sk"]
    },
    {
      "condition": {
        "typeReachable": "<condition-class>"
      },
      "name": "fully.qualified.bundle.name",
      "classNames": [
        "fully.qualified.bundle.name_en",
        "fully.qualified.bundle.name_de"
      ]
    }
  ]
}
```

### 使用 Java Agent 自动生成元数据配置文件

对于一个大型应用，人工编写以上元数据配置文件的工作量显然是很大的，而且容易出现遗漏。所以 GraalVM 提供了一个 Tracing Agent。只要在启动 Java 进程时加上相关的参数，这个 Agent 就会基于进程在运行过程中执行的反射操作、访问的资源来生成对应的元数据配置文件。

`$JAVA_HOME``/bin/java` `-agentlib:native-image-agent=config-output-``dir``=``/path/to/config-dir/`

为了确保生成配置的完整性，我们在整个进程的运行过程中需要尽可能多覆盖应用的业务场景和逻辑分支，避免由于覆盖问题导致运行时相应功能无法正常执行。



### Spring 的 Native Image 支持

Spring 自身就大量依赖运行时的 Java 动态机制，而在 Native Image 的运行环境下，这些机制很多都无法正常运行了。所以 Spring 通过 AOT 技术，使用 Maven 编译插件在构建时进行了一系列的配置扫描和代码配置生成工作，在不违反“封闭世界假定”的前提下实现了为 Native Image 的构建提供了支持。

```pom

<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>process-aot</id>
      <goals>
        <goal>process-aot</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

但“封闭世界假定”也导致运行时要加载哪些 Spring Bean 必须要在构建时确定下来。这也就使得以下两个功能的工作方式与常规 Java 应用有一些区别：

1. @Profile 注解的工作有一些限制：需要在执行 Spring AOT 时传入对应的 Profile 名称
2. 通过配置来控制 Bean 是否加载的机制不再有效（如 @ConditionalOnProperty）



### 参考文档

- GraalVM Native Image Reference Manual：https://www.graalvm.org/latest/reference-manual/native-image/
- Spring Boot GraalVM Native Image Support：https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html