package ltd.beihu.sample.advice.agentv2.advisor;

import ltd.beihu.sample.advice.agentv2.advisor.advice.AnnotationRpcLogInterceptor;
import ltd.beihu.sample.advice.agentv2.advisor.pointcut.PackageMatchingPointcut;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationAttributes;

/**
 * Agent 包模块 切片
 *
 * @author Adam
 * @since 2023/3/15
 */
public class AgentPackageAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

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

    public AgentPackageAdvisor(AnnotationAttributes enableRpcLogV2) {
        this.enableRpcLogV2 = enableRpcLogV2;

        this.advice = buildAdvice();
        this.pointcut = buildPointcut();
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

    private Pointcut buildPointcut() {
        String agentPackage = this.enableRpcLogV2.getString("agentPackage");
        // 可直接用类 filter

        // ComposablePointcut 可利用这个类进行 类过滤；方法匹配；pointcut匹配
        // 类匹配 - 注解了 selfAnnotationType 的类
        Pointcut cpc = new PackageMatchingPointcut(agentPackage);
        ComposablePointcut result = new ComposablePointcut(cpc);
        return result;
    }
}
