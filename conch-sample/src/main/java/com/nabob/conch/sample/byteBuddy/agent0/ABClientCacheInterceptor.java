package com.nabob.conch.sample.byteBuddy.agent0;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Adam
 * @since 2023/8/10
 */
public class ABClientCacheInterceptor {

    // 提示ByteBuddy根据被拦截方法的实际类型，对此拦截器的返回值进行Cast
    @RuntimeType
    public static Object sync(
                            // 所有入参的数组
                            @AllArguments Object[] allArguments,
                            // 被拦截的原始方法
                            @Origin Method method,

                            @SuperCall Callable<?> zuper) {

        System.out.println("拦截开始");
        try {
            Object call = zuper.call();
            System.out.println("拦截结束");
            return call;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
