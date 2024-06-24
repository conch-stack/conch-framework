package com.nabob.conch.akka.persession;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.function.Consumer;

public class Home {

  public interface Command {}

  public static class LeaveHome implements Command {
    public final String who;
    public final ActorRef<ReadyToLeaveHome> respondTo;

    public LeaveHome(String who, ActorRef<ReadyToLeaveHome> respondTo) {
      this.who = who;
      this.respondTo = respondTo;
    }
  }

  public static class ReadyToLeaveHome {
    public final String who;
    public final Keys keys;
    public final Wallet wallet;

    public ReadyToLeaveHome(String who, Keys keys, Wallet wallet) {
      this.who = who;
      this.keys = keys;
      this.wallet = wallet;
    }
  }

  public static class ReadyToLeaveHomeBehavior extends AbstractBehavior<ReadyToLeaveHome> {

    public ReadyToLeaveHomeBehavior(ActorContext<ReadyToLeaveHome> context) {
      super(context);
    }

    public static Behavior<ReadyToLeaveHome> create() {
      return Behaviors.setup(ReadyToLeaveHomeBehavior::new);
    }

    @Override
    public Receive<ReadyToLeaveHome> createReceive() {
      return newReceiveBuilder().onMessage(ReadyToLeaveHome.class, this::onReadyToLeaveHome).build();
    }

    private Behavior<ReadyToLeaveHome> onReadyToLeaveHome(ReadyToLeaveHome readyToLeaveHome) {
      getContext().getLog().info("ReadyToLeaveHome: who {} key is {} wallet is {}", readyToLeaveHome.who, readyToLeaveHome.keys, readyToLeaveHome.wallet);
      // do others if need
//      return Behaviors.stopped();

      // 复用
      return Behaviors.same();
    }
  }

  private final ActorContext<Command> context;

  private final ActorRef<KeyCabinet.GetKeys> keyCabinet;
  private final ActorRef<Drawer.GetWallet> drawer;

  private Home(ActorContext<Command> context) {
    this.context = context;
    this.keyCabinet = context.spawn(KeyCabinet.create(), "key-cabinet");
    this.drawer = context.spawn(Drawer.create(), "drawer");
  }

  private Behavior<Command> behavior() {
    return Behaviors.receive(Command.class)
        .onMessage(LeaveHome.class, this::onLeaveHome)
        .build();
  }

  private Behavior<Command> onLeaveHome(LeaveHome message) {
    ActorRef<Object> spawn = context.spawn(
            PrepareToLeaveHome.create(message.who, message.respondTo, keyCabinet, drawer),
            "leaving" + message.who);

    Consumer<ActorRef<Object>> sendRequests =
            new Consumer<ActorRef<Object>>() {
              @Override
              public void accept(ActorRef<Object> replyTo) {
                keyCabinet.tell(new KeyCabinet.GetKeys(message.who, replyTo.narrow()));
                drawer.tell(new Drawer.GetWallet(message.who, replyTo.narrow()));
              }
            };

    sendRequests.accept(spawn);

    return Behaviors.same();
  }

  // actor behavior
  public static Behavior<Command> create() {
    return Behaviors.setup(context -> new Home(context).behavior());
  }
}