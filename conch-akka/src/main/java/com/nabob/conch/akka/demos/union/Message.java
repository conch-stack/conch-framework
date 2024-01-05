package com.nabob.conch.akka.demos.union;

import akka.actor.typed.ActorRef;

public class Message {

    private EMessage message;

    private ActorRef parent;

    public EMessage getMessage() {

        return message;

    }

    public void setMessage(EMessage message) {

        this.message = message;

    }

    public ActorRef getParent() {

        return parent;

    }

    public void setParent(ActorRef parent) {

        this.parent = parent;

    }

    public static enum EMessage {

        START, FINISHED, DATA_SOURCE_FINISHED;

    }

    public Message(EMessage message, ActorRef parent) {

        super();

        this.message = message;

        this.parent = parent;

    }

}