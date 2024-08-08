package com.nabob.conch.sample.bootenhance;

import com.nabob.conch.sample.User;
import com.nabob.conch.sample.bootenhance.zdomain.SuperUser;
import com.nabob.conch.sample.uitl.JsonUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private List<User> users;

    @PostConstruct
    public void test() {
        System.out.println("==========================================================================================");
        System.out.println(user);
        System.out.println(superUser);
        System.out.println();

        System.out.println(JsonUtil.object2Json(users));
        System.out.println();
        System.out.println("==========================================================================================");
    }
}
