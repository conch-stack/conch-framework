package com.nabob.conch.akka.persession;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;

/**
 * @author Adam
 * @since 2024/6/24
 */
public class Test {
    public static void main(String[] args) {
        ActorSystem<Home.Command> leaveHomeSystem = ActorSystem.create(Home.create(), "leaveHome");

        ActorRef<Home.ReadyToLeaveHome> readyToLeaveHomeBehavior = leaveHomeSystem.systemActorOf(Home.ReadyToLeaveHomeBehavior.create(), "ReadyToLeaveHomeBehavior", Props.empty());

        // Invalid actor path element [leaving李四]   actor path 不能有中文
//        leaveHomeSystem.tell(new Home.LeaveHome("李四", readyToLeaveHomeBehavior));
//        leaveHomeSystem.tell(new Home.LeaveHome("王二", readyToLeaveHomeBehavior));
//        leaveHomeSystem.tell(new Home.LeaveHome("张三", readyToLeaveHomeBehavior));

        leaveHomeSystem.tell(new Home.LeaveHome("adam", readyToLeaveHomeBehavior));
        leaveHomeSystem.tell(new Home.LeaveHome("ace", readyToLeaveHomeBehavior));
        leaveHomeSystem.tell(new Home.LeaveHome("solon", readyToLeaveHomeBehavior));

        // 等待一段时间，然后终止 Actor 系统
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            leaveHomeSystem.terminate();
        }
    }
}
