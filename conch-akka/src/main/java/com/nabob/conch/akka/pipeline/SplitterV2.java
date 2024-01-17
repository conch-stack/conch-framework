package com.nabob.conch.akka.pipeline;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.pipeline.dto.PhotoImage;
import com.nabob.conch.akka.pipeline.dto.PhotoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Splitter
 *
 * @author Adam
 * @since 2024/1/15
 */
public class SplitterV2 extends AbstractBehavior<PhotoImage> {

    private static AtomicLong globalId = new AtomicLong(0);

    private final PipelineContext pipelineContext;

    private final List<ActorRef<PhotoLabel>> workers = new ArrayList<>();

    public SplitterV2(ActorContext<PhotoImage> context, PipelineContext pipelineContext) {
        super(context);
        this.pipelineContext = pipelineContext;

        BehaviorHelper behaviorHelper = pipelineContext.getBehaviorHelper();
        // 创建两个子任务
        workers.add(context.spawn(behaviorHelper.getPhotoLabelJob1(), "job1"));
        workers.add(context.spawn(behaviorHelper.getPhotoLabelJob2(), "job2"));
    }

    @Override
    public Receive<PhotoImage> createReceive() {
        return newReceiveBuilder()
                .onMessage(PhotoImage.class, photoImage -> {
                    long id = globalId.incrementAndGet();

                    workers.forEach(worker -> worker.tell(PhotoLabel.builder()
                            .id(id)
                            .photoImage(photoImage)
                            .to(pipelineContext.getAggregatorV2())
                            .build()));

                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<PhotoImage> create(final PipelineContext pipelineContext) {
        return Behaviors.setup(context -> new SplitterV2(context, pipelineContext));
    }
}
