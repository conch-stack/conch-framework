package com.nabob.conch.sample.bootenhance.zdomain;

import com.nabob.conch.sample.User;

/**
 * @author Adam
 * @since 2020/3/30
 */
public class SuperUser extends User {

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "SuperUser{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
