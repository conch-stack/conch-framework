package ltd.beihu.core.aop.advice;

import java.lang.reflect.Method;

/**
 * 前置通知接口
 */
public interface MethodBeforeAdvice extends Advice {
    /**
     * 前置方法
     */
    void before(Class<?> clz, Method method, Object[] args) throws Throwable;
}
