package com.nabob.conch.akka.graph.dag;

public interface Executor {
    boolean execute(TaskCallBack callBack);
}