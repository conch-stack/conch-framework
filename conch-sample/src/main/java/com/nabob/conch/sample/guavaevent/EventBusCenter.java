package com.nabob.conch.sample.guavaevent;

import com.google.common.eventbus.EventBus;

public class EventBusCenter {
    private static EventBus eventBus;

    //双重锁单例模式
    private static EventBus getEventBus(){
        if(eventBus==null){
            synchronized (EventBus.class){
                if(eventBus==null){
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }
    public static void post(Object event){
        getEventBus().post(event);
    }
    public static void register(Object object){
        getEventBus().register(object);
    }
 
}