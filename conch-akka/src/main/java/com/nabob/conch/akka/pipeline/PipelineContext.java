package com.nabob.conch.akka.pipeline;

import akka.actor.typed.ActorRef;
import com.nabob.conch.akka.pipeline.dto.Photo;
import com.nabob.conch.akka.pipeline.dto.PhotoMsg;
import lombok.Builder;
import lombok.Data;

/**
 * @author Adam
 * @since 2024/1/15
 */
@Data
public class PipelineContext {

    /**
     * 并行worker帮助类
     */
    private BehaviorHelper behaviorHelper;

    /**
     * 聚合 Actor
     */
    private ActorRef<PhotoMsg> aggregator;

    /**
     * 结果 Actor
     */
    private ActorRef<Photo> resultStream;

}
