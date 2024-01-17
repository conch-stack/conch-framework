package com.nabob.conch.akka.pipeline;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nabob.conch.akka.pipeline.dto.Command;
import com.nabob.conch.akka.pipeline.dto.Photo;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;
import com.nabob.conch.akka.pipeline.dto.Timeout;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Aggregator V2
 * <p>
 * support timeout  https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html#scheduling-messages-to-self
 *
 * @author Adam
 * @since 2024/1/15
 */
public class AggregatorV2 extends AbstractBehavior<Command> {

    private static Map<Long, List<PhotoMsg>> CACHE = Maps.newConcurrentMap();

    private PipelineContext pipelineContext;

    private TimerScheduler<Command> timer;

    public AggregatorV2(ActorContext<Command> context, PipelineContext pipelineContext, TimerScheduler<Command> timer) {
        super(context);
        this.pipelineContext = pipelineContext;
        this.timer = timer;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Timeout.class, timeout -> {
                    CACHE.remove(timeout.getWhichId());
                    System.err.println(timeout.getWhichId() + "已过期删除");
                    return Behaviors.same();
                })
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
                    } else {
                        // 新的 or 第一条消息已超时的
                        timer.startSingleTimer(new Timeout(photoMsg.id), pipelineContext.getTimeout());
                    }

                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<Command> create(final PipelineContext pipelineContext) {
        return Behaviors.withTimers(timer -> Behaviors.setup(context -> new AggregatorV2(context, pipelineContext, timer)));
    }
}
