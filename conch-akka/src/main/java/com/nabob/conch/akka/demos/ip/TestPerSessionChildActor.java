package com.nabob.conch.akka.demos.ip;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Adam
 * @since 2024/1/8
 */
public class TestPerSessionChildActor {

    // dummy data types just for this sample
    public static class Keys {
    }

    public static class Wallet {
    }

    public static class KeyCabinet {
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

    public static class Drawer {

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

    public static class Home {

        public interface Command {
        }

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

        static class ReadyToLeaveHomeBehavior extends AbstractBehavior<ReadyToLeaveHome> {

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
                return Behaviors.stopped();
            }
        }

        private ActorContext<Command> context;

        private ActorRef<KeyCabinet.GetKeys> keyCabinet;
        private ActorRef<Drawer.GetWallet> drawer;

        public Map<String, ActorRef<Object>> child = new ConcurrentHashMap<>();

        private Home() {
        }

        public void init(ActorContext<Command> context) {
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
                    PrepareToLeaveHome.create(message.who, message.respondTo, keyCabinet, drawer, this),
                    "leaving" + message.who);
            child.put(message.who, spawn);
            return Behaviors.same();
        }
    }

    // per session actor behavior
    static class PrepareToLeaveHome extends AbstractBehavior<Object> {
        static Behavior<Object> create(
                String whoIsLeaving,
                ActorRef<Home.ReadyToLeaveHome> replyTo,
                ActorRef<KeyCabinet.GetKeys> keyCabinet,
                ActorRef<Drawer.GetWallet> drawer, Home home) {
            return Behaviors.setup(
                    context -> new PrepareToLeaveHome(context, whoIsLeaving, replyTo, keyCabinet, drawer, home));
        }

        private final String whoIsLeaving;
        private final ActorRef<Home.ReadyToLeaveHome> replyTo;
        private final ActorRef<KeyCabinet.GetKeys> keyCabinet;
        private final ActorRef<Drawer.GetWallet> drawer;
        private Optional<Wallet> wallet = Optional.empty();
        private Optional<Keys> keys = Optional.empty();

        private Home home;

        private PrepareToLeaveHome(
                ActorContext<Object> context,
                String whoIsLeaving,
                ActorRef<Home.ReadyToLeaveHome> replyTo,
                ActorRef<KeyCabinet.GetKeys> keyCabinet,
                ActorRef<Drawer.GetWallet> drawer,
                Home home) {
            super(context);
            this.whoIsLeaving = whoIsLeaving;
            this.replyTo = replyTo;
            this.keyCabinet = keyCabinet;
            this.drawer = drawer;
            this.home = home;
        }

        @Override
        public Receive<Object> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Wallet.class, this::onWallet)
                    .onMessage(Keys.class, this::onKeys)
                    .onSignal(PostStop.class, signal -> onPostStop())
                    .build();
        }

        private Behavior<Object> onPostStop() {
            getContext().getLog().info("PrepareToLeaveHome Stopped");
            home.child.remove(whoIsLeaving);
            return this;
        }

        private Behavior<Object> onWallet(Wallet wallet) {
            this.wallet = Optional.of(wallet);
            return completeOrContinue();
        }

        private Behavior<Object> onKeys(Keys keys) {
            this.keys = Optional.of(keys);
            return completeOrContinue();
        }

        private Behavior<Object> completeOrContinue() {
            if (wallet.isPresent() && keys.isPresent()) {
                replyTo.tell(new Home.ReadyToLeaveHome(whoIsLeaving, keys.get(), wallet.get()));
                return Behaviors.stopped();
            } else {
                return this;
            }
        }
    }

    public static void main(String[] args) {

        Home home = new Home();

        // actor behavior
        Behavior<Home.Command> homeBehavior = Behaviors.setup(context -> {
            home.init(context);
            return home.behavior();
        });

        //#actor-system
        ActorSystem<Home.Command> testPerSessionChildActor = ActorSystem.create(homeBehavior, "TestPerSessionChildActor");
        //#actor-system

        // 拿到多个返回后的Actor
        ActorRef<Home.ReadyToLeaveHome> readyToLeaveHomeBehavior = testPerSessionChildActor.systemActorOf(Home.ReadyToLeaveHomeBehavior.create(), "ReadyToLeaveHomeBehavior", Props.empty());

        try {
            for (int i = 0; i < 3; i++) {
                System.out.println(">>> Press ENTER who <<<");
                byte[] b = new byte[1024];
                System.in.read(b);
                String who = String.valueOf(b);

                //#main-send-messages
                testPerSessionChildActor.tell(new Home.LeaveHome(who, readyToLeaveHomeBehavior));
                //#main-send-messages

                Thread.sleep(TimeUnit.SECONDS.toMillis(5));

                ActorRef<Object> objectActorRef = home.child.get(who);
                objectActorRef.tell(new Keys());
                objectActorRef.tell(new Wallet());

                System.in.read();
                System.out.println("end " + i);
            }
        } catch (IOException ignored) {
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            testPerSessionChildActor.terminate();
        }
    }

}
