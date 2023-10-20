package com.nabob.conch.akka.demos.ask;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.LoggingAdapter;
import com.nabob.conch.akka.demos.greet.GreeterMain;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class SupervisedAskSpec {

    public Object execute(
            Class<? extends AbstractActor> someActor,
            Object message,
            Duration timeout,
            ActorRefFactory actorSystem) throws Exception {
        // example usage
        try {
            ActorRef supervisorCreator = SupervisedAsk.createSupervisorCreator(actorSystem);
            CompletionStage<Object> finished = SupervisedAsk.askOf(supervisorCreator, Props.create(someActor), message, timeout);
            return finished.toCompletableFuture().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // exception propagated by supervision
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        SupervisedAskSpec supervisedAskSpec = new SupervisedAskSpec();

        ActorSystem actorSystem = ActorSystem.create("MyActorSystem");

        for (int i = 0; i < 100; i++) {
            DemoActor.GreetResponse greetResponse = (DemoActor.GreetResponse) supervisedAskSpec.execute(DemoActor.class, new DemoActor.Greet("Alice" + i), Duration.ofSeconds(30), actorSystem);
            System.out.println("来了：" + greetResponse.getMessage());
        }

        DemoActor.GreetResponse greetResponse = (DemoActor.GreetResponse) supervisedAskSpec.execute(DemoActor.class, new DemoActor.Greet("stop"), Duration.ofSeconds(30), actorSystem);
        System.out.println("来了：" + greetResponse.getMessage());

    }
}