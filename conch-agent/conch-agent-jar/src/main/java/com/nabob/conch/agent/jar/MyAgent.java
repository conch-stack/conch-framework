package com.nabob.conch.agent.jar;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class MyAgent {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("premain----start");
        System.out.println("premain getPid = " + getPid());
        System.out.println("premain----end");
    }

    private static String getPid() {
        String[] name = ManagementFactory.getRuntimeMXBean().getName().split("@");
        if (name.length < 1) {
            return "";
        }
        return name[0];
    }
}