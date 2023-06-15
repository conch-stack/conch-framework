package com.nabob.conch.spring.dependency.injection.basictype;

import org.springframework.core.io.Resource;

/**
 * @author Adam
 * @date 2020/4/14
 */
public class UserTwo {

    /**
     * 原生类型
     */
    private boolean isStudent;

    /**
     * 常规类型
     */
    private String name;

    /**
     * 标量类型
     */
    private City city;

    /**
     * Spring类型
     */
    private Resource resource;


    public UserTwo() {
    }

    public UserTwo(boolean isStudent, String name) {
        this.isStudent = isStudent;
        this.name = name;
    }

    public UserTwo(boolean isStudent, String name, City city, Resource resource) {
        this.isStudent = isStudent;
        this.name = name;
        this.city = city;
        this.resource = resource;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "UserTwo{" +
                "isStudent=" + isStudent +
                ", name='" + name + '\'' +
                ", city=" + city +
                ", resource=" + resource +
                '}';
    }
}
