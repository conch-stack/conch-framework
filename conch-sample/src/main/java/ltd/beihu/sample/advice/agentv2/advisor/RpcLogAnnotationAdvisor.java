package ltd.beihu.sample.advice.agentv2.advisor;

import ltd.beihu.sample.advice.agentv2.RpcLog;
import ltd.beihu.sample.advice.agentv2.advisor.advice.AnnotationRpcLogInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * RpcLog 注解 切片
 *
 * @author Adam
 * @since 2023/3/15
 */
public class RpcLogAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    /**
     * 切面
     */
    private Advice advice;

    /**
     * 切点
     */
    private Pointcut pointcut;

    /**
     * 可以转换成对应的ConfigBean，我这里就简单使用一下了
     */
    protected AnnotationAttributes enableRpcLogV2;

    public RpcLogAnnotationAdvisor(AnnotationAttributes enableRpcLogV2) {
        this.enableRpcLogV2 = enableRpcLogV2;

        Set<Class<? extends Annotation>> selfAnnotationTypes = new LinkedHashSet<>(1);
        selfAnnotationTypes.add(RpcLog.class);

        this.advice = buildAdvice();
        this.pointcut = buildPointcut(selfAnnotationTypes);
    }

    public void setSelfAnnotationType(Class<? extends Annotation> selfAnnotationType) {
        Assert.notNull(selfAnnotationType, "'asyncAnnotationType' must not be null");
        Set<Class<? extends Annotation>> selfAnnotationTypes = new LinkedHashSet<>(1);
        selfAnnotationTypes.add(selfAnnotationType);
        this.pointcut = buildPointcut(selfAnnotationTypes);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    protected Advice buildAdvice() {
        boolean recordCk = enableRpcLogV2.getBoolean("recordCk");
        AnnotationRpcLogInterceptor interceptor = new AnnotationRpcLogInterceptor(recordCk);
        return interceptor;
    }

    private Pointcut buildPointcut(Set<Class<? extends Annotation>> selfAnnotationTypes) {
        // ComposablePointcut 可利用这个类进行 类过滤；方法匹配；pointcut匹配
        ComposablePointcut result = null;
        // 注解匹配方式
        for (Class<? extends Annotation> selfAnnotationType : selfAnnotationTypes) {
            // 类匹配 - 注解了 selfAnnotationType 的类
            Pointcut cpc = new AnnotationMatchingPointcut(selfAnnotationType, true);
            // 方法匹配 - 注解了 selfAnnotationType 的方法
            Pointcut mpc = new AnnotationMatchingPointcut(null, selfAnnotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                result.union(cpc);
            }
            result = result.union(mpc);
        }
        return (result != null ? result : Pointcut.TRUE);
    }
}
