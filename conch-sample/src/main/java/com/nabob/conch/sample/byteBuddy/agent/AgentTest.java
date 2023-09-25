package com.nabob.conch.sample.byteBuddy.agent;

import com.nabob.conch.sample.byteBuddy.TestClass2;
import com.nabob.conch.sample.byteBuddy.TestClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;

/**
 * 虽然我们可以动态创建类，我们也可以操作已经加载的类。
 * ByteBuddy可以重定义已经存在的类，然后使用ByteBuddyAgent将重定义的类重新加载到JVM中
 * <p>
 * 得益于JVM的HostSwap特性，已加载的类可以被重新定义
 * 当前HostSwap具有限制：
 * - 类再重新载入前后，必须具有相同的Schema，也就是方法、字段不能减少（可以增加）
 * - 不支持具有静态初始化块的类
 * <p>
 * 修改类:
 * - redefine: 重定义一个类时，Byte Buddy 可以对一个已有的类添加属性和方法，或者删除已经存在的方法实现。新添加的方法，如果签名和原有方法一致，则原有方法会消失。
 * - rebase: 类似于redefine，但是原有的方法不会消失，而是被重命名，添加后缀 $original
 * <p>
 * <pre>
 * 通过 MethodDelegation 去完成
 * - 在intercept方法中，使用MethodDelegation.to委托到静态方法
 *      intercept(MethodDelegation.to(DelegateClazz.class)) // 委托到 DelegateClazz 的静态方法
 * - 在intercept方法中，使用MethodDelegation.to委托到成员方法
 *      intercept(MethodDelegation.to(new DelegateClazz()) // 委托到 DelegateClazz 的实例方法
 * </pre>
 * <p>
 * 加载策略
 * ClassLoadingStrategy.Default.BOOTSTRAP_LOADER
 * <p>
 * https://blog.gmem.cc/byte-buddy-study-note
 * <p>
 * JVM启动时加载 java agent：https://blog.csdn.net/qq_17589253/article/details/118364827
 * JVM运行时加载？
 *
 * @author Adam
 * @since 2023/8/10
 */
public class AgentTest {

    public static void main(String[] args) throws InterruptedException {
//        simpleTest();

//        ABClientCache testClass = ABClientCache.getInstance();
//        Thread.sleep(10000000000L);

//        moreDelegationTest();
    }

    public static void moreDelegationTest() throws InterruptedException {
        // 安装Byte Buddy的Agent，除了通过-javaagent静态安装，还可以
        ByteBuddyAgent.install();

        DynamicType.Unloaded<ABClientCache> sync = new ByteBuddy()
                .redefine(ABClientCache.class)
                .method(ElementMatchers.named("sync"))
                // 委托到静态方法
                .intercept(MethodDelegation.to(ABClientCacheDelegation.class))
                .make();
        sync.load(TestClass.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        // 保存到文件
        try {
            sync.saveIn(new File("D:\\data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ABClientCache testClass = ABClientCache.getInstance();

        Thread.sleep(10000000000L);
    }

    public static void simpleTest() {
        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(TestClass.class)
                .method(ElementMatchers.named("getOid1"))
                .intercept(FixedValue.value("Dynamic Type Intercepted"))
                .make()
                .load(TestClass.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        TestClass testClass = new TestClass();
        System.out.println(testClass.getOid1());
    }

}
