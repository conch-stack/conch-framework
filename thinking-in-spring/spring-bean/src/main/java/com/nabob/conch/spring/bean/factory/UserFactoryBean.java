package com.nabob.conch.spring.bean.factory;

import com.nabob.conch.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Adam
 * @since 2020/4/2
 */
public class UserFactoryBean implements FactoryBean<User> {

    @Override
    public User getObject() throws Exception {
        return new User("测试FactoryBean", 1);
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }
}
