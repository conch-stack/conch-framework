package com.nabob.conch.agent.my;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

/**
 * @author Adam
 * @since 2024/2/6
 */
public class AdamAgent {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("premain----start");
        System.out.println("premain getPid = " + getPid());
        System.out.println("premain----end");
    }

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
