package com.nabob.conch.spring.lifecycle.beanlifecycle.cycledepend;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Adam
 * @date 2020/6/5
 */

public class TestA {

    private String name;

    @Autowired
    private TestB testB;

    @PostConstruct
    public void init() {
        System.out.println("TestA : " + testB);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestB getTestB() {
        return testB;
    }

    public void setTestB(TestB testB) {
        this.testB = testB;
    }

    @Override
    public String toString() {
        return "TestA{" +
                "name='" + name + '\'' +
                '}';
    }
}
