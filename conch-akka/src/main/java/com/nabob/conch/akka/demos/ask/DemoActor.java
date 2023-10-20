package com.nabob.conch.akka.demos.ask;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class DemoActor extends AbstractActor {

    public static Props props() {
        return Props.create(DemoActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greet.class, greet -> {
//                    System.out.println("Hello, " + greet.getName() + "!");
                    if (greet.getName().equals("stop")) {
                        System.out.println("我要开始关闭了");
                        getContext().stop(getSelf());
                    } else {
                        getSender().tell(new GreetResponse("Hello, " + greet.getName() + "!"), getSelf());
                    }
                })
                .build();
    }

    public static class Greet {
        private final String name;

        public Greet(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class GreetResponse {
        private final String message;

        public GreetResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}