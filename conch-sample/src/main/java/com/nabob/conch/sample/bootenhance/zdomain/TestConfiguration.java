package com.nabob.conch.sample.bootenhance.zdomain;

import org.springframework.context.annotation.Bean;

/**
 * 无法生效
 *
 * @author Adam
 * @since 2024/8/13
 */
public class TestConfiguration {
    @Bean(name = "TestSuperUser")
    public SuperUser superUser() {
        SuperUser superUser = new SuperUser();
        superUser.setName("TestSuperUser");
        superUser.setAge(200);
        superUser.setAddress("韩国");
        return superUser;
    }
}
