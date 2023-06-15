package ltd.beihu.core.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代理创建器
 *
 * @author Adam
 * @since 2021/6/29
 */
public class ProxyCreator {

    public Object createProxy(Class<?> targetClass, ProxyAdvisor proxyAdvisor) {
        return Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                return proxyAdvisor.doProxy(o, targetClass, method, objects, methodProxy);
            }
        });
    }

}
