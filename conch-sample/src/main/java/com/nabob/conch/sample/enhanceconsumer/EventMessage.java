package com.nabob.conch.sample.enhanceconsumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Adam
 * @since 2025/3/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {

    private String topic;

    private String body;

}
