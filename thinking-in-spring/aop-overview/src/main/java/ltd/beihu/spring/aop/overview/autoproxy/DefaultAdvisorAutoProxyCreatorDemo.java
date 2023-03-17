package ltd.beihu.spring.aop.overview.autoproxy;

import ltd.beihu.spring.aop.overview.DefaultEchoService;
import ltd.beihu.spring.aop.overview.EchoService;
import ltd.beihu.spring.aop.overview.aspectj.interceptor.EchoServiceMethodInterceptor;
import ltd.beihu.spring.aop.overview.pointcut.EchoServicePointcut;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * SpringBeanNameAutoProxy Demo
 * <p>
 * DefaultAdvisorAutoProxyCreator
 *
 * @author Adam
 * @since 2023/3/15
 */
public class DefaultAdvisorAutoProxyCreatorDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class
        applicationContext.register(DefaultAdvisorAutoProxyCreatorDemo.class);
        applicationContext.refresh();

        // 获取代理对象
        EchoService echoServiceProxyFactoryBean = applicationContext.getBean("echoService", EchoService.class);
        echoServiceProxyFactoryBean.echo("来了");

        applicationContext.close();
    }

    /**
     * AutoProxy
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * Target Bean
     */
    @Bean
    public EchoService echoService() {
        return new DefaultEchoService();
    }

    /**
     * Pointcut
     */
    @Bean
    public EchoServicePointcut echoServicePointcut() {
        return new EchoServicePointcut("echo", EchoService.class);
    }

    /**
     * Advice
     */
    @Bean
    public EchoServiceMethodInterceptor echoServiceMethodInterceptor() {
        return new EchoServiceMethodInterceptor();
    }

    /**
     * Advisor
     */
    @Bean
    public DefaultPointcutAdvisor advisor(EchoServicePointcut echoServicePointcut, EchoServiceMethodInterceptor echoServiceMethodInterceptor) {
        return new DefaultPointcutAdvisor(echoServicePointcut, echoServiceMethodInterceptor);
    }

}
