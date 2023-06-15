package com.nabob.conch.akka.demos.pay;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

/**
 * 支付处理器
 *
 * @author Adam
 * @date 2021/2/25
 */
public class PaymentProcessor {

    public static Behavior<Void> create() {
        return Behaviors.setup(context -> {
            context.getLog().info("Payment Processor Started");
            context.spawn(Configuration.create(), "config");
           return Behaviors.empty();
        });
    }

}
