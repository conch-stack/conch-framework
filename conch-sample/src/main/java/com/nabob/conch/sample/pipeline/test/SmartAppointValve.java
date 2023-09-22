package com.nabob.conch.sample.pipeline.test;

import com.nabob.conch.sample.pipeline.ValveComponent;
import com.nabob.conch.sample.pipeline.PipelineContext;
import com.nabob.conch.sample.pipeline.Valve;

/**
 * Smart Appoint Valve
 *
 * @author Adam
 * @since 2023/9/18
 */
@ValveComponent(groupName = "a")
public class SmartAppointValve implements Valve<AppointRequestHolder, Boolean> {

    @Override
    public void handle(PipelineContext<AppointRequestHolder, Boolean> ctx) {
        System.out.println("SmartAppointValve is called");
        ctx.next();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
