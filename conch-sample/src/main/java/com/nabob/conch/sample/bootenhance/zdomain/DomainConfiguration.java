package com.nabob.conch.sample.bootenhance.zdomain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Adam
 * @since 2024/8/7
 */
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
}
