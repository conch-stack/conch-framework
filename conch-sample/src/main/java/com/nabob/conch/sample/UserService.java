package com.nabob.conch.sample;

/**
 * @author Adam
 * @since 2022/2/10
 */
public class UserService {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserName() {
        return "nice";
    }
}
