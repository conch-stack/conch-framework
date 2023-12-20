package com.nabob.conch.agent.attach.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

/**
 * agentmain 在 main 函数开始运行后才启动（依赖于Attach机制）
 */
public class MyAgent {

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("agentmain----attach----start");
        System.out.println("agentmain getPid = " + getPid());
        System.out.println("agentmain----attach----end");
    }

    private static String getPid() {
        String[] name = ManagementFactory.getRuntimeMXBean().getName().split("@");
        if (name.length < 1) {
            return "";
        }
        return name[0];
    }
}