package com.nabob.conch.akka.demos;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorSystem;

public class AkkaMultiThreadDemo {

    // 定义一个消息类型
    static class Greet {
        final String name;

        public Greet(String name) {
            this.name = name;
        }
    }

    // 创建一个 Actor，继承自 AbstractBehavior
    static class GreeterActor extends AbstractBehavior<Greet> {

        private GreeterActor(ActorContext<Greet> context) {
            super(context);
        }

        // 重写 createReceive 方法来处理消息
        @Override
        public Receive<Greet> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Greet.class, this::onGreet)
                    .build();
        }

        // 处理 Greet 消息
        private Behavior<Greet> onGreet(Greet message) {
            getContext().getLog().info("Hello, " + message.name + "!");
            return this;
        }

        // 创建一个行为工厂方法
        public static Behavior<Greet> create() {
            return Behaviors.setup(GreeterActor::new);
        }
    }

    public static void main(String[] args) {
        // 创建 Actor 系统
        akka.actor.typed.ActorSystem<Greet> system =
                akka.actor.typed.ActorSystem.create(GreeterActor.create(), "GreeterSystem");

        // 向 Actor 发送消息
        system.tell(new Greet("John"));
        system.tell(new Greet("Alice"));
        for (int i = 0; i < 1000; i++) {
            system.tell(new Greet("Alice"));
        }

        // 等待一段时间，然后终止 Actor 系统
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }
}
