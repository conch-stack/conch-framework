package ltd.beihu.spring.dependency.injection.event;

import ltd.beihu.spring.ioc.overview.domain.User;
import org.springframework.context.ApplicationEvent;

/**
 * @author Adam
 * @date 2020/4/21
 */
public class UserEvent extends ApplicationEvent {

    private User user;

    public UserEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "user=" + user +
                '}';
    }
}
