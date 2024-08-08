package com.nabob.conch.sample.bootenhance.zdomain;

import com.nabob.conch.sample.bootenhance.dinitialization.InitializationBean;
import com.nabob.conch.sample.bootenhance.eaware.EnableTestImportAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author Adam
 * @since 2024/8/7
 */
@EnableTestImportAware
@Configuration
public class DomainConfiguration {

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
}
