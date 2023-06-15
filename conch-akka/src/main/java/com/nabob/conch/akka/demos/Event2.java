package com.nabob.conch.akka.demos;

import akka.actor.typed.ActorRef;

/**
 * 事件主体2
 *
 * @author Adam
 * @date 2021/2/23
 */
public class Event2 {

    public final String whom;
    public final ActorRef<Event3> event3ActorRef;

    public Event2(String whom, ActorRef<Event3> event3ActorRef) {
        this.whom = whom;
        this.event3ActorRef = event3ActorRef;
    }
}
