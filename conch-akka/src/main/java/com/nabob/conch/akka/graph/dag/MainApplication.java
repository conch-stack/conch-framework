package com.nabob.conch.akka.graph.dag;

public class MainApplication {

    public static void main(String[] args) {
        // 创建工作流
        Process process = new Process();
        // 注册任务
        Task task1 = new Task(1L, "task1", 0, -1);
        Task task2 = new Task(2L, "task2", 0, -1);
        Task task3 = new Task(3L, "task3", 0, -1);
        Task task4 = new Task(4L, "task4", 0, -1);
        Task task5 = new Task(5L, "task5", 0, -1);
        Task task6 = new Task(6L, "task6", 0, -1);

        process.addTask(task1);
        process.addTask(task2);
        process.addTask(task3);
        process.addTask(task4);
        process.addTask(task5);
        process.addTask(task6);

        process.addEdge(task1, task2);
        process.addEdge(task1, task5);
        process.addEdge(task6, task2);
        process.addEdge(task2, task3);
        process.addEdge(task2, task4);

        // 创建调度器,执行DAG调度
        Scheduler scheduler = new Scheduler();
        scheduler.schedule(process);
    }

}