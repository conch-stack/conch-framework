package com.nabob.conch.sample.byteBuddy.test1;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class LogInterceptor {

    // 将返回值转换成具体的，方法返回值类型，加了这个注解intercept方法才会被执行 ：不进行严格的参数类型检测，在参数匹配失败时，尝试使用类型转换方式（runtime type casting）进行类型转换，匹配相应方法。
    @RuntimeType
    public Object intercept(
            // 注入被拦截的目标对象（动态生成的目标对象）
            @This
            Object target,
            // 注入正在执行的方法Method对象（目标对象父类Method) ：注入正在执行的方法Method 对象（目标对象父类的Method）。如果拦截的是字段的话，该注解应该标注到 Field 类型参数。
            @Origin
            Method method,
            // 注入正在执行的方法的全部參敬
            @AllArguments
            Object[] argumengts,
            // 注入目标对象的一个代理
            @Super
            Object delegate,
            // 方法的调用者对象 对原始方法的调用依靠它
            // 这个注解比较特殊，我们要在 intercept() 方法中调用 被代理/增强 的方法的话，需要通过这种方式注入，与 Spring AOP 中的 ProceedingJoinPoint.proceed()  方法有点类似，
            // 需要注意的是，这里不能修改调用参数，从上面的示例的调用也能看出来，参数不用单独传递，都包含在其中了。另外，@SuperCall 注解还可以修饰 Runnable 类型的参数，只不过目标方法的返回值就拿不到了。
            @SuperCall
            Callable<?> callable) throws Exception {

        // 目标方法执行前执行日志记录
        System.out.println("准备执行Method-" + method.getName());
        // 调用目标方法
        Object result = callable.call();
        // 目标方法执行后执行日志记录
        System.out.println("方法执行完成Method-" + method.getName());
        return result;
    }
}