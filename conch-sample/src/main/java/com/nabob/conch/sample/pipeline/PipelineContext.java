package com.nabob.conch.sample.pipeline;

/**
 * PipelineContext
 *
 * @author Adam
 * @since 2023/9/18
 */
public interface PipelineContext<req, res> {

    void next();

    void stop();

    req getRequest();

    res getResult();

    void setResult(res result);

}
