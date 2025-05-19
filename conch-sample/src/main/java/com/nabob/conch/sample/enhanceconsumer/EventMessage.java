package com.nabob.conch.sample.enhanceconsumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam
 * @since 2025/3/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {

    private final Map<String, String> tags = new HashMap<>();

    private String topic;

    private String body;

}
