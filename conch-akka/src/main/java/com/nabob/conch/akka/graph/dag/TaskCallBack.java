package com.nabob.conch.akka.graph.dag;

public interface TaskCallBack {
     Object invoke(TaskInstanceResult result);
}