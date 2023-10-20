package com.nabob.conch.akka.demos.event1;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Event1 事件 处理行为定义
 *
 * @author Adam
 * @date 2021/2/23
 */
public class Event1Behavior extends AbstractBehavior<Event1> {

    // 子Actor
    private final ActorRef<Event2> event2ActorRef;

    private Event1Behavior(ActorContext<Event1> context) {
        super(context);

        // Create a child Actor from the given Behavior and with the given name.
        // 创建子Actor
        event2ActorRef = context.spawn(Event2Behavior.create(), "Event2Behavior");
    }

    /**
     * 构建 Event1 行为处理 - 由ActorSystem调用
     *
     * @return Behavior Event1
     */
    public static Behavior<Event1> create() {
        return Behaviors.setup(Event1Behavior::new);
    }

    @Override
    public Receive<Event1> createReceive() {
        return newReceiveBuilder().onMessage(Event1.class, this::handleEvent1).build();
    }

    /**
     * 处理 event1
     *
     * @param event1 event1
     * @return Behavior Event1
     */
    public Behavior<Event1> handleEvent1(Event1 event1) {
        // 构建Event3的ActorRef给Event2用
        ActorRef<Event3> event3ActorRef = getContext().spawn(Event3Behavior.create(3), "Event3Behavior");
        // 发消息给Event2
        event2ActorRef.tell(new Event2(event1.name, event3ActorRef));
        return this;
    }
}
