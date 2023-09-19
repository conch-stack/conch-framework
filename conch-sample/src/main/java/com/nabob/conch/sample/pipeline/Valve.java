package com.nabob.conch.sample.pipeline;

/**
 * Valve
 *
 * @author Adam
 * @since 2023/9/18
 */
public interface Valve<req, res> {

    void handle(PipelineContext<req, res> ctx);

}
