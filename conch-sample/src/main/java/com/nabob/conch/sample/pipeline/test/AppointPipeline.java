package com.nabob.conch.sample.pipeline.test;

import com.google.common.collect.Lists;
import com.nabob.conch.sample.pipeline.DefaultPipelineContext;
import com.nabob.conch.sample.pipeline.Pipeline;
import com.nabob.conch.sample.pipeline.Valve;
import com.nabob.conch.sample.pipeline.ValveRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Appoint Pipeline
 *
 * @author Adam
 * @since 2023/9/18
 */
@Component
public class AppointPipeline implements Pipeline<AppointRequestHolder, Boolean> {

    private static final List<Valve<AppointRequestHolder, Boolean>> APPOINT_VALVES;

    static {
        APPOINT_VALVES = Lists.newArrayList();
        APPOINT_VALVES.add(new SmartAppointValve());
        APPOINT_VALVES.add(new SmartTimeoutAppointValve());
        APPOINT_VALVES.add(new TicketUpgradeAppointValve());
    }

    @Resource
    private ValveRegistry valveRegistry;

    @Override
    public Boolean invoke(AppointRequestHolder request) {
//        DefaultPipelineContext<AppointRequestHolder, Boolean> ctx = new DefaultPipelineContext<>(APPOINT_VALVES, request);
//        ctx.next();
//        return ctx.getResult();

//        List<Valve<AppointRequestHolder, Boolean>> targetValves = valveRegistry.getTargetValves(AppointRequestHolder.class, Boolean.class);
        List<Valve<AppointRequestHolder, Boolean>> targetValves = valveRegistry.getTargetValves1("c", AppointRequestHolder.class, Boolean.class);
        DefaultPipelineContext<AppointRequestHolder, Boolean> ctx = new DefaultPipelineContext<>(targetValves, request);
        ctx.next();
        return ctx.getResult();
    }
}
