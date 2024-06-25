package com.nabob.conch.akka.demos.askv3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;

/**
 * @author Adam
 * @since 2024/6/25
 */
public class Test {

    public static void main(String[] args) {
        ActorSystem<CookieSchedule.Command> cookieSchedule = ActorSystem.create(CookieSchedule.create(), "CookieSchedule");

        cookieSchedule.tell(new CookieSchedule.Start("adam", 3));

    }

}
