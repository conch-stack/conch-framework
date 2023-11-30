## **准备**

Ztrian Common Framework 引入

添加最新版本~

## **Maven依赖添加**

Ztrain的项目多为多模块项目，需要跟POM中添加Maven依赖。

```
<!--- 定义 jacoco 版本 -->
<jacoco.version>0.8.5</jacoco.version>
<sonar.coverage.exclustions>  // 根据具体项目进行配置，注意这里的配置，尽量和 GitLab  Auto DevOps 的 Pipeline 中的 Test 模块的 Jacoco 的 Exclude 一致 （参考下面具体描述）
    **/adapter/**,
    **/agent/**,
    **/utils/**,
    **/models/**,
    **/dto/**,
    **/deprecated/**,
    **/repository/**,
    **/enums/**,
    **/custom/**,
    **/constant/**,
    **/service/**,
    **/infrastructure/**,
    **/transaction/**,
    **/holder/**,
</sonar.coverage.exclustions>

<!--- 定义 jacoco 依赖  注意不是在dependencyManagement， dependencyManagement中只是声明-->
<dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>org.jacoco.agent</artifactId>
    <version>${jacoco.version}</version>
    <classifier>runtime</classifier>
</dependency>
<dependency>
    <groupId>xxx</groupId>
    <artifactId>common-framework-zut</artifactId>
    <scope>test</scope>
</dependency>

<build>
    <plugins>
        <!-- Mandatory plugins for using Spock -->
        <plugin>
            <groupId>org.codehaus.gmavenplus</groupId>
            <artifactId>gmavenplus-plugin</artifactId>
            <version>1.13.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compileTests</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <!-- Optional plugins for using Spock -->
        <!--- 定义 jacoco 执行 offline 模式 goals -->
        <!-- 注意不是在pluginManagement， pluginManagement中只是声明 -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <execution>
                    <id>default-instrument</id>
                    <goals>
                        <goal>instrument</goal>
                    </goals>
                </execution>
                <execution>
                    <id>default-restore-instrumented-classes</id>
                    <goals>
                        <goal>restore-instrumented-classes</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.12.2</version>
            <!--离线模式必需指定， 否则到模块根目录而不是target目录了-->
            <configuration>
                <systemPropertyVariables>
                    <jacoco-agent.destfile>target/jacoco.exec</jacoco-agent.destfile>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## **注意点**

**问题描述：****由于PowerMock 和 Jacoco 都是基于 【字节码】 增强，混用的使用会出现异常情况，所以，我们配置的 Jacoco 运行在 【离线模式】，但是这样会带来一个问题 “ Offline模式单元测试不能跨模块，即不能源码在A模块单测写在B模块 ”**

- 代理模式：jaoco 代理模式的原理是编译的 class 加探针，PowerMock 的方式也是加探针，有可能会冲突

- [重要] 默认情况下您不需要配置任何东西就可以使用单元测试覆盖率，但是由于 JaCoCo 和一些mock有冲突, 比如和 [**PowerMock** 冲突](https://github.com/powermock/powermock/wiki/Code-coverage-with-JaCoCo)，当您使用 PowerMock(或其他 Mock 工具) 时，请使用 JaCoCo 的 Offline 模式( Offline模式单元测试不能跨模块不能源码在A模块单测写在B模块 ), 修改 pom.xml 添加如下内容:

  ```
  去除Settings> CI/CD > AutDevops > Package  中默认 Maven 参数的 `org.jacoco:jacoco-maven-plugin:prepare-agent`
  ```

注：此处的 Jacoco Exculdes 需按每个项目不同进行配置，一旦配置， UT Coverage 将会排除对应代码的 检测！ 另，此处的配置尽量与 SonarQube一致（sonar.coverage.exclustions）



### **待测类的Spring属性处理方式**

```groovy
import QmqUtil
import ZutSpock
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor

/**
 * @author Adam
 * @since 2023/2/22
 */
@PrepareForTest([TargetService, QmqUtil])
@SuppressStaticInitializationFor(["com.xx.qmq.QmqUtil"])
class TargetService1Test extends ZutSpock {

    def service = new TargetService()

    def targetComponent = Mock(TargetComponent)

    void setup() {
        // 方式一：Spock方式
        service.targetComponent = targetComponent

        // 方式二：PowerMock方式
        PowerMockito.field(TargetService.class, "targetComponent").set(service, targetComponent)


        // 项目特有静态工具类Mock if necessary
        PowerMockito.mockStatic(QmqUtil.class)
    }

    def "Test BizMethod"() {
        given:
        targetComponent.bizMethod() >> targetComponentResponse

        expect:
        service.bizMethod() == responseT

        where:
        targetComponentResponse || responseT
        "rs1" || "Service biz method called rs1"
        "rs2" || "Service biz method called rs2"
    }
}
```



### **私有方法单独测试方式**

```groovy
// 方式一：
@Unroll
def "测试私有方法 + void方法"() {
    given: "反射获取私有方法"
    Method method = service.getClass().getDeclaredMethod("targetPrivateMethod", ParamClass.class)
    method.setAccessible(true)

    and: "mock grab order"
    def param = Mock(ParamClass)

    when: "反射调用私有方法"
    method.invoke(service, param)

    then: "判断Void方法内部是否调用"
    1 * param.method1Call()
    2 * param.method2Call()
}

// 方式二： 
@Unroll
def "测试私有方法 + 非void方法"() {
    given: "反射获取私有方法"
    def method = PowerMockito.method(TargetService.class, "targetPrivateMethod", BigDecimal.class)
    PowerMockito.when(QConfigUtil.getTList(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(listData)

    expect: "反射调用私有方法"
    method.invoke(service, ticketPrice) == response

    where:
    ticketPrice        | listData                                                                                                               || response
    BigDecimal.ZERO    | null                                                                                                                   || null
    BigDecimal.ZERO    | []                                                                                                                     || null
    new BigDecimal(20) | [new DTO(lowPrice: new BigDecimal(10), highPrice: new BigDecimal(100), productId: "testProductId-1")] || "testProductId-1"
    BigDecimal.ZERO    | [new DTO(lowPrice: new BigDecimal(10), highPrice: new BigDecimal(100), productId: "testProductId-1")] || null
}
```

### **私有方法mock支持动态参数方式**

```groovy
// smartType 可在 Spock 的 where 语句内进行动态引入
MemberModifier.stub(MemberMatcher.method(TargetService.class, "targetPrivateMethod", BigDecimal.class)).toReturn(smartType)

// void 返回的处理方式
MemberModifier.stub(MemberMatcher.method(TargetService.class, "targetPrivateMethod", BigDecimal.class)).toReturn(void)
```

## **Groovy语法（[W3C](https://www.w3cschool.cn/groovy/groovy_overview.html)）**

- [语法基础1](https://juejin.cn/post/6844903918565064718)
- [语法基础2](https://www.jianshu.com/p/1e95d03060f7)
- [匹配语法糖](https://cloud.tencent.com/developer/article/1799645)

## **Spock使用指南（[Spock1.3官方](https://spockframework.org/spock/docs/1.3/index.html)）**

- [Spock译本](https://www.yuque.com/lugew/spock)
- [美团](https://tech.meituan.com/2021/08/06/spock-practice-in-meituan.html)

## **PowerMock**

- [常见用法](https://www.jianshu.com/p/394e590b00b8)

## **其他**

- [BDD](https://bbs.huaweicloud.com/blogs/detail/198120)