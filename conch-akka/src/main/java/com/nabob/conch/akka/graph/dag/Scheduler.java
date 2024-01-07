package com.nabob.conch.akka.graph.dag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scheduler {

    public void schedule(Process process) {
        while (true) {
            // 1、构建 todoTaskList
            List<Task> todoTaskList = new ArrayList<Task>();

            for (Task task : process.getTasks()) {
                if (!task.hasExecuted()) {
                    Set<Task> prevs = process.getMap().get(task);
                    if (prevs != null && !prevs.isEmpty()) {
                        boolean toAdd = true;
                        for (Task tsk : prevs) {
                            if (!tsk.hasExecuted()) {
                                toAdd = false;
                                break;
                            }
                        }
                        if (toAdd) {
                            todoTaskList.add(task);
                        }
                    } else {
                        todoTaskList.add(task);
                    }
                }
            }

            // 2.执行 todoTaskList
            if (!todoTaskList.isEmpty()) {
                for (Task task : todoTaskList) {

                    task.execute(new TaskCallBack() {
                        @Override
                        public Object invoke(TaskInstanceResult taskInstanceResult) {
                            System.out.println(taskInstanceResult);
                            return null;
                        }
                    });

                }
            } else {
                break;
            }

        }
    }

}