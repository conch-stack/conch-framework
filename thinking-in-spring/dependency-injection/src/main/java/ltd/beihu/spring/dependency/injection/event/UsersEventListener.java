package ltd.beihu.spring.dependency.injection.event;

import org.springframework.context.event.EventListener;

/**
 * @author Adam
 * @date 2020/4/21
 */
public class UsersEventListener {

    @EventListener
    public void handleUserEvent(UserEvent userEvent) {
        System.out.println("UserEvent: " + userEvent.toString());
    }

    @EventListener
    public void handleUserEvent(UserTwoEvent UserTwoEvent) {
        System.out.println("UserTwoEvent: " + UserTwoEvent.toString());
    }

}
