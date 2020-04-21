package ltd.beihu.spring.dependency.injection.event;

import ltd.beihu.spring.dependency.injection.basictype.UserTwo;
import org.springframework.context.ApplicationEvent;

/**
 * @author Adam
 * @date 2020/4/21
 */
public class UserTwoEvent extends ApplicationEvent {

    private UserTwo userTwo;

    public UserTwoEvent(Object source, UserTwo userTwo) {
        super(source);
        this.userTwo = userTwo;
    }

    public UserTwo getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(UserTwo userTwo) {
        this.userTwo = userTwo;
    }

    @Override
    public String toString() {
        return "UserTwoEvent{" +
                "userTwo=" + userTwo +
                '}';
    }
}
