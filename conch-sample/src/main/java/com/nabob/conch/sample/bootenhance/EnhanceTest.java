package com.nabob.conch.sample.bootenhance;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.bootenhance.zdomain.SuperUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Adam
 * @since 2024/8/7
 */
@Component
public class EnhanceTest {

    @Resource
    private SuperUser superUser;
    @Resource
    private User user;

    @PostConstruct
    public void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(user);
            System.out.println(superUser);
            System.out.println();
        }
    }
}
