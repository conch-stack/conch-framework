package com.nabob.conch.dubbo.server.spring;

import com.nabob.conch.dubbo.facade.User;
import com.nabob.conch.dubbo.facade.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

/**
 * @author Adam
 * @date 2021/4/2
 */
@DubboService
@Component
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById() {
        User user = new User();
        user.setId(1);
        // 进程名称
        user.setName(ManagementFactory.getRuntimeMXBean().getName());
        return user;
    }

    @Override
    public String getUserName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }
}
