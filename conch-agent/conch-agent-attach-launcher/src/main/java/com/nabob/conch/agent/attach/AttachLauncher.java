package com.nabob.conch.agent.attach;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class AttachLauncher {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
//        if (args.length <= 1) {
//            System.out.println("Usage: java AttachLauncher <PID> /PATH/TO/AGENT.jar");
//            return;
//        }

        System.out.println("我来了");

        String jvmPid = args[0];
        System.out.println(jvmPid);
//        System.out.println(args[1]);

        VirtualMachine vm = VirtualMachine.attach(jvmPid);
        System.out.println("我开始了");
//        vm.loadAgent(args[1]);
        vm.loadAgent("D:\\conch\\conch-framework\\conch-agent\\conch-agent-attach-agent\\target\\conch-agent-attach-agent-0.0.1.jar");
        System.out.println("我结束了");
        vm.detach();
    }
}