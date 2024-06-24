package com.nabob.conch.akka.persessionv2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;

import java.math.BigDecimal;

/**
 * @author Adam
 * @since 2024/6/24
 */
public class Test {

    public static Behavior<Hotel1.RequestQuote> createHotel1RequestQuoteBehavior() {
        return Behaviors.receive((context, hotel1RequestQuote) -> {
            // 通知下游
            hotel1RequestQuote.replyTo.tell(new Hotel1.Quote("hotel1", BigDecimal.valueOf(100L)));
            return Behaviors.same();
        });
    }

    public static Behavior<Hotel2.RequestPrice> createHotel2RequestPriceBehavior() {
        return Behaviors.receive((context, hotel2RequestPrice) -> {
            // 通知下游
            hotel2RequestPrice.replyTo.tell(new Hotel2.Price("hotel2", BigDecimal.valueOf(900L)));
            return Behaviors.same();
        });
    }


    public static Behavior<Void> create() {
        return Behaviors.setup(
                context -> {
                    ActorRef<Hotel1.RequestQuote> hotel1 = context.spawn(Test.createHotel1RequestQuoteBehavior(), "hotel1");
                    ActorRef<Hotel2.RequestPrice> hotel2 = context.spawn(Test.createHotel2RequestPriceBehavior(), "hotel2");
                    context.spawn(HotelCustomer2.create(hotel1, hotel2), "HotelTest2");

                    return Behaviors.receive(Void.class)
                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
                            .build();
                });
    }

    public static void main(String[] args) {
        // 无类型转换为有类型ActorSystem
//        akka.actor.ActorSystem classicActorSystem = akka.actor.ActorSystem.create();
//        ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);

//        ActorSystem<HotelCustomer.Command> hotelTest = ActorSystem.create(HotelCustomer.create(), "HotelTest");

        ActorSystem.create(create(), "HotelTestSystem");
    }
}
