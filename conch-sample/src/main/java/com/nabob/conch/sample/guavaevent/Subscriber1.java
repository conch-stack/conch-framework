package com.nabob.conch.sample.guavaevent;

import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;


import java.time.Instant;

@Component
public class Subscriber1 {
    @Subscribe
    public void test1(ExecutionEvent1 event){
        System.out.println(Instant.now() +"监听者1-->回调1,收到事件："+event.getName()+"，线程号为："+Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void test2(ExecutionEvent2 event){
        System.out.println(Instant.now() +"监听者1-->回调2,收到事件："+event.getAddress()+"，线程号为："+Thread.currentThread().getName());
    }
}