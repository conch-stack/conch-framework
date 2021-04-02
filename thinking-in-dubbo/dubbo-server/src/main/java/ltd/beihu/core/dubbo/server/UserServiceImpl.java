package ltd.beihu.core.dubbo.server;

import ltd.beihu.core.dubbo.facade.User;
import ltd.beihu.core.dubbo.facade.UserService;

import java.lang.management.ManagementFactory;

/**
 * @author Adam
 * @date 2021/4/2
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById() {
        User user = new User();
        user.setId(1);
        // 进程名称
        user.setName(ManagementFactory.getRuntimeMXBean().getName());
        return user;
    }
}
