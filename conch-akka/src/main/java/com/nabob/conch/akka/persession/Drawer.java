package com.nabob.conch.akka.persession;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Drawer {

  public static class GetWallet {
    public final String whoseWallet;
    public final ActorRef<Wallet> replyTo;

    public GetWallet(String whoseWallet, ActorRef<Wallet> replyTo) {
      this.whoseWallet = whoseWallet;
      this.replyTo = replyTo;
    }
  }

  public static Behavior<GetWallet> create() {
    return Behaviors.receiveMessage(Drawer::onGetWallet);
  }

  private static Behavior<GetWallet> onGetWallet(GetWallet message) {
    message.replyTo.tell(new Wallet());
    return Behaviors.same();
  }
}