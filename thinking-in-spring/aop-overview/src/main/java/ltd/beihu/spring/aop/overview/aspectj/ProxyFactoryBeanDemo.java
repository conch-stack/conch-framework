package ltd.beihu.spring.aop.overview.aspectj;

import ltd.beihu.spring.aop.overview.DefaultEchoService;
import ltd.beihu.spring.aop.overview.EchoService;
import ltd.beihu.spring.aop.overview.aspectj.interceptor.EchoServiceMethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * ProxyFactoryBean Demo
 *
 * @author Adam
 * @since 2023/3/15
 */
public class ProxyFactoryBeanDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class
        applicationContext.register(ProxyFactoryBeanDemo.class);
        applicationContext.refresh();

        // 获取代理对象
        EchoService echoServiceProxyFactoryBean = applicationContext.getBean("echoServiceProxyFactoryBean", EchoService.class);
        echoServiceProxyFactoryBean.echo("来了");

        applicationContext.close();
    }

    @Bean
    public EchoServiceMethodInterceptor echoServiceMethodInterceptor() {
        return new EchoServiceMethodInterceptor();
    }

    @Bean
    public EchoService echoService() {
        return new DefaultEchoService();
    }

    @Bean
    public ProxyFactoryBean echoServiceProxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTargetName("echoService");
        proxyFactoryBean.setInterceptorNames("echoServiceMethodInterceptor");
        return proxyFactoryBean;
    }

}
