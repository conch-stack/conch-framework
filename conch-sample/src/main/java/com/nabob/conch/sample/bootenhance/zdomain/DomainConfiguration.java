package com.nabob.conch.sample.bootenhance.zdomain;

import com.nabob.conch.sample.bootenhance.dinitialization.InitializationBean;
import com.nabob.conch.sample.bootenhance.eaware.EnableTestImportAware;
import com.nabob.conch.sample.bootenhance.importselect.EnableImportSelectTest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author Adam
 * @since 2024/8/7
 */
@EnableTestImportAware
@EnableImportSelectTest
@Configuration
public class DomainConfiguration extends TestConfiguration implements ApplicationContextAware {

    @Bean
    public SuperUser superUser() {
        SuperUser superUser = new SuperUser();
        superUser.setName("小飞飞");
        superUser.setAge(18);
        superUser.setAddress("上海");
        return superUser;
    }

    @Bean(name = "test-InitializationBean", initMethod = "initMethod")
    public InitializationBean initializationBean() {
        return new InitializationBean();
    }

    @Bean(name = "test-InitializationBean_Lazy", initMethod = "initMethod")
    @Lazy
    public InitializationBean initializationBean1() {
        return new InitializationBean();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
        System.out.println(ctx.getBeanDefinitionCount());
    }
}
