package com.nabob.conch.akka.pipeline;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nabob.conch.akka.pipeline.dto.Photo;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Aggregator
 *
 * @author Adam
 * @since 2024/1/15
 */
public class Aggregator extends AbstractBehavior<PhotoMsg> {

    private static Map<Long, List<PhotoMsg>> CACHE = Maps.newConcurrentMap();

    private PipelineContext pipelineContext;


    public Aggregator(ActorContext<PhotoMsg> context, PipelineContext pipelineContext) {
        super(context);
        this.pipelineContext = pipelineContext;
    }

    @Override
    public Receive<PhotoMsg> createReceive() {
        return newReceiveBuilder()
                .onMessage(PhotoMsg.class, photoMsg -> {
                    List<PhotoMsg> photoMsgs = CACHE.computeIfAbsent(photoMsg.id, k -> Lists.newArrayList());
                    photoMsgs.add(photoMsg);

                    if (photoMsgs.size() == 2) {

                        Photo photo = new Photo();
                        for (PhotoMsg msg : photoMsgs) {
                            if (Objects.nonNull(msg.speed)) {
                                photo.setSpeed(msg.speed);
                            }
                            if (StringUtils.isNotBlank(msg.license)) {
                                photo.setLicense(msg.license);
                            }
                        }
                        pipelineContext.getResultStream().tell(photo);

                        CACHE.remove(photoMsg.id);
                    }

                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<PhotoMsg> create(final PipelineContext pipelineContext) {
        return Behaviors.setup(context -> new Aggregator(context, pipelineContext));
    }
}
