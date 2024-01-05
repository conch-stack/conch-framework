package com.nabob.conch.akka.dag;

public class TaskInstance {
    private String taskInstanceId;
    private Long taskId;
    private String name;
    private int state;

    public TaskInstance() {
    }

    public TaskInstance(String taskInstanceId, Long taskId, String name, int state) {
        this.taskInstanceId = taskInstanceId;
        this.taskId = taskId;
        this.name = name;
        this.state = state;
    }

    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(String taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}


