package com.nabob.conch.spring.dependency.injection.setter;

import com.nabob.conch.spring.ioc.overview.domain.User;

/**
 * @author Adam
 * @date 2020/4/10
 */
public class UserHolder {

    private User user;

    public UserHolder() {
    }

    public UserHolder(User user) {
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
        return "UserHolder{" +
                "user=" + user +
                '}';
    }
}
