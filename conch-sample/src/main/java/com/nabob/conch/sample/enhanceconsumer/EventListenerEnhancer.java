package com.nabob.conch.sample.enhanceconsumer;

import com.nabob.conch.sample.uitl.JsonUtil;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

// 2. CGLIB增强逻辑
public class EventListenerEnhancer {

    public static MessageListener createListener(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setInterfaces(new Class[]{MessageListener.class});
        
        enhancer.setCallback(new MethodInterceptor() {
            // 缓存被注解的方法
            private Method findAnnotatedMethod() {
                for (Method method : target.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(EventConsumer.class)) {
                        method.setAccessible(true);
                        return method;
                    }
                }
                throw new IllegalStateException("No @EventConsumer method found");
            }

            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                // 处理MessageListener接口方法
                if (method.getName().equals("onMessage")) {
                    Method targetMethod = findAnnotatedMethod();

                    Class<?>[] parameterTypes = targetMethod.getParameterTypes();

                    EventMessage eventMessage = (EventMessage) args[0];
                    String body = eventMessage.getBody();

                    // other class
                    Object o = JsonUtil.json2Object(body, parameterTypes[0]);
                    return targetMethod.invoke(target, o); // 将Message传递给目标方法
                }
                return proxy.invokeSuper(obj, args); // 其他方法保持原逻辑
            }
        });

        return (MessageListener) enhancer.create();
    }
}