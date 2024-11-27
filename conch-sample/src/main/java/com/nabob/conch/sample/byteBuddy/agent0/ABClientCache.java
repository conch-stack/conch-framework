//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.nabob.conch.sample.byteBuddy.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ABClientCache {
    private static final int MINOR_SYNC_INTERVAL = 3000;
    private static final int MAJOR_SYNC_INTERVAL = 300000;
    private List<String> codes;
    private AtomicBoolean toSync;
    private volatile Map<String, String> keyMap;
    private static String SYNC = "sync";

    private ABClientCache() {
        this.codes = new CopyOnWriteArrayList();
        this.toSync = new AtomicBoolean(true);
        this.keyMap = new HashMap();
        this.init();
    }

    public static ABClientCache getInstance() {
        return Singleton.instance;
    }

    private void init() {
        System.out.println("init start");
        (new Thread(new Runnable() {
            int syncInterval = 0;

            public void run() {
                while(true) {
                    if (ABClientCache.this.toSync.compareAndSet(true, false) || this.syncInterval >= 300000) {
                        this.syncInterval = 0;
                        ABClientCache.this.sync();
                    }

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var2) {
                    }

                    this.syncInterval += 3000;
                }
            }
        })).start();

        System.out.println("init end");
    }

    public Map<String, String> getKeyMap() {
        return this.keyMap;
    }

    public void setKeyMap(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }

    public void setKey(String key, String value) {
        this.keyMap.put(key, value);
    }

    public void pauseSync() {
        this.toSync.compareAndSet(true, false);
    }

    public void sync() {
        System.out.println("sync is called");
    }

    private static class Singleton {
        static ABClientCache instance = new ABClientCache();

        private Singleton() {
        }
    }
}
