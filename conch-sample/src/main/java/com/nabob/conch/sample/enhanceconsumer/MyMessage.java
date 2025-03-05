package com.nabob.conch.sample.enhanceconsumer;

import lombok.Data;

/**
 * @author Adam
 * @since 2025/3/5
 */
@Data
public class MyMessage {

    private EventMessage message;

    private String eventId;

}
