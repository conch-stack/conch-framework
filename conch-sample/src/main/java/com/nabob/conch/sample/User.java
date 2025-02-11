package com.nabob.conch.sample;

import com.nabob.conch.sample.reflect.FirstAnnotation;

/**
 * @author Adam
 * @since 2020/3/30
 */
public class User {

    /**
     * 姓名
     */
    @FirstAnnotation("name_zjz")
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
