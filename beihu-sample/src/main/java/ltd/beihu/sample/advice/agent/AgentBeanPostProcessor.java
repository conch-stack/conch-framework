package ltd.beihu.sample.advice.agent;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 通过自定义BeanPostProcessor，实现项目中agent模块统一切面
 */
public class AgentBeanPostProcessor implements BeanPostProcessor {
    /**
     * agent模块包名
     */
    private String agentPackage;
    /**
     * 是否记录ck
     */
    private Boolean recordCk;

    /**
     * 设置包名
     */
    public void setAgentPackage(String agentPackage) {
        this.agentPackage = agentPackage;
    }

    public void setRecordCk(Boolean recordCk) {
        this.recordCk = recordCk;
    }

    /**
     * 实例化、依赖注入完毕，在调用显示的初始化之前完成一些定制的初始化任务
     * 注意：方法返回值不能为null
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 实例化、依赖注入、初始化完毕时执行
     * 注意：方法返回值不能为null
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!bean.getClass().getName().contains(agentPackage)) {
            return bean;
        }
        if (bean.getClass().isInterface()) {
            return bean;
        }
        //通过FactoryBean和实现Advice的方式，统一为包切面
        ProxyFactoryBean pfb = new ProxyFactoryBean();
        pfb.setTarget(bean);
        pfb.setAutodetectInterfaces(false);

        pfb.addAdvice(new AgentMethodInterceptorAdvice(recordCk));
        return pfb.getObject();
    }
}