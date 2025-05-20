package com.nabob.conch.sample.enhanceconsumer;

import com.nabob.conch.sample.uitl.JsonUtil;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 事件监听器工厂
public class MultiEventListenerFactory {
    
    public static List<MessageListener> createListeners(Object target) {
        List<Method> consumerMethods = findConsumerMethods(target.getClass());
        return consumerMethods.stream()
            .map(method -> createProxy(target, method))
            .collect(Collectors.toList());
    }

    // 扫描所有被注解的方法
    private static List<Method> findConsumerMethods(Class<?> targetClass) {
        return Arrays.stream(targetClass.getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(EventConsumer.class))
            .peek(m -> {
                if (m.getParameterCount() != 1) {
                    throw new IllegalArgumentException(
                        "Method " + m.getName() + " 必须包含一个Message类型参数");
                }
                m.setAccessible(true);
            })
            .collect(Collectors.toList());
    }

    // 创建单个方法的代理
    private static MessageListener createProxy(Object target, Method targetMethod) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{MessageListener.class});
        
//        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
//            if (method.getName().equals("onMessage")) {
//                Class<?>[] parameterTypes = targetMethod.getParameterTypes();
//
//                EventMessage eventMessage = (EventMessage) args[0];
//                String body = eventMessage.getBody();
//
//                // other class
//                Object o = JsonUtil.json2Object(body, parameterTypes[0]);
//                return targetMethod.invoke(target, o); // 将Message传递给目标方法
//            }
//            throw new UnsupportedOperationException("未实现的方法: " + method.getName());
//        });

        enhancer.setCallback(new MessageListenerMethodInterceptor(target, targetMethod));
        return (MessageListener) enhancer.create();
    }

    private static class MessageListenerMethodInterceptor implements MethodInterceptor, EventStateTrace {

        private final Object target;
        private final Method targetMethod;

        public MessageListenerMethodInterceptor(Object target, Method targetMethod) {
            this.target = target;
            this.targetMethod = targetMethod;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getName().equals("onMessage")) {
                Class<?>[] parameterTypes = targetMethod.getParameterTypes();

                EventMessage eventMessage = (EventMessage) args[0];
                String body = eventMessage.getBody();

                // other class
                Object o = JsonUtil.json2Object(body, parameterTypes[0]);
                state(eventMessage, EventState.IN_PROGRESS);
                System.out.println(JsonUtil.object2Json(eventMessage.getTags()));
                Object rs = targetMethod.invoke(target, o); // 将Message传递给目标方法
                state(eventMessage, EventState.SUCCESS);
                System.out.println(JsonUtil.object2Json(eventMessage.getTags()));
                return rs;
            }
            return methodProxy.invokeSuper(obj, args);
        }
    }

}