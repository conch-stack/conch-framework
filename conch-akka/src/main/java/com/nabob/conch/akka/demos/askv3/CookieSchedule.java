package com.nabob.conch.akka.demos.askv3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.utils.JsonUtil;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Adam
 * @since 2024/6/25
 */
public class CookieSchedule extends AbstractBehavior<CookieSchedule.Command> {

    public CookieSchedule(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<CookieSchedule.Command> create() {
        return Behaviors.setup(CookieSchedule::new);
    }

    interface Command {
    }

    public static final class Start implements Command {
        public final String name;
        public int num;

        public Start(String name, int num) {
            this.name = name;
            this.num = num;
        }
    }

    public enum GracefulShutdown implements Command {
        INSTANCE
    }

    @Override
    public Receive<CookieSchedule.Command> createReceive() {
        return newReceiveBuilder().onMessage(Start.class, this::onSchedule)
                .onMessage(GracefulShutdown.class, message -> onGracefulShutdown())
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<CookieSchedule.Command> onSchedule(CookieSchedule.Start start) throws ExecutionException, InterruptedException, TimeoutException {
        getContext().getLog().info("onSchedule {}", JsonUtil.object2Json(start));

        ActorContext<Command> context = getContext();
        ActorSystem<Void> system = context.getSystem();

//        CompletionStage<CookieFabric.Reply> rsCompose = null;
//        while (start.num > 0) {
//            ActorRef<CookieFabric.Command> cookieFabric = context.spawn(CookieFabric.create(), "cookie" + start.num);
//            if (Objects.isNull(rsCompose)) {
//                rsCompose = AskPattern.ask(
//                        cookieFabric,
//                        replyTo -> new CookieFabric.GiveMeCookies(start.num, replyTo),
//                        // asking someone requires a timeout and a scheduler, if the timeout hits without
//                        // response the ask is failed with a TimeoutException
//                        Duration.ofSeconds(3),
//                        system.scheduler());
//            } else {
//                rsCompose = rsCompose.thenCompose((CookieFabric.Reply reply) -> {
//                    ActorRef<CookieFabric.Command> tmp = context.spawn(CookieFabric.create(), "cookie" + start.num);
//                    CompletionStage<CookieFabric.Reply> result = AskPattern.ask(
//                            tmp,
//                            replyTo -> new CookieFabric.GiveMeCookies(start.num, replyTo),
//                            // asking someone requires a timeout and a scheduler, if the timeout hits without
//                            // response the ask is failed with a TimeoutException
//                            Duration.ofSeconds(3),
//                            system.scheduler());
//                    return result;
//                });
//            }
//
//            start.num--;
//        }

        ActorRef<CookieFabric.Command> cookieFabric = context.spawn(CookieFabric.create(), "cookie" + start.num);
        CompletionStage<CookieFabric.Reply> rsCompose = AskPattern.ask(
                cookieFabric,
                replyTo -> new CookieFabric.GiveMeCookies(start.num, replyTo),
                // asking someone requires a timeout and a scheduler, if the timeout hits without
                // response the ask is failed with a TimeoutException
                Duration.ofSeconds(3),
                system.scheduler());
//        rsCompose.toCompletableFuture().get(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
        rsCompose.whenComplete(
                (reply, failure) -> {
                    if (reply instanceof CookieFabric.Cookies)
                        System.out.println("Yay, " + ((CookieFabric.Cookies) reply).count + " cookies!");
                    else if (reply instanceof CookieFabric.InvalidRequest)
                        System.out.println(
                                "No cookies for me. " + ((CookieFabric.InvalidRequest) reply).reason);
                    else System.out.println("Boo! didn't get cookies in time. " + failure);
                });

        start.num--;

        ActorRef<CookieFabric.Command> tmp = context.spawn(CookieFabric.create(), "cookie" + start.num);
        CompletionStage<CookieFabric.Reply> replyCompletionStage = AskPattern.ask(
                tmp,
                replyTo -> new CookieFabric.GiveMeCookies(start.num, replyTo),
                // asking someone requires a timeout and a scheduler, if the timeout hits without
                // response the ask is failed with a TimeoutException
                Duration.ofSeconds(3),
                system.scheduler());
//        replyCompletionStage.toCompletableFuture().get(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
        replyCompletionStage.whenComplete(
                (reply, failure) -> {
                    if (reply instanceof CookieFabric.Cookies)
                        System.out.println("Yay, " + ((CookieFabric.Cookies) reply).count + " cookies!");
                    else if (reply instanceof CookieFabric.InvalidRequest)
                        System.out.println(
                                "No cookies for me. " + ((CookieFabric.InvalidRequest) reply).reason);
                    else System.out.println("Boo! didn't get cookies in time. " + failure);
                });

        start.num--;

        ActorRef<CookieFabric.Command> tmp1 = context.spawn(CookieFabric.create(), "cookie" + start.num);
        CompletionStage<CookieFabric.Reply> replyCompletionStage1= AskPattern.ask(
                tmp1,
                replyTo -> new CookieFabric.GiveMeCookies(start.num, replyTo),
                // asking someone requires a timeout and a scheduler, if the timeout hits without
                // response the ask is failed with a TimeoutException
                Duration.ofSeconds(3),
                system.scheduler());
//        replyCompletionStage1.toCompletableFuture().get(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
        replyCompletionStage1.whenComplete(
                (reply, failure) -> {
                    if (reply instanceof CookieFabric.Cookies)
                        System.out.println("Yay, " + ((CookieFabric.Cookies) reply).count + " cookies!");
                    else if (reply instanceof CookieFabric.InvalidRequest)
                        System.out.println(
                                "No cookies for me. " + ((CookieFabric.InvalidRequest) reply).reason);
                    else System.out.println("Boo! didn't get cookies in time. " + failure);
                });

        return this;
    }

    private Behavior<Command> onGracefulShutdown() {
        getContext().getSystem().log().info("Initiating graceful shutdown...");

        // Here it can perform graceful stop (possibly asynchronous) and when completed
        // return `Behaviors.stopped()` here or after receiving another message.
        return Behaviors.stopped();
    }

    private Behavior<Command> onPostStop() {
        getContext().getSystem().log().info("Master Control Program stopped");
        return this;
    }
}
