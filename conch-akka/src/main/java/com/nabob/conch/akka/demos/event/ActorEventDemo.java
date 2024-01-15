package com.nabob.conch.akka.demos.event;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

/**
 * Actor Event Demo
 *
 * @author Adam
 * @date 2021/2/23
 */
public class ActorEventDemo {

    public static void main(String[] args) {

        ActorSystem<Event1> actorSystemNew = ActorSystem.create(Event1Behavior.create(), "Event1ActorSystem");
        actorSystemNew.tell(new Event1("小明"));
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            // 或stop掉当前System下所有的Actor
            actorSystemNew.terminate();
        }
    }

}
