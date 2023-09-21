package com.nabob.conch.sample.pipeline.test;

import com.google.common.collect.Lists;
import com.nabob.conch.sample.pipeline.DefaultPipelineContext;
import com.nabob.conch.sample.pipeline.Pipeline;
import com.nabob.conch.sample.pipeline.Valve;

import java.util.List;

/**
 * Appoint Pipeline
 *
 * @author Adam
 * @since 2023/9/18
 */
public class AppointPipeline implements Pipeline<AppointRequestHolder, Boolean> {

    private static final List<Valve<AppointRequestHolder, Boolean>> APPOINT_VALVES;

    static {
        APPOINT_VALVES = Lists.newArrayList();
        APPOINT_VALVES.add(new SmartAppointValve());
        APPOINT_VALVES.add(new SmartTimeoutAppointValve());
        APPOINT_VALVES.add(new TicketUpgradeAppointValve());
    }

    @Override
    public Boolean invoke(AppointRequestHolder request) {
        DefaultPipelineContext<AppointRequestHolder, Boolean> ctx = new DefaultPipelineContext<>(APPOINT_VALVES, request);
        ctx.next();
        return ctx.getResult();
    }
}
