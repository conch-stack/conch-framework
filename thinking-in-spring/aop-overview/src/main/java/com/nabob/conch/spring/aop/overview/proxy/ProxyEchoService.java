package com.nabob.conch.spring.aop.overview.proxy;

import com.nabob.conch.spring.aop.overview.EchoService;

/**
 * @author Adam
 * @since 2023/3/14
 */
public class ProxyEchoService implements EchoService {
    @Override
    public void echo(String info) {
        System.out.println("ProxyEchoService" + info);
    }

    @Override
    public String echo() {
        return "Proxy Echo";
    }
}
