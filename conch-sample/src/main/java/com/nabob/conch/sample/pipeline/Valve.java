package com.nabob.conch.sample.pipeline;

import org.springframework.core.PriorityOrdered;

/**
 * Valve
 *
 * @author Adam
 * @since 2023/9/18
 */
public interface Valve<req, res> extends PriorityOrdered {

    void handle(PipelineContext<req, res> ctx);

}
