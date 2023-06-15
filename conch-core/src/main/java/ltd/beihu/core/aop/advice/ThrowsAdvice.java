package ltd.beihu.core.aop.advice;

import java.lang.reflect.Method;

/**
 * 异常通知接口
 */
public interface ThrowsAdvice extends Advice {
    /**
     * 异常方法
     */
    void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e);
}
