package com.nabob.conch.akka.persession;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class KeyCabinet {
  public static class GetKeys {
    public final String whoseKeys;
    public final ActorRef<Keys> replyTo;

    public GetKeys(String whoseKeys, ActorRef<Keys> respondTo) {
      this.whoseKeys = whoseKeys;
      this.replyTo = respondTo;
    }
  }

  public static Behavior<GetKeys> create() {
    return Behaviors.receiveMessage(KeyCabinet::onGetKeys);
  }

  private static Behavior<GetKeys> onGetKeys(GetKeys message) {
    message.replyTo.tell(new Keys());
    return Behaviors.same();
  }
}