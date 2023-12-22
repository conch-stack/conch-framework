package com.nabob.conch.sample.pipeline;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Adam
 * @since 2023/12/22
 */
@Component
public class PipelineHelper {

    @Resource
    private ValveRegistry valveRegistry;

    /**
     * call
     */
    public <res, req> res call(String group, req request, Class<req> reqClazz, Class<res> resClazz) {
        List<Valve<req, res>> targetValves = valveRegistry.getTargetValves1(group, reqClazz, resClazz);
        DefaultPipelineContext<req, res> ctx = new DefaultPipelineContext<>(targetValves, request);
        ctx.next();
        return ctx.getResult();
    }

}
