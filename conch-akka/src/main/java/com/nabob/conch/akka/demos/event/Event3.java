package com.nabob.conch.akka.demos.event;

import akka.actor.typed.ActorRef;

import java.util.Objects;

/**
 * 事件主体3
 *
 * @author Adam
 * @date 2021/2/23
 */
public class Event3 {

    public final String whom;
    public final ActorRef<Event2> event2ActorRef;

    public Event3(String whom, ActorRef<Event2> event2ActorRef) {
        this.whom = whom;
        this.event2ActorRef = event2ActorRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event2 greeted = (Event2) o;
        return Objects.equals(whom, greeted.whom) &&
                Objects.equals(event2ActorRef, greeted.event3ActorRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whom, event2ActorRef);
    }

    @Override
    public String toString() {
        return "Greeted{" +
                "whom='" + whom + '\'' +
                ", from=" + event2ActorRef +
                '}';
    }
}
