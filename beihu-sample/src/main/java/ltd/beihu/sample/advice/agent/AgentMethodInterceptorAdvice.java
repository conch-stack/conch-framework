package ltd.beihu.sample.advice.agent;

import ltd.beihu.sample.advice.BaseAspectSupport;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 统一实现agent包层面的日志切面
 */
public class AgentMethodInterceptorAdvice extends BaseAspectSupport implements MethodInterceptor {

    private final List<String> ObjectMethodName;
    private final Boolean recordCk;

    public AgentMethodInterceptorAdvice(Boolean recordCk) {
        ObjectMethodName = getObjectMethodName();
        this.recordCk = recordCk;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //如果方法是Object自带方法，不记录日志
        if (ObjectMethodName.contains(method.getName())) {
            return invocation.proceed();
        }

        return doLogAround(invocation);
    }

    /**
     * 实现aop的环绕切面
     */
    private Object doLogAround(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        Object response = null;
        try {
            System.out.println("AOP代理开始");
            response = invocation.proceed();
            System.out.println("AOP代理结束");
        } finally {
        }
        return response;
    }


    /**
     * 获取Object类的所有方法
     */
    private List<String> getObjectMethodName() {
        Method[] declaredMethods = Object.class.getDeclaredMethods();
        return Stream.of(declaredMethods).map(Method::getName).collect(Collectors.toList());
    }

}
