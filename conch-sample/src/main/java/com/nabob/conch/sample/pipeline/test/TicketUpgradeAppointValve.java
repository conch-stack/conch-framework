package com.nabob.conch.sample.pipeline.test;

import com.nabob.conch.sample.pipeline.ValveComponent;
import com.nabob.conch.sample.pipeline.PipelineContext;
import com.nabob.conch.sample.pipeline.Valve;

/**
 * Ticket Upgrade Timeout Appoint Valve
 *
 * @author Adam
 * @since 2023/9/18
 */
@ValveComponent(groupName = "c")
public class TicketUpgradeAppointValve implements Valve<AppointRequestHolder, Boolean> {

    @Override
    public void handle(PipelineContext<AppointRequestHolder, Boolean> ctx) {
        System.out.println("TicketUpgradeAppointValve is called");
        ctx.next();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
