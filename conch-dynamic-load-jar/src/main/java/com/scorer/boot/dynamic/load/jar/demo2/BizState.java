package com.scorer.boot.dynamic.load.jar.demo2;

public enum BizState {
    /**
     * not init or not start install yet
     */
    UNRESOLVED("unresolved"),
    /**
     * installing
     */
    RESOLVED("resolved"),

    /**
     * install succeed, and start serving
     */
    ACTIVATED("activated"),

    /**
     * install succeed, but stop serving, usually caused by a new version installed
     */
    DEACTIVATED("deactivated"),

    /**
     * install or uninstall failed.
     */
    BROKEN("broken"),

    /**
     * uninstall succeed
     */
    STOPPED("stopped");

    private String state;

    BizState(String state) {
        this.state = state;
    }

    public String getBizState() {
        return state;
    }

    @Override
    public String toString() {
        return getBizState();
    }

    public static BizState of(String state) {
        if (UNRESOLVED.name().equalsIgnoreCase(state)) {
            return UNRESOLVED;
        } else if (RESOLVED.name().equalsIgnoreCase(state)) {
            return RESOLVED;
        } else if (ACTIVATED.name().equalsIgnoreCase(state)) {
            return ACTIVATED;
        } else if (DEACTIVATED.name().equalsIgnoreCase(state)) {
            return DEACTIVATED;
        } else if (STOPPED.name().equalsIgnoreCase(state)) {
            return STOPPED;
        } else {
            return BROKEN;
        }
    }
}