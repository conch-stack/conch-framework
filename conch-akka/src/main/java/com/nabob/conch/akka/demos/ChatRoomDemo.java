package com.nabob.conch.akka.demos;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.Behavior;

import java.util.HashSet;
import java.util.Set;

public class ChatRoomDemo {

    // 定义消息类型
    interface ChatMessage {
        String getMessage();
    }

    static class UserMessage implements ChatMessage {
        private final String userName;
        private final String message;

        public UserMessage(String userName, String message) {
            this.userName = userName;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return userName + ": " + message;
        }
    }

    static class SystemMessage implements ChatMessage {
        private final String message;

        public SystemMessage(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return "[System]: " + message;
        }
    }

    // 创建一个聊天室 Actor
    static class ChatRoom extends AbstractBehavior<ChatMessage> {
        private final Set<String> users = new HashSet<>();

        private ChatRoom(ActorContext<ChatMessage> context) {
            super(context);
        }

        public static Behavior<ChatMessage> create() {
            return Behaviors.setup(ChatRoom::new);
        }

        @Override
        public Receive<ChatMessage> createReceive() {
            return newReceiveBuilder()
                    .onMessage(UserMessage.class, this::onUserMessage)
                    .onMessage(SystemMessage.class, this::onSystemMessage)
                    .build();
        }

        private Behavior<ChatMessage> onUserMessage(UserMessage message) {
            users.add(message.userName);
            broadcast(new SystemMessage(message.userName + " has joined the chat."));
            getContext().getLog().info("User '{}' says: {}", message.userName, message.message);
            return this;
        }

        private Behavior<ChatMessage> onSystemMessage(SystemMessage message) {
            broadcast(message);
            getContext().getLog().info("System message: {}", message.message);
            return this;
        }

        private void broadcast(ChatMessage message) {
            users.forEach(userName -> {
                getContext().getLog().info("Broadcasting message to '{}'", userName);
                getContext().getSelf().tell(message);
            });
        }
    }

    public static void main(String[] args) {
        // 创建 Actor 系统
        akka.actor.typed.ActorSystem<ChatMessage> system =
                akka.actor.typed.ActorSystem.create(ChatRoom.create(), "ChatRoomSystem");

        // 模拟用户加入聊天室并发送消息
        system.tell(new UserMessage("User1", "Hello, everyone!"));
        system.tell(new UserMessage("User2", "Hi there!"));
        system.tell(new UserMessage("User3", "Welcome!"));

        // 发送系统消息
        system.tell(new SystemMessage("Chat room is now open."));

        // 等待一段时间，然后终止 Actor 系统
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }
    }
}
