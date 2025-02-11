package com.scorer.boot.dynamic.load.jar.demo4;

import com.nabob.conch.dynamic.jar.DirectDalClient;
import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @since 2024/12/18
 */
@Service
public class DCacheClientLocal extends DirectDalClient {

    private DCacheProxy proxy = new DCacheProxy();

    @Override
    public void test() {
        proxy.test();
    }

    public DCacheProxy getProxy() {
        return proxy;
    }
}
