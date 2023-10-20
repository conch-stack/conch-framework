package com.nabob.conch.akka.demos.ask;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Adam
 * @since 2023/9/26
 */
public class AskDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        final Duration t = Duration.ofSeconds(5);
//
//        // using 1000ms timeout
//        CompletableFuture<Object> future1 =
//                ask(actorA, "request", Duration.ofMillis(1000)).toCompletableFuture();
//
//        // using timeout from above
//        CompletableFuture<Object> future2 = ask(actorB, "another request", t).toCompletableFuture();
//
//        CompletableFuture<Result> transformed =
//                CompletableFuture.allOf(future1, future2)
//                        .thenApply(
//                                v -> {
//                                    String x = (String) future1.join();
//                                    String s = (String) future2.join();
//                                    return new Result(x, s);
//                                });
//
//        pipe(transformed, system.dispatcher()).to(actorC);

        // 创建Actor系统
        ActorSystem system = ActorSystem.create("DemoSystem");

        // 创建DemoActor
        ActorRef demoActor = system.actorOf(DemoActor.props(), "demoActor");

        // 创建一个超时时间
        Duration timeout = Duration.ofSeconds(5);

        // 发送消息并等待响应
        CompletionStage<Object> finished = Patterns.ask(demoActor, new DemoActor.Greet("Alice"), timeout);
        DemoActor.GreetResponse greetResponse = (DemoActor.GreetResponse) finished.toCompletableFuture().get(timeout.toMillis(), TimeUnit.MILLISECONDS);

        // 打印响应消息
        System.out.println(greetResponse.getMessage());

        // 关闭Actor系统
        system.terminate();
    }
}
