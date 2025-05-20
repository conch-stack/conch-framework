package com.nabob.conch.sample.enhanceconsumer;

public interface EventStateTrace {

    default void state(EventMessage msg, EventState state) {
        msg.getTags().put("event", state.name());
    }
}