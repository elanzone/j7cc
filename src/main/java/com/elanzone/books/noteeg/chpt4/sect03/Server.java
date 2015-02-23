package com.elanzone.books.noteeg.chpt4.sect03;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private ThreadPoolExecutor executor;

    public Server() {
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
    }

    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);
        System.out.printf("Server: Pool Size: %d\n",executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",executor.getActiveCount());
        System.out.printf("Server: Completed Tasks: %d\n",executor.getCompletedTaskCount());
        System.out.printf("Server: Task Count: %d\n",executor.getTaskCount());
    }

    public void endServer() {
        executor.shutdown();
    }
}
