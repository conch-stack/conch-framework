package com.nabob.conch.sample.pipeline;

/**
 * Pipeline
 *
 * @author Adam
 * @since 2023/9/18
 */
public interface Pipeline<req, res> {

    /**
     * invoke
     */
    res invoke(req request);
}
