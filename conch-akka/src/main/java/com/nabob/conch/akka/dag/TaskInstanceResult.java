package com.nabob.conch.akka.dag;

public class TaskInstanceResult {
    String resultJson;

    @Override
    public String toString() {
        return "TaskInstanceResult{" +
                "resultJson='" + resultJson + '\'' +
                '}';
    }

    public TaskInstanceResult(String resultJson) {
        this.resultJson = resultJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}