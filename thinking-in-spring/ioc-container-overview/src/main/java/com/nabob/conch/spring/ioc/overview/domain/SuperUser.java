package com.nabob.conch.spring.ioc.overview.domain;

import com.nabob.conch.spring.ioc.overview.annotation.Super;

/**
 * @author Adam
 * @since 2020/3/30
 */
@Super
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
