package com.nabob.conch.akka.demos.askv2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Props;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import com.nabob.conch.akka.persessionv2.Hotel1;
import com.nabob.conch.akka.persessionv2.Hotel2;
import com.nabob.conch.akka.persessionv2.HotelCustomer2;
import com.nabob.conch.akka.persessionv2.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

class NotShown {

//    public static Behavior<Void> create() {
//        return Behaviors.setup(
//                context -> {
//                    ActorRef<CookieFabric.Command> cookie = context.spawn(CookieFabric.create(), "cookie");
//
//                    return Behaviors.receive(Void.class)
//                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
//                            .build();
//                });
//    }

    public static void main(String[] args) {
        akka.actor.ActorSystem classicActorSystem = akka.actor.ActorSystem.create();
        ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);

        ActorRef<CookieFabric.Command> cookie = typedActorSystem.systemActorOf(CookieFabric.create(), "cookie", Props.empty());

        askAndPrint(typedActorSystem, cookie);

    }

        // #standalone-ask

        public static void askAndPrint(
                ActorSystem<Void> system, ActorRef<CookieFabric.Command> cookieFabric) {
            CompletionStage<CookieFabric.Reply> result =
                    AskPattern.ask(
                            cookieFabric,
                            replyTo -> new CookieFabric.GiveMeCookies(3, replyTo),
                            // asking someone requires a timeout and a scheduler, if the timeout hits without
                            // response the ask is failed with a TimeoutException
                            Duration.ofSeconds(3),
                            system.scheduler());

            result.whenComplete(
                    (reply, failure) -> {
                        if (reply instanceof CookieFabric.Cookies)
                            System.out.println("Yay, " + ((CookieFabric.Cookies) reply).count + " cookies!");
                        else if (reply instanceof CookieFabric.InvalidRequest)
                            System.out.println(
                                    "No cookies for me. " + ((CookieFabric.InvalidRequest) reply).reason);
                        else System.out.println("Boo! didn't get cookies in time. " + failure);
                    });
        }
        // #standalone-ask

        public void askAndMapInvalid(
                ActorSystem<Void> system, ActorRef<CookieFabric.Command> cookieFabric) {
            // #standalone-ask-fail-future
            CompletionStage<CookieFabric.Reply> result =
                    AskPattern.ask(
                            cookieFabric,
                            replyTo -> new CookieFabric.GiveMeCookies(3, replyTo),
                            Duration.ofSeconds(3),
                            system.scheduler());

            CompletionStage<CookieFabric.Cookies> cookies =
                    result.thenCompose(
                            (CookieFabric.Reply reply) -> {
                                if (reply instanceof CookieFabric.Cookies) {
                                    return CompletableFuture.completedFuture((CookieFabric.Cookies) reply);
                                } else if (reply instanceof CookieFabric.InvalidRequest) {
                                    CompletableFuture<CookieFabric.Cookies> failed = new CompletableFuture<>();
                                    failed.completeExceptionally(
                                            new IllegalArgumentException(((CookieFabric.InvalidRequest) reply).reason));
                                    return failed;
                                } else {
                                    throw new IllegalStateException("Unexpected reply: " + reply.getClass());
                                }
                            });

            cookies.whenComplete(
                    (cookiesReply, failure) -> {
                        if (cookiesReply != null)
                            System.out.println("Yay, " + cookiesReply.count + " cookies!");
                        else System.out.println("Boo! didn't get cookies in time. " + failure);
                    });
            // #standalone-ask-fail-future
        }
    }