package com.nabob.conch.akka.demos;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @date 2021/3/16
 */
public class TestCode {

    public static void main(String[] args) {
        // 创建 Actor 系统
        ActorSystem system = ActorSystem.create("MyActorSystem");

        // 创建一个名为 "printer" 的 Actor
        ActorRef printer = system.actorOf(Props.create(Printer.class), "printer");

        // 创建多个名为 "counter" 的 Actor
        List<ActorRef> conters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ActorRef counter = system.actorOf(Props.create(Counter.class, printer), "counter" + i);
            conters.add(counter);
        }

        for (int i = 0; i < 10; i++) {
            // 向每个 "counter" Actor 发送消息
            conters.forEach(conter -> conter.tell("Increment", ActorRef.noSender()));
        }
    }

    // 打印消息的 Actor
    static class Printer extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(String.class, msg -> System.out.println("Printer: " + msg))
                    .build();
        }
    }

    // 计数的 Actor
    static class Counter extends AbstractActor {
        private final ActorRef printer;
        private int count = 0;

        public Counter(ActorRef printer) {
            this.printer = printer;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .matchEquals("Increment", msg -> {
                        count++;
                        printer.tell("Count: " + count, self());
                    })
                    .build();
        }
    }
}


