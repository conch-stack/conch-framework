package com.nabob.conch.sample.pipeline;

import java.util.List;

/**
 * Abstract PipelineContext
 *
 * @author Adam
 * @since 2023/9/18
 */
public class DefaultPipelineContext<req, res> implements PipelineContext<req, res> {

    private int index = 0;

    /**
     * Stop this Pipeline and return
     */
    private boolean stopFlag;

    /**
     * Valves
     */
    private final List<Valve<req, res>> valves;

    /**
     * Result
     */
    private final req request;

    /**
     * Result
     */
    private res result;

    public DefaultPipelineContext(List<Valve<req, res>> valves, req request) {
        this.valves = valves;
        this.request = request;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    @Override
    public void next() {
        if (stopFlag) {
            return;
        }

        if (index < valves.size()) {
            Valve<req, res> valve = valves.get(index);
            index++;
            valve.handle(this);
        }
    }

    @Override
    public void stop() {
        this.stopFlag = true;
    }

    @Override
    public req getRequest() {
        return request;
    }

    @Override
    public res getResult() {
        return result;
    }

    @Override
    public void setResult(res result) {
        this.result = result;
    }

}
