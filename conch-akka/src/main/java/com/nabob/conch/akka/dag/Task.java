package com.nabob.conch.akka.dag;

import java.util.UUID;

import static java.lang.Thread.sleep;

public class Task implements Executor {
    private Long id;
    private String name;
    private int state;
    private long timeout;

    public Task() {
    }

    public Task(Long id, String name, int state, long timeout) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.timeout = timeout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean execute(TaskCallBack callBack) {
        System.out.println("Task id: [" + id + "], " + "task name: [" + name + "] is running");
        state = 1;
        try {
            sleep(3000L);
        } catch (InterruptedException e) {
        }

        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setTaskId(id);
        String taskInstanceId = UUID.randomUUID().toString();
        taskInstance.setTaskInstanceId(taskInstanceId);

        TaskInstanceResult taskInstanceResult = new TaskInstanceResult("Task[" + id + "], taskInstanceId=" + taskInstanceId + " TaskInstanceResult = " + UUID.randomUUID());
        callBack.invoke(taskInstanceResult);
        return true;
    }

    public boolean hasExecuted() {
        return state == 1;
    }
}






