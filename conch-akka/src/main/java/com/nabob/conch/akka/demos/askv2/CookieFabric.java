package com.nabob.conch.akka.demos.askv2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

// #standalone-ask
public class CookieFabric extends AbstractBehavior<CookieFabric.Command> {

    interface Command {
    }

    public static class GiveMeCookies implements Command {
        public final int count;
        public final ActorRef<Reply> replyTo;

        public GiveMeCookies(int count, ActorRef<Reply> replyTo) {
            this.count = count;
            this.replyTo = replyTo;
        }
    }

    interface Reply {
    }

    public static class Cookies implements Reply {
        public final int count;

        public Cookies(int count) {
            this.count = count;
        }
    }

    public static class InvalidRequest implements Reply {
        public final String reason;

        public InvalidRequest(String reason) {
            this.reason = reason;
        }
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(CookieFabric::new);
    }

    private CookieFabric(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().onMessage(GiveMeCookies.class, this::onGiveMeCookies).build();
    }

    private Behavior<Command> onGiveMeCookies(GiveMeCookies request) {
        if (request.count >= 5) request.replyTo.tell(new InvalidRequest("Too many cookies."));
        else request.replyTo.tell(new Cookies(request.count));

        return this;
    }
}
// #standalone-ask