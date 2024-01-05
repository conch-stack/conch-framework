package com.nabob.conch.akka.dag;

public interface TaskCallBack {
     Object invoke(TaskInstanceResult result);
}