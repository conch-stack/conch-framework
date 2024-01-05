package com.nabob.conch.akka.dag;

public interface Executor {
    boolean execute(TaskCallBack callBack);
}