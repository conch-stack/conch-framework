package com.nabob.conch.akka.pipeline.dto;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Data;

/**
 * @author Adam
 * @since 2024/1/14
 */
@Data
@Builder
public class PhotoLabel {

    public long id;

    public PhotoImage photoImage;

    public ActorRef<PhotoMsg> to;
}
