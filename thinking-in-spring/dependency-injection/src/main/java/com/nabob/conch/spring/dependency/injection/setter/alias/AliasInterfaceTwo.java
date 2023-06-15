package com.nabob.conch.spring.dependency.injection.setter.alias;

import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @date 2020/4/28
 */
@Service
public class AliasInterfaceTwo extends AbstractAliasInterface {

    private String enName;

    public AliasInterfaceTwo(String enName) {
        this.enName = enName;
    }

    @Override
    public void print() {
        System.out.println("AliasInterfaceTwo: " + this.enName);
    }
}
