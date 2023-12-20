package com.nabob.conch.sample.dynamic;

import com.nabob.conch.sample.User;
import org.springframework.context.annotation.Bean;

/**
 * @author Adam
 * @since 2023/7/20
 */
public class DynamicConfig {

    @Bean
    public User dynamicUser1() {
        return new User("Dynamic User1", 10);
    }

    @Bean
    public User dynamicUser2() {
        return new User("Dynamic User2", 20);
    }
}
