package ltd.beihu.sample.advice;

import lombok.extern.slf4j.Slf4j;
import ltd.beihu.sample.uitl.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author kunjiang
 */
@Slf4j
public abstract class BaseAspectSupport {

    /**
     * spel表达式解析器
     */
    private ExpressionParser parser = new SpelExpressionParser();

    /**
     * 参数名发现器
     */
    private DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    /**
     * 获取切面方法
     *
     * @param point
     * @return
     */
    protected Method getMethod(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();

        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    /**
     * 获取本类中方法
     *
     * @param clazz
     * @param name
     * @param parameterTypes
     * @return
     */
    protected Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }

    /**
     * 获取方法的注解,如果不存在再从类上面获取注解
     *
     * @param method
     * @param annotationClass
     * @param <T>
     * @return
     */
    protected <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        try {
            T annotation = method.getAnnotation(annotationClass);
            if (Objects.nonNull(annotation)) {
                return annotation;
            }
            return method.getDeclaringClass().getDeclaredAnnotation(annotationClass);
        } catch (Exception e) {
            log.warn("getAnnotation is error", e);
        }
        return null;
    }

    /**
     * 安全的获取请求字符串
     *
     * @return
     */
    protected String getSafeRequestStr(Object[] args) {
        String requestStr = "";
        try {
            requestStr = JsonUtil.object2Json(args);
        } catch (Exception ignored) {
            log.warn("getSafeRequestStr is error", ignored);
        }
        return requestStr;
    }

    /**
     * 解析 spel 表达式
     */
    protected String parseSpelExpression(JoinPoint point, String spel) {
        if (StringUtils.isBlank(spel)) {
            return "";
        }
        Method method = getMethod(point);
        Object[] arguments = point.getArgs();
        return parseSpelExpression(method, arguments, spel);
    }

    /**
     * 解析 spel 表达式
     */
    protected String parseSpelExpression(Method method, Object[] arguments, String spel) {
        return parseSpelExpression(method, arguments, spel, String.class, "");
    }

    /**
     * 解析 spel 表达式
     */
    protected <T> T parseSpelExpression(Method method, Object[] arguments, String spel, Class<T> clazz, T defaultResult) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0, j = params.length; len < j; len++) {
            context.setVariable(params[len], arguments[len]);
        }
        try {
            Expression expression = parser.parseExpression(spel);
            return expression.getValue(context, clazz);
        } catch (Exception e) {
            return defaultResult;
        }
    }

}
