package ltd.beihu.spring.aop.overview.aspectj;

import ltd.beihu.spring.aop.overview.DefaultEchoService;
import ltd.beihu.spring.aop.overview.EchoService;
import ltd.beihu.spring.aop.overview.aspectj.interceptor.EchoServiceMethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @author Adam
 * @since 2023/3/15
 */
public class ProxyFactoryDemo {

    public static void main(String[] args) {
        DefaultEchoService defaultEchoService = new DefaultEchoService();
        ProxyFactory proxyFactory = new ProxyFactory(defaultEchoService);
        proxyFactory.setTargetClass(DefaultEchoService.class);
        proxyFactory.addAdvice(new EchoServiceMethodInterceptor());

        EchoService echoService = (EchoService) proxyFactory.getProxy();
//        echoService.echo("来了");
        echoService.echo();
    }

}
