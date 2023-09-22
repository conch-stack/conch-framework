package com.nabob.conch.sample.pipeline.test;

import com.nabob.conch.sample.pipeline.ValveComponent;
import com.nabob.conch.sample.pipeline.PipelineContext;
import com.nabob.conch.sample.pipeline.Valve;

/**
 * Smart Timeout Appoint Valve
 *
 * @author Adam
 * @since 2023/9/18
 */
@ValveComponent(groupName = "b")
public class SmartTimeoutAppointValve implements Valve<AppointRequestHolder, Boolean> {

    @Override
    public void handle(PipelineContext<AppointRequestHolder, Boolean> ctx) {
        System.out.println("SmartTimeoutAppointValve is called");
        ctx.setResult(Boolean.TRUE);

        ctx.stop();
        ctx.next();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
