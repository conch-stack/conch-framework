/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nabob.conch.spring.aop.overview.advisor;

import com.nabob.conch.spring.aop.overview.EchoService;
import org.springframework.aop.IntroductionInfo;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * IntroductionAdvisor Demo
 * <p>
 * {@link org.springframework.aop.IntroductionAdvisor} 示例
 *
 * @author Adam
 * @since 2023/3/15
 */
public class IntroductionAdvisorDemo extends Object implements EchoService, Comparable<IntroductionAdvisorDemo> {

    public static void main(String[] args) {
        // EchoService 和 Comparable
        IntroductionAdvisorDemo target = new IntroductionAdvisorDemo();
        // 使用该构造器会使得 IntroductionInfo 失效
        // target -> EchoService 和 Comparable
//        ProxyFactory proxyFactory = new ProxyFactory(target);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        // 添加 IntroductionAdvisor
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("BeforeAdvice : " + method);
            }
        }, new IntroductionInfo() {
            // 甚至可添加毫不相干的类，但是最好不要这样弄
            @Override
            public Class<?>[] getInterfaces() {
                return new Class[]{EchoService.class, Comparable.class, Map.class};
            }
        }));

        Object proxy = proxyFactory.getProxy();

        EchoService echoService = (EchoService) proxy;

        echoService.echo("Hello,World");

        Comparable comparable = (Comparable) proxy;
        comparable.compareTo(null);

        // 也会触发AOP但是无法正常执行方法
        Map map = (Map) proxy;
        map.put("1", "A");

    }

    @Override
    public int compareTo(IntroductionAdvisorDemo o) {
        return 0;
    }

    @Override
    public void echo(String message) throws NullPointerException {
        System.out.println("IntroductionAdvisorDemo : " + message);
    }

    @Override
    public String echo() {
        return "IntroductionAdvisorDemo : echo()";
    }
}
