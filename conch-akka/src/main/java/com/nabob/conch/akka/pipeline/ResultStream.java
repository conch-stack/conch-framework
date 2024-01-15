package com.nabob.conch.akka.pipeline;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.pipeline.dto.Photo;

/**
 * ResultStream
 *
 * @author Adam
 * @since 2024/1/15
 */
public class ResultStream extends AbstractBehavior<Photo> {

    public ResultStream(ActorContext<Photo> context) {
        super(context);
    }

    @Override
    public Receive<Photo> createReceive() {
        return newReceiveBuilder()
                .onMessage(Photo.class, photo -> {
                    System.out.println(photo);
                    return Behaviors.same();
                })
                .build();
    }
}
