package com.nabob.conch.akka.demos;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Event2 事件 处理行为定义
 *
 * @author Adam
 * @date 2021/2/23
 */
public class Event2Behavior extends AbstractBehavior<Event2> {

    public Event2Behavior(ActorContext<Event2> context) {
        super(context);
    }

    public static Behavior<Event2> create() {
        return Behaviors.setup(Event2Behavior::new);
    }

    @Override
    public Receive<Event2> createReceive() {
        return newReceiveBuilder().onMessage(Event2.class, this::handleEvent2).build();
    }

    /**
     * 处理 event2
     *
     * @param event2 event2
     * @return Behavior Event2
     */
    public Behavior<Event2> handleEvent2(Event2 event2) {
        getContext().getLog().info("Hello {}!", event2.whom);
        // 发消息给Event3
        event2.event3ActorRef.tell(new Event3(event2.whom, getContext().getSelf()));
        return this;
    }
}
