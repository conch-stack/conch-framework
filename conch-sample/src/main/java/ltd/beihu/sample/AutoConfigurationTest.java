package ltd.beihu.sample;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfigurationTest {

    @Bean(name = "UserService")
    @ConditionalOnBean(name = "user")
    @ConditionalOnMissingBean(name = "UserService")
    public UserService userService() {
        UserService userService = new UserService();
        userService.setUser(user());
        return userService;
    }

    @Bean(name = "user")
    public User user() {
        return new User();
    }
}
