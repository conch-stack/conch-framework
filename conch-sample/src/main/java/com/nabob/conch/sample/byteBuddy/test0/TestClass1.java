package com.nabob.conch.sample.byteBuddy.test0;

import net.bytebuddy.implementation.bind.annotation.BindingPriority;

/**
 * 如果在TestClass1.class中有超过一个可调用的方法的签名和返回类型一致，
 * 我们可以使用@BindingPriority来解决冲突。@BindingPriority有一个整型参数-这个值越大优先级越高。
 *
 * @author Adam
 * @since 2023/8/9
 */
public class TestClass1 {

    @BindingPriority(3)
    public static String getOid() {
        return "TestClass1#getOid-static";
    }

    @BindingPriority(2)
    public static String getOid2() {
        return "TestClass1#getOid2-static";
    }
}
